package com.xqf.srb.sms.controller;

import cn.hutool.core.util.RandomUtil;
import com.xqf.common.exception.Assert;
import com.xqf.common.exception.BusinessException;
import com.xqf.common.result.ResponseEnum;
import com.xqf.common.result.ResultResponse;
import com.xqf.common.util.RegexValidateUtils;
import com.xqf.srb.sms.client.CoreUserInfoClient;
import com.xqf.srb.sms.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Xqf
 * @version 1.0
 */
@RestController
@RequestMapping("/api/sms")
@Api(tags = "短信管理")
//@CrossOrigin
@Slf4j
public class WebSmsController {

    @Resource
    SmsService smsService;
    @Resource
    RedisTemplate redisTemplate;

    @Resource
    CoreUserInfoClient coreUserInfoClient;

    @GetMapping("/send/{mobile}")
    @ApiOperation("发送验证码")
    public ResultResponse send(
            @ApiParam(value = "手机号", required = true)
            @PathVariable String mobile) {

        //校验手机号是否已注册
        boolean checkMobile = coreUserInfoClient.checkMobile(mobile);
        Assert.isTrue(checkMobile==false, ResponseEnum.MOBILE_EXIST_ERROR);
        //重复发送验证码
        Set keys = redisTemplate.keys("srb:sms:code:" + mobile);
        Assert.equals(keys.size(), 0, ResponseEnum.REPEAT_CAPTCHA);
        //手机号非空
        Assert.notNull(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        //校验手机号是否合法
        if (RegexValidateUtils.checkCellphone(mobile)) {
            throw new BusinessException(ResponseEnum.MOBILE_ERROR);
        }
        //生成验证码
        String verificationCode = RandomUtil.randomNumbers(6);

        //发送短信
        // smsService.send(mobile, SmsProperties.TEMPLATE_CODE, param);
        //将验证码存入redis
        redisTemplate.opsForValue().set("srb:sms:code:" + mobile, verificationCode, 5, TimeUnit.MINUTES);
        return ResultResponse.ok().message("验证码发送成功");


    }
}
