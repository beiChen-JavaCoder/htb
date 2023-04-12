package com.xqf.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xqf.srb.core.dao.UserInfoMapper;
import com.xqf.srb.core.pojo.entity.TransFlow;
import com.xqf.srb.core.dao.TransFlowMapper;
import com.xqf.srb.core.pojo.entity.UserInfo;
import com.xqf.srb.core.pojo.vo.TransFlowBO;
import com.xqf.srb.core.service.TransFlowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 交易流水表 服务实现类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@Service
public class TransFlowServiceImpl extends ServiceImpl<TransFlowMapper, TransFlow> implements TransFlowService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveTransFlow(TransFlowBO transFlowBO) {

        //获取用户信息
        String bindCode = transFlowBO.getBindCode();
        LambdaQueryWrapper<UserInfo> userInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userInfoLambdaQueryWrapper.eq(UserInfo::getBindCode,bindCode);
        UserInfo userInfo = userInfoMapper.selectOne(userInfoLambdaQueryWrapper);

        //生成流水记录
        TransFlow transFlow = new TransFlow();
        transFlow.setTransNo(transFlowBO.getAgentBillNo());
        transFlow.setUserId(userInfo.getId());
        transFlow.setTransAmount(transFlowBO.getAmount());
        transFlow.setTransTypeName(transFlowBO.getTransTypeEnum().getTransTypeName());
        transFlow.setTransType(transFlowBO.getTransTypeEnum().getTransType());
        transFlow.setMemo(transFlowBO.getMemo());
        transFlow.setUserName(userInfo.getName());

        baseMapper.insert(transFlow);




    }

    @Override
    public boolean isSaveTransFlow(String agentBillNo) {
        QueryWrapper<TransFlow> queryWrapper = new QueryWrapper();
        queryWrapper.eq("trans_no", agentBillNo);
        int count = baseMapper.selectCount(queryWrapper);
        if(count > 0) {
            return true;
        }
        return false;
    }
}
