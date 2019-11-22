package com.qihoo.finance.chronus.dispatcher.dubbo.util;

import com.qihoo.finance.chronus.sdk.domain.JobConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.Invocation;

/**
 * Created by xiongpu on 2019/9/7.
 */
@Slf4j
public class InvocationUtil {
    public static JobConfig getJobConfigByArgs(final Invocation invocation){
        Object[] args = invocation.getArguments();
        JobConfig jobConfig = null;
        if (args != null) {
            for (Object o : args) {
                if (!(o instanceof JobConfig)) {
                    continue;
                }
                jobConfig = (JobConfig) o;
                if (StringUtils.isBlank(jobConfig.getSysCode())) {
                    log.error("ClientLoadBalance doSelect() error, SysCode isBlank!");
                    return null;
                }
                break;
            }
        }
        return jobConfig;
    }
}
