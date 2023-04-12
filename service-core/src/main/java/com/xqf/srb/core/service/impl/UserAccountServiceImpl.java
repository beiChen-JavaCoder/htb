package com.xqf.srb.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqf.common.exception.Assert;
import com.xqf.common.result.ResponseEnum;
import com.xqf.srb.base.SmsDTO;
import com.xqf.srb.core.dao.UserAccountMapper;
import com.xqf.srb.core.dao.UserInfoMapper;
import com.xqf.srb.core.enums.TransTypeEnum;
import com.xqf.srb.core.hfb.FormHelper;
import com.xqf.srb.core.hfb.HfbConst;
import com.xqf.srb.core.hfb.RequestHelper;
import com.xqf.srb.core.pojo.entity.UserAccount;
import com.xqf.srb.core.pojo.entity.UserInfo;
import com.xqf.srb.core.pojo.vo.TransFlowBO;
import com.xqf.srb.core.service.TransFlowService;
import com.xqf.srb.core.service.UserAccountService;
import com.xqf.srb.core.service.UserInfoService;
import com.xqf.srb.core.util.LendNoUtils;
import com.xqf.srb.rabbitutil.constant.MQConst;
import com.xqf.srb.rabbitutil.service.MQService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@Service
@Slf4j
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    TransFlowService transFlowService;

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private MQService mqService;

    @Override
    public String commitCharge(BigDecimal chargeAmt, Long userId) {


        //判断用户绑定状态
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Assert.notEmpty(userInfo.getBindCode(), ResponseEnum.USER_NO_BIND_ERROR);

        //封装表单数据
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getNo());
        paramMap.put("bindCode", userInfo.getBindCode());
        paramMap.put("chargeAmt", chargeAmt);
        paramMap.put("feeAmt", new BigDecimal("0"));
        //检查常量是否正确
        paramMap.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        //生成一个动态表单字符串
        String formStr = FormHelper.buildForm(HfbConst.RECHARGE_URL, paramMap);

        return formStr;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String notify(Map<String, Object> paramMap) {

        //检查幂等性
        String agentBillNo = (String) paramMap.get("agentBillNo");
        boolean isSave = transFlowService.isSaveTransFlow(agentBillNo);
        if (isSave) {
            //当存在相同流水那么，直接退出
            log.warn("已存在流水账单:" + agentBillNo);
            return "success";
        }

        //修改账户余额
        log.info("充值成功：" + JSONObject.toJSONString(paramMap));
        String bindCode = (String) paramMap.get("bindCode");
        String chargeAmt = (String) paramMap.get("chargeAmt");
        baseMapper.updateAccount(bindCode, new BigDecimal(chargeAmt), new BigDecimal(0));

        //发消息
        log.info("发消息");
        String mobile = userInfoService.getMobileByBindCode(bindCode);
        SmsDTO smsDTO = new SmsDTO();
        smsDTO.setMobile(mobile);
        smsDTO.setMessage("充值成功");
        mqService.sendMessage(MQConst.EXCHANGE_TOPIC_SMS, MQConst.ROUTING_SMS_ITEM, smsDTO);

        //封装Bo对象，新增流水记录
        TransFlowBO transFlowBO = new TransFlowBO();
        transFlowBO.setAgentBillNo((String) paramMap.get("agentBillNo"));
        transFlowBO.setBindCode(bindCode);
        transFlowBO.setAmount(new BigDecimal(chargeAmt));
        transFlowBO.setTransTypeEnum(TransTypeEnum.RECHARGE);
        transFlowBO.setMemo(" 充值");
        transFlowService.saveTransFlow(transFlowBO);

        return "success";
    }

    @Override
    public BigDecimal getAccount(Long userId) {

        LambdaQueryWrapper<UserAccount> userAccountLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userAccountLambdaQueryWrapper.eq(UserAccount::getUserId, userId)
                .select(UserAccount::getAmount);
        List<Object> amount = baseMapper.selectObjs(userAccountLambdaQueryWrapper);

        return (BigDecimal) amount.get(0);
    }
}
