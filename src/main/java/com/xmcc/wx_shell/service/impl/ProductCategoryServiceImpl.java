package com.xmcc.wx_shell.service.impl;

import com.xmcc.wx_shell.common.ResultResponse;
import com.xmcc.wx_shell.dto.ProductCategoryDto;
import com.xmcc.wx_shell.service.ProductCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Override
    public ResultResponse<List<ProductCategoryDto>> findAll() {
        return null;
    }
}
