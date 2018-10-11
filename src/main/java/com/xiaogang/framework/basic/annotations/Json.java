package com.xiaogang.framework.basic.annotations;

import java.lang.annotation.*;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 14:46
 * @Description:
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Json {
    String path() default "";
}
