package com.xqf.srb.sms;

import cn.hutool.core.util.RandomUtil;
import com.xqf.srb.sms.util.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Xqf
 * @version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class text {

    @Test
    public void text01() {


        log.info(SmsProperties.KEY_ID);
        log.info(SmsProperties.KEY_SECRET);
        log.info(SmsProperties.REGION_Id);
        log.info(SmsProperties.SIGN_NAME);
        log.info(SmsProperties.TEMPLATE_CODE);
    }
    @Test
    public void testRandom(){
        for (int i = 0; i <100; i++) {
            log.info( RandomUtil.randomNumbers(6));
        }


    }
}
