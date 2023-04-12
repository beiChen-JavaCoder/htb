package com.xqf.srb.core.controller.api;

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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Api(tags = "标的")
@RestController
@RequestMapping("/api/core/lend")
@Slf4j
public class LendController {

    @Resource
    LendService lendService;

    @ApiOperation("标的列表")
    @GetMapping("/list")
    public ResultResponse list() {

        List<Lend> lendList = lendService.lendList();

        return ResultResponse.ok().data("lendList", lendList);
    }
    @ApiOperation("获取标的信息")
    @GetMapping("/show/{id}")
    public ResultResponse show(
            @ApiParam(value = "标的id", required = true)
            @PathVariable Long id) {
        Map<String, Object> lendDetail = lendService.getLendDetail(id);
        return ResultResponse.ok().data("lendDetail", lendDetail);
    }
    @ApiOperation("计算投资收益")
    @GetMapping("/getInterestCount/{invest}/{yearRate}/{totalmonth}/{returnMethod}")
    public ResultResponse getInterestCount(
            @ApiParam(value = "投资金额", required = true)
            @PathVariable("invest") BigDecimal invest,

            @ApiParam(value = "年化收益", required = true)
            @PathVariable("yearRate")BigDecimal yearRate,

            @ApiParam(value = "期数", required = true)
            @PathVariable("totalmonth")Integer totalmonth,

            @ApiParam(value = "还款方式", required = true)
            @PathVariable("returnMethod")Integer returnMethod) {

        BigDecimal  interestCount = lendService.getInterestCount(invest, yearRate, totalmonth, returnMethod);
        return ResultResponse.ok().data("interestCount", interestCount);
    }

}