package com.qihoo.finance.chronus.common.controller;

import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.common.util.SecureUtils;
import com.qihoo.finance.chronus.metadata.api.user.entity.UserEntity;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;


@Controller
public class LoginController {

    /**
     * 登录页面
     */
    @RequestMapping(value = "/login")
    public String login() {
        return "login.html";
    }

    @RequestMapping(value = "/index")
    public String index() {
        return "index.html";
    }


    /**
     * 登录
     */
    @RequestMapping(value = "/api/login", method = RequestMethod.POST)
    @ResponseBody
    public Response doLogin(@RequestBody UserEntity user) {
        Response response = new Response().success();
        //添加用户认证信息
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUserNo(), user.getPwd());
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            String userNo = (String) SecureUtils.getPrincipal();
            response.setData(userNo);
        } catch (UnknownAccountException e) {
            response.hinderFail(e.getMessage());
        } catch (Exception e) {
            response.hinderFail("账号或密码错误!");
        }
        return response;
    }

    /**
     * 登出
     */
    @RequestMapping(value = "/api/logout", method = RequestMethod.POST)
    @ResponseBody
    public Response doLogout(HttpServletRequest request) {
        Response response = new Response().success();
        //添加用户认证信息
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return response;
    }

    /**
     * 错误页面展示
     */
    @RequestMapping(value = "/api/error", method = RequestMethod.GET)
    public String error() {
        return "error";
    }

    /**
     * 错误页面展示
     */
    @RequestMapping(value = "/unauthorizedurl", method = RequestMethod.GET)
    @ResponseBody
    public String unauthorizedurl() {
        return "error";
    }
}