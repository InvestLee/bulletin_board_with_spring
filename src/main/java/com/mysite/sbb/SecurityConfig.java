package com.mysite.sbb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.mysite.sbb.user.UserSecurityService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration //스프링의 환경설정 파일임을 나타냄(스프링 시큐리티 설정 용도)
@EnableWebSecurity //모든 요청 URL이 스프링 시큐리티의 제어를 받도록 함(내부적으로 SpringSecurityFilterChain 동작하여 URL필터 적용)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
	private final UserSecurityService userSecurityService;
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/**").permitAll() //모든 인증되지 않은 요청을 허락
        	.and() //http 객체의 설정을 이어서 할 수 있게 하는 메서드
            	.csrf().ignoringAntMatchers("/h2-console/**") //스프링 시큐리티가 CSRF 처리시 H2 콘솔을 예외
            .and()
            	.headers()
            	.addHeaderWriter(new XFrameOptionsHeaderWriter( //H2 콘솔의 화면이 frame 구조이므로 clickjacking 방지를 위한 X-Frame-Options 동작(접속 불가능)
            			XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)) //X-Frame-Options 헤더값을 sameorigin으로 설정하여 오류 발생 방지(frame에 포함된 페이지가 페이지를 제공하는 사이트와 동일한 경우 정상 동작)
        	.and()
        		.formLogin()
        		.loginPage("/user/login") //로그인 페이지 url
        		.defaultSuccessUrl("/") //로그인 성공시 이동
        	.and()
        		.logout()
        		.logoutRequestMatcher(new AntPathRequestMatcher("/user/logout")) //로그아웃 URL 설정
        		.logoutSuccessUrl("/") //로그아웃 성공 시 기본 페이지로 이동
        		.invalidateHttpSession(true) //로그아웃시 생성된 사용자 세션 삭제
            ;
    }
    
	//AuthenticationManagerBuilder 객체(auth)를 입력으로 하는 configure 메서드를 오버라이딩
	//AuthenticationManagerBuilder 스프링 시큐리티의 인증을 담당
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//auth 객체에 UserSecurityService를 등록하여 사용자 조회를 UserSecurityService가 담당
		//비밀번호 검증에 사용할 passwordEncoder도 함께 등록
		auth.userDetailsService(userSecurityService).passwordEncoder(passwordEncoder());
	}
	
	
    //PasswordEncoder를 @Bean으로 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}