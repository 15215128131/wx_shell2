package com.xmcc.wx_shell.repository;

import com.xmcc.wx_shell.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

//泛型参数一：当前类对应的实体类类型；    泛型参数二：其实体类中主键的类型
public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Integer> {
}
