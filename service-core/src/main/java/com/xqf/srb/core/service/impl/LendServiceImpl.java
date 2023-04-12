package com.xqf.srb.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqf.common.exception.BusinessException;
import com.xqf.srb.core.dao.BorrowerMapper;
import com.xqf.srb.core.dao.LendMapper;
import com.xqf.srb.core.dao.UserAccountMapper;
import com.xqf.srb.core.dao.UserInfoMapper;
import com.xqf.srb.core.enums.LendItemStatusEnum;
import com.xqf.srb.core.enums.LendStatusEnum;
import com.xqf.srb.core.enums.ReturnMethodEnum;
import com.xqf.srb.core.enums.TransTypeEnum;
import com.xqf.srb.core.hfb.HfbConst;
import com.xqf.srb.core.hfb.RequestHelper;
import com.xqf.srb.core.pojo.entity.*;
import com.xqf.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.xqf.srb.core.pojo.vo.BorrowerDetailVO;
import com.xqf.srb.core.pojo.vo.InvestVO;
import com.xqf.srb.core.pojo.vo.TransFlowBO;
import com.xqf.srb.core.service.*;
import com.xqf.srb.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务实现类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@Service
@Slf4j
public class LendServiceImpl extends ServiceImpl<LendMapper, Lend> implements LendService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private LendItemService lendItemService;

    @Resource
    private TransFlowService transFlowService;
    @Resource
    private LendMapper lendMapper;
    @Resource
    private DictService dictService;

    @Resource
    private BorrowerService borrowerService;

    @Resource
    private BorrowerMapper borrowerMapper;

    @Override
    public void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo) {


        Lend lend = new Lend();
        lend.setUserId(borrowInfo.getUserId());
        lend.setBorrowInfoId(borrowInfo.getId());
        lend.setLendNo(LendNoUtils.getLendNo());//生成编号
        lend.setTitle(borrowInfoApprovalVO.getTitle());
        lend.setAmount(borrowInfo.getAmount());
        lend.setPeriod(borrowInfo.getPeriod());
        lend.setLendYearRate(borrowInfoApprovalVO.getLendYearRate()
                .divide(new BigDecimal("100")));
        lend.setServiceRate(borrowInfoApprovalVO.getServiceRate()
                .divide(new BigDecimal("100")));

        lend.setReturnMethod(borrowInfo.getReturnMethod());
        lend.setLowestAmount(new BigDecimal("100"));
        lend.setInvestAmount(new BigDecimal("0"));
        lend.setPublishDate(borrowInfo.getCreateTime());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate lendStart = LocalDate.parse(borrowInfoApprovalVO.getLendStartDate(), dateTimeFormatter);
        lend.setLendStartDate(lendStart);
        LocalDate lendEnd = lendStart.plusMonths(borrowInfo.getReturnMethod());
        lend.setLendEndDate(lendEnd);

        lend.setLendInfo(borrowInfoApprovalVO.getLendInfo());

        //预期收益=标价*年化率/12*期数
        BigDecimal monthRate = lend.getLendYearRate()
                .divide(new BigDecimal(12), 8, BigDecimal.ROUND_DOWN);
        BigDecimal expect_amount = borrowInfo.getAmount()
                .multiply(monthRate
                        .multiply(new BigDecimal(lend.getPeriod())));
        lend.setExpectAmount(expect_amount);

        //实际收益
        lend.setRealAmount(new BigDecimal(0));
        //状态
        lend.setStatus(LendStatusEnum.INVEST_RUN.getStatus());
        //审核时间
        lend.setCheckTime(LocalDateTime.now());
        //审核人
        lend.setCheckAdminId(1L);

        lendMapper.insert(lend);


    }

    @Override
    public List<Lend> lendList() {

        List<Lend> lendList = list();

        lendList.forEach(lend ->
        {
            String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", lend.getReturnMethod());
            String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
            Map<String, Object> param = lend.getParam();
            param.put("returnMethod", returnMethod);
            param.put("status", status);
            lend.setParam(param);
        });

        return lendList;
    }

    @Override
    public Map<String, Object> getLendDetail(Long id) {
        //组装标的详情
        Lend lend = lendMapper.selectById(id);
        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", lend.getReturnMethod());
        String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
        HashMap<String, Object> param = new HashMap<>();
        param.put("returnMethod", returnMethod);
        param.put("status", status);
        lend.setParam(param);

        //组装借款人详情
        Long userId = lend.getUserId();
        LambdaQueryWrapper<Borrower> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Borrower::getUserId, userId);
        Borrower borrower = borrowerMapper.selectOne(lambdaQueryWrapper);
        BorrowerDetailVO borrowerDetail = borrowerService.getBorrowerDetailVOById(borrower.getId());

        //组装Map
        HashMap<String, Object> result = new HashMap<>();
        result.put("lend", lend);
        result.put("borrower", borrowerDetail);

        return result;

    }

    @Override
    public BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalmonth, Integer returnMethod) {

        BigDecimal interestCount;
        if (returnMethod.intValue() == ReturnMethodEnum.ONE.getMethod().intValue()) {
            interestCount = Amount1Helper.getInterestCount(invest, yearRate, totalmonth);
        } else if (returnMethod.intValue() == ReturnMethodEnum.TWO.getMethod().intValue()) {
            interestCount = Amount2Helper.getInterestCount(invest, yearRate, totalmonth);
        } else if (returnMethod.intValue() == ReturnMethodEnum.THREE.getMethod().intValue()) {
            interestCount = Amount3Helper.getInterestCount(invest, yearRate, totalmonth);
        } else {
            interestCount = Amount4Helper.getInterestCount(invest, yearRate, totalmonth);
        }
        return interestCount;

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void makeLoan(Long id) {
        //获取标的信息
        Lend lend = baseMapper.selectById(id);
        String lendId = lend.getLendNo();
        HashMap<String, Object> param = new HashMap<>();
        param.put("agentId", HfbConst.AGENT_ID);
        param.put("agentProjectCode", lend.getLendNo());
        String agentBillNo = LendNoUtils.getLoanNo();//放款编号
        param.put("agentBillNo", agentBillNo);

        //平台收益，放款扣除，借款人借款实际金额=借款金额-平台收益
        //月年化
        BigDecimal monthRate = lend.getLendYearRate().divide(new BigDecimal("12"),8,BigDecimal.ROUND_DOWN);
        //平台收益
        BigDecimal realAmount = lend.getInvestAmount().multiply(monthRate).multiply(new BigDecimal(lend.getPeriod()));

        param.put("mchFee", realAmount); //商户手续费(平台实际收益)
        param.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(param);
        param.put("sign", sign);

        log.info("放款参数：" + JSONObject.toJSONString(param));
        //发送同步远程调用
        JSONObject result = RequestHelper.sendRequest(param, HfbConst.MAKE_LOAD_URL);
        log.info("放款结果：" + result.toJSONString());
        if (!"0000".equals(result.getString("resultCode"))) {

            throw new BusinessException(result.getString("resultMsg"));

        }
        //更新标的信息
        lend.setRealAmount(realAmount);
        lend.setStatus(LendStatusEnum.PAY_RUN.getStatus());
        lend.setPaymentTime(LocalDateTime.now());
        baseMapper.updateById(lend);

        //获取借款人信息
        Long userId = lend.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();
        //给借款账号转入金额
        BigDecimal voteAmt = new BigDecimal(result.getString("voteAmt"));
        userAccountMapper.updateAccount(bindCode, voteAmt, new BigDecimal(0));
        //新增借款人交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo, bindCode,
                voteAmt,
                TransTypeEnum.BORROW_BACK,
                "借款放款到账，编号：" + lend.getLendNo());
        transFlowService.saveTransFlow(transFlowBO);
        //获取投资列表信息
        List<LendItem> lendItemList = lendItemService.selectByLendId(lendId, 1);
        lendItemList.stream().forEach(lendItem ->
                {
                    //获取投资人信息
                    Long investUserId = lendItem.getInvestUserId();
                    UserInfo investUserInfo = userInfoMapper.selectById(investUserId);
                    String investBindCode = investUserInfo.getBindCode();
                    //转出投资人账户金额
                    //投资金额
                    BigDecimal investAmount = lendItem.getInvestAmount();
                    userAccountMapper.updateAccount(
                            investBindCode,
                            new BigDecimal(LendItemStatusEnum.PAY_OK.getStatus()),
                            investAmount);
                    //新增投资人交易流水
                    TransFlowBO investTransFlowBO = new TransFlowBO(
                            LendNoUtils.getTransNo(),
                            investBindCode,
                            investAmount,
                            TransTypeEnum.INVEST_UNLOCK,
                            //项目编号
                            "冻结资金转出，出借放款，编号：" + lend.getLendNo());
                    transFlowService.saveTransFlow(investTransFlowBO);

                }
        );

        //放款成功生成借款人还款计划和投资人回款计划

        // TODO

    }

}
