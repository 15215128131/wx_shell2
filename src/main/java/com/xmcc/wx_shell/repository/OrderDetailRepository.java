package com.xmcc.wx_shell.repository;

import com.xmcc.wx_shell.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
    List<OrderDetail> findByOrderIdIn(String orderId);

//    void deleteByOrderId(String orderId);
}
