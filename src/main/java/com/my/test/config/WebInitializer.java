package com.my.test.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		Class<?>[] rootConfigArr = {RootConfig.class};
		return rootConfigArr;
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		Class<?>[] servletConfigArr = {ServletConfig.class};
		return servletConfigArr;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}

}
