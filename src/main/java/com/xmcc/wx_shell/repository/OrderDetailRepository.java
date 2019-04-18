package com.xmcc.wx_shell.repository;

import com.xmcc.wx_shell.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
}
