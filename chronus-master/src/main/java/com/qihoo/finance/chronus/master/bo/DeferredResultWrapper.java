package com.qihoo.finance.chronus.master.bo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Created by xiongpu on 2019/9/16.
 */
@Slf4j
public class DeferredResultWrapper<T> {
    //60 seconds
    private static final long TIMEOUT = 60 * 1000;
    /**
     * 默认返回304
     */
    private static final ResponseEntity NOT_MODIFIED_RESPONSE = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    private String instanceId;
    private String version;
    private DeferredResult<ResponseEntity<T>> result;

    /**
     * 构造函数
     */
    public DeferredResultWrapper(String instanceId, String version) {
        this.instanceId = instanceId;
        this.version = version;
        result = new DeferredResult<>(TIMEOUT, NOT_MODIFIED_RESPONSE);

        this.onTimeout();
        this.onCompletion();
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getVersion() {
        return version;
    }

    /**
     * 超时动作
     */
    public void onTimeout() {
        result.onTimeout(() -> log.debug("pullAssignResult timeout {}", this.instanceId));
    }

    public void onCompletion() {
        result.onCompletion(() -> log.debug("返回给{} 信息:{}", this.instanceId, this.result.getResult()));
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
