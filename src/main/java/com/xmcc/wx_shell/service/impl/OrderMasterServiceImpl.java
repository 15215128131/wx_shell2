package com.xmcc.wx_shell.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xmcc.wx_shell.common.*;
import com.xmcc.wx_shell.dao.AbstractBatchDao;
import com.xmcc.wx_shell.dto.OrderDetailDto;
import com.xmcc.wx_shell.dto.OrderMasterDetailDto;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *  订单业务层开发
 */
@Service
public class OrderMasterServiceImpl extends AbstractBatchDao<OrderMaster> implements OrderMasterService {

    @Autowired
    private ProductInfoService productInfoService;
    @Autowired
    private OrderMasterRepository orderMasterRepository;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

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

    /**
     * 订单列表开发
     * @param openid
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResultResponse orderList(String openid, Integer page, Integer size) {
        //TODO：参数验证
        if (StringUtils.isBlank(openid)){
            throw new CustomException(ResultResponse.fail(OrderEnums.OPENID_ERROR.getMsg()).getMsg());
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<List<OrderMaster>> pageList = orderMasterRepository.findByBuyerOpenidIn(openid, pageable);

        return ResultResponse.success(pageList);
    }

    /**
     * 查询订单详情
     * @param openid
     * @param orderId
     * @return
     */
    @Override
    public ResultResponse orderDetail(String openid, String orderId) {
        //TODO：参数验证2
        if (StringUtils.isBlank(openid)){
            throw new CustomException(ResultResponse.fail(OrderEnums.OPENID_ERROR.getMsg()).getMsg());
        }
        if (StringUtils.isBlank(orderId)){
            throw new CustomException(ResultResponse.fail(OrderEnums.ORDER_ERROR.getMsg()).getMsg());
        }

        //查询出订单信息
        Optional<OrderMaster> orderMasterOptional = orderMasterRepository.findById(orderId);
        if (!orderMasterOptional.isPresent()){
            return ResultResponse.fail(OrderEnums.ORDER_NOT_EXITS.getMsg());
        }
        OrderMasterDetailDto orderMasterDetailDto = OrderMasterDetailDto.build(orderMasterOptional.get());

        //查询出对应的所有的订单项信息
        List<OrderDetail> byOrderidIn = orderDetailRepository.findByOrderIdIn(orderId);

        //将订单项信息装入订单信息
        orderMasterDetailDto.setOrderDetailList(byOrderidIn);

        return ResultResponse.success(orderMasterDetailDto);
    }

    /**
     * 取消订单
     * @param openid
     * @param orderId
     * @return
     */
    @Override
    @Transactional
    public ResultResponse orderCancel(String openid, String orderId) {
        if (StringUtils.isBlank(openid)){
            throw new CustomException(ResultResponse.fail(OrderEnums.OPENID_ERROR.getMsg()).getMsg());
        }
        if (StringUtils.isBlank(orderId)){
            throw new CustomException(ResultResponse.fail(OrderEnums.ORDER_ERROR.getMsg()).getMsg());
        }

        //查看订单是否存在
        Optional<OrderMaster> orderMasterOptional = orderMasterRepository.findById(orderId);
        if (!orderMasterOptional.isPresent()){
            return ResultResponse.fail(OrderEnums.ORDER_NOT_EXITS.getMsg());
        }
        List<OrderMaster> orderMasterList = orderMasterRepository.findByOrderIdAndOrderStatusIn(orderId, OrderEnums.NEW.getCode());
        if (orderMasterList.size()==0){
            return ResultResponse.fail(OrderEnums.FINSH_CANCEL.getMsg());
        }

        //先删除订单中的订单项
//        orderDetailRepository.deleteByOrderId(orderId);
        //删除订单（改变订单存在状态）
        List<OrderMaster> collect = orderMasterList.stream()
                .filter(orderMaster -> orderMaster.getOrderStatus()==OrderEnums.NEW.getCode())
                .map(orderMaster -> {orderMaster.setOrderStatus(OrderEnums.CANCEL.getCode());return orderMaster;})
                .collect(Collectors.toList());

        super.batchInsert(collect);

        return ResultResponse.success();
    }


}
