package com.xqf.srb.core.service;

import com.xqf.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 */
public interface DictService extends IService<Dict> {

    /**
     * 上传excel文件
     * @param fileInputStream 文件输入流
     */
    void importData(InputStream fileInputStream);

    /**
     * 导出excel文件
     * @return
     */
    List listDictData();


    /**
     * 根据父节点id查询所有节点
     *
     * @return
     */
    List<Dict> listByParentId(Long parentId);

    /**
     * 根据数据字典编码获取下级节点
     * @param dictCode 编码
     * @return 返回下级节点
     */
    List<Dict> findByDictCode(String dictCode);

    /**
     * 根据行业代码和职业代码返回相应的职业名称
     * @param dictCode 行业代码
     * @param value 行业下的职业代码
     * @return 职业名称
     */
    String getNameByParentDictCodeAndValue(String dictCode,Integer value);
}
