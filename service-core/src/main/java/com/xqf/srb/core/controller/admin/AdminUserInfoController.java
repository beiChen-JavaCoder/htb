package com.xqf.srb.core.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xqf.common.result.ResultResponse;
import com.xqf.srb.core.pojo.entity.UserInfo;
import com.xqf.srb.core.query.UserInfoQuery;
import com.xqf.srb.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author xqf
 * @since 2023-01-10
 */
@Api(tags = "后台会员管理")
@RestController
@RequestMapping("/admin/core/userInfo")
//@CrossOrigin
public class AdminUserInfoController {

    String mobil_Prefix_Redis = "srb:sms:code:";

    @Resource
    UserInfoService userInfoService;

    @ApiOperation("会员分页列表")
    @GetMapping("/list/{page}/{limit}")
    public ResultResponse listPage(@ApiParam(value = "分页条件对象", required = false) UserInfoQuery userInfoQuery,
                                   @ApiParam(value = "当前页码", required = true) @PathVariable Long page,
                                   @ApiParam(value = "每页记录数", required = true) @PathVariable Long limit) {

        Page<UserInfo> pageParam = new Page<>(page, limit);
        IPage<UserInfo> pageModel = userInfoService.listPage(userInfoQuery, pageParam);

        return ResultResponse.ok().data("pageModel", pageModel);


    }

    @ApiOperation("锁定与解锁")
    @PutMapping("/lock/{id}/{status}")
    public ResultResponse lock(@ApiParam(value = "用户id", required = true)
                               @PathVariable Long id,
                               @ApiParam(value = "(0:锁定，1:正常)", required = true)
                               @PathVariable Integer status) {


        userInfoService.lock(id, status);
        return ResultResponse.ok().message(status == 1 ? "解锁成功" : "锁定成功");
    }
}
