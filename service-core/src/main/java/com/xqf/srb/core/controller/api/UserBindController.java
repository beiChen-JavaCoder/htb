package com.xqf.srb.core.controller.api;

import com.alibaba.fastjson.JSON;
import com.xqf.common.result.ResultResponse;
import com.xqf.srb.core.hfb.RequestHelper;
import com.xqf.srb.core.pojo.vo.UserBindVO;
import com.xqf.srb.core.service.UserBindService;
import com.xqf.srb.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Xqf
 * @version 1.0
 */
@RestController
@Api(tags = "会员账号绑定")
@RequestMapping("/api/core/userBind")
@Slf4j
public class UserBindController {

    @Resource
    private UserBindService userBindService;

    @ApiOperation("账户绑定提交数据")
    @PostMapping("/auth/bind")
    public ResultResponse bind(@ApiParam("绑定信息") @RequestBody UserBindVO userBindVO, HttpServletRequest request) {
        String token = request.getHeader("token");
        //获取用户id
        Long userId = JwtUtils.getUserId(token);
        //返回动态表单信息
        String formStr = userBindService.commitBindUser(userBindVO, userId);
        //反动态表单前端解析
        return ResultResponse.ok().data("formStr", formStr);

    }

    @ApiOperation("账户绑定异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {

        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户账号绑定异步回调：" + JSON.toJSONString(paramMap));

        if (!RequestHelper.isSignEquals(paramMap)) {
            log.error("异步回调校验签名失败");
            return "fail";
        }
        //修改绑定状态
        userBindService.notify(paramMap);
        return "success";


    }
}