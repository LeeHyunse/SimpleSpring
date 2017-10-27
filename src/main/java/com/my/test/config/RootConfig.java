package com.my.test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PropertiesConfig.class, DBConfig.class})
public class RootConfig {
}
