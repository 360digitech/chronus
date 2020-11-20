package com.qihoo.finance.chronus.protocol.http;

import com.alibaba.fastjson.JSONObject;
import com.qihoo.finance.chronus.common.exception.ChronusErrorCodeEnum;
import com.qihoo.finance.chronus.common.exception.SystemException;
import com.qihoo.finance.chronus.sdk.ChronusSdkFacade;
import com.qihoo.finance.chronus.sdk.domain.JobData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by xiongpu on 2019/8/29.
 */
@Slf4j
public class ChronusSdkRestTemplateFacade<T> implements ChronusSdkFacade<T> {
    private static final String JOB_SELECT_TASKS_URL = "/job/selectTasks/";
    private static final String JOB_EXECUTE_URL = "/job/execute/";
    private static final String JOB_EXECUTE_SIMPLE_URL = "/job/executeSimple/";
    private RestTemplate restTemplate;

    public ChronusSdkRestTemplateFacade(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<T> selectTasks(JobData jobData) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<JobData> requestEntity = new HttpEntity<>(jobData, headers);
        return execute(JOB_SELECT_TASKS_URL, requestEntity, List.class);
    }

    @Override
    public boolean execute(JobData jobData, List<T> itemList) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        JSONObject body = new JSONObject();
        body.put("jobData", jobData);
        body.put("itemList", itemList);
        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(body, headers);
        return execute(JOB_EXECUTE_URL, requestEntity, Boolean.class);
    }

    @Override
    public boolean execute(JobData jobData) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<JobData> requestEntity = new HttpEntity<>(jobData, headers);
        return execute(JOB_EXECUTE_SIMPLE_URL, requestEntity, Boolean.class);
    }

    private <K> K execute(String url, HttpEntity requestEntity, Class<K> clazz) {
        try {
            ResponseEntity<K> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, clazz);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
            throw new SystemException(ChronusErrorCodeEnum.CALL_BIZ_SYS_ERROR, responseEntity.getStatusCode().getReasonPhrase());
        } catch (Exception e) {
            throw new SystemException(ChronusErrorCodeEnum.CALL_BIZ_SYS_ERROR, e);
        }
    }
}
