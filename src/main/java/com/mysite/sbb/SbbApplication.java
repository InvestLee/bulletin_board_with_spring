package com.mysite.sbb;

//스프링부트 에플리케이션의 시작을 담당하는 파일
//스프링부트 프로젝트 생성시 자동 생성

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication //스프링부트의 모든 설정을 관리
public class SbbApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbbApplication.class, args);
	}

}
