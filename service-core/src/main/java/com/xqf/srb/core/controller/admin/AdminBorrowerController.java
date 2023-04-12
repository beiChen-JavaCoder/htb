package com.xqf.srb.core.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xqf.common.result.ResultResponse;
import com.xqf.srb.core.enums.BorrowInfoStatusEnum;
import com.xqf.srb.core.enums.BorrowerStatusEnum;
import com.xqf.srb.core.pojo.entity.Borrower;
import com.xqf.srb.core.pojo.vo.BorrowerApprovalVO;
import com.xqf.srb.core.pojo.vo.BorrowerDetailVO;
import com.xqf.srb.core.pojo.vo.BorrowerVO;
import com.xqf.srb.core.service.BorrowerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Xqf
 * @version 1.0
 */
@Api(tags = "借款人管理")
@RestController
@RequestMapping("/admin/core/borrower")
public class AdminBorrowerController {

    @Resource
    BorrowerService borrowerService;

    @ApiOperation("获取借款人分页列表")
    @GetMapping("/list/{page}/{limit}")
    public ResultResponse listPage(
            @ApiParam(value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(value = "每页记录数", required = true)
            @PathVariable Long limit,

            @ApiParam(value = "查询关键字", required = false)
            @RequestParam String keyword) {
        //这里的@RequestParam其实是可以省略的，但是在目前的swagger版本中（2.9.2）不能省略，
        //否则默认将没有注解的参数解析为body中的传递的数据
        Page<Borrower> pageParam = new Page<>(page, limit);
        IPage<Borrower> pageModel = borrowerService.listPage(pageParam, keyword);
        return ResultResponse.ok().data("pageModel", pageModel);

    }
    @ApiOperation("获取借款人信息")
    @GetMapping("/show/{id}")
    public ResultResponse show(@PathVariable @ApiParam("借款人id") Long id){

        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(id);

        return ResultResponse.ok().data("borrowerDetailVO",borrowerDetailVO);


    }
    @ApiOperation("借款额度审批")
    @PostMapping("/approval")
    public ResultResponse approval(@RequestBody BorrowerApprovalVO borrowerApprovalVO){

        borrowerService.approval(borrowerApprovalVO);

        return ResultResponse.ok().message("借款额度审批完成");
    }
}
