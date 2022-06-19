package com.mysite.sbb;

//컴퓨터(localhost)가 웹 서버가 되어 8080포트를 실행

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller //HelloController 클래스가 컨트롤러의 기능을 수행
public class HelloController {
	@RequestMapping("/hello") //http://localhost:8080/hello URL요청이 발생하면 /hello URL과 hello 메서드를 매핑
	@ResponseBody //hello 메서드의 응답 결과가 문자열 그 자체임을 나타냄
	public String Hello() {
		return "Hello SBB";
	}
}

