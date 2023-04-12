package com.xqf.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.injector.methods.SelectList;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.xqf.srb.core.listener.ExcelDictDTOListener;
import com.xqf.srb.core.pojo.dto.ExcelDictDTO;
import com.xqf.srb.core.pojo.entity.Dict;
import com.xqf.srb.core.dao.DictMapper;
import com.xqf.srb.core.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 **/
@Service
@Slf4j
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Resource
    RedisTemplate redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importData(InputStream fileInputStream) {
        EasyExcel.read(fileInputStream, ExcelDictDTO.class, new ExcelDictDTOListener(baseMapper)).sheet().doRead();
        log.info("导入excel文件成功");


    }

    @Override
    public List listDictData() {

        List<Dict> dicts = baseMapper.selectList(null);
        ArrayList<ExcelDictDTO> excelDictDTOS = new ArrayList<>(dicts.size());
        dicts.forEach(dict -> {
            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(dict, excelDictDTO);
            excelDictDTOS.add(excelDictDTO);
        });
        return excelDictDTOS;
    }

    @Override
    public List<Dict> listByParentId(Long parentId) {
        //从redis中获取数据

        try {
            List<Dict> dictList = (List<Dict>) redisTemplate.opsForValue().get("srb:core:dictList:" + parentId);
            if (dictList != null) {
                log.info("从redis中取值");
                return dictList;
            }
        } catch (Exception e) {
            log.error("redis服务器异常：" + ExceptionUtils.getStackTrace(e));//此处不抛出异常，继续执行后面的代码
        }

        //从数据库中获取数据
        List<Dict> dictList = baseMapper.selectList(new QueryWrapper<Dict>().eq("parent_id", parentId));
        dictList.stream().forEach(dict -> {
            boolean b = this.hasChildren(dict.getId());
            dict.setHasChildren(b);
        });
        //从数据库中获取数据后存入redis
        redisTemplate.opsForValue().set("srb:core:dictList:" + parentId, dictList, 5, TimeUnit.MINUTES);
        log.info("从数据库取出并且存入redis");
        return dictList;
    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {

        LambdaQueryWrapper<Dict> dictLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dictLambdaQueryWrapper.eq(Dict::getDictCode, dictCode);

        Dict dict = baseMapper.selectOne(dictLambdaQueryWrapper);

        List<Dict> dictList = listByParentId(dict.getId());
        log.info(dictList.toString());
        return dictList;

    }

    @Override
    public String getNameByParentDictCodeAndValue(String dictCode, Integer value) {

        LambdaQueryWrapper<Dict> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dict::getDictCode, dictCode);
        Dict parentDict = baseMapper.selectOne(lambdaQueryWrapper);

        lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dict::getParentId, parentDict.getId())
                .eq(Dict::getValue, value);

        Dict dict = baseMapper.selectOne(lambdaQueryWrapper);

        if (dict == null) {
            return "";
        }

        return dict.getName();

    }

    private boolean hasChildren(Long id) {
        Integer count = baseMapper.selectCount(new QueryWrapper<Dict>().eq("parent_id", id));
        if (count > 0) {
            //存在子节点
            return true;
        }   //不存在子节点
        return false;
    }
}
