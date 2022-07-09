package com.mysite.sbb.answer;

//현재 로그인한 사용자에 대한 정보를 알기 위해서 Principal 객체를 사용, principal.getName()을 호출하면 현재 로그인한 사용자의 사용자명(ID)를 알 수 있다. 이를 통해 SiteUser 객체 조회 가능
import java.security.Principal; 

import javax.validation.Valid; 

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
//RequestParam String content : 템플릿에서 답변으로 입력한 내용(Content)을 얻기 위해 추가, textarea(템플릿의 답변 내용)의 name 속성명이 content이므로 변수명을 contect로 사용(다른 이름으로 사용할시 오류 발생)
import org.springframework.web.bind.annotation.RequestParam; 
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;


@RequestMapping("/answer") //URL 프리픽스 /answer로 고정
@RequiredArgsConstructor
@Controller
public class AnswerController {
	
	private final QuestionService questionService;
	private final AnswerService answerService; //답변을 저장하기 위한 답변 서비스 클래스
	private final UserService userService;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{id}") // /answer/create/{id}와 같은 URL 요청시 createAnswer 메서드 호출되도록 PostMapping, {id}는 질문의 id, PostMapping은 @RequestMapping과 동일하게 매핑을 담당하지만 POST요청만 받아들일 경우에 사용, GET방식으로 요청할 경우 오류 발생
    public String createAnswer(Model model, @PathVariable("id") Integer id, 
            @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName()); //principal 객체를 통해 사용자명을 얻은 후에 SiteUser 객체를 얻어서 답변 등록
        if (bindingResult.hasErrors()) {
            //검증에 실패할 경우에는 다시 답변을 등록할 수 있는 question_detail 템플릿을 랜더링
        	//question_detail 템플릿은 Question 객체가 필요하므로 model 객체에 Question 객체를 저장한 후에 question_detail 템플릿을 랜더링
        	model.addAttribute("question", question);
            return "question_detail";
        }
        //답변 등록 및 수정 시 지정한 앵커 태그로 이동하도록 코드 수정, 리다이렉트되는 질문 상세 URL에 #answer_%s와 같은 앵커 추가
        Answer answer = this.answerService.create(question, answerForm.getContent(), siteUser); //답변 저장
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
    }
	
	//GET방식의 /answer/modify/답변ID URL을 처리
	//URL의 답변 아이디를 통해 조회한 답변 데이터의 내용을 AnswerForm 객체에 대입하여 answer_form.html 템플릿에서 사용할 수 있도록 함
	//답변 수정 시 기존의 내용의 필요하므로 AnswerForm 객체에 조회한 값을 저장
	@PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        answerForm.setContent(answer.getContent());
        return "answer_form";
    }
	
	//POST방식의 /answer/modify/답변ID URL을 처리
	//답변 수정을 완료한 후에는 질문 상세 페이지로 돌아가기 위해 answer.getQuestion().getId()로 질문의 아이디를 가져옴
	@PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
	public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult, @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "answer_form";
        }
        Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.answerService.modify(answer, answerForm.getContent());
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
    }
	
	//GET방식의 /answer/delete/답변ID URL을 처리
	//답변을 삭제하는 메서드, 답변을 삭제한 후 해당 답변이 있던 질문 상세 화면으로 리다이렉트
	@PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
        Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.answerService.delete(answer);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }
	
	//추천 버튼을 눌렀을 때 호출되는 URL을 처리
    @PreAuthorize("isAuthenticated()") //추천은 로그인한 사람만 가능해야 함
    @GetMapping("/vote/{id}")
    public String answerVote(Principal principal, @PathVariable("id") Integer id) {
        //AnswerService의 vote 메서드를 호출하여 추천인을 저장, 오류가 없다면 질문 상세화면으로 리다이렉트
    	Answer answer = this.answerService.getAnswer(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.answerService.vote(answer, siteUser);
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
    }
    
    /*
    @RequestMapping(value = "/detail/{id}")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<Answer> paging = this.answerService.getList(page);
        model.addAttribute("paging", paging);
        return "question_detail";
    }*/
}
