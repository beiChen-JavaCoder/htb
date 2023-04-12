package com.xqf.srb.sms.client;

import com.xqf.srb.sms.client.impl.CoreUserInfoClientFallbac;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Xqf
 * @version 1.0
 */
@FeignClient(value = "service-core",fallback = CoreUserInfoClientFallbac.class)
public interface CoreUserInfoClient {

    @ApiOperation("校验手机号是否注册")
    @GetMapping("/api/core/userInfo/checkMobile/{mobile}")
    public boolean checkMobile(@ApiParam("手机号")
                               @PathVariable String mobile);
}
