package com.xiaogang.framework.basic.annotations;

import org.springframework.core.MethodParameter;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 15:16
 * @Description:
 */
public interface BeanWrapper {

    /**
     * 支持性判断
     *
     * @return
     */
    boolean supportsType(MethodParameter returnType);

    /**
     * 对象包装
     *
     * @param bean
     * @return
     */
    Object wrap(Object bean);
}
