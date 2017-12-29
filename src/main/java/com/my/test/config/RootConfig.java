package com.my.test.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Configuration
@Import({PropertiesConfig.class, DBConfig.class})
@ComponentScan(
	basePackages = {"com.my.test.*"}, 
	includeFilters=@ComponentScan.Filter(value={Service.class, Repository.class}),
	useDefaultFilters=false
)
public class RootConfig {
}
