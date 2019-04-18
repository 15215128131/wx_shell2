package com.xmcc.wx_shell.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xmcc.wx_shell.common.*;
import com.xmcc.wx_shell.dto.OrderDetailDto;
import com.xmcc.wx_shell.dto.OrderMasterDto;
import com.xmcc.wx_shell.entity.OrderDetail;
import com.xmcc.wx_shell.entity.OrderMaster;
import com.xmcc.wx_shell.entity.ProductInfo;
import com.xmcc.wx_shell.repository.OrderDetailRepository;
import com.xmcc.wx_shell.repository.OrderMasterRepository;
import com.xmcc.wx_shell.service.OrderDetailService;
import com.xmcc.wx_shell.service.OrderMasterService;
import com.xmcc.wx_shell.service.ProductInfoService;
import com.xmcc.wx_shell.utils.BigDecimalUtil;
import com.xmcc.wx_shell.utils.CustomException;
import com.xmcc.wx_shell.utils.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  订单业务层开发
 */
@Service
public class OrderMasterServiceImpl implements OrderMasterService {

    @Autowired
    private ProductInfoService productInfoService;
    @Autowired
    private OrderMasterRepository orderMasterRepository;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     *  订单业务开发
     * @param orderMasterDto
     * @return
     */
    @Override
    public ResultResponse insertOrder(OrderMasterDto orderMasterDto) {

        //获得前台传来的订单中的所有订单项（商品部分信息）
        List<OrderDetailDto> items = orderMasterDto.getItems();
        //创建订单detail 集合 将符合的放入其中 待会批量插入
        List<OrderDetail> orderDetailList = Lists.newArrayList();
        //创建订单总金额为0  涉及到钱的都用 高精度计算
        BigDecimal totalPrice = new BigDecimal(0);


        //根据订单项items中的商品编号，所得所有商品信息
        for (OrderDetailDto item : items){
            ResultResponse<ProductInfo> resultResponse = productInfoService.queryById(item.getProductId());

            //TODO:code是怎么传值的？？？
            //说明该商品未查询到 生成订单失败，因为这儿涉及到事务 需要抛出异常 事务机制是遇到异常才会回滚
            if (resultResponse.getCode() == ResultEnums.FAIL.getCode()){
                throw new CustomException(resultResponse.getMsg());
            }

            //获得查询的商品
            ProductInfo productInfo = resultResponse.getData();

            //商品库存不足 订单生成失败 直接抛出异常 事务回滚
            if (productInfo.getProductStock()<item.getProductQuantity()){
                throw new CustomException(ProductEnums.PRODUCT_NOT_ENOUGH.getMsg());
            }

            //根据将前台传入的订单项DTO与数据库查询到的 商品数据组装成OrderDetail 放入集合中  @builder
            OrderDetail orderDetail = OrderDetail.builder()
                    .detailId(IDUtils.createIdbyUUID()).productId(item.getProductId())
                    .productName(productInfo.getProductName()).productPrice(productInfo.getProductPrice())
                    .productQuantity(productInfo.getProductStock())
                    .productIcon(productInfo.getProductIcon()).build();

            orderDetailList.add(orderDetail);

            //减少商品库存数量
            productInfo.setProductStock(productInfo.getProductStock()-item.getProductQuantity());
            productInfoService.updateProduct(productInfo);

            //计算价格  总价 = 之前的总价 + 这次订单项的总价；  这次订单项的总价 = 单价 × 数量
            totalPrice = BigDecimalUtil.add(totalPrice, BigDecimalUtil.multi(productInfo.getProductPrice(), item.getProductQuantity()));

        }


        //生成订单id
        String orderId = IDUtils.createIdbyUUID();

        //将生成的订单id，设置到订单项中
        List<OrderDetail> detailList = orderDetailList.stream().map(order_Detail -> {
            order_Detail.setOrderId(orderId);
            return order_Detail;
        }).collect(Collectors.toList());

        //构建订单信息  日期等都用默认的即可
        OrderMaster orderMaster = OrderMaster.builder()
                .orderId(orderId).buyerName(orderMasterDto.getName())
                .buyerPhone(orderMasterDto.getPhone()).buyerAddress(orderMasterDto.getAddress())
                .buyerOpenid(orderMasterDto.getOpenid()).orderAmount(totalPrice)
                .orderStatus(OrderEnums.NEW.getCode()).payStatus(PayEnums.WAIT.getCode())
                .build();

        //插入订单项
        orderDetailService.batchInsert(detailList);

        //插入订单
        orderMasterRepository.save(orderMaster);

        //按照前台要求的数据结构传入
        HashMap<String, String> map = Maps.newHashMap();
        map.put("orderId", orderId);
        return ResultResponse.success(map);
    }
}
