package com.xmcc.wx_shell.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_category")
@Entity
public class ProductCategory implements Serializable {
    /** 类目id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //设置主键的生成策略
    private Integer categoryId;

    /** 类目名字. */
    private String categoryName;

    /** 类目编号. */
    private Integer categoryType;

    private Date createTime;

    private Date updateTime;
}
