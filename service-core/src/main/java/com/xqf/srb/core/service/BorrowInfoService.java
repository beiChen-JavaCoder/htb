package com.xqf.srb.core.service;

import com.xqf.srb.core.pojo.entity.BorrowInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xqf.srb.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
public interface BorrowInfoService extends IService<BorrowInfo> {

    /**
     * 根据用户id获取积分计算相应额度
     * @param userId 用户id
     * @return 借款额度
     */
    BigDecimal getBorrowAmount(Long userId);

    /**
     * @param borrowInfo 提交申请参数对象
     * @param userId 当前用户id
     */
    void saveBorrowInfo(BorrowInfo borrowInfo, Long userId);

    /**
     * 获取借款信息审核状态
     * @param userId
     * @return
     */
    Integer getBorrowInfoStatus(Long userId);

    /**
     * 获取借款信息列表
     *
     * @return
     */
    List<BorrowInfo> selectList();

    /**
     * 查询借款详情
     * @param id 借款信息id
     * @return 借款详情
     */
    Map<String, Object> getBorrowInfoDetail(Long id);

    /**
     * 审批借款信息
     *
     * @param borrowInfoApprovalVO
     */
    void approval(BorrowInfoApprovalVO borrowInfoApprovalVO);

}
