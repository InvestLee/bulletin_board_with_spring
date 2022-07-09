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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.answer.Answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification; //정교한 쿼리 작성을 돕는 JPA 도구

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service //서비스로 인식
public class QuestionService {
	
	private final QuestionRepository questionRepository;
	
	//검색어(kw)를 입력받아 쿼리의 조인문과 where문을 생성하여 리턴하는 search 메서드
	private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            /*
            q : Root, 즉 기준을 의미하는 Question 엔티티 객체(질문 제목과 내용을 검색하기 위해 필요)
            u1 : Question 엔티티와 SiteUser 엔티티를 아우터 조인(JoinType.LEFT)하여 만든 SiteUser 엔티티 객체
                 Question 엔티티와 SiteUser 엔티티는 author 속성으로 연결되어 있으므로 q.join("author")와 같이 조인 필요(질문 작성자 검색을 위해)
            a : Question 엔티티와 Answer 엔티티를 아우터 조인(JoinType.LEFT)하여 만든 Answer 엔티티 객체
                Question 엔티티와 Answer 엔티티는 answerList 속성으로 연결되어 있으므로 q.join("answerList")와 같이 조인 필요(답변 내용 검색하기 위해)
            u2 : a 객체와 SiteUser 엔티티를 아우터 조인하여 만든 SiteUser 엔티티의 객체 (답변 작성자를 검색하기 위해)
            */   
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거 
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                //검색어가 포함되어 있는지 like로 검색, OR 검색 통해 검색 유형 선택
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목 
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용 
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자 
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용 
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자 
            }
        };
    }
	/* 아래 쿼리문을 카바 코드로 구현(inner join은 교집합이므로 결과가 누락될 가능성이 있기 때문에 합집합인 Outer join 사용)
	select
    	distinct q.id,
    	q.author_id,
    	q.content,
    	q.create_date,
    	q.modify_date,
    	q.subject 
	from question q 
	left outer join site_user u1 on q.author_id=u1.id 
	left outer join answer a on q.id=a.question_id 
	left outer join site_user u2 on a.author_id=u2.id 
	where
    	q.subject like '%스프링%' 
    	or q.content like '%스프링%' 
    	or u1.username like '%스프링%' 
    	or a.content like '%스프링%' 
    	or u2.username like '%스프링%' 
	 */
	
	
	//질문 목록을 조회하여 리턴, 정수 타입의 페이지번호를 입력받아 해당 페이지의 질문 목록을 리턴하는 메서드
    public Page<Question> getList(int page, String kw) {
    	List<Sort.Order> sorts = new ArrayList<>(); //Sort.Order 객체로 구성된 리스트에 Sort.Order 객체를 추가하고 Sort.by(소트리스트)로 소트 객체 생성
        sorts.add(Sort.Order.desc("createDate")); //날짜기준으로 역순으로 정렬(최신글이 1페이지)
        //아래 문장을 통해 데이터 전체를 조회하지 않고 해당 페이지의 데이터만 조회하도록 쿼리가 변경됨
        //PageRequest.of(page, 10, Sort.by(sorts)) = (조회할 페이지 번호, 한 페이지에 보여줄 게시물의 갯수, Sort 객체 전달)
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); 
        Specification<Question> spec = search(kw); //검색어를 의미하는 매개변수 kw를 getList에 추가하고 findAll 메서드 호출 시 전달 
        return this.questionRepository.findAll(spec, pageable);
        //return this.questionRepository.findAllByKeyword(kw, pageable); => 자바코드가 아닌 직접 쿼리를 사용할 떄 사용
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
    
    //추천인을 저장(Question 엔티티에 사용자를 추천인으로 저장하는 vote 메서드)
    public void vote(Question question, SiteUser siteUser) {
        if (question.getVoter().contains(siteUser)) { 
        	question.getVoter().remove(siteUser);
        } else {
        	question.getVoter().add(siteUser);
        }
        this.questionRepository.save(question);
    }
}
