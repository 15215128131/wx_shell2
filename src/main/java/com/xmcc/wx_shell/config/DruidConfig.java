package com.xmcc.wx_shell.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DruidConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.druid")
    public DruidDataSource druidDataSource(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setProxyFilters(Lists.newArrayList(statFilter()));
        return druidDataSource;
    }

    //配置过滤数据
    @Bean
    public StatFilter statFilter(){
        StatFilter statFilter = new StatFilter();
        statFilter.setLogSlowSql(true);     //日志显示sql
        statFilter.setMergeSql(true);       //合并sql
        statFilter.setSlowSqlMillis(5);     //当前sql执行时间超过5秒，则认为是慢sql
        return statFilter;
    }

    //配置访问druid的路径
    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        //localhost:8888/sell/druid
        return new ServletRegistrationBean(new StatViewServlet(),"/druid/*");
    }

}
