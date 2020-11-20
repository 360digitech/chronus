package com.qihoo.finance.chronus.controller;

import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.common.util.ControllerUtil;
import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.common.util.SecureUtils;
import com.qihoo.finance.chronus.core.tag.TagService;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.tag.entity.TagEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Created by xiongpu on 2018/8/21.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/tag")
public class TagController {

    @Resource
    private TagService tagService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Response insert(@RequestBody @Valid TagEntity tagEntity, BindingResult bindingResult) throws Exception {
        Response response = new Response().success();
        try {
            if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
                return response;
            }
            String user = (String)SecureUtils.getPrincipal();
            tagEntity.setCreatedBy(user);
            tagEntity.setUpdatedBy(user);
            tagService.insert(tagEntity);
        } catch (Exception e) {
            log.error("新增Tag配置异常! tagEntity:{}", tagEntity, e);
            response.hinderFail("新增Tag配置异常" + e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/{tag}", method = RequestMethod.DELETE)
    public Response delete(@PathVariable("tag") String tag) throws Exception {
        Response response = new Response().success();
        try {
            tagService.delete(tag);
        } catch (Exception e) {
            log.error("删除Tag配置异常! tag:{}", tag, e);
            response.hinderFail("删除Tag配置异常" + e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public Response put(@RequestBody @Valid TagEntity tagEntity, BindingResult bindingResult) throws Exception {
        Response response = new Response().success();
        try {
            if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
                return response;
            }
            if (tagEntity == null || tagEntity.getId() == null) {
                return response.hinderFail("Tag.id为空,无法更新!");
            }
            String user = (String)SecureUtils.getPrincipal();
            tagEntity.setUpdatedBy(user);
            tagEntity.setDateUpdated(DateUtils.now());
            tagService.update(tagEntity);
        } catch (Exception e) {
            log.error("更新Tag配置异常! tagEntity:{}", tagEntity, e);
            response.hinderFail("更新Tag配置异常" + e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/loadAllTagCodes", method = RequestMethod.GET)
    public Response loadAllTagCodes() throws Exception {
        Response response = new Response().success();
        List<TagEntity> tagEntityList = tagService.selectListAll();
        Set<String> tagCodes = tagEntityList.stream().map(TagEntity::getTag).collect(Collectors.toSet());
        response.setData(tagCodes);
        return response;
    }

    @RequestMapping(value = "/getAllTag", method = RequestMethod.POST)
    public Response getAllTag(@RequestBody TagEntity tagEntity) throws Exception {
        Response response = new Response().success();
        PageResult<TagEntity> tagEntityList = tagService.selectListByPage(tagEntity);
        response.setData(tagEntityList);
        return response;
    }
}
