package com.xiaogang.framework.basic.processors;

import org.springframework.core.MethodParameter;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 15:27
 * @Description:
 */
public class DefaultBeanWrapper extends AbstractBeanWrapper {

    public Object wrap(Object bean) {
        return new SuccessData(bean);
    }

    // public Integer getPriority() {
    // return Integer.MAX_VALUE;
    // }

    @Override
    public boolean supports(MethodParameter returnType) {
        return true;
    }

}
