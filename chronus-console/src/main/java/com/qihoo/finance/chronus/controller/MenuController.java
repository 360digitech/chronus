package com.qihoo.finance.chronus.controller;

import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.common.util.SecureUtils;
import com.qihoo.finance.chronus.metadata.api.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
public class MenuController {

    private static final Map<String, List<String>> roleMenu = new HashMap<>();

    static {
        List<String> admin = Arrays.asList("task", "group", "system", "tag", "node", "cluster", "log","authorization");
        List<String> guest = Arrays.asList("task", "log");

        roleMenu.put("admin", admin);
        roleMenu.put("guest", guest);
    }

    @RequestMapping(value = "/api/menu", method = RequestMethod.GET)
    public Response getMenu() throws Exception {
        Response response = new Response();
        String user = (String)SecureUtils.getPrincipal();
        return response.success(roleMenu.get(user));
    }

}