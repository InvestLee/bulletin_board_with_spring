package com.mysite.sbb.question;

import java.security.Principal;
import java.util.List;
import javax.validation.Valid;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import com.mysite.sbb.answer.AnswerService;

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
import org.springframework.security.access.prepost.PreAuthorize; 
//@PreAuthorize("isAuthenticated()")이 붙은 메서드는 로그인이 필요한 메서드를 의미, 만약 로그아웃 상태로 호출되면 로그인 페이지로 이동
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@RequestMapping("/question") //URL에 대한 mapping
@RequiredArgsConstructor //questionService 속성을 포함하는 생성자를 생성, final이 붙은 속성을 포함하는 생성자를 자동으로 생성하는 역할
@Controller
public class QuestionController {
	
	private final QuestionService questionService; //생성자 주입 DI (컨트롤러 -> 서비스 -> 리포지터리 순서로 데이터 처리)
	private final AnswerService answerService; //답변 페이징 기능을 위해 추가
	private final UserService userService;

    @RequestMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page,
    		@RequestParam(value = "kw", defaultValue = "") String kw) {
    	//GET방식으로 요청된 URL에서 page값을 가져오기 위해 @RequestParam(value="page", defaultValue="0") int page 매개변수 추가, 파라미터 page가 URL에 전달되지 않은 경우 디폴트 값으로 0 설정
    	//스프링부트의 페이징은 첫페이지 번호가 1이 아닌 0이다.
    	//템플릿에 Page<Question> 객체인 paging을 전달
    	//검색어에 해당하는 kw 파라미터를 추가했고 디폴트 값으로 빈 문자열 설정
        Page<Question> paging = this.questionService.getList(page, kw);
        model.addAttribute("paging", paging); //Model 객체에 값을 담아두면 템플릿에서 그 값을 사용 가능(Model객체는 컨트롤러 메서드의 매개변수로 지정하기만 하면 스프링 부트가 자동으로 생성)
        model.addAttribute("kw", kw); //화면에서 입력한 검색어를 화면에 유지하기 위해 kw 값으로 저장
        return "question_list"; //question_list.html 템플릿 파일 리턴
	}
    
    //@PathVariable은 {id}를 숫자 n처럼 변하는 id 값으로 지정할 때 사용
    @RequestMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm, @RequestParam(value="page", defaultValue="0") int page) {
    	//question_detail 템플릿이 AnswerForm을 사용하므로 매개변수 AnswerForm answerForm 추가
    	Question question = this.questionService.getQuestion(id); //QuestionService의 getQuestion 메서드 호출
    	Page<Answer> paging = this.answerService.getList(question, page);
    	model.addAttribute("paging", paging);
    	model.addAttribute("question", question);
    	return "question_detail";
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create") // 질문 등록하기 버튼을 통한 /question/create 요청은 GET요청이므로 GetMapping 사용 
    public String questionCreate(QuestionForm questionForm) {
    	//th:object(검증 실패 표시)에 의해 QuestionForm 객체 필요하므로 GetMapping 관련 메서드도 변경
        return "question_form"; //question_form 템플릿을 렌더링하여 출력
    }
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create") // 질문 등록 저장에서 POST 방식으로 요청한 /question/create URL을 처리하기 위함, GetMapping시 사용한 메서드 명과 동일하게 사용, 메서드 오버로딩
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        //@Valid는 QuestionForm의 @NotEmpty, @Size 등으로 설정한 검증 기능이 동작
    	//QuestionForm 객체는 subject, content 항목을 지닌 폼이 전송되면 QuestionForm의 subject, content 속성이 자동으로 바인딩(스프링 프레임워크 바인딩 기능)
    	//BindingResult 매개변수는 @Valid로 인해 검증이 수행된 결과를 의미하는 객체(항상 BindingResult가 @Valid 뒤에 위치, 이를 준수하지 않으면 @Valid만 적용되어 400 오류 발생)
    	if (bindingResult.hasErrors()) {
    		//오류가 있는 경우 다시 폼을 작성하는 화면을 렌더링, 오류가 없는 경우에만 질문 등록 진행
            return "question_form";
        }
    	SiteUser siteUser = this.userService.getUser(principal.getName());
        // 질문 등록 템플릿에서 필드 항목으로 사용한 subject, content, QuestionService로 질문 데이터를 저장
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        return "redirect:/question/list"; // 질문 저장후 질문목록으로 이동
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        //로그인한 사용자와 질문의 작성자가 동일하지 않은 경우 오류 발생
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        //수정할 질문의 제목과 내용을 화면에 보여주기 위해 questionForm 객체에 값을 담아서 템플릿으로 전달
        //질문 등록시 사용했던 question_form 템플릿을 질문 수정에도 사용하므로 질문이 수정되는 것이 아니라 새로운 질문이 등록됨 => 템플릿 폼 태그의 action을 활용하여 대처
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult, 
            Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        //로그인한 사용자와 질문의 작성자가 동일하지 않은 경우 오류 발생
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        //위 조건문의 검증이 통과되면 QuestionForm에서 작성한 modify 메서드를 호출하여 질문 데이터를 수정한 후 질문 상세화면을 다시 호출
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }
    
    // @{|/question/delete/${question.id}|} URL을 처리하기 위한 기능
    //URL로 전달받은 id값을 사용하여 Question 데이터를 조회한 후 QuestionService의 delete 메서드로 질문을 삭제, 삭제 후 질문 목록 화면으로 돌아갈 수 있도록 투르 페이지로 리다이렉트
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        //로그인한 사용자와 질문의 작성자가 동일하지 않은 경우 오류 발생
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }
    
    //추천 버튼을 눌렀을 때 호출되는 URL을 처리
    @PreAuthorize("isAuthenticated()") //추천은 로그인한 사람만 가능해야 함
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        //QuestionService의 vote 메서드를 호출하여 추천인을 저장, 오류가 없다면 질문 상세화면으로 리다이렉트
    	Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }
}
