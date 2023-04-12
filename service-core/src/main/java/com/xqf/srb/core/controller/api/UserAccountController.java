package com.xqf.srb.core.controller.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.api.R;
import com.xqf.common.result.ResultResponse;
import com.xqf.srb.core.hfb.RequestHelper;
import com.xqf.srb.core.pojo.entity.UserAccount;
import com.xqf.srb.core.service.UserAccountService;
import com.xqf.srb.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

import static com.xqf.srb.core.hfb.RequestHelper.isSignEquals;

@Api(tags = "会员账户")
@RestController
@RequestMapping("/api/core/userAccount")
@Slf4j
public class UserAccountController {

    @Resource
    private UserAccountService userAccountService;

    @ApiOperation("充值")
    @PostMapping("/auth/commitCharge/{chargeAmt}")
    public ResultResponse commitCharge(
            @ApiParam(value = "充值金额", required = true)
            @PathVariable BigDecimal chargeAmt, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        String formStr = userAccountService.commitCharge(chargeAmt, userId);

        return ResultResponse.ok().data("formStr", formStr);
    }

    @ApiOperation(value = "用户充值异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户充值异步回调：" + JSON.toJSONString(paramMap));
        //校验签名
        if (isSignEquals(paramMap)) {
            //校验充值是否成功
            if ("0001".equals(paramMap.get("resultCode"))) {
                return userAccountService.notify(paramMap);
            } else {
                //充值失败,停止重试
                log.info("用户充值异步回调充值失败：" + JSON.toJSONString(paramMap));
                return "success";
            }

        } else {
            //签名校验失败,重试
            return "fail";
        }
    }
    @ApiOperation("查询账户余额")
    @GetMapping("/auth/getAccount")
    public ResultResponse getAccount(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        BigDecimal account = userAccountService.getAccount(userId);
        return ResultResponse.ok().data("account", account);
    }
}