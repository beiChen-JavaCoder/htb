package com.xqf.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xqf.srb.core.pojo.entity.BorrowerAttach;
import com.xqf.srb.core.dao.BorrowerAttachMapper;
import com.xqf.srb.core.pojo.vo.BorrowerAttachVO;
import com.xqf.srb.core.service.BorrowerAttachService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务实现类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@Service
public class BorrowerAttachServiceImpl extends ServiceImpl<BorrowerAttachMapper, BorrowerAttach> implements BorrowerAttachService {

    @Override
    public List<BorrowerAttachVO> selectBorrowerAttachVOList(Long id) {
        LambdaQueryWrapper<BorrowerAttach> borrowerAttachLambdaQueryWrapper = new LambdaQueryWrapper<>();
        borrowerAttachLambdaQueryWrapper.eq(BorrowerAttach::getBorrowerId,id);
        List<BorrowerAttach> borrowerAttachList = baseMapper.selectList(borrowerAttachLambdaQueryWrapper);

        ArrayList<BorrowerAttachVO> borrowerAttachVOS = new ArrayList<>();
        borrowerAttachList.forEach(borrowerAttach -> {

            BorrowerAttachVO borrowerAttachVO = new BorrowerAttachVO();
            borrowerAttachVO.setImageType(borrowerAttach.getImageType());
            borrowerAttachVO.setImageUrl(borrowerAttach.getImageUrl());

            borrowerAttachVOS.add(borrowerAttachVO);

        });
        return borrowerAttachVOS;


    }
}
