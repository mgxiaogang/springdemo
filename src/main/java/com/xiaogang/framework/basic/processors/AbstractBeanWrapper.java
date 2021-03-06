package com.xiaogang.framework.basic.processors;

import com.xiaogang.framework.basic.processors.interfaces.BeanWrapper;
import com.xiaogang.framework.basic.processors.interfaces.ResponseData;
import org.springframework.core.MethodParameter;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 15:26
 * @Description:
 */
public abstract class AbstractBeanWrapper implements BeanWrapper {

    public boolean supportsType(MethodParameter returnType) {
        if (ResponseData.class.isAssignableFrom(returnType.getParameterType())) {
            return false;
        }
        return supports(returnType);
    }

    public abstract boolean supports(MethodParameter returnType);

}
