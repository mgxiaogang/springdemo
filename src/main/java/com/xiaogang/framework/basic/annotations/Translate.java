package com.xiaogang.framework.basic.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 15:14
 * @Description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Translate {

    String source();

    Class<? extends FieldTranslater> using();

    String additional() default "";

    Class<? extends Object> additionalClass() default Object.class;
}

