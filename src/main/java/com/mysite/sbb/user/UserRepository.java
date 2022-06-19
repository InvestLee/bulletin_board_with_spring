package com.mysite.sbb.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

//PK의 타입 = Long
public interface UserRepository extends JpaRepository<SiteUser, Long> {
	Optional<SiteUser> findByusername(String username); //UserSecurityService의 사용자 조회 기능을 위한 findByUsername 메서드
}