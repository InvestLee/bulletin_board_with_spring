package com.mysite.sbb.question;

//스프링에서 데이터 처리를 위해 작성하는 클래스
//대부분의 규모있는 스프링부트 프로젝트는 컨트롤러에서 리포지터리를 직접 호출하지 않고 중간에 서비스를 두어 데이터를 처리한다.
//서비스는 모듈화, 보안, 엔티티 객체와 DTO 객체의 변환을 위해 작성한다.
//모듈화 - 서비스를 만들지 않고 컨트롤러에서 구현하려 한다면 해당 기능을 필요로 하는 모든 컨트롤러가 동일한 기능을 중복으로 구현해야함=>서비스틑 통해 컨트롤러에서는 해당 서비스를 호출하여 사용
//보안 - 해커가 해킹을 통해 컨트롤러를 제어할 수 있게 되더라도 리포지터리에 직접 접근할 수는 없게 된다.
//엔티티 객체와 DTO 객체의 변환 - 엔티티 클래스는 데이터베이스와 직접 맞닿아 있는 클래스이므로 컨트롤러나 템플릿 엔진(타임리프)에 전달하여 사용하는 것은 좋지 않음.
//							엔티티를 직접 사용하여 속성을 변경하게 되면 테이블 컬럼이 변경되어 오류 발생할 수 있음. 그러므로 엔티티 클래스는 컨트롤러에서 사용할 수 없게 설계해야함
//							이를 위해 엔티티 클래스 대신 사용할 DTO(Data Transfer Object) 클래스가 필요, 서비스 클래스에서 엔티티 객체와 DTO 객체를 서로 변환하여 양방향에 전달하는 역할을 함
//							허나 스프링을 지금 공부하고 있으므로 간결한 이해를 위해 별도의 DTO를 만들지 않고 그대로 사용

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.user.SiteUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service //서비스로 인식
public class QuestionService {
	
	private final QuestionRepository questionRepository;

	//질문 목록을 조회하여 리턴, 정수 타입의 페이지번호를 입력받아 해당 페이지의 질문 목록을 리턴하는 메서드
    public Page<Question> getList(int page) {
    	List<Sort.Order> sorts = new ArrayList<>(); //Sort.Order 객체로 구성된 리스트에 Sort.Order 객체를 추가하고 Sort.by(소트리스트)로 소트 객체 생성
        sorts.add(Sort.Order.desc("createDate")); //날짜기준으로 역순으로 정렬(최신글이 1페이지)
        //아래 문장을 통해 데이터 전체를 조회하지 않고 해당 페이지의 데이터만 조회하도록 쿼리가 변경됨
        //PageRequest.of(page, 10, Sort.by(sorts)) = (조회할 페이지 번호, 한 페이지에 보여줄 게시물의 갯수, Sort 객체 전달)
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); 
        return this.questionRepository.findAll(pageable);
    }
	
    //id 값으로 Question 데이터 조회
    public Question getQuestion(Integer id) {  
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found"); //id값에 해당하는 Question 데이터가 없을 경우 작성한 DataNotFoundException 클래스 호출
        }
    }
    
    //제목과 내용을 입력으로 하여 질문 데이터를 저장하는 create 메서드
    public void create(String subject, String content, SiteUser user) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.questionRepository.save(q);
    }
    
    //질문 데이터를 수정할 수 있는 modify 메서드 추가
    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }
    
    //Question 객체를 입력으로 받아 Question 리포지터리를 사용하여 질문 데이터를 삭제하는 delete 메서드 추가
    public void delete(Question question) {
        this.questionRepository.delete(question);
    }
}
