package com.xqf.srb.core.controller.api;


import cn.hutool.jwt.JWT;
import com.xqf.common.exception.Assert;
import com.xqf.common.result.ResponseEnum;
import com.xqf.common.result.ResultResponse;
import com.xqf.srb.core.pojo.entity.UserInfo;
import com.xqf.srb.core.pojo.vo.LoginVO;
import com.xqf.srb.core.pojo.vo.RegisterVO;
import com.xqf.srb.core.pojo.vo.UserInfoVO;
import com.xqf.srb.core.service.UserInfoService;
import com.xqf.srb.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author xqf
 * @since 2023-01-10
 */
@Api(tags = "前台会员接口")
@RestController
@RequestMapping("/api/core/userInfo")
//@CrossOrigin
public class UserInfoController {

    String mobil_Prefix_Redis = "srb:sms:code:";

    @Resource
    UserInfoService userInfoService;
    @Resource
    RedisTemplate redisTemplate;


    @ApiOperation("会员注册")
    @PostMapping("/register")
    public ResultResponse register(@RequestBody @ApiParam("注册用户信息") RegisterVO registerVO) {

        //断言验证码非空
        Assert.notNull(registerVO.getCode(), ResponseEnum.CODE_NULL_ERROR);
        //断言验证码错误
        String code = (String) redisTemplate.opsForValue().get(mobil_Prefix_Redis + registerVO.getMobile());
        Assert.equals(registerVO.getCode(), code, ResponseEnum.CODE_ERROR);

        //用户注册
        userInfoService.register(registerVO);

        return ResultResponse.ok().message("注册成功");
    }

    @ApiOperation("会员登陆")
    @PostMapping("/login")
    public ResultResponse login(@RequestBody @ApiParam("登录信息") LoginVO loginVO, HttpServletRequest request) {
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();

        //手机号不能为空
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);

        //密码不能为空
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);
        String clientIP = request.getRemoteAddr();
        UserInfoVO userInfoVO = userInfoService.login(loginVO, clientIP);

        return ResultResponse.ok().data("userInfo", userInfoVO);
    }

    @ApiOperation("校验token")
    @GetMapping("/checkToken")
    public ResultResponse checkToken(HttpServletRequest request) {

        String token = request.getHeader("token");
        boolean checkToken = JwtUtils.checkToken(token);
        if (checkToken) {
            return ResultResponse.ok().message("已登陆");
        } else {
            //LOGIN_AUTH_ERROR(-211, "未登录"),
            return ResultResponse.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
        }
    }

    @ApiOperation("校验手机号是否注册")
    @GetMapping("/checkMobile/{mobile}")
    public boolean checkMobile(@ApiParam("手机号")
                                      @PathVariable String mobile,String type) {
        boolean checkMobile = userInfoService.checkMobile(mobile);
        return checkMobile;
    }
//    @ApiOperation("获取会员id")
//    @GetMapping("/auth/getIndexUserInfo")
//    public ResultResponse getIndexUserInfo(HttpServletRequest request) {
//        String token = request.getHeader("token");
//        Long userId = JwtUtils.getUserId(token);
//        UserInfo userInfo = userInfoService.getUserInfoById(userId);
//        return ResultResponse.ok().data("userInfo", userInfo);
//    }



}
