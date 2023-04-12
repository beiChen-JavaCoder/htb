package com.xqf.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xqf.common.result.ResultResponse;
import com.xqf.srb.core.dao.BorrowerAttachMapper;
import com.xqf.srb.core.dao.UserInfoMapper;
import com.xqf.srb.core.dao.UserIntegralMapper;
import com.xqf.srb.core.enums.BorrowInfoStatusEnum;
import com.xqf.srb.core.enums.BorrowerStatusEnum;
import com.xqf.srb.core.enums.IntegralEnum;
import com.xqf.srb.core.pojo.entity.Borrower;
import com.xqf.srb.core.dao.BorrowerMapper;
import com.xqf.srb.core.pojo.entity.BorrowerAttach;
import com.xqf.srb.core.pojo.entity.UserInfo;
import com.xqf.srb.core.pojo.entity.UserIntegral;
import com.xqf.srb.core.pojo.vo.BorrowerApprovalVO;
import com.xqf.srb.core.pojo.vo.BorrowerAttachVO;
import com.xqf.srb.core.pojo.vo.BorrowerDetailVO;
import com.xqf.srb.core.pojo.vo.BorrowerVO;
import com.xqf.srb.core.service.BorrowerAttachService;
import com.xqf.srb.core.service.BorrowerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqf.srb.core.service.DictService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;
    @Resource
    private  DictService dictService;
    @Resource
    private BorrowerAttachService borrowerAttachService;
    @Resource
    private UserIntegralMapper userIntegralMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBorrowerVoByUserId(BorrowerVO borrowerVO, Long userId) {

        UserInfo userInfo = userInfoMapper.selectById(userId);

        //保存借款人信息
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO, borrower);
        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());//认证中
        baseMapper.insert(borrower);

        //保存附件信息
        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        borrowerAttachList.forEach(borrowerAttach -> {
            borrowerAttach.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(borrowerAttach);
        });
        //更新会员状态，更新为认证中
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);


    }

    @Override
    public Integer getBorrowerStatus(Long userId) {

        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.select("status").eq("user_id", userId);

        List<Object> status = baseMapper.selectObjs(borrowerQueryWrapper);

        if (status.size() == 0) {
            //当前借款人未提交借款信息
            return BorrowerStatusEnum.NO_AUTH.getStatus();
        }
        //查询到当前用户有借款信息,返回当前借款信息的状态
        return (Integer) status.get(0);
    }

    @Override
    public IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword) {

        if (StringUtils.isBlank(keyword)) {
            //查询条件为空返回所有查询结果
            return baseMapper.selectPage(pageParam, null);

        }
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.like("name", keyword).or()
                .like("id_card", keyword).or()
                .like("mobile", keyword).or()
                .orderByDesc("id");
        Page<Borrower> borrowerPage = baseMapper.selectPage(pageParam, borrowerQueryWrapper);

        return borrowerPage;


    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BorrowerDetailVO getBorrowerDetailVOById(Long id) {

        //查询当前借款人信息
        Borrower borrower = baseMapper.selectById(id);

        //填充基本借款人信息
        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();
        BeanUtils.copyProperties(borrower, borrowerDetailVO);

        //婚否
        borrowerDetailVO.setMarry(borrower.getMarry()?"是":"否");
        //性别
        borrowerDetailVO.setSex(borrower.getSex()==1?"男":"女");

        //计算下拉列表选中内容填充
        borrowerDetailVO.setEducation(dictService.getNameByParentDictCodeAndValue("education", borrower.getEducation()));
        borrowerDetailVO.setIndustry(dictService.getNameByParentDictCodeAndValue("industry", borrower.getIndustry()));
        borrowerDetailVO.setIncome(dictService.getNameByParentDictCodeAndValue("income", borrower.getIncome()));
        borrowerDetailVO.setReturnSource(dictService.getNameByParentDictCodeAndValue("returnSource", borrower.getReturnSource()));
        borrowerDetailVO.setContactsRelation(dictService.getNameByParentDictCodeAndValue("relation", borrower.getContactsRelation()));

        //审批状态
        borrowerDetailVO.setStatus(BorrowerStatusEnum.getMsgByStatus(borrower.getStatus()));

        //封装获取附件VO列表
        List<BorrowerAttachVO> borrowerAttachVOList = borrowerAttachService.selectBorrowerAttachVOList(borrower.getId());

        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachVOList);

        return borrowerDetailVO;

    }

    @Override
    public void approval(BorrowerApprovalVO borrowerApprovalVO) {

        Long borrowerId = borrowerApprovalVO.getBorrowerId();
        Borrower borrower = baseMapper.selectById(borrowerId);
        UserInfo userInfo = userInfoMapper.selectById(borrower.getUserId());

        //判断前端审核是否通过
        if (borrowerApprovalVO.getStatus().equals(BorrowInfoStatusEnum.CHECK_FAIL.getStatus())) {
            //审核不通过

            borrower.setStatus(borrowerApprovalVO.getStatus());
            baseMapper.updateById(borrower);
            //修改用户表审核状态
            userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_FAIL.getStatus());
            //更新userinfo信息
            userInfoMapper.updateById(userInfo);

            return;
        }

        //审核借款信息状态,并对用户表新增积分
        //借款人认证状态
        borrower.setStatus(borrowerApprovalVO.getStatus());
        baseMapper.updateById(borrower);

        //基本信息认证
        UserIntegral userIntegral=new UserIntegral();
        userIntegral.setUserId(borrowerId);
        userIntegral.setIntegral(borrowerApprovalVO.getInfoIntegral());
        userIntegral.setContent("基本信息认证");
        userIntegralMapper.insert(userIntegral);


        int curIntegral = userInfo.getIntegral() + borrowerApprovalVO.getInfoIntegral();

        //身份信息认证
        if (borrowerApprovalVO.getIsCarOk()) {
            userIntegral = new UserIntegral();
            userIntegral.setUserId(borrowerId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            userIntegralMapper.insert(userIntegral);
            curIntegral+=IntegralEnum.BORROWER_IDCARD.getIntegral();


        }
        //房屋信息认证
        if (borrowerApprovalVO.getIsHouseOk()) {
            userIntegral = new UserIntegral();
            userIntegral.setUserId(borrowerId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            userIntegralMapper.insert(userIntegral);
            curIntegral+=IntegralEnum.BORROWER_HOUSE.getIntegral();
        }
        //车辆信息认证
        if (borrowerApprovalVO.getIsCarOk()) {
            userIntegral = new UserIntegral();
            userIntegral.setUserId(borrowerId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(userIntegral);
            curIntegral+=IntegralEnum.BORROWER_CAR.getIntegral();
        }
        //修改用户表审核状态
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_OK.getStatus());
        userInfo.setIntegral(curIntegral);
        //更新userinfo信息
        userInfoMapper.updateById(userInfo);
    }
}
