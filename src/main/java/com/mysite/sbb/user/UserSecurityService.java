package com.mysite.sbb.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {
	//스프링 시큐리티의 UserDetailsService는 loadUserByUsername 메서드를 구현하도록 하는 인터페이스
    private final UserRepository userRepository;

    @Override
    //loadUserByUsername 메서드는 사용자 명으로 비밀번호를 조회하여 리던하는 메서드
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //사용자명으로 SiteUser 객체 조회
    	Optional<SiteUser> _siteUser = this.userRepository.findByusername(username);
        if (_siteUser.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        SiteUser siteUser = _siteUser.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        //사용자 명이 admin일 경우 ADMIN 권한 부여 아니면 User의 권한 부여
        if ("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }
        //사용자명, 비밀번호, 권한을 입력으로 스프링 시큐리티 User 객체 생성
        return new User(siteUser.getUsername(), siteUser.getPassword(), authorities);
        //스프링 시큐리티는 loadUserByUsername 메서드에 의해 리턴된 User 객체의 비밀번호가 화면으로부터 입력받은 비밀번호와 일치하는지를 검사하는 로직
    }
}