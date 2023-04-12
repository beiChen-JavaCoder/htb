package com.xqf.srb.core.service;

import com.xqf.srb.core.pojo.entity.IntegralGrade;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 积分等级表 服务类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
public interface IntegralGradeService extends IService<IntegralGrade> {

    List<IntegralGrade> listAll();

    /**
     *
     * @param id
     * @return
     */
    boolean remove(Long id);


    boolean add(IntegralGrade integralGrade);

    boolean update(IntegralGrade integralGrade);

    IntegralGrade get(Long id);
}
