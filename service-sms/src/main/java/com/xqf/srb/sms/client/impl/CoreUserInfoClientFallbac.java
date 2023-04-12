package com.xqf.srb.sms.client.impl;

import com.xqf.srb.sms.client.CoreUserInfoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Xqf
 * @version 1.0
 */
@Service
@Slf4j
public class CoreUserInfoClientFallbac implements CoreUserInfoClient {
    @Override
    public boolean checkMobile(String mobile) {
        log.error("远程调用失败，服务熔断");
        //降低后台容错,提高用户体验
        return false;
    }
}
