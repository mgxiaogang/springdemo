package com.xiaogang.framework.basic.annotations;

import java.lang.annotation.*;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 14:45
 * @Description:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseJson {
    boolean translate() default false;

    Location location() default Location.UNDEFINED;

    enum Location {
        UNDEFINED, DATA, MESSAGE
    }
}
