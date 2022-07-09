package com.mysite.sbb.answer;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany; //하나의 질문에 여러 사람이 추천할 수 있고 한 사람이 여러 개의 질문을 추천할 수 있음, 대등 관계

import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;

import lombok.Getter;
import lombok.Setter;

@Getter //관련 에너테이션 설명은 Question.java 참고
@Setter
@Entity
public class Answer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer Id;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	private LocalDateTime createDate;
	
	//속성을 추가할 뿐만 아니라 질문 엔티티와 연결된 속성이라는 것을 명시적으로 표시(Answer 엔티티의 quention 속성과 Question 엔티티가 서로 연결됨(실제 데이터베이스에서는 외래키 관계))
	@ManyToOne //답변은 하나의 질문에 여러개가 달릴 수 있는 구조
	private Question question; //답변 엔티티에서 질문 엔티티를 참조하기 위해 추가
	
	@ManyToOne
	private SiteUser author; //author_id 컬럼에는 site_user 테이블의 id 값이 저장되어 SiteUser 엔티티와 연결된다.
	
	private LocalDateTime modifyDate; //수정 일시
	
	@ManyToMany
	Set<SiteUser> voter; //추천인은 중복되면 안되므로 Set
	/*
	@ManyToMany 관계로 속성을 생성하면 새로운 테이블을 생성하여 데이터를 관리
	테이블에는 서로 연관된 엔티티의 고유번호(id) 2개가 primary key로 되어 있기 때문에 다대다(N:N) 관계가 성립하는 구조다.
	*/
}
