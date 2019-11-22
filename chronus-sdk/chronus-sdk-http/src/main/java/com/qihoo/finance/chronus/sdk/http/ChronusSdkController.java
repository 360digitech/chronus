package com.qihoo.finance.chronus.sdk.http;

import com.qihoo.finance.chronus.sdk.BaseSdkService;
import com.qihoo.finance.chronus.sdk.domain.JobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * TODO
 * Created by xiongpu on 2019/8/20.
 */
@Slf4j
@RestController("/chronus/sdk/")
public class ChronusSdkController extends BaseSdkService {

    @Override
    @RequestMapping(value = "/version/", method = RequestMethod.GET)
    public String getVersion() {
        return super.getVersion();
    }

    @Override
    @RequestMapping(value = "/execute/", method = RequestMethod.POST)
    public boolean execute(JobConfig jobConfig, Object item) {
        return super.execute(jobConfig, item);
    }

    @Override
    @RequestMapping(value = "/executeBatch/", method = RequestMethod.POST)
    public boolean executeBatch(JobConfig jobConfig, Object[] item) {
        return super.executeBatch(jobConfig, item);
    }

    @Override
    @RequestMapping(value = "/selectTasks/", method = RequestMethod.POST)
    public List selectTasks(JobConfig jobConfig, List taskItemList, int eachFetchDataNum) throws Exception {
        return super.selectTasks(jobConfig, taskItemList, eachFetchDataNum);
    }

}
