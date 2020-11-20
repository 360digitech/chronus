package com.qihoo.finance.chronus.controller;

import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.common.spi.UserService;
import com.qihoo.finance.chronus.common.util.ControllerUtil;
import com.qihoo.finance.chronus.common.util.Md5Utils;
import com.qihoo.finance.chronus.common.util.SecureUtils;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenxiyu
 * @date 2019/12/23
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 新增用户
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Response insert(@RequestBody @Valid UserEntity userEntity, BindingResult bindingResult) throws Exception {
        Response response = new Response().success();
        try {
            if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
                return response;
            }
            userEntity.setGroup(StringUtils.join(userEntity.getGroups()));
            String user = (String)SecureUtils.getPrincipal();
            Date now = new Date();
            userEntity.setDateCreated(now);
            userEntity.setDateUpdated(now);
            userEntity.setCreatedBy(user);
            userEntity.setUpdatedBy(user);
            userEntity.setState(ChronusConstants.Y);
            userEntity.setPwd(Md5Utils.getMD5("88888888".getBytes()));
            userService.insert(userEntity);
        } catch (Exception e) {
            log.error("新增用户异常! userEntity:{}", userEntity, e);
            response.hinderFail("新增用户异常" + e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/{userNo}", method = RequestMethod.DELETE)
    public Response delete(@PathVariable("userNo") String userNo) throws Exception {
        Response response = new Response().success();
        try {
            userService.delete(userNo);
        } catch (Exception e) {
            log.error("删除用户异常! userNo:{}", userNo, e);
            response.hinderFail("删除用户异常" + e.getMessage());
        }
        return response;
    }

    /**
     * 修改用户权限
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public Response update(@RequestBody @Valid UserEntity userEntity, BindingResult bindingResult) throws Exception {
        Response response = new Response().success();
        userEntity.setGroup(StringUtils.join(userEntity.getGroups()));
        try {
            if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
                return response;
            }
            String user = (String)SecureUtils.getPrincipal();
            userEntity.setUpdatedBy(user);
            userEntity.setDateUpdated(new Date());
            userService.update(userEntity);
        } catch (Exception e) {
            log.error("更新用户权限异常! userEntity:{}", userEntity, e);
            response.hinderFail("更新用户权限异常" + e.getMessage());
        }
        return response;
    }

    /**
     * 用户权限列表
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getAllUser", method = RequestMethod.POST)
    public Response getAllUser(@RequestBody UserEntity userEntity) throws Exception {
        Response response = new Response().success();
        PageResult<UserEntity> userEntityList = userService.selectListByPage(userEntity);
        List<UserEntity> newList= userEntityList.getList().stream().map(s->{s.setGroups(s.getGroup().split(",")); return s;}).collect(Collectors.toList());
        userEntityList.setList(newList);
        response.setData(userEntityList);
        return response;
    }



}
