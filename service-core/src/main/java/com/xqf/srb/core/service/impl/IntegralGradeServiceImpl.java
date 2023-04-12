package com.xqf.srb.core.service.impl;

import com.xqf.srb.core.pojo.entity.IntegralGrade;
import com.xqf.srb.core.dao.IntegralGradeMapper;
import com.xqf.srb.core.service.IntegralGradeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 积分等级表 服务实现类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@Service
public class IntegralGradeServiceImpl extends ServiceImpl<IntegralGradeMapper, IntegralGrade> implements IntegralGradeService {

    @Override
    public List<IntegralGrade> listAll() {
        return list();

    }

    @Override
    public boolean remove(Long id) {
        boolean result = removeById(id);
        return result;
    }

    @Override
    public boolean add(IntegralGrade integralGrade) {
        boolean reslut = save(integralGrade);
        save(integralGrade);
        return reslut;
    }

    @Override
    public boolean update(IntegralGrade integralGrade) {
        boolean result = updateById(integralGrade);
        return result;

    }

    @Override
    public IntegralGrade get(Long id) {
        IntegralGrade result = getById(id);
        return result;
    }
}
