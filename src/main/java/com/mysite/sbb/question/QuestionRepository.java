package com.mysite.sbb.question;

//리포지터리 : 엔티티에 의해 생성된 데이터베이스 테이블에 접근하는 메서드들을 사용하기 위한 인터페이스
//즉 CRUD를 어떻게 처리할지 정의하는 계층

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

//엔티티만으로는 데이터베이스에 데이저를 저장하거나 조회할 수 없으므로 데이터 처리를 위해서는 실제 데이터베이스와 연동하는 JPA 리포지터리가 필요
//리포지터리는 엔티티에 의해 생성된 데이터베이스 테이블에 접근하는 메서드들(ex: findAll,save 등)을 사용하기 위한 인터페이스
//즉 CRUD를 어떻게 처리할지 정의하는 계층
//JpaRepository 인터페이스를 상속하여 리포지터리로 만듬, 상속할 때는 제네릭 타입으로 리포지터리의 대상이 되는 엔티티의 타입(Question)과 해당 엔티티의 기본키의 속성 타입(Integer)을 지정해야한다.
public interface QuestionRepository extends JpaRepository<Question, Integer> {
	//JpaRepository를 상속하여 DI에 의해 스프링이 자동으로 QuestionRepository 객체 생성(프록시 패턴)하므로 메서드 선언만 하고 구현은 안해도 된다
	Question findBySubject(String subject); //즉 findBy + 엔티티의 속성명과 같은 리포지터리 메서드를 작성하면 해당 속성의 값으로 데이터 조회가능
	Question findBySubjectAndContent(String subject, String content); //위와 같이 리포지터리의 메서드 명은 데이터를 조회하는 쿼리문의 where 조건을 결정하는 역할을 한다.
	List<Question> findBySubjectLike(String subject); //응답 결과가 여러건인 경우에는 리턴 타입을 Question이 아닌 List<Question>으로 한다
	Page<Question> findAll(Pageable pageable);
}
