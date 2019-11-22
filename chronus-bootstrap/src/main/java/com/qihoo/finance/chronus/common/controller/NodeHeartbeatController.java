package com.qihoo.finance.chronus.common.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xiongpu on 2019/7/31.
 */
@RestController
public class NodeHeartbeatController {
    @RequestMapping("/testing")
    public boolean testing() {
        return true;
    }
}
