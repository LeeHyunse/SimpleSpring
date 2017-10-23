package com.my.test.controller;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.my.test.dao.MemberDao;
import com.my.test.model.Member;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private MemberDao memberDao;
	
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.debug("로그출력 debug");
		logger.info("로그출력 info");
		logger.warn("로그출력 warn");
		logger.error("로그출력 error");
		
		List<Member> memberList = memberDao.selectMember();
		for(Member member : memberList) {
			logger.info("member({}) name={}, age={}", member.getNo(), member.getName(), member.getAge());
		}
		
		
		return "home";
	}
}
