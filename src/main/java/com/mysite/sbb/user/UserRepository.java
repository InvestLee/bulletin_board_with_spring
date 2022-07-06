package com.mysite.sbb.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

//SiteUser의 PK의 타입은 Long이므로 JpaRepository<SiteUser, Long>으로 사용
public interface UserRepository extends JpaRepository<SiteUser, Long> {
	//데이터베이스에서 회원정보를 조회하여 스프링 시큐리티를 통해 로그인을 수행
	Optional<SiteUser> findByusername(String username); //UserSecurityService의 사용자 조회 기능을 위한 findByUsername 메서드
}