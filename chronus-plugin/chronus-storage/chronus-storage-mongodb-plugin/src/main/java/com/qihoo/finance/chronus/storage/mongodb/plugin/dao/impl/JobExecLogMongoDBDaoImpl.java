package com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.common.PageQueryParams;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.common.TableConstant;
import com.qihoo.finance.chronus.metadata.api.log.dao.JobExecLogDao;
import com.qihoo.finance.chronus.metadata.api.log.entity.JobExecLogEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

/**
 * Created by xiongpu on 2019/7/29.
 */
public class JobExecLogMongoDBDaoImpl extends AbstractMongoBaseDao<JobExecLogEntity> implements JobExecLogDao {
    public JobExecLogMongoDBDaoImpl(@Autowired MongoTemplate mongoTemplate) {
        super(mongoTemplate, TableConstant.JOB_EXEC_LOG_INFO);
    }

    @Override
    public void batchInsert(List<JobExecLogEntity> jobExecLogList) {
        super.insert(jobExecLogList);
    }

    @Override
    public List<JobExecLogEntity> selectListAll() {
        return super.selectListAll();
    }

    @Override
    public List<JobExecLogEntity> selectListByCluster(String cluster, String taskName, String sysCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("cluster").is(cluster));
        query.addCriteria(Criteria.where("taskName").is(taskName));
        query.addCriteria(Criteria.where("sysCode").is(sysCode));
        return super.selectList(query);
    }

    @Override
    public PageResult<JobExecLogEntity> findAllByPage(JobExecLogEntity jobExecLogEntity) {

        Map<String, String> param = new HashMap<>(3);
        if (StringUtils.isNotBlank(jobExecLogEntity.getCluster())) {
            param.put("cluster", jobExecLogEntity.getCluster());
        }
        if (StringUtils.isNotBlank(jobExecLogEntity.getSysCode())) {
            param.put("sysCode", jobExecLogEntity.getSysCode());
        }
        if (StringUtils.isNotBlank(jobExecLogEntity.getTaskName())) {
            param.put("taskName", jobExecLogEntity.getTaskName());
        }

        int page = jobExecLogEntity.getPageNum() - 1;
        PageQueryParams pageQueryParams = new PageQueryParams(page, jobExecLogEntity.getPageSize());
        Query query = getWhereParamsByRequest(param);
        if (jobExecLogEntity.getStartDate() != null) {
            //使用调度的开始时间判断
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar.setTime(jobExecLogEntity.getStartDate());
            Date startDate = calendar.getTime();
            if (jobExecLogEntity.getEndDate() != null) {
                calendar.setTime(jobExecLogEntity.getEndDate());
                Date endDate = calendar.getTime();
                query.addCriteria(Criteria.where("startDate").gte(startDate).andOperator(Criteria.where("startDate").lte(endDate)));
            } else {
                query.addCriteria(Criteria.where("startDate").gte(startDate));
            }
        }
        Long count = this.countByQuery(query);

        query.with(new Sort(Sort.Direction.DESC, "startDate"));

        final PageResult<JobExecLogEntity> pageResult = new PageResult<>();
        pageResult.setTotal(count);
        pageResult.setPageSize(jobExecLogEntity.getPageSize());
        pageResult.setPageNum(page);
        pageResult.setList(this.selectByQuery(query, pageQueryParams));
        return pageResult;
    }
}
