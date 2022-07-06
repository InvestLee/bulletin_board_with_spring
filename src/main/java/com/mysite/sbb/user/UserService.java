package com.mysite.sbb.user;

//BCryptPasswordEncoder는 BCrypt 해심 함수를 사용하여 비밀번호를 암호화한다.
//BCryptPasswordEncoder 객체를 직접 new로 생성하는 방식보다 PasswordEncoder(BCryptPasswordEncoder의 인터페이스) 빈(bean)으로 등록하여 사용
//Because 직접 생성하는 경우 암호화 방식을 변경하면 BCryptPasswordEncoder를 사용한 모든 프로그램을 일일이 찾아서 수정해야된다.
//PasswordEncoder 빈(bean)을 만들기 위해 @Configuration이 적용된 SecurityConfig에 @Bean 메서드를 생성한다.(PasswordEncoder는 BCryptPasswordEncoder의 인터페이스)
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import com.mysite.sbb.DataNotFoundException;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    //User 데이터 생성
    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); //BCryptPasswordEncoder객체를 직접 생성하여 사용하지 않고 빈으로 등록한 PasswordEncoder 객체 주입
        this.userRepository.save(user);
        return user;
    }
    
    //UserRepository에 이미 findByusername을 선언
    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }
}