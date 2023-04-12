package com.xqf.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xqf.common.exception.Assert;
import com.xqf.common.result.ResponseEnum;
import com.xqf.common.result.ResultResponse;
import com.xqf.srb.core.dao.LendMapper;
import com.xqf.srb.core.dao.UserAccountMapper;
import com.xqf.srb.core.enums.LendStatusEnum;
import com.xqf.srb.core.enums.ReturnMethodEnum;
import com.xqf.srb.core.enums.TransTypeEnum;
import com.xqf.srb.core.hfb.FormHelper;
import com.xqf.srb.core.hfb.HfbConst;
import com.xqf.srb.core.hfb.RequestHelper;
import com.xqf.srb.core.pojo.entity.Lend;
import com.xqf.srb.core.pojo.entity.LendItem;
import com.xqf.srb.core.dao.LendItemMapper;
import com.xqf.srb.core.pojo.entity.UserAccount;
import com.xqf.srb.core.pojo.vo.InvestVO;
import com.xqf.srb.core.pojo.vo.TransFlowBO;
import com.xqf.srb.core.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xqf.srb.core.util.LendNoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务实现类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
@Service
@Slf4j
public class LendItemServiceImpl extends ServiceImpl<LendItemMapper, LendItem> implements LendItemService {

    @Resource
    private LendMapper lendMapper;

    @Resource
    private LendService lendService;

    @Resource
    private UserAccountService userAccountService;

    @Resource
    private UserBindService userBindService;
    @Resource
    private TransFlowService transFlowService;
    @Resource
    private UserAccountMapper userAccountMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String commitInvest(InvestVO investVO) {

        //输入校验==========================================

        Long investUserId = investVO.getInvestUserId();

        //标的状态必须为募资中
        Lend lend = lendMapper.selectById(investVO.getLendId());
        Assert.isTrue(lend.getStatus().intValue()
                == LendStatusEnum.INVEST_RUN.getStatus().intValue(), ResponseEnum.LEND_INVEST_ERROR);

        //标的不能超卖当前->投标金额+获得当前总金额<=标的金额
        BigDecimal sum = new BigDecimal(investVO.getInvestAmount()).add(lend.getInvestAmount());
        Assert.isTrue(sum.intValue() <= lend.getAmount().intValue(), ResponseEnum.LEND_FULL_SCALE_ERROR);

        //账户可用余额充足,账户余额>投标余额
        BigDecimal account = userAccountService.getAccount(investUserId);


        Assert.isTrue(Double.parseDouble(investVO.getInvestAmount()) < account.doubleValue(), ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);

        //在商户平台中生成投资信息==========================================
        //标的下的投资信息
        LendItem lendItem = new LendItem();
        lendItem.setInvestUserId(investUserId);//投资人id
        lendItem.setInvestName(investVO.getInvestName());//投资人名字
        String lendItemNo = LendNoUtils.getLendItemNo();
        lendItem.setLendItemNo(lendItemNo); //投资条目编号（一个Lend对应一个或多个LendItem）
        lendItem.setLendId(investVO.getLendId());//对应的标的id
        lendItem.setInvestAmount(new BigDecimal(investVO.getInvestAmount())); //此笔投资金额
        lendItem.setLendYearRate(lend.getLendYearRate());//年化
        lendItem.setInvestTime(LocalDateTime.now()); //投资时间
        lendItem.setLendStartDate(lend.getLendStartDate()); //开始时间
        lendItem.setLendEndDate(lend.getLendEndDate()); //结束时间

        //投资预期收益
        BigDecimal expectAmount = lendService.getInterestCount(
                new BigDecimal(investVO.getInvestAmount()),
                lend.getLendYearRate(),
                lend.getPeriod(),
                lend.getReturnMethod());
        lendItem.setExpectAmount(expectAmount);

        //实际收益
        lendItem.setRealAmount(new BigDecimal(0));

        //默认状态：刚刚创建
        lendItem.setStatus(0);
        baseMapper.insert(lendItem);

        //组装投资相关的参数，提交到航汇通资金托管平台==========================================
        //在托管平台同步用户的投资信息，修改用户的账户资金信息==========================================
        //获取投资人的绑定协议号
        String bindCode = userBindService.getBindCodeByUserId(investUserId);
        //获取借款人的绑定协议号
        String benefitBindCode = userBindService.getBindCodeByUserId(lend.getUserId());

        //封装提交至航汇通的参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("voteBindCode", bindCode);
        paramMap.put("benefitBindCode", benefitBindCode);
        paramMap.put("agentProjectCode", lend.getLendNo());
        //项目标号
        paramMap.put("agentProjectName", lend.getTitle());

        //在资金托管平台上的投资订单的唯一编号，要和lendItemNo保持一致。
        paramMap.put("agentBillNo", lendItemNo);//订单编号
        paramMap.put("voteAmt", investVO.getInvestAmount());
        paramMap.put("votePrizeAmt", "0");
        paramMap.put("voteFeeAmt", "0");
        paramMap.put("projectAmt", lend.getAmount()); //标的总金额
        paramMap.put("note", "");
        paramMap.put("notifyUrl", HfbConst.INVEST_NOTIFY_URL); //检查常量是否正确
        paramMap.put("returnUrl", HfbConst.INVEST_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        String formStr = FormHelper.buildForm(HfbConst.INVEST_URL, paramMap);

        return formStr;


    }

    /**
     * 投资异步回调
     *
     * @param paramMap
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void notify(Map<String, Object> paramMap) {

        log.info("投标成功");

        //获取投资编号
        String agentBillNo = (String) paramMap.get("agentBillNo");

        boolean result = transFlowService.isSaveTransFlow(agentBillNo);
        if (result) {
            log.warn("流水记录已存在：{}", agentBillNo);
            return;
        }

        //获取用户的绑定协议号
        String bindCode = (String) paramMap.get("voteBindCode");
        String voteAmt = (String) paramMap.get("voteAmt");

        //修改商户系统中的用户账户金额：余额、冻结金额
        userAccountMapper.updateAccount(bindCode, new BigDecimal("-" + voteAmt), new BigDecimal(voteAmt));

        //修改投资记录的投资状态改为已支付
        LendItem lendItem = this.getByLendItemNo(agentBillNo);
        lendItem.setStatus(1);//已支付
        baseMapper.updateById(lendItem);

        //修改标的信息：投资人数、已投金额
        Long lendId = lendItem.getLendId();
        Lend lend = lendMapper.selectById(lendId);
        lend.setInvestNum(lend.getInvestNum() + 1);
        lend.setInvestAmount(lend.getInvestAmount().add(lendItem.getInvestAmount()));
        lendMapper.updateById(lend);

        //新增交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(voteAmt),
                TransTypeEnum.INVEST_LOCK,
                "投资项目编号：" + lend.getLendNo() + "，项目名称：" + lend.getTitle());
        transFlowService.saveTransFlow(transFlowBO);
    }

    @Override
    public List<LendItem> selectByLendId(String lendId, Integer status) {

        LambdaQueryWrapper<LendItem> lendItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
        lendItemLambdaQueryWrapper.eq(LendItem::getLendItemNo, lendId)
                .eq(LendItem::getStatus,status);
        List<LendItem> lendItems = baseMapper.selectList(lendItemLambdaQueryWrapper);

        return lendItems;


    }

    private LendItem getByLendItemNo(String agentBillNo) {

        QueryWrapper<LendItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lend_item_no", agentBillNo);
        return baseMapper.selectOne(queryWrapper);

    }
}
