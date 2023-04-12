package com.xqf.srb.core.service;

import com.xqf.srb.core.pojo.entity.LendItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xqf.srb.core.pojo.vo.InvestVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
public interface LendItemService extends IService<LendItem> {

    /**
     * 提交投资数据
     * @param investVO
     * @return 生成动态表单
     */
    String commitInvest(InvestVO investVO);

    /**
     * 投资回调
     * @param paramMap 通知参数
     */
    void notify(Map<String, Object> paramMap);

    /**
     * 根据标的id返回投资列表信息
     * @param lendId
     * @param status
     * @return
     */
    List<LendItem> selectByLendId(String lendId, Integer status);
}
