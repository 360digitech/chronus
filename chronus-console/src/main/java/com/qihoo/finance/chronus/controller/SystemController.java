package com.qihoo.finance.chronus.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Ordering;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.common.spi.UserService;
import com.qihoo.finance.chronus.common.util.ControllerUtil;
import com.qihoo.finance.chronus.common.util.SecureUtils;
import com.qihoo.finance.chronus.core.system.SystemGroupService;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.system.entity.SystemGroupEntity;
import com.qihoo.finance.chronus.metadata.api.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by xiongpu on 2018/8/21.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/system")
public class SystemController {

    @Resource
    private SystemGroupService systemGroupService;

    @Resource
    private UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public Response insert(@RequestBody @Valid SystemGroupEntity systemGroupEntity, BindingResult bindingResult) throws Exception {
        Response response = new Response().success();
        if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
            return response;
        }
        String user = (String) SecureUtils.getPrincipal();
        systemGroupEntity.setCreatedBy(user);
        systemGroupEntity.setUpdatedBy(user);
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
            String user = (String) SecureUtils.getPrincipal();
            systemGroupEntity.setUpdatedBy(user);
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
        String userNo = (String) SecureUtils.getPrincipal();
        UserEntity user = userService.findByUserNo(userNo);
        Set<String> t;
        if (StringUtils.equals(ChronusConstants.ALL, user.getGroup())) {
            t = systemGroupService.loadAllSysCodes();
        } else {
            List<String> list = new ArrayList<>(Arrays.asList(user.getGroup().split(",")));
            list.add("PUBLIC");
            t = systemGroupService.loadSystemGroupByGroupName(list).stream().map(s -> s.getSysCode()).collect(Collectors.toSet());
        }
        Set<String> allSysCodes = new TreeSet<>(Comparator.naturalOrder());
        if (CollectionUtils.isNotEmpty(t)) {
            allSysCodes.addAll(t);
        }
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
