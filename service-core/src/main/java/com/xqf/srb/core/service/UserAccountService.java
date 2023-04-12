package com.xqf.srb.core.service;

import com.xqf.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
public interface UserAccountService extends IService<UserAccount> {

    /**
     * 生成动态自动提交充值表单
     * @param chargeAmt 充值金额
     * @param userId 当前用户userId
     * @return 动态表单
     */
    String commitCharge(BigDecimal chargeAmt, Long userId);

    /**
     * 用户充值异步回调
     *
     * @param paramMap
     * @return
     */
    String notify(Map<String, Object> paramMap);

    /**
     * 获取账户余额
     * @param userId
     * @return
     */
    BigDecimal getAccount(Long userId);
}
