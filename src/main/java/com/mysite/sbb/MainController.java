package com.mysite.sbb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//URL 매핑을 추가하기 위한 컨트롤러

@Controller //이 클래스는 스프링부트의 컨트롤러가 된다.
public class MainController {
	
	@RequestMapping("/sbb") //요청한 URL과의 매핑(서버에 요청이 발생하면 스프링부트는 요청 페이지와 매핑되는 메서드를 컨트롤러에서 찾는다.)
	@ResponseBody //URL요청에 대한 응답을 문자열로 리턴(얘가 없을 경우 파일을 찾음)
	public String index() {
		return "안녕하세요 sbb에 오신것을 환영합니다.";
	}

	@RequestMapping("/")
	public String root() {
		return "redirect:/question/list";
		//redirect:<URL> - 완전히 새로운 URL로 요청
		//forward:<URL> - 기존 요청 값들을 유지된 상태로 URL이 전환
	}
}
