package com.xiaogang.framework.basic.annotations;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 15:07
 * @Description:
 */
public class JsonResponseValidationProcessor implements HandlerMethodReturnValueHandler, InitializingBean {

    private HttpMessageConverter defaultMessageConverter;

    private HttpMessageConverter messageConverter;

    private List<BeanWrapper> beanWrappers;

    private BeanTranslateProcessor beanTranslateProcessor;

    public BeanTranslateProcessor getBeanTranslateProcessor() {
        return beanTranslateProcessor;
    }

    public void setBeanTranslateProcessor(BeanTranslateProcessor beanTranslateProcessor) {
        this.beanTranslateProcessor = beanTranslateProcessor;
    }

    public List<BeanWrapper> getBeanWrappers() {
        return beanWrappers;
    }

    public void setBeanWrappers(List<BeanWrapper> beanWrappers) {
        this.beanWrappers = beanWrappers;
    }

    public HttpMessageConverter getMessageConverter() {
        return messageConverter;
    }

    public void setMessageConverter(HttpMessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public HttpMessageConverter getDefaultMessageConverter() {
        return defaultMessageConverter;
    }

    public void setDefaultMessageConverter(HttpMessageConverter defaultMessageConverter) {
        this.defaultMessageConverter = defaultMessageConverter;
    }

    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getMethodAnnotation(ResponseJson.class) != null;
    }

    @SuppressWarnings("unchecked")
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {
        RpcContext context = RpcContext.getContext();
        RpcContext.removeContext(); //正常返回流程清除上下文
        Object result = returnValue;
        mavContainer.setRequestHandled(true);
        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);
        if (returnValue == null) {
            Map<String, Object> message = new HashMap<String, Object>();
            message.put("success", true);
            String msg = context.getMessage();
            if (!StringUtils.isEmpty(msg)) {
                message.put("msg", msg);
            }
            message.put("data", new HashMap<String, String>());
            result = message;
        } else {
            try {
                //校验出参,如果出错则直接抛出异常，否则继续向下
//                BeanValidator.validator(result, Direction.OUT);
                ResponseJson responseJson = returnType.getMethodAnnotation(ResponseJson.class);
                if (responseJson.location() == ResponseJson.Location.MESSAGE
                        || returnValue instanceof String && responseJson.location() == ResponseJson.Location.UNDEFINED) {
                    Map<String, Object> message = new HashMap<String, Object>();
                    message.put("success", true);
                    message.put("msg", returnValue);
                    result = message;
                } else {
                    if (responseJson.translate()) {
                        beanTranslateProcessor.translate(returnValue);
                    }
                    if (context.isNeedOrigin()) {
                        //如果不需要对象转换直接返回对象
                        result = returnValue;
                    } else if (!context.isNeedWrap()) {
                        //是否需要wrapper转换
                        Map<String, Object> message = new HashMap<String, Object>();
                        message.put("success", true);
                        //从上下文获取msg信息
                        String msg = context.getMessage();
                        if (!StringUtils.isEmpty(msg)) {
                            message.put("msg", msg);
                        }
                        message.put("data", returnValue);
                        result = message;
                    } else {
                        for (BeanWrapper beanWrapper : beanWrappers) {
                            if (beanWrapper.supportsType(returnType)) {
                                result = beanWrapper.wrap(returnValue);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //构造参数校验不符合的返回值
                Map<String, Object> message = new HashMap<String, Object>();
                message.put("success", false);
                message.put("msg", e.getMessage());
                result = message;
            }
        }
        messageConverter.write(result, new MediaType(MediaType.APPLICATION_JSON,
                Collections.singletonMap("charset", "UTF-8")), outputMessage);
    }

    protected ServletServerHttpResponse createOutputMessage(NativeWebRequest webRequest) {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        return new ServletServerHttpResponse(response);
    }

    public void afterPropertiesSet() throws Exception {
        if (beanWrappers == null || beanWrappers.size() == 0) {
            throw new Exception("beanWrappers undefined");
        }
    }

}
