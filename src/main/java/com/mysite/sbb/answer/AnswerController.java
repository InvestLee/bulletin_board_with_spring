package com.mysite.sbb.answer;

import javax.validation.Valid;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
//RequestParam String content : 템플릿에서 답변으로 입력한 내용(Content)을 얻기 위해 추가, textarea(템플릿의 답변 내용)의 name 속성명이 content이므로 변수명을 contect로 사용(다른 이름으로 사용할시 오류 발생)
import org.springframework.web.bind.annotation.RequestParam; 
import org.springframework.validation.BindingResult;

@RequestMapping("/answer") //URL 프리픽스 /answer로 고정
@RequiredArgsConstructor
@Controller
public class AnswerController {
	
	private final QuestionService questionService;
	private final AnswerService answerService; //답변을 저장하기 위한 답변 서비스 클래스

	//PostMapping은 @RequestMapping과 동일하게 매핑을 담당하지만 POST요청만 받아들일 경우에 사용, GET방식으로 요청할 경우 오류 발생
	@PostMapping("/create/{id}") // /answer/create/{id}와 같은 URL 요청시 createAnswer 메서드 호출되도록 PostMapping, {id}는 질문의 id
    public String createAnswer(Model model, @PathVariable("id") Integer id, 
            @Valid AnswerForm answerForm, BindingResult bindingResult) {
        Question question = this.questionService.getQuestion(id);
        if (bindingResult.hasErrors()) {
            //검증에 실패할 경우에는 다시 답변을 등록할 수 있는 question_detail 템플릿을 랜더링
        	//question_detail 템플릿은 Question 객체가 필요하므로 model 객체에 Question 객체를 저장한 후에 question_detail 템플릿을 랜더링
        	model.addAttribute("question", question);
            return "question_detail";
        }
        this.answerService.create(question, answerForm.getContent()); //답변 저장
        return String.format("redirect:/question/detail/%s", id);
    }
}
