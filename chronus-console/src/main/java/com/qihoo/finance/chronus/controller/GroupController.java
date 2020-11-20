package com.qihoo.finance.chronus.controller;

import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.common.util.ControllerUtil;
import com.qihoo.finance.chronus.common.util.SecureUtils;
import com.qihoo.finance.chronus.core.group.GroupService;
import com.qihoo.finance.chronus.metadata.api.group.entity.GroupEntity;
import com.qihoo.finance.chronus.metadata.api.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping(value = "/api/group")
public class GroupController {

	@Resource
	private GroupService groupService;

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	public Response insert(@RequestBody @Valid GroupEntity groupEntity, BindingResult bindingResult) throws Exception {
		Response response = new Response().success();
		if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
			return response;
		}
		String userNo = (String) SecureUtils.getPrincipal();
		groupEntity.setCreatedBy(userNo);
		groupEntity.setUpdatedBy(userNo);
		groupService.insert(groupEntity);
		return response;
	}


	@RequestMapping(value = "/{groupName}", method = RequestMethod.DELETE)
	public Response delete(@PathVariable("groupName") String groupName) throws Exception {
		groupService.deleteByGroupName(groupName);
		return new Response().success();
	}

	@RequestMapping(value = "/", method = RequestMethod.PUT)
	public Response put(@RequestBody @Valid GroupEntity groupEntity, BindingResult bindingResult) throws Exception {
		Response response = new Response().success();
		try {
			if (ControllerUtil.checkResponse(response, bindingResult).failed()) {
				return response;
			}
			if (groupEntity == null || groupEntity.getId() == null) {
				return response.hinderFail("groupEntity.id为空,无法更新!");
			}
			String userNo = (String) SecureUtils.getPrincipal();
			groupEntity.setUpdatedBy(userNo);
			groupEntity.setDateUpdated(new Date());
			groupService.update(groupEntity);
		} catch (Exception e) {
			log.error("更新group配置异常! groupEntity:{}", groupEntity, e);
			response.hinderFail("更新group配置异常" + e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/getAll", method = RequestMethod.POST)
	@ResponseBody
	public Response getAllGroup() throws Exception {
		Response response = new Response().success();
		response.setData(groupService.loadAllGroup());
		return response;
	}
}
