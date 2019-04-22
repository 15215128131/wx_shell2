package com.xmcc.wx_shell.repository;

import com.xmcc.wx_shell.entity.OrderMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderMasterRepository extends JpaRepository<OrderMaster,String> {
    Page<List<OrderMaster>> findByBuyerOpenidIn(String buyer_Openid, Pageable pageable);

    List<OrderMaster> findByOrderIdAndOrderStatusIn(String order_id, Integer order_status);

}
