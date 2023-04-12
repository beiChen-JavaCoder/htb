package com.heepay.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * UserBind
 *
 * 封装航汇通向航投宝发起的具体的请求地址和参数积分
 * </p>
 *
 * @author qy
 */
@Data
public class NotifyVo implements Serializable {
	
	private static final long serialVersionUID = 1L;


	private String notifyUrl;

	private Map<String, Object> paramMap;

	public NotifyVo() {}

	public NotifyVo(String notifyUrl, Map<String, Object> paramMap) {
		this.notifyUrl = notifyUrl;
		this.paramMap = paramMap;
	}
}

