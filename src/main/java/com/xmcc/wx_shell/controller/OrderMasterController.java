package com.xmcc.wx_shell.controller;

import com.google.common.collect.Maps;
import com.xmcc.wx_shell.common.ResultResponse;
import com.xmcc.wx_shell.dto.OrderMasterDto;
import com.xmcc.wx_shell.service.OrderMasterService;
import com.xmcc.wx_shell.utils.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("buyer/order")
@Api(value = "订单相关接口", description = "完成订单的增删改查")
public class OrderMasterController {
    @Autowired
    private OrderMasterService orderMasterService;

    //@Valid配合刚才在DTO上的JSR303注解完成校验
    //注意：JSR303的注解默认是在Contorller层进行校验
    //如果想在service层进行校验 需要使用javax.validation.Validator  也就是上个项目用到的工具

    @RequestMapping("create")
    @ApiOperation(value = "创建订单接口", httpMethod = "POST", response = ResultResponse.class)
    public ResultResponse create(
            @Valid @ApiParam(name = "订单对象", value = "传入json格式", required = true) OrderMasterDto orderMasterDto
            , BindingResult bindingResult){

        Map<String,String> map = Maps.newHashMap();

        //判断是否有参数校验问题
        if (bindingResult.hasErrors()){
            List<String> errList = bindingResult.getFieldErrors()
                    .stream().map(fieldError -> fieldError.getDefaultMessage()).collect(Collectors.toList());
            map.put("参数校验错误", JsonUtil.object2string(errList));
            return ResultResponse.fail(map);
        }
        return orderMasterService.insertOrder(orderMasterDto);
    }

    @RequestMapping("list")
    @ApiOperation(value = "订单列表", httpMethod = "GET", response = ResultResponse.class)
    public ResultResponse orderList(@RequestParam("openid") String openid, @RequestParam("page") Integer page, @RequestParam("size") Integer size){
        return orderMasterService.orderList(openid,page,size);
    }

    @RequestMapping("detail")
    @ApiOperation(value = "查询订单详情", httpMethod = "GET", response = ResultResponse.class)
    public ResultResponse detail(@RequestParam("openid") String openid, @RequestParam("orderId") String orderId){
        return orderMasterService.orderDetail(openid,orderId);
    }

    @RequestMapping("cancel")
    @ApiOperation(value = "取消订单", httpMethod = "POST", response = ResultResponse.class)
    public ResultResponse cancel(@RequestParam("openid") String openid, @RequestParam("orderId") String orderId){
        return orderMasterService.orderCancel(openid,orderId);
    }

}
