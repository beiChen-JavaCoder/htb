package com.xqf.srb.core.service;

import com.xqf.srb.core.pojo.entity.UserLoginRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
public interface UserLoginRecordService extends IService<UserLoginRecord> {

    /**
     * 返回以id降序排列的前五十条登录日志
     * @param userId 用户id
     * @return 登录日志集合
     */
    List<UserLoginRecord> listTop50(Long userId);
}
