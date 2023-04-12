package com.xqf.srb.core.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.xqf.common.exception.BusinessException;
import com.xqf.common.result.ResponseEnum;
import com.xqf.common.result.ResultResponse;
import com.xqf.srb.core.pojo.dto.ExcelDictDTO;
import com.xqf.srb.core.pojo.entity.Dict;
import com.xqf.srb.core.service.DictService;
import com.xqf.srb.core.service.impl.DictServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author Xqf
 * @version 1.0
 */
@RestController
@Slf4j
//@CrossOrigin
@Api(tags = "数据字典管理")
@RequestMapping("/admin/core/dict")
public class AdminDictController {

    @Resource
    DictService dictService;

    @ApiOperation("Excel批量导入数据字典")
    @PostMapping("/import")
    public ResultResponse batchImport(@ApiParam("导入的Excel文件") @RequestParam("file") MultipartFile file){

        try {
            InputStream fileInputStream = file.getInputStream();
            dictService.importData(fileInputStream);
            return ResultResponse.ok().message("导入excel成功");
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }

    }
    @ApiOperation("Excel批量导出数据字典")
    @GetMapping("/export")
    public void export(HttpServletResponse response ){
        try {
            //这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("mydict", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("数据字典").doWrite(dictService.listDictData());

        } catch (IOException e) {
            //EXPORT_DATA_ERROR(104, "数据导出失败"),
            throw  new BusinessException(ResponseEnum.EXPORT_DATA_ERROR, e);
        }
    }
    @ApiOperation("根据上级id获取子节点数据列表")
    @GetMapping("/listByParentId/{parentId}")
    public ResultResponse listByParentId(@ApiParam("父节点id") @PathVariable Long parentId){

        List<Dict> dicts= dictService.listByParentId(parentId);
        return ResultResponse.ok().data("list",dicts);

    }
}
