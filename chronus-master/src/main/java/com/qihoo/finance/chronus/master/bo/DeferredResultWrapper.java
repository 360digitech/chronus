package com.qihoo.finance.chronus.master.bo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Created by xiongpu on 2019/9/16.
 */
public class DeferredResultWrapper<T> {
    //60 seconds
    private static final long TIMEOUT = 60 * 1000;
    /**
     * 默认返回304
     */
    private static final ResponseEntity NOT_MODIFIED_RESPONSE = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    private String instanceId;
    private DeferredResult<ResponseEntity<T>> result;

    /**
     * 构造函数
     */
    public DeferredResultWrapper(String instanceId) {
        this.instanceId = instanceId;
        result = new DeferredResult<>(TIMEOUT, NOT_MODIFIED_RESPONSE);
    }

    public String getInstanceId() {
        return instanceId;
    }

    /**
     * 超时动作
     * @param timeoutCallback
     */
    public void onTimeout(Runnable timeoutCallback) {
        result.onTimeout(timeoutCallback);
    }

    public void onCompletion(Runnable completionCallback) {
        result.onCompletion(completionCallback);
    }

    /**
     * 设置返回结果
     *
     * @param data
     */
    public void setResult(T data) {
        result.setResult(new ResponseEntity<>(data, HttpStatus.OK));
    }

    public DeferredResult<ResponseEntity<T>> getResult() {
        return result;
    }

}
