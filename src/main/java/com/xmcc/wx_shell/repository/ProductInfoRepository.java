package com.xmcc.wx_shell.repository;

import com.xmcc.wx_shell.entity.ProductCategory;
import com.xmcc.wx_shell.entity.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//泛型参数一：当前类对应的实体类类型；    泛型参数二：其实体类中主键的类型
public interface ProductInfoRepository extends JpaRepository<ProductInfo,String> {
    //根据类目编号和状态查询
    List<ProductInfo> findByProductStatusAndCategoryTypeIn(Integer status, List<Integer> typeList);
}
