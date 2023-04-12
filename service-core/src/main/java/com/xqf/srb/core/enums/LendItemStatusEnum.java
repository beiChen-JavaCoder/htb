package com.xqf.srb.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Xqf
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum LendItemStatusEnum {

    DEFAULT(0, "默认"),
    REPAYMENT_OK(2, "已还款"),
    PAY_OK(1, "已支付");

    private Integer status;
    private String msg;


    public static String getMsgByStatus(int status) {
        LendStatusEnum arrObj[] = LendStatusEnum.values();
        for (LendStatusEnum obj : arrObj) {
            if (status == obj.getStatus().intValue()) {
                return obj.getMsg();
            }
        }
        return "";
    }
}
