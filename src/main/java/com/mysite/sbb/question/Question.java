package com.mysite.sbb.question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne; //여러 개의 질문이 한 명의 사용자에게 작성될 수 있으므로 ManyToOne 
import javax.persistence.ManyToMany; //하나의 질문에 여러 사람이 추천할 수 있고 한 사람이 여러 개의 질문을 추천할 수 있음, 대등 관계

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;

import lombok.Getter;
import lombok.Setter;

@Getter //Getter 메서드 자동 구현
@Setter //Setter 메서드 자동 구현
@Entity //JPA(ORM의 기술 표준으로 사용하는 인터페이스의 모음, 데이터베이스 처리)가 엔티티로 인식하기 위함(엔티티 : 데이터베이스 테이블과 매핑되는 자바 클래스)
public class Question {
	@Id //기본키로 지정(동일한 값 저장 불가능)
	@GeneratedValue(strategy = GenerationType.IDENTITY) //데이터를 저장할 떄 해당 속성에 값을 따로 세팅하지 않아도 1씩 자동으로 증가하여 저장 
	private Integer Id; //strategy는 고유번호 생성 옵션으로 Generation.IDENTITY는 해당 컬럼만의 독립적인 시퀀스를 생성하여 번호를 증가시킬 떄 사용
	//strategy 옵션을 생략할 경우에 @GeneratedValue 이 지정된 컬럼들이 모두 동일한 시퀀스로 번호를 생성하기 때문에 일정한 순서로 고유번호를 가질 수 없음. 그러므로 Generation.IDENTITY를 많이 사용
	
	@Column(length = 200) //엔티티의 속성은 테이블의 컬럼명과 일치하는 데 세부 설정을 위해 @Column을 사용, length는 컬럼의 길이를 설정할 때 사용
	private String subject; //엔티티의 속성은 @Column을 사용하지 않더라도 테이블 컬럼으로 인식, 테이블 컬럼으로 인식하고 싶지 않은 경우에만 @Transient 사용 
	
	@Column(columnDefinition = "TEXT") //columnDefinition은 컬럼의 속성을 정의할 떄 사용한다. columnDefinition = "TEXT"는 '내용'처럼 글자수를 제한할 수 없는 경우에 사용 
	private String content;
	
	private LocalDateTime createDate; //실제 컬럼 명 : create_date ('대문자'가 '_소문자'가 된다.)
	
	//mappedBy는 참조 엔티티의 속성명을 의미(Answer 엔티티에서 Question 엔티티를 참조한 속성명 question을 mappedBy에 전달)
	//CascadeType.REMOVE는 질문을 삭제하면 그에 달린 답변들도 모두 삭제하기 위해 사용
	@OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE) //질문은 하나 답변은 여러 개
	private List<Answer> answerList;
	
	@ManyToOne
	private SiteUser author;
	
	private LocalDateTime modifyDate; //수정 일시
	
	@ManyToMany
	Set<SiteUser> voter; //추천인은 중복되면 안되므로 Set
	/*
	@ManyToMany 관계로 속성을 생성하면 새로운 테이블을 생성하여 데이터를 관리
	테이블에는 서로 연관된 엔티티의 고유번호(id) 2개가 primary key로 되어 있기 때문에 다대다(N:N) 관계가 성립하는 구조다.
	*/
}
