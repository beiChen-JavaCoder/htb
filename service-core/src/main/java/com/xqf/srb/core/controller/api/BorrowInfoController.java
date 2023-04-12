package com.xqf.srb.core.controller.api;


import com.baomidou.mybatisplus.extension.api.R;
import com.xqf.common.result.ResultResponse;
import com.xqf.srb.core.pojo.entity.BorrowInfo;
import com.xqf.srb.core.service.BorrowInfoService;
import com.xqf.srb.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@Api(tags = "借款信息")
@RestController
@RequestMapping("/api/core/borrowInfo")
public class BorrowInfoController {

    @Resource
    private BorrowInfoService borrowInfoService;

    @ApiOperation("获取借款额度")
    @GetMapping("/auth/getBorrowAmount")
    public ResultResponse getBorrowAmount(HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        BigDecimal borrowAmount = borrowInfoService.getBorrowAmount(userId);

        return ResultResponse.ok().data("borrowAmount", borrowAmount);


    }

    @ApiOperation("提交借款申请")
    @PostMapping("/auth/save")
    public ResultResponse save(@RequestBody BorrowInfo borrowInfo, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        borrowInfoService.saveBorrowInfo(borrowInfo, userId);

        return ResultResponse.ok().message("提交成功");


    }
    @ApiOperation("获取借款申请审批状态")
    @GetMapping("/auth/getBorrowInfoStatus")
    public ResultResponse getBorrowInfoStatus(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        Integer status = borrowInfoService.getBorrowInfoStatus(userId);

        return ResultResponse.ok().data("borrowInfoStatus",status);
    }


}
//    @ApiOperation("获取借款申请审批状态")
//    @GetMapping("/auth/getBorrowInfoStatus")
//    public ResultResponse getBorrowerStatus(HttpServletRequest request){
//        String token = request.getHeader("token");
//        Long userId = JwtUtils.getUserId(token);
//        Integer status = borrowInfoService.getStatusByUserId(userId);
//        return ResultResponse.ok().data("borrowInfoStatus", status);
//    }
//}

