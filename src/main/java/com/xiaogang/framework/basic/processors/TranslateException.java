package com.xiaogang.framework.basic.processors;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 15:13
 * @Description:
 */
public class TranslateException extends Exception {
    private static final long serialVersionUID = -5676399898848577947L;

    public TranslateException(String message) {
        super(message);
    }

    public TranslateException(String message, Throwable cause) {
        super(message, cause);
    }

    public TranslateException(Throwable cause) {
        super(cause);
    }

}
