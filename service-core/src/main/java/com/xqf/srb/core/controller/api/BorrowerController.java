package com.xqf.srb.core.controller.api;


import com.xqf.common.result.ResultResponse;
import com.xqf.srb.core.pojo.vo.BorrowerVO;
import com.xqf.srb.core.service.BorrowerService;
import com.xqf.srb.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.invoke.VolatileCallSite;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@Api(tags = "借款人信息")
@RestController
@RequestMapping("/api/core/borrower")
public class BorrowerController {

    @Resource
    private BorrowerService borrowerService;

    @ApiOperation("提交借款人信息")
    @PostMapping("/auth/save")
    public ResultResponse save(@RequestBody BorrowerVO borrowerVO, HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        borrowerService.saveBorrowerVoByUserId(borrowerVO, userId);

        return ResultResponse.ok().message("借款信息提交成功");


    }

    @ApiOperation("获取借款人状态{根据状态判断是否显示申请借款表单}")
    @GetMapping("/auth/getBorrowerStatus")
    public ResultResponse getBorrowerStatus(HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        Integer status = borrowerService.getBorrowerStatus(userId);
        return ResultResponse.ok().data("borrowerStatus", status);
    }

}

