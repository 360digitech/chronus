package com.qihoo.finance.chronus.common.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
public class HeartbeatCheckController {
    public HeartbeatCheckController() {
    }

    @RequestMapping({"/status"})
    public void status(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (this.isPauseConsume()) {
            log.debug("优雅停机-拒绝服务");
            response.getWriter().write("Instance is offline!\n");
        } else {
            response.getWriter().write("ok\n");
        }
    }

    private boolean isPauseConsume() {
        //TODO
        return false;
    }
}
