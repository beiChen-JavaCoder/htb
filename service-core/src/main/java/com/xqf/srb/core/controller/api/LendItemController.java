package com.xqf.srb.core.controller.api;


import com.xqf.common.result.ResultResponse;
import com.xqf.srb.core.pojo.entity.Lend;
import com.xqf.srb.core.pojo.entity.LendItem;
import com.xqf.srb.core.pojo.entity.UserInfo;
import com.xqf.srb.core.pojo.vo.InvestVO;
import com.xqf.srb.core.service.LendItemService;
import com.xqf.srb.core.service.LendService;
import com.xqf.srb.core.service.UserInfoService;
import com.xqf.srb.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 标的出借记录表 前端控制器
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */

@Api(tags = "标的的投资")
@RestController
@RequestMapping("/api/core/lendItem")
@Slf4j
public class LendItemController {
    @Resource
    private LendItemService lendItemService;
    @ApiOperation("会员投资提交数据")
    @PostMapping("/auth/commitInvest")
    public ResultResponse commitInvest(@RequestBody InvestVO investVO, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        String userName = JwtUtils.getUserName(token);

        investVO.setInvestUserId(userId);
        investVO.setInvestName(userName);

        String formStr = lendItemService.commitInvest(investVO);

        return ResultResponse.ok().data("formStr",formStr);

    }
}

