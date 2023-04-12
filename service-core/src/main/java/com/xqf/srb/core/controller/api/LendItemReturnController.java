package com.xqf.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.xqf.srb.core.hfb.RequestHelper;
import com.xqf.srb.core.service.LendItemService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 前端控制器
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@RestController
@RequestMapping("/lendItemReturn")
@Slf4j
public class LendItemReturnController {

    @Resource
    private LendItemService lendItemService;

    @ApiOperation("会员投资异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {

        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户投资异步回调：" + JSON.toJSONString(paramMap));

        //校验签名 P2pInvestNotifyVo
        if(RequestHelper.isSignEquals(paramMap)) {
            if("0001".equals(paramMap.get("resultCode"))) {
                lendItemService.notify(paramMap);
            } else {
                log.info("用户投资异步回调失败：" + JSON.toJSONString(paramMap));
                return "fail";
            }
        } else {
            log.info("用户投资异步回调签名错误：" + JSON.toJSONString(paramMap));
            return "fail";
        }
        return "success";
    }

}

