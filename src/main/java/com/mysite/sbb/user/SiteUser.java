package com.mysite.sbb.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser { 
//User대신 SiteUser 엔티티를 만든 이유는 스프링 시큐리티에 이미 User 클래스가 있고 패키지 오용으로 인한 오류가 발생할 수 있기 때문이다.
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true) //값 저장 중복 금지
	private String username;
	
	private String password;
	
	@Column(unique = true)
	private String email;
}
