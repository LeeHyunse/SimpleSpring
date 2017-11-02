package com.my.test.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.stereotype.Repository;

@Configuration
@Import({PropertiesConfig.class, DBConfig.class})
@ComponentScan(basePackages = {"com.my.test.dao"},
includeFilters = @Filter(value = {Repository.class}), 
useDefaultFilters = false
)
public class RootConfig {
}
