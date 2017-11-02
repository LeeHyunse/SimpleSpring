package com.my.test.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.my.test.dao.MemberDao;
import com.my.test.model.Member;

@Service
public class MemberService {
	
	private static final Logger logger = LoggerFactory.getLogger(MemberService.class);
	
	@Autowired
	private MemberDao memberDao;
	
	public void testTransaction0() throws Exception {
		Member member = new Member();
		member.setName("마해영");
		member.setAge("20");
		memberDao.insertMember(member);
	}
	
	@Transactional(value = "transactionManager", rollbackFor = {Exception.class})
	public void testTransaction1() throws Exception {
		Member member = new Member();
		member.setName("박찬호");
		member.setAge("25");
		
		memberDao.insertMember(member);
		
		Member findMember = memberDao.selectMemberByName("박찬호");
		logger.info("박찬호 찾았나요? {}", findMember.toString());
		throw new Exception("고의적 예외 발생");
	}
	
	public void testTransaction2() throws Exception {
		Member member = new Member();
		member.setName("박찬호");
		member.setAge("25");
		
		memberDao.insertMember(member);
		
		Member findMember = memberDao.selectMemberByName("박찬호");
		logger.info("박찬호 찾았나요? {}", findMember.toString());
		throw new Exception("고의적 예외 발생");
	}
	
}
