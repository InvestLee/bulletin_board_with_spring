package com.mysite.sbb.user;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.dao.DataIntegrityViolationException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/signup") // /user/signup URL이 GET으로 요청되면 회원 가입을 위한 템플릿을 렌더링
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup") // POST로 요청되면 회원가입 진행
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) { //비밀번호, 비밀번호 확인이 동일한지 검증
            bindingResult.rejectValue("password2", "passwordInCorrect", 
                    "2개의 패스워드가 일치하지 않습니다."); // 동일하지 않으면 bindingResult.rejectValue(필드명, 오류코드, 에러메세지)사용하여 오류 발생
            return "signup_form";
        }
        
        
        try {
        	userService.create(userCreateForm.getUsername(), 
        			userCreateForm.getEmail(), userCreateForm.getPassword1());
        }catch(DataIntegrityViolationException e) {
        	//기존 사용자와 동일한 사용한ID 또는 동일한 이메일 주소인 경우 SiteUser.jave의 unique=true 설정으로 DataIntegrityViolationException이 발생
        	//다른 오류의 경우에는 e.getMessage()로 출력
        	//bindingResult.reject(오류코드, 오류메시지)
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }
        
        return "redirect:/";
    }
    
    @GetMapping("/login")
    public String login() {
    	return "login_form"; //login_form.html 템플릿을 렌터링하는 GET방식의 login 메서드
    } //실제 로그인을 진행하는 @PostMapping 방식의 메서드는 스프링 시큐리티가 대신 처리하므로 직접 구현할 필요 없음
}