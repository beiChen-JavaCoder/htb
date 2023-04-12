package com.xqf.srb.core.controller.admin;

import com.xqf.common.result.ResultResponse;
import com.xqf.srb.core.pojo.entity.IntegralGrade;
import com.xqf.srb.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "积分等级管理")
@RestController
//@CrossOrigin
@RequestMapping("/admin/core/integralGrade")
@Slf4j
public class AdminIntegralGradeController {

    @Resource
    private IntegralGradeService integralGradeService;

    @ApiOperation("积分等级列表")
    @GetMapping("/admin/list")
    public ResultResponse listAll() {
        log.info("info...............");
        log.error("error..........");
        log.warn("warn............");
        List<IntegralGrade> list = integralGradeService.listAll();
        return ResultResponse.ok().data("list", list);
    }

    @ApiOperation(value = "根据id删除积分等级", notes = "逻辑删除")
    @DeleteMapping("/admin/remove/{id}")
    public ResultResponse removeById(@PathVariable("id") @ApiParam(value = "积分等级id", required = true, example = "100") Long id) {
        boolean result = integralGradeService.remove(id);

        if (result) {
            return ResultResponse.ok().message("根据id删除积分等级成功");
        } else {
            return ResultResponse.error().message("根据id删除积分等级失败");
        }

    }

    @ApiOperation("添加积分等级")
    @PostMapping("/admin/save")
    public ResultResponse save(@RequestBody IntegralGrade integralGrade) {
        boolean result = integralGradeService.add(integralGrade);
        if (result) {
            return ResultResponse.ok().message("查询积分等级列表成功");
        } else {
            return ResultResponse.error().message("查询积分等级列表失败");
        }
    }
    @ApiOperation("修改积分等级")
    @PutMapping("/admin/update")
    public ResultResponse update(@RequestBody IntegralGrade integralGrade) {
        boolean result = integralGradeService.update(integralGrade);
        if (result) {
            return ResultResponse.ok().message("根据id修改积分等级成功");
        } else {
            return ResultResponse.error().message("根据id修改积分等级失败");
        }
    }
    @ApiOperation("根据id获取积分等级")
    @GetMapping("/admin/get/{id}")
    public ResultResponse getById(@PathVariable("id") Long id) {
        IntegralGrade result = integralGradeService.get(id);
        if (result!=null) {
            return ResultResponse.ok().message("查询积分等级成功").data("list",result);
        } else {
            return ResultResponse.error().message("数据不存在");
        }
    }
}