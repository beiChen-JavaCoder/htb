package com.xqf.srb.core.service;

import com.xqf.srb.core.pojo.entity.BorrowerAttach;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xqf.srb.core.pojo.vo.BorrowerAttachVO;

import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
public interface BorrowerAttachService extends IService<BorrowerAttach> {

    /**
     * 根据借款人id查询附件列表
     * @param id
     * @return
     */
    List<BorrowerAttachVO> selectBorrowerAttachVOList(Long id);
}
