package com.my.test.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.my.test.model.Member;

@Repository
public class MemberDao {

	@Qualifier("sqlSession")
	@Autowired
	private SqlSessionTemplate sqlSession;
	 
	private static final String MAPPER_NAMESPACE = "mapper.memberMapper.";
	 
	public List<Member> selectMember() {
		return sqlSession.selectList(MAPPER_NAMESPACE + "selectMember");
	}
	
	public Member selectMemberByName(String name) {
		return sqlSession.selectOne(MAPPER_NAMESPACE + "selectMemberByName", name);
	}
	
	public void insertMember(Member member) {
		sqlSession.insert(MAPPER_NAMESPACE + "insertMember", member);
	}
}
