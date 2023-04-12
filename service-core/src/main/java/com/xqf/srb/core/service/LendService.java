package com.xqf.srb.core.service;

import com.xqf.srb.core.pojo.entity.BorrowInfo;
import com.xqf.srb.core.pojo.entity.Lend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xqf.srb.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
public interface LendService extends IService<Lend> {

    /**
     * 审核借款信息通过
     * 生成标的
     * @param borrowInfoApprovalVO
     * @param borrowInfo
     */
    void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo);

    /**
     * 标的列表
     * @return
     */
    List<Lend> lendList();

    /**
     * 获取标的详情信息
     * @param id 标的id
     * @return
     */
    Map<String, Object> getLendDetail(Long id);

    /**
     * @param invest 本金
     * @param yearRate 年利率
     * @param totalmonth 期数
     * @param returnMethod 还款方式
     * @return 本息
     */
    BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalmonth, Integer returnMethod);

    /**
     * 放款
     * @param id 标的id
     */
    void makeLoan(Long id);

}
