package com.xqf.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xqf.common.exception.Assert;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqf.common.result.ResponseEnum;
import com.xqf.srb.core.dao.UserAccountMapper;
import com.xqf.srb.core.dao.UserInfoMapper;
import com.xqf.srb.core.dao.UserLoginRecordMapper;
import com.xqf.srb.core.pojo.entity.UserAccount;
import com.xqf.srb.core.pojo.entity.UserInfo;
import com.xqf.srb.core.pojo.entity.UserLoginRecord;
import com.xqf.srb.core.pojo.vo.LoginVO;
import com.xqf.srb.core.pojo.vo.RegisterVO;
import com.xqf.srb.core.pojo.vo.UserInfoVO;
import com.xqf.srb.core.query.UserInfoQuery;
import com.xqf.srb.core.service.UserInfoService;
import com.xqf.srb.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    UserAccountMapper userAccountMapper;
    @Resource
    UserLoginRecordMapper userLoginRecordMapper;


    @Override
    public void register(RegisterVO registerVO) {

        //验证手机号是否存在
        LambdaQueryWrapper<UserInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserInfo::getMobile, registerVO.getMobile());
        Assert.notNull(baseMapper.selectList(lambdaQueryWrapper), ResponseEnum.MOBILE_EXIST_ERROR);


        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(registerVO, userInfo);
        //插入用户基本信息
        userInfo.setUserType(registerVO.getUserType());
        userInfo.setNickName(registerVO.getMobile());
        userInfo.setName(registerVO.getMobile());
        userInfo.setMobile(registerVO.getMobile());
        userInfo.setPassword(SecureUtil.md5(registerVO.getPassword()));
        //正常为1锁定为0
        userInfo.setStatus(UserInfo.STATUS_NORMAL);

        //设置一张静态资源服务器上的头像图片
        userInfo.setHeadImg("https://srb-file.oss-cn-beijing.aliyuncs.com/avatar/07.jpg");
        baseMapper.insert(userInfo);
        //创建会员账户
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);

    }

    @Override
    public UserInfoVO login(LoginVO loginVO, String clientIP) {
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        Integer userType = loginVO.getUserType();

        LambdaQueryWrapper<UserInfo> loginVOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //判断用户是否存在
        loginVOLambdaQueryWrapper.eq(UserInfo::getMobile, mobile)
                .eq(UserInfo::getUserType, userType);
        UserInfo userInfo = baseMapper.selectOne(loginVOLambdaQueryWrapper);
        //断言登录用户存在抛出异常
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);


        Assert.equals(SecureUtil.md5(password), userInfo.getPassword(), ResponseEnum.LOGIN_PASSWORD_ERROR);

        //用户是否被禁用
        Assert.equals(userInfo.getStatus(), UserInfo.STATUS_NORMAL, ResponseEnum.LOGIN_LOKED_ERROR);
        //记录登录日志
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(clientIP);
        userLoginRecordMapper.insert(userLoginRecord);

        //生成token组装userInfo对象
        String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());
        UserInfoVO userInfoVO = new UserInfoVO();

        userInfoVO.setUserType(userType);
        userInfoVO.setName(userInfo.getName());
        userInfoVO.setNickName(userInfo.getNickName());
        userInfoVO.setMobile(mobile);
        userInfoVO.setToken(token);
        userInfoVO.setHeadImg(userInfo.getHeadImg());

        //返回
        return userInfoVO;
    }

    @Override
    public IPage<UserInfo> listPage(UserInfoQuery userInfoQuery, Page<UserInfo> pageParam) {

        Integer userType = userInfoQuery.getUserType();
        Integer status = userInfoQuery.getStatus();
        String mobile = userInfoQuery.getMobile();
        //封装查询条件
        LambdaQueryWrapper<UserInfo> userInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userInfoLambdaQueryWrapper.eq(StringUtils.isNotBlank(mobile), UserInfo::getMobile, mobile);
        userInfoLambdaQueryWrapper.eq(status != null, UserInfo::getStatus, status);
        userInfoLambdaQueryWrapper.eq(userType != null, UserInfo::getUserType, userType);
        //查询结果
        Page<UserInfo> userInfoPage = baseMapper.selectPage(pageParam, userInfoLambdaQueryWrapper);
        //返回对象
        return userInfoPage;
    }

    @Override
    public void lock(Long id, Integer status) {

        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        updateById(userInfo);
    }

    @Override
    public boolean checkMobile(String mobile) {

        LambdaQueryWrapper<UserInfo> userInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userInfoLambdaQueryWrapper.eq(UserInfo::getMobile, mobile);

        int count = count(userInfoLambdaQueryWrapper);

        //返回查询结果
        return count > 0;
    }

    @Override
    public UserInfo getUserInfoById(Long userId) {

        UserInfo userInfo = baseMapper.selectById(userId);
        return userInfo;

    }

    @Override
    public String getMobileByBindCode(String bindCode) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("bind_code", bindCode);
        UserInfo userInfo = baseMapper.selectOne(userInfoQueryWrapper);
        return userInfo.getMobile();
    }
}
