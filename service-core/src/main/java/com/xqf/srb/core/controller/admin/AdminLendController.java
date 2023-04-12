package com.xqf.srb.core.controller.admin;


import com.baomidou.mybatisplus.extension.api.R;
import com.xqf.common.result.ResultResponse;
import com.xqf.srb.core.pojo.entity.Lend;
import com.xqf.srb.core.service.LendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 前端控制器
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@RestController
@RequestMapping("/admin/core/lend")
@Slf4j
@Api(tags = "标的管理")
public class AdminLendController {

    @Resource
    private LendService lendService;


    @ApiOperation("标的列表")
    @GetMapping("/list")
    public ResultResponse list() {

        List<Lend> lendList = lendService.lendList();

        return ResultResponse.ok().data("list", lendList);

    }

    @ApiOperation("标的的详情信息")
    @GetMapping("/show/{id}")
    public ResultResponse show(@PathVariable Long id) {
        Map<String, Object> result = lendService.getLendDetail(id);
        return ResultResponse.ok().data("lendDetail", result);
    }

    @ApiOperation("放款")
    @GetMapping("/makeLoan/{id}")
    public ResultResponse makeLoan(@ApiParam(value = "标的id", required = true)
                                   @PathVariable("id") Long id) {
        lendService.makeLoan(id);
        return ResultResponse.ok().message("放款成功");

    }


}

