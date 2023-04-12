package com.xqf.common.result;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Xqf
 * @version 1.0
 */

@Data
public class ResultResponse {

    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap<>();

    /**
     * 构造器私有
     */
    private ResultResponse() {
    }

    /**
     * 返回成功
     */
    public static ResultResponse ok() {
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setCode(ResponseEnum.SUCCESS.getCode());
        resultResponse.setMessage(ResponseEnum.SUCCESS.getMessage());
        return resultResponse;
    }

    /**
     * 返回失败
     */
    public static ResultResponse error() {
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setCode(ResponseEnum.ERROR.getCode());
        resultResponse.setMessage(ResponseEnum.ERROR.getMessage());
        return resultResponse;
    }

    /**
     * 返回特定对象
     */
    public static ResultResponse setResult(ResponseEnum responseEnum) {
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setCode(responseEnum.getCode());
        resultResponse.setMessage(responseEnum.getMessage());
        return resultResponse;
    }

    public ResultResponse code(Integer code) {
        this.setCode(code);
        return this;
    }

    public ResultResponse message(String message) {
        this.setMessage(message);
        return this;
    }

    public ResultResponse data(Map<String, Object> data) {
        this.setData(data);
        return this;
    }

    public ResultResponse data(String key, Object value) {
        this.getData().put(key, value);
        return this;
    }

}
