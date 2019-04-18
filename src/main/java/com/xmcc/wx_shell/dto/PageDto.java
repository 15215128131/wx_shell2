package com.xmcc.wx_shell.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("订单列表参数实体类")
public class PageDto implements Serializable {

    @NotBlank(message = "必须输入用户的微信openid")
    @ApiModelProperty(value = "买家微信openid",dataType = "String")     //swagger 参数的描述信息
    private String openid;

    @NotNull(message = "页面参数有误")
    @Min(value = 0,message = "从第0页开始")
    @ApiModelProperty(value = "页面参数",dataType = "Integer")     //swagger 参数的描述信息
    private Integer page;

    @NotNull(message = "每页显示数量不能小于1")
    @Min(value = 1,message = "每页显示数量不能小于1")
    @ApiModelProperty(value = "页面偏移量",dataType = "Integer")     //swagger 参数的描述信息
    private Integer size;

}
