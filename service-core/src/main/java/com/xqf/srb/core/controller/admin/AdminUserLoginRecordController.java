package com.xqf.srb.core.controller.admin;

import com.xqf.common.result.ResultResponse;
import com.xqf.srb.core.pojo.entity.UserLoginRecord;
import com.xqf.srb.core.service.UserLoginRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Xqf
 * @version 1.0
 */
@Api(tags = "会员登录日志接口")
@RestController
@RequestMapping("/admin/core/userLoginRecord")
@Slf4j
//@CrossOrigin
public class AdminUserLoginRecordController {

    @Resource
    UserLoginRecordService userLoginRecordService;

    @ApiOperation("获取登录日志列表")
    @GetMapping("/listTop50/{userId}")
    public ResultResponse listTop50(@ApiParam(value = "用户id",required = true)
                                        @PathVariable Long userId){

        List<UserLoginRecord> listTop50 = userLoginRecordService.listTop50(userId);

        return ResultResponse.ok().data("list",listTop50);


    }

}
