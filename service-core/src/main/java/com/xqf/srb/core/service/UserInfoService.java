package com.xqf.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xqf.srb.core.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xqf.srb.core.pojo.vo.LoginVO;
import com.xqf.srb.core.pojo.vo.RegisterVO;
import com.xqf.srb.core.pojo.vo.UserInfoVO;
import com.xqf.srb.core.query.UserInfoQuery;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 用户注册
     *
     * @param registerVO 注册用户vo
     */
    void register(RegisterVO registerVO);

    UserInfoVO login(LoginVO loginVO, String clientIP);

    /**
     * @param userInfoQuery 分页条件对象
     * @param pageParam     分页属性
     * @return 分页对象
     */
    IPage<UserInfo> listPage(UserInfoQuery userInfoQuery, Page<UserInfo> pageParam);

    /**
     * 修改用户状态
     *
     * @param id     用户id
     * @param status 目标状态
     */
    void lock(Long id, Integer status);

    /**
     * 由sms微服务调用校验手机号是否注册
     *
     * @param mobile
     * @return
     */
    boolean checkMobile(String mobile);

    /**
     * 根据用户id获取用户信息
     */
    UserInfo getUserInfoById(Long userId);

    /**
     * 根据bindCode获取手机号
     * @param bindCode
     * @return
     */
    String getMobileByBindCode(String bindCode);
}
