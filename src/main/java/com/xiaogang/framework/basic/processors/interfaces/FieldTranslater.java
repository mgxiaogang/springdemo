package com.xiaogang.framework.basic.processors.interfaces;

import com.xiaogang.framework.basic.processors.TranslateException;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 15:13
 * @Description:
 */
public interface FieldTranslater {
    public <T> T translate(final Object targetObj, final String srcFieldName, final String additional,
                           final Class<? extends Object> additionalClass, final Class<T> type) throws TranslateException;

}
