package com.xiaogang.framework.basic.processors;

import com.xiaogang.framework.basic.annotations.Json;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 14:54
 * @Description:
 */
public class JsonMapperArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Logger log = LoggerFactory.getLogger(JsonMapperArgumentResolver.class);

    private ObjectMapper objectMapper;
    private static final String PATH_DELIMITER = "/";

    public JsonMapperArgumentResolver() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Json.class);
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        try {
            Json jsonAnn = parameter.getParameterAnnotation(Json.class);
            String path = jsonAnn.path();
            String allParam = getRequestParam(webRequest);
            if (allParam == null) {
                // throw new
                // Exception("HttpSerlvetRequest input param is null");
                return null;
            }
            JsonNode node = objectMapper.readTree(allParam);
            if (path == null || "".equals(path)) {
                path = parameter.getParameterName();
                if (node.has(path)) {
                    return objectMapper.readValue(node.path(path), getReferenceType(parameter));
                }
                try {
                    return objectMapper.readValue(allParam, getReferenceType(parameter));
                } catch (Throwable e) {
                    log.error("json map error", e);
                    return null;
                }

            } else {
                String[] paths = StringUtils.split(path, PATH_DELIMITER);
                for (String p : paths) {
                    node = node.path(p);
                }
                if (node == null) {
                    return null;
                }
                return objectMapper.readValue(node, getReferenceType(parameter));
            }
        } catch (Exception e) {
            log.error("can't generate param [" + parameter.getParameterName() + "] for url "
                    + webRequest.getNativeRequest(HttpServletRequest.class).getServletPath() + ", and source input is "
                    + getRequestParam(webRequest));
            throw e;
        }
    }

    /**
     * 获取反射的对象类型
     */
    private JavaType getReferenceType(MethodParameter parameter) {
        Type type = parameter.getGenericParameterType();
        try {
            return getReferenceType(type);
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unsuppored Reference " + parameter.getParameterName()
                    + " To JavaType " + parameter.getParameterType().getName(), e);
        }
    }

    private JavaType getReferenceType(Type type) {
        if (type instanceof ParameterizedType) {
            Type[] genericTypes = ((ParameterizedType) type).getActualTypeArguments();
            Class<?> parameterType = (Class<?>) ((ParameterizedType) type).getRawType();
            if (Collection.class.isAssignableFrom(parameterType)) {
                if (genericTypes.length >= 1) {
                    return objectMapper.getTypeFactory().constructCollectionType(
                            (Class<? extends Collection>) parameterType, getReferenceType(genericTypes[0]));
                }

            } else if (Map.class.isAssignableFrom(parameterType)) {
                if (genericTypes.length >= 2) {
                    return objectMapper.getTypeFactory().constructMapType((Class<? extends Map>) parameterType,
                            getReferenceType(genericTypes[0]), getReferenceType(genericTypes[1]));
                } else if (genericTypes.length == 1) {
                    return objectMapper.getTypeFactory().constructMapType((Class<? extends Map>) parameterType,
                            getReferenceType(genericTypes[0]), getReferenceType(Object.class));
                } else {
                    return objectMapper.getTypeFactory().constructMapType((Class<? extends Map>) parameterType,
                            Object.class, Object.class);
                }

            }
            throw new UnsupportedOperationException("Unsuppored Reference To JavaType " + type);
        }
        return objectMapper.getTypeFactory().constructType(type);
    }

    /**
     * 获取HttpServletRequest参数体
     *
     * @param webRequest
     * @return
     * @throws IOException
     */
    private String getRequestParam(NativeWebRequest webRequest) throws IOException {
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String method = httpServletRequest.getMethod();
        if (method.equals("GET") || method.equals("DELETE")) {
            return httpServletRequest.getQueryString();
        }
        StringBuilder buffer = new StringBuilder();
        String line;
        BufferedReader reader = httpServletRequest.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

}
