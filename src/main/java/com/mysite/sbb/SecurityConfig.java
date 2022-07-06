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
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity; 
//로그인 하지 않은 상태에서 질문 등록 버튼을 누르면 로그인 화면으로 이동 
//로그인 후에 원래 하려고 했던 페이지로 리다이렉트 시키는 스프링 시큐리티 기능

import com.mysite.sbb.user.UserSecurityService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration //스프링의 환경설정 파일임을 나타냄(스프링 시큐리티 설정 용도)
@EnableWebSecurity //모든 요청 URL이 스프링 시큐리티의 제어를 받도록 함(내부적으로 SpringSecurityFilterChain 동작하여 URL필터 적용)
@EnableGlobalMethodSecurity(prePostEnabled = true) //QuestionController와 AnswerController에서 로그인 여부 판별을 위해 사용했던 @PreAuthorize를 사용하기 위해 반드시 필요
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
	private final UserSecurityService userSecurityService;
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/**").permitAll() //모든 인증되지 않은 요청을 허락
        	.and() //http 객체의 설정을 이어서 할 수 있게 하는 메서드
            	.csrf().ignoringAntMatchers("/h2-console/**") //스프링 시큐리티가 CSRF 처리시 H2 콘솔을 예외, CSRF(Cross site request forgery)는 웹 사이트 취약점 공격을 방지를 위해 사용하는 기술
            .and() //(스프링 시큐리티가 CSRF 토큰 값을 세션을 통해 발행하고 웹 페이지에서는 폼 전송 시 해당 토큰을 함께 전송하여 실제 웹 페이지에서 작성된 데이터가 전달되는지를 검증하는 기술), H2 콘솔은 CSRF 토큰을 발행하는 기능이 없음
            	.headers()
            	.addHeaderWriter(new XFrameOptionsHeaderWriter( //H2 콘솔의 화면이 frame 구조이므로 clickjacking 방지를 위해 X-Frame-Options 헤더값을 사용하여 스프링 시큐리티는 사이트의 콘텐츠가 다른 사이트에 포함되는 것을 방지(접속 불가능)
            			XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)) //X-Frame-Options 헤더값을 sameorigin으로 설정하여 오류 발생 방지(frame에 포함된 페이지가 페이지를 제공하는 사이트와 동일한 경우 정상 동작)
        	.and()
        		.formLogin()
        		.loginPage("/user/login") //로그인 설정을 담당하는 부분으로 로그인 페이지 url
        		.defaultSuccessUrl("/") //로그인 성공시 디폴트 페이지로 이동
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
	
	
    //PasswordEncoder를 @Bean으로 등록, UserService 참조
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}