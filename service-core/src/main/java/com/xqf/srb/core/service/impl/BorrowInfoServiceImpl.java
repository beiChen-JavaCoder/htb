package com.xqf.srb.core.service.impl;

import com.baomidou.mybatisplus.core.assist.ISqlRunner;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqf.common.exception.Assert;
import com.xqf.common.result.ResponseEnum;
import com.xqf.srb.core.dao.*;
import com.xqf.srb.core.enums.BorrowInfoStatusEnum;
import com.xqf.srb.core.enums.BorrowerStatusEnum;
import com.xqf.srb.core.enums.UserBindEnum;
import com.xqf.srb.core.pojo.entity.*;
import com.xqf.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.xqf.srb.core.pojo.vo.BorrowerDetailVO;
import com.xqf.srb.core.service.BorrowInfoService;
import com.xqf.srb.core.service.BorrowerService;
import com.xqf.srb.core.service.DictService;
import com.xqf.srb.core.service.LendService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private IntegralGradeMapper integralGradeMapper;

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerMapper borrowerMapper;

    @Resource
    private BorrowerService borrowerService;

    @Resource
    private LendService lendService;

    @Resource
    private BorrowInfoMapper borrowerInfoMapper;


    @Override
    public BigDecimal getBorrowAmount(Long userId) {

        UserInfo userInfo = userInfoMapper.selectById(userId);
        Integer integral = userInfo.getIntegral();
        LambdaQueryWrapper<IntegralGrade> integralGradeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        integralGradeLambdaQueryWrapper
                .le(IntegralGrade::getIntegralStart, integral)
                .ge(IntegralGrade::getIntegralEnd, integral)
                .select(IntegralGrade::getBorrowAmount);
        List<Object> objects = integralGradeMapper.selectObjs(integralGradeLambdaQueryWrapper);
        if (objects.get(0) == null) {
            return new BigDecimal("0");
        }
        return (BigDecimal) objects.get(0);
    }

    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {

        //判断用户绑定状态
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Assert.isTrue(
                userInfo.getBindStatus().intValue()
                        == UserBindEnum.BIND_OK.getStatus().intValue(), ResponseEnum.USER_NO_BIND_ERROR
        );
        //判断审核借款人信息
        Assert.isTrue(
                userInfo.getBorrowAuthStatus().intValue() == BorrowerStatusEnum.AUTH_OK.getStatus().intValue(),
                ResponseEnum.USER_NO_AMOUNT_ERROR);

        //审核额度是否满足最大额度
        BigDecimal borrowAmount = this.getBorrowAmount(userId);

        Assert.isTrue(
                borrowInfo.getAmount().intValue() <= borrowAmount.intValue(), ResponseEnum.USER_AMOUNT_LESS_ERROR);

        //绑定当前用户信息
        borrowInfo.setUserId(userId);
        //修改借款信息状态为认证中
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());
        //换算年利率为小数
        borrowInfo.setBorrowYearRate(borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)));
        baseMapper.insert(borrowInfo);
    }

    @Override
    public Integer getBorrowInfoStatus(Long userId) {
        LambdaQueryWrapper<BorrowInfo> borrowInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        borrowInfoLambdaQueryWrapper.eq(BorrowInfo::getUserId, userId)
                .select(BorrowInfo::getStatus);

        List<Object> objects = baseMapper.selectObjs(borrowInfoLambdaQueryWrapper);

        if (objects.size() == 0) {
            return BorrowInfoStatusEnum.NO_AUTH.getStatus();
        }
        return (Integer) objects.get(0);

    }

    @Override
    public List<BorrowInfo> selectList() {


        List<BorrowInfo> list = list();
        list.forEach(borrowInfo ->
                {
                    UserInfo userInfo = userInfoMapper.selectById(borrowInfo.getUserId());
                    borrowInfo.setName(userInfo.getName());
                    borrowInfo.setMobile(userInfo.getMobile());

                    String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
                    String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
                    String msgByStatus = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());

                    Map<String, Object> objectHashMap = new HashMap<>();
                    objectHashMap.put("returnMethod", returnMethod);
                    objectHashMap.put("status", msgByStatus);
                    objectHashMap.put("moneyUse", moneyUse);

                    borrowInfo.setParam(objectHashMap);
                }

        );

        return list;
    }

    @Override
    public Map<String, Object> getBorrowInfoDetail(Long id) {

        BorrowInfo borrowInfo = baseMapper.selectById(id);

        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
        String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
        String msgByStatus = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
        //封装借款信息
        borrowInfo.getParam().put("returnMethod", returnMethod);
        borrowInfo.getParam().put("moneyUse", moneyUse);
        borrowInfo.getParam().put("status", msgByStatus);

        //根据借款申请信息获取借款人信息
        LambdaQueryWrapper<Borrower> borrowerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        borrowerLambdaQueryWrapper.eq(Borrower::getUserId, borrowInfo.getUserId());
        Borrower borrower = borrowerMapper.selectOne(borrowerLambdaQueryWrapper);

        //借款人信息
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(borrower.getId());

        //组装数据
        Map<String, Object> result = new HashMap<>();
        result.put("borrowInfo", borrowInfo);
        result.put("borrower", borrowerDetailVO);


        return result;


    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowInfoApprovalVO borrowInfoApprovalVO) {


        Long id = borrowInfoApprovalVO.getId();
        BorrowInfo borrowInfo = baseMapper.selectById(id);


        borrowInfo.setStatus(borrowInfoApprovalVO.getStatus().intValue());
        borrowInfo.setBorrowYearRate(borrowInfoApprovalVO.getLendYearRate().divide(new BigDecimal(100)));
        baseMapper.updateById(borrowInfo);


        if (borrowInfoApprovalVO.getStatus().intValue() == BorrowInfoStatusEnum.CHECK_OK.getStatus().intValue()) {


            lendService.createLend(borrowInfoApprovalVO, borrowInfo);
        }


    }

}
