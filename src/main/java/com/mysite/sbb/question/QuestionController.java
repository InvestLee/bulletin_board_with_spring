package com.mysite.sbb.question;

import java.util.List;
import javax.validation.Valid;

import com.mysite.sbb.answer.AnswerForm;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; //질문 목록을 조회하기 위해 Question 리포지터리를 사용해야 하고 조회한 질문 목록은 Model 클래스를 사용하여 템플릿에 전달
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;

import lombok.RequiredArgsConstructor;

@RequestMapping("/question") //URL에 대한 mapping
@RequiredArgsConstructor //questionService 속성을 포함하는 생성자를 생성, final이 붙은 속성을 포함하는 생성자를 자동으로 생성하는 역할
@Controller
public class QuestionController {
	
	private final QuestionService questionService; //생성자 주입 DI (컨트롤러 -> 서비스 -> 리포지터리 순서로 데이터 처리)

    @RequestMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<Question> paging = this.questionService.getList(page);
        model.addAttribute("paging", paging); //Model 객체에 값을 담아두면 템플릿에서 그 값을 사용 가능(Model객체는 컨트롤러 메서드의 매개변수로 지정하기만 하면 스프링 부트가 자동으로 생성)
        return "question_list"; //question_list.html 템플릿 파일 리턴
	}
    
    //@PathVariable은 {id}를 숫자 n처럼 변하는 id 값으로 지정할 때 사용
    @RequestMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
    	Question question = this.questionService.getQuestion(id); //QuestionService의 getQuestion 메서드 호출
        model.addAttribute("question", question);
    	return "question_detail";
    }
    
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }
    
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        this.questionService.create(questionForm.getSubject(), questionForm.getContent());
        return "redirect:/question/list"; // 질문 저장후 질문목록으로 이동
    }
}
