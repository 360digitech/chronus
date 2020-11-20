package com.qihoo.finance.chronus.modules.job;

import com.qihoo.finance.chronus.sdk.domain.JobData;
import com.qihoo.finance.chronus.sdk.domain.TaskItemDefineDomain;
import com.qihoo.finance.chronus.sdk.service.ChronusSdkExecuteFlowService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by xiongpu on 2019/11/15.
 */
@Service("selectExecuteBatchBean")
public class SelectExecuteBatchBean implements ChronusSdkExecuteFlowService<Integer> {
    private static final Logger logger = LogManager.getLogger(SelectExecuteBatchBean.class);

    @Override
    public List<Integer> selectTasks(JobData jobData) throws Exception {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < jobData.getEachFetchDataNum(); i++) {
            result.add(i);
        }
        if (StringUtils.isNotBlank(jobData.getTaskParameter())) {
            Thread.sleep(Long.valueOf(jobData.getTaskParameter().trim()));
        }
        return result;
    }

    @Override
    public boolean execute(JobData jobData,List<Integer> domain) throws Exception {
        System.out.println(domain);
        if (StringUtils.isNotBlank(jobData.getTaskParameter())) {
            Thread.sleep(Long.valueOf(jobData.getTaskParameter().trim()));
        }
        logger.info("结束处理 item:{}，当前时间:{}", domain, LocalDateTime.now());
        return true;
    }
}
