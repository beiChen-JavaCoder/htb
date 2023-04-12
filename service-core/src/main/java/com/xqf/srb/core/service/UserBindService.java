package com.xqf.srb.core.service;

import com.xqf.srb.core.pojo.entity.UserBind;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xqf.srb.core.pojo.vo.UserBindVO;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author xqf
 * @since 2023-01-10
 */
public interface UserBindService extends IService<UserBind> {

    /**
     * 提交表单信息，返回动态html页面
     * @param userBindVO 绑定信息
     * @param userId 绑定用户id
     * @return 动态页面
     */
    String commitBindUser(UserBindVO userBindVO, Long userId);

    /**
     * 校验签名成功后修改绑定状态
     * @param paramMap
     */
    void notify(Map<String, Object> paramMap);

    /**
     * 根据userId查询bindCode
     * @param UserId
     * @return
     */
    String getBindCodeByUserId(Long UserId);
}
