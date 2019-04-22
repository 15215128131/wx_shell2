package com.xmcc.wx_shell.dto;

import com.xmcc.wx_shell.entity.OrderDetail;
import com.xmcc.wx_shell.entity.OrderMaster;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("订单详情参数实体类")   //swagger 参数的描述信息
public class OrderMasterDetailDto extends OrderMaster implements Serializable {

    @ApiModelProperty(value = "买家订单项详情", dataType = "List")
    private List<OrderDetail> orderDetailList;

    //转换成Dto
    public static OrderMasterDetailDto build(OrderMaster orderMaster){
        OrderMasterDetailDto dto = new OrderMasterDetailDto();
        BeanUtils.copyProperties(orderMaster, dto);
        return dto;
    }
}
