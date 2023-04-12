package com.xqf.srb.core.dao;

import com.xqf.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户 Mapper 接口
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> {

    /**
     * 更新用户绑定账户余额
     * @param bindCode
     * @param chargeAmt
     * @param freezeAmount
     */
    void updateAccount(@Param("bindCode") String bindCode,
                       @Param("chargeAmt") BigDecimal chargeAmt,
                       @Param("freezeAmount") BigDecimal freezeAmount);
}
