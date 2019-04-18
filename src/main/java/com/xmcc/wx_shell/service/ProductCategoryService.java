package com.xmcc.wx_shell.service;

import com.xmcc.wx_shell.common.ResultResponse;
import com.xmcc.wx_shell.dto.ProductCategoryDto;

import java.util.List;

public interface ProductCategoryService {
    ResultResponse<List<ProductCategoryDto>> findAll();
}
