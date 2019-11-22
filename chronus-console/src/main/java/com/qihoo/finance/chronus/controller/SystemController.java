package com.qihoo.finance.chronus.controller;

import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.common.util.ControllerUtil;
import com.qihoo.finance.chronus.core.system.service.SystemGroupService;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.system.entity.SystemGroupEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.Set;


/**
 * Created by xiongpu on 2018/8/21.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/system")
public class SystemController {

    @Resource
    private SystemGroupService systemGroupService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public Response insert(@RequestBody @Valid SystemGroupEntity systemGroupEntity, BindingResult bindingResult) throws Exception {
        Response response = new Response().success();
        if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
            return response;
        }
        String userName = (String) SecurityUtils.getSubject().getPrincipal();
        systemGroupEntity.setCreatedBy(userName);
        systemGroupEntity.setUpdatedBy(userName);
        systemGroupService.insert(systemGroupEntity);
        return response;
    }


    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public Response delete(@RequestBody @Valid SystemGroupEntity systemGroupEntity, BindingResult bindingResult) throws Exception {
        Response response = new Response().success();
        if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
            return response;
        }
        systemGroupService.delete(systemGroupEntity);
        return response;
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public Response put(@RequestBody @Valid SystemGroupEntity systemGroupEntity, BindingResult bindingResult) throws Exception {
        Response response = new Response().success();
        try {
            if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
                return response;
            }
            if (systemGroupEntity == null || systemGroupEntity.getId() == null) {
                return response.hinderFail("systemGroupEntity.id为空,无法更新!");
            }
            String userName = (String) SecurityUtils.getSubject().getPrincipal();
            systemGroupEntity.setUpdatedBy(userName);
            systemGroupEntity.setDateUpdated(new Date());
            systemGroupService.update(systemGroupEntity);
        } catch (Exception e) {
            log.error("更新group配置异常! systemGroupEntity:{}", systemGroupEntity, e);
            response.hinderFail("更新group配置异常" + e.getMessage());
        }
        return response;
    }


    @RequestMapping(value = "/loadAllSysCodes", method = RequestMethod.GET)
    @ResponseBody
    public Response loadAllSysCodes() throws Exception {
        Response response = new Response().success();
        Set<String> allSysCodes = systemGroupService.loadAllSysCodes();
        response.setData(allSysCodes);
        return response;
    }
    @RequestMapping(value = "/getAllGroup", method = RequestMethod.POST)
    @ResponseBody
    public Response getAllGroup(@RequestBody SystemGroupEntity systemGroupEntity) throws Exception {
        Response response = new Response().success();
        PageResult<SystemGroupEntity> systemGroupEntityList = systemGroupService.selectListByPage(systemGroupEntity);
        response.setData(systemGroupEntityList);
        return response;
    }
}
