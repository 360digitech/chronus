package com.qihoo.finance.chronus.modules.job;

import com.qihoo.finance.chronus.sdk.annotation.Job;
import com.qihoo.finance.chronus.sdk.annotation.JobMethod;
import com.qihoo.finance.chronus.sdk.domain.JobData;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Created by xiongpu on 2019/11/15.
 */
@Job
@Service("annotationExecuteBean")
public class AnnotationExecuteBean {
    private static final Logger logger = LogManager.getLogger(AnnotationExecuteBean.class);

    @JobMethod(key = "annotationExecuteBean.execute1")
    public boolean execute(JobData jobData) throws Exception {
        System.out.println(jobData.getTaskParameter());
        if (StringUtils.isNotBlank(jobData.getTaskParameter())) {
            Thread.sleep(Long.valueOf(jobData.getTaskParameter().trim()));
        }
        logger.info("结束处理 item:{}，当前时间:{}", jobData.getTaskParameter(), LocalDateTime.now());
        return true;
    }
}
