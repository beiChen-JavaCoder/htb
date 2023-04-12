package com.xqf.srb.core.controller.admin;


import com.baomidou.mybatisplus.extension.api.R;
import com.xqf.common.result.ResultResponse;
import com.xqf.srb.core.pojo.entity.BorrowInfo;
import com.xqf.srb.core.pojo.entity.Borrower;
import com.xqf.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.xqf.srb.core.service.BorrowInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@Api(tags = "借款信息管理")
@RestController
@RequestMapping("/admin/core/borrowInfo")
public class AdminBorrowInfoController {

    @Resource
    private BorrowInfoService borrowInfoService;

    @ApiOperation("借款信息列表")
    @GetMapping("/list")
    public ResultResponse list() {

        List<BorrowInfo> borrowInfos = borrowInfoService.selectList();

        return ResultResponse.ok().data("list",borrowInfos);


    }
    @ApiOperation("获取借款信息")
    @GetMapping("/show/{id}")
    public ResultResponse show(
            @ApiParam(value = "借款id", required = true)
            @PathVariable Long id) {
        Map<String, Object> borrowInfoDetail = borrowInfoService.getBorrowInfoDetail(id);
        return ResultResponse.ok().data("borrowInfoDetail", borrowInfoDetail);
    }
    @ApiOperation("审批借款信息")
    @PostMapping("/approval")
    public ResultResponse approval(@RequestBody BorrowInfoApprovalVO borrowInfoApprovalVO){

        borrowInfoService.approval(borrowInfoApprovalVO);

        return ResultResponse.ok().message("审批完成");
    }
}