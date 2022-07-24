package com.macro.mall.tiny.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 * Created by macro on 2019/4/8.
 */
@Configuration
@MapperScan(value = {"com.macro.mall.tiny.dao", "com.macro.mall.tiny.mbg.mapper"})
public class MyBatisConfig {
}
