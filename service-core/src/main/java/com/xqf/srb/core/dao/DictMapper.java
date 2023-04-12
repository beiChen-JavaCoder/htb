package com.xqf.srb.core.dao;

import com.xqf.srb.core.pojo.dto.ExcelDictDTO;
import com.xqf.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 */
@Mapper
public interface DictMapper extends BaseMapper<Dict> {

    /**
     * 批量导入数据库excel文件
     * @param list
     */
    void insertBatch(@Param("list") List<ExcelDictDTO> list);
}
