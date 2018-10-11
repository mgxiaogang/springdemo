package com.xiaogang.framework.basic.annotations;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 15:27
 * @Description:
 */
public class SuccessData implements ResponseData {

    private boolean success = true;

    private Object data;

    public SuccessData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
