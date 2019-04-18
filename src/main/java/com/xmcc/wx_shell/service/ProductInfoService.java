package com.xmcc.wx_shell.service;

import com.xmcc.wx_shell.common.ResultResponse;
import com.xmcc.wx_shell.entity.ProductInfo;

public interface ProductInfoService {
    ResultResponse queryList();

    ResultResponse<ProductInfo> queryById(String productId);

    void updateProduct(ProductInfo productInfo);

}
