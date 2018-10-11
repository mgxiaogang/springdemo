package com.xiaogang.framework.basic.processors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 15:09
 * @Description:
 */
public class RpcContext {
    //暂存request对象
    private Object request;
    //执行返回消息
    private String message;
    //对象是否需要包装为responseVo类型
    private boolean needOrigin = false;
    //对象是否需要额外转换，兼容以前的老逻辑并给出扩展
    private boolean needWrap = true;

    //异步保存上下文
    private Future<?> future;

    private final Map<String, Object> values = new HashMap<String, Object>();
    private final Map<String, String> attachments = new HashMap<String, String>();

    private static final ThreadLocal<RpcContext> LOCAL = new ThreadLocal<RpcContext>() {
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };

    public static RpcContext getContext() {
        return LOCAL.get();
    }

    public static void removeContext() {
        LOCAL.remove();
    }

    /**
     * 对象是否需要转换
     */

    public boolean isNeedWrap() {
        return this.needWrap;
    }

    public void setNeedWrap(boolean needWrap) {
        this.needWrap = needWrap;
    }

    public boolean isNeedOrigin() {
        return this.needOrigin;
    }

    public void setNeedOrigin(boolean needOrigin) {
        this.needOrigin = needOrigin;
    }

    /**
     * 自定义上下文message
     */

    public void setMessage(String message) {
        //一旦设置了message直接使用自己的返回封装
        this.setNeedWrap(false);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    /**
     * 异步调用保存Future
     */

    public <T> Future<T> getFuture() {
        return (Future<T>) future;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    /**
     * 处理额外上下文参数
     */

    public String getAttachment(String key) {
        return attachments.get(key);
    }

    public RpcContext setAttachment(String key, String value) {
        if (value == null) {
            attachments.remove(key);
        } else {
            attachments.put(key, value);
        }
        return this;
    }

    public RpcContext removeAttachment(String key) {
        attachments.remove(key);
        return this;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public RpcContext setAttachments(Map<String, String> attachment) {
        this.attachments.clear();
        if (attachment != null && attachment.size() > 0) {
            this.attachments.putAll(attachment);
        }
        return this;
    }

    public void clearAttachments() {
        this.attachments.clear();
    }

    /**
     * 保留待扩展用
     */

    public Object getRequest() {
        return request;
    }

    public <T> T getRequest(Class<T> clazz) {
        return (request != null && clazz.isAssignableFrom(request.getClass())) ? (T) request : null;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public Map<String, Object> get() {
        return values;
    }

    public RpcContext set(String key, Object value) {
        if (value == null) {
            values.remove(key);
        } else {
            values.put(key, value);
        }
        return this;
    }

    public RpcContext remove(String key) {
        values.remove(key);
        return this;
    }

    public Object get(String key) {
        return values.get(key);
    }
}
