package com.xmcc.wx_shell.service.impl;

import com.xmcc.wx_shell.common.ProductEnums;
import com.xmcc.wx_shell.common.ResultEnums;
import com.xmcc.wx_shell.common.ResultResponse;
import com.xmcc.wx_shell.dto.ProductCategoryDto;
import com.xmcc.wx_shell.dto.ProductInfoDto;
import com.xmcc.wx_shell.entity.ProductCategory;
import com.xmcc.wx_shell.entity.ProductInfo;
import com.xmcc.wx_shell.repository.ProductCategoryRepository;
import com.xmcc.wx_shell.repository.ProductInfoRepository;
import com.xmcc.wx_shell.service.ProductInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductInfoServiceImpl implements ProductInfoService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private ProductInfoRepository productInfoRepository;

    /**
     * 商品列表开发
     * @return
     */
    @Override
    public ResultResponse<List<ProductCategoryDto>> queryList() {

        List<ProductCategory> productCategoryList = productCategoryRepository.findAll();
        if (CollectionUtils.isEmpty(productCategoryList)){
            return ResultResponse.fail();
        }
        //利用流转换为dto集合
        List<ProductCategoryDto> productCategoryDtoList
                = productCategoryList.stream().map(productCategory -> ProductCategoryDto.build(productCategory)).collect(Collectors.toList());
        //获取类目编号集合
        List<Integer> typeList
                = productCategoryDtoList.stream().map(productCategoryDto -> productCategoryDto.getCategoryType()).collect(Collectors.toList());
        //根据typeList查询商品
        List<ProductInfo> productInfoList
                = productInfoRepository.findByProductStatusAndCategoryTypeIn(ProductEnums.PRODUCT_UP.getCode(), typeList);

        //对类目集合collect进行遍历 取出每个商品的类目编号 据此添加到对应的目录中
        List<ProductCategoryDto> productCategoryDtos = productCategoryDtoList.parallelStream().map(productCategoryDto -> {
            productCategoryDto.setProductInfoDtoList(productInfoList.stream()
                    .filter(productInfo -> productInfo.getCategoryType() == productCategoryDto.getCategoryType())
                    .map(productInfo -> ProductInfoDto.build(productInfo)).collect(Collectors.toList()));
            return productCategoryDto;
        }).collect(Collectors.toList());

        return ResultResponse.success(productCategoryDtos);
    }

    /**
     * 根据Id查询商品
     * @param productId
     * @return
     */
    @Override
    public ResultResponse<ProductInfo> queryById(String productId) {
        //参数检查
        if (StringUtils.isBlank(productId)){
            return ResultResponse.fail(ResultEnums.PRODUCT_ERROR.getMsg()+":"+productId);
        }

        //Optional：一个容器对象，它可能包含也可能不包含非空值。如果存在一个值，isPresent()将返回true, get()将返回值。
        Optional<ProductInfo> byId = productInfoRepository.findById(productId);
        if (!byId.isPresent()){
            return ResultResponse.fail(productId+":"+ResultEnums.NOT_EXITS.getMsg());
        }
        //判断商品的存在状态
        ProductInfo productInfo = byId.get();
        if (productInfo.getProductStatus()== ProductEnums.PRODUCT_NOT_ENOUGH.getCode()){
            return ResultResponse.fail(productId+":"+ProductEnums.PRODUCT_DOWN.getMsg());
        }

        return ResultResponse.success(productInfo);
    }

    /**
     * 修改商品信息（库存）
     * @param productInfo
     */
    @Override
    public void updateProduct(ProductInfo productInfo) {
        productInfoRepository.save(productInfo);
    }
}
