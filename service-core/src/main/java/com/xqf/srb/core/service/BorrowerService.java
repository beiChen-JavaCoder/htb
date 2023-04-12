package com.xqf.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xqf.srb.core.pojo.entity.Borrower;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xqf.srb.core.pojo.vo.BorrowerApprovalVO;
import com.xqf.srb.core.pojo.vo.BorrowerDetailVO;
import com.xqf.srb.core.pojo.vo.BorrowerVO;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
public interface BorrowerService extends IService<Borrower> {

    /**
     * 提交借款信息
     * @param borrowerVO 借款人信息Vo
     * @param userId 借款人userId
     */
    void saveBorrowerVoByUserId(BorrowerVO borrowerVO, Long userId);

    /**
     * 根据用户id获取当前申请借款信息状态
     *
     * @param userId 当前登录的用户id
     * @return 返回状态
     */
    Integer getBorrowerStatus(Long userId);

    /**
     * 后台分页查询借款信息进行审核
     * @param pageParam 分页参数
     * @param keyword 模糊查询参数
     * @return 查询结果
     */
    IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword);

    /**
     * @param id 借款人id
     * @return 借款信息详情vo
     */
    BorrowerDetailVO getBorrowerDetailVOById(Long id);

    /**
     * 借款额度审批
     * @param borrowerApprovalVO
     */
    void approval(BorrowerApprovalVO borrowerApprovalVO);

}
