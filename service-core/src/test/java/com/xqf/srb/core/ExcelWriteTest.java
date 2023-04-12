package com.xqf.srb.core;

import com.alibaba.excel.EasyExcel;
import com.xqf.srb.core.pojo.dto.ExcelDictDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ExcelWriteTest {
    @Test
    public void simpleWriteXlsx() {
        String fileName = "F:\\ProgrammingTools\\JetBrains\\IDEA\\ProJect\\srb\\service-core\\simpleWrite.xlsx"; //需要提前新建目录
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName, ExcelDictDTO.class).sheet("模板").doWrite(data());
    }
    
    //辅助方法
    private List<ExcelDictDTO> data(){
        List<ExcelDictDTO> list = new ArrayList<>();

        //算上标题，做多可写65536行
        //超出：java.lang.IllegalArgumentException: Invalid row number (65536) outside allowable range (0..65535)
        for (int i = 0; i < 100; i++) {
            ExcelDictDTO data = new ExcelDictDTO();
            data.setName("Helen" + i);
            data.setDictCode("Helen" + i);
            data.setId(Long.getLong(i+""));
            data.setValue(i);
            list.add(data);
        }

        return list;
    }
}