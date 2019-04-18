package com.xmcc.wx_shell.service.impl;

import com.xmcc.wx_shell.dao.AbstractBatchDao;
import com.xmcc.wx_shell.entity.OrderDetail;
import com.xmcc.wx_shell.service.OrderDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单项业务类
 */
@Service
public class OrderDetailServiceImpl extends AbstractBatchDao<OrderDetail> implements OrderDetailService {

    /**
     * 批量插入
     */
    @Override
    @Transactional      //开启事务管理
    public void batchInsert(List<OrderDetail> orderDetailList) {
        super.batchInsert(orderDetailList);
    }

}
