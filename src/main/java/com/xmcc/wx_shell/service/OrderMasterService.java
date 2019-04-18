package com.xmcc.wx_shell.service;

import com.xmcc.wx_shell.common.ResultResponse;
import com.xmcc.wx_shell.dto.OrderMasterDto;

public interface OrderMasterService {
    ResultResponse insertOrder(OrderMasterDto orderMasterDto);
}
