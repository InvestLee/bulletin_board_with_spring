package com.mysite.sbb.answer;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.mysite.sbb.question.Question;

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
}
