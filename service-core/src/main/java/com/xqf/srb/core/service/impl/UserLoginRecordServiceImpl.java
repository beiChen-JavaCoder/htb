package com.xqf.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xqf.srb.core.pojo.entity.UserLoginRecord;
import com.xqf.srb.core.dao.UserLoginRecordMapper;
import com.xqf.srb.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {


    @Override
    public List<UserLoginRecord> listTop50(Long userId) {
        LambdaQueryWrapper<UserLoginRecord> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(UserLoginRecord::getUserId,userId)
                .orderByDesc(UserLoginRecord::getId)
                .last("limit 50");
        List<UserLoginRecord> userLoginRecordList = baseMapper.selectList(lambdaQueryWrapper);
        return userLoginRecordList;
    }
}
