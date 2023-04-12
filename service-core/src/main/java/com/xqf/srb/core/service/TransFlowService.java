package com.xqf.srb.core.service;

import com.xqf.srb.core.pojo.entity.TransFlow;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xqf.srb.core.pojo.vo.TransFlowBO;

import java.util.Map;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
public interface TransFlowService extends IService<TransFlow> {

    /**
     * 新增账户流水记录
     * @param transFlowBO
     */
    void saveTransFlow(TransFlowBO transFlowBO);

    /**
     * 查询流水记录（校验幂等性）
     * @param agentBillNo
     * @return
     */
    boolean isSaveTransFlow(String agentBillNo);
}
