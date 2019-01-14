package com.my.test.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.my.test.interceptor.AsyncInterceptor;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.my.test.controller"}, 
includeFilters=@ComponentScan.Filter(value={Controller.class}),
useDefaultFilters=false)
public class ServletConfig extends WebMvcConfigurerAdapter {
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/**").resourceChain(true);
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		registry.addInterceptor(new AsyncInterceptor());
		super.addInterceptors(registry);
	}

	
	@Bean
    public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager) {
		List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();
		// jtsl defualt view
		InternalResourceViewResolver defaultViewResolver = new InternalResourceViewResolver();
		defaultViewResolver.setPrefix("/WEB-INF/views/");
		defaultViewResolver.setSuffix(".jsp");
		defaultViewResolver.setViewClass(JstlView.class);
		viewResolvers.add(defaultViewResolver);
		
        ContentNegotiatingViewResolver contentViewResolver = new ContentNegotiatingViewResolver();
        contentViewResolver.setViewResolvers(viewResolvers);
        contentViewResolver.setContentNegotiationManager(manager);
        contentViewResolver.setOrder(0);
        return contentViewResolver;
	}
	
}
