package com.qihoo.finance.chronus.modules.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qihoo.finance.chronus.sdk.AbstractSdkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by xiongpu on 2018/8/21.
 */
@Slf4j
@RestController
@RequestMapping(value = "/job")
public class JobController extends AbstractSdkService {

    @RequestMapping(value = "/selectTasks/", method = RequestMethod.POST)
    public List<Integer> selectTasks(HttpServletRequest request, @RequestBody @Valid JSONObject jsonObject) throws Exception {
        System.out.println(JSON.toJSONString(request.getHeaderNames()));
        System.out.println(jsonObject);
        // super.selectTasks()
        List<Integer> a = new ArrayList<>();
        a.add(1);
        return a;
    }

    @RequestMapping(value = "/execute/", method = RequestMethod.POST)
    public boolean execute(@RequestBody @Valid JSONObject jsonObject) throws Exception {
        System.out.println(jsonObject);
        // super.selectTasks()
        return true;
    }

    @RequestMapping(value = "/executeSimple/", method = RequestMethod.POST)
    public boolean executeSimple(@RequestBody @Valid JSONObject jsonObject) throws Exception {
        System.out.println(jsonObject);
        // super.selectTasks()
        return true;
    }
}
