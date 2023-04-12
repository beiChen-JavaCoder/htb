package com.xqf.srb.sms.service;

import java.util.Map;

/**
 * @author Xqf
 * @version 1.0
 */
public interface SmsService {
    /**
     * @param mobile 手机号
     * @param templateCode 验证参数
     * @param param 验证码
     */
    void send(String mobile, String templateCode, Map<String, Object> param);
}
