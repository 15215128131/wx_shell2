package com.xmcc.wx_shell.repository;

import com.xmcc.wx_shell.entity.OrderMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderMasterRepository extends JpaRepository<OrderMaster,String> {
}
