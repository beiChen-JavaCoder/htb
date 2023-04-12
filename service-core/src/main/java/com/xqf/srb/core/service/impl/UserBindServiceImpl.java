package com.xqf.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xqf.common.exception.Assert;
import com.xqf.common.result.ResponseEnum;
import com.xqf.srb.core.dao.UserInfoMapper;
import com.xqf.srb.core.enums.UserBindEnum;
import com.xqf.srb.core.hfb.FormHelper;
import com.xqf.srb.core.hfb.HfbConst;
import com.xqf.srb.core.hfb.RequestHelper;
import com.xqf.srb.core.pojo.entity.UserBind;
import com.xqf.srb.core.dao.UserBindMapper;
import com.xqf.srb.core.pojo.entity.UserInfo;
import com.xqf.srb.core.pojo.vo.UserBindVO;
import com.xqf.srb.core.service.UserBindService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {

    @Resource
    UserInfoMapper userInfoMapper;

    @Override
    public String commitBindUser(UserBindVO userBindVO, Long userId) {

        //查询身份证号码是否绑定
        LambdaQueryWrapper<UserBind> userBindLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userBindLambdaQueryWrapper.eq(UserBind::getIdCard, userBindVO.getIdCard())
                .ne(UserBind::getUserId, userId);
        UserBind userBind = baseMapper.selectOne(userBindLambdaQueryWrapper);

        //USER_BIND_IDCARD_EXIST_ERROR(-301, "身份证号码已绑定"),
        Assert.isNull(userBind, ResponseEnum.USER_BIND_IDCARD_EXIST_ERROR);
        //查询用户绑定记录
        userBindLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userBindLambdaQueryWrapper.eq(UserBind::getUserId, userId);
        userBind = baseMapper.selectOne(userBindLambdaQueryWrapper);

        //如果未创建绑定记录，则创建一条记录仅仅是一条记录
        if (userBind == null) {
            userBind = new UserBind();
            BeanUtils.copyProperties(userBindVO, userBind);
            userBind.setUserId(userId);
            //插入绑定状态
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            baseMapper.insert(userBind);
        } else {

            //曾经跳转到托管平台，但是未操作完成，此时将用户最新填写的数据同步到userBind对象
            BeanUtils.copyProperties(userBindVO, userBind);
            baseMapper.updateById(userBind);
        }

        //组建动态信息
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentUserId", userId);
        paramMap.put("idCard", userBindVO.getIdCard());
        paramMap.put("personalName", userBindVO.getName());
        paramMap.put("bankType", userBindVO.getBankType());
        paramMap.put("bankNo", userBindVO.getBankNo());
        paramMap.put("mobile", userBindVO.getMobile());
        paramMap.put("returnUrl", HfbConst.USERBIND_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));

        //动态组装form表单
        return FormHelper.buildForm(HfbConst.USERBIND_URL, paramMap);
    }

    @Override
    public void notify(Map<String, Object> paramMap) {

        String bindCode = (String) paramMap.get("bindCode");
        String userId = (String) paramMap.get("agentUserId");

        LambdaQueryWrapper<UserBind> userBindLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userBindLambdaQueryWrapper.eq(UserBind::getUserId, userId);
        UserBind userBind = baseMapper.selectOne(userBindLambdaQueryWrapper);

        //给予userBind绑定信息
        userBind.setBindCode(bindCode);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        baseMapper.updateById(userBind);

        //给予userInfo绑定信息
        //更新用户表
        UserInfo userInfo = userInfoMapper.selectById(userId);
        userInfo.setBindCode(bindCode);
        userInfo.setName(userBind.getName());
        userInfo.setIdCard(userBind.getIdCard());
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        log.info(userId.toString());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public String getBindCodeByUserId(Long UserId) {

        LambdaQueryWrapper<UserBind> userBindLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userBindLambdaQueryWrapper.eq(UserBind::getUserId, UserId)
                .select(UserBind::getBindCode);
        List<Object> bindCodes = baseMapper.selectObjs(userBindLambdaQueryWrapper);

        return (String) bindCodes.get(0);

    }
}
