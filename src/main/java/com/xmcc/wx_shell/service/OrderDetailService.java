package com.xmcc.wx_shell.service;

import com.xmcc.wx_shell.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    void batchInsert(List<OrderDetail> orderDetailList);
}
