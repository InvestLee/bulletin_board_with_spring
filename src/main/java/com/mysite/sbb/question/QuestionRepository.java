package com.mysite.sbb.question;

//리포지터리 : 엔티티에 의해 생성된 데이터베이스 테이블에 접근하는 메서드들을 사용하기 위한 인터페이스
//즉 CRUD를 어떻게 처리할지 정의하는 계층

import java.util.List;

import org.springframework.data.domain.Page; //JPA 관련 라이브러리의 페이징 패키지
import org.springframework.data.domain.Pageable; //JPA 관련 라이브러리의 페이징 패키지
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.domain.Specification;

//엔티티만으로는 데이터베이스에 데이저를 저장하거나 조회할 수 없으므로 데이터 처리를 위해서는 실제 데이터베이스와 연동하는 JPA 리포지터리가 필요
//리포지터리는 엔티티에 의해 생성된 데이터베이스 테이블에 접근하는 메서드들(ex: findAll,save 등)을 사용하기 위한 인터페이스
//즉 CRUD를 어떻게 처리할지 정의하는 계층
//JpaRepository 인터페이스를 상속하여 리포지터리로 만듬, 상속할 때는 제네릭 타입으로 리포지터리의 대상이 되는 엔티티의 타입(Question)과 해당 엔티티의 기본키의 속성 타입(Integer)을 지정해야한다.
public interface QuestionRepository extends JpaRepository<Question, Integer> {
	//JpaRepository를 상속하여 DI에 의해 스프링이 자동으로 QuestionRepository 객체 생성(프록시 패턴)하므로 메서드 선언만 하고 구현은 안해도 된다
	Question findBySubject(String subject); //즉 findBy + 엔티티의 속성명과 같은 리포지터리 메서드를 작성하면 해당 속성의 값으로 데이터 조회가능
	Question findBySubjectAndContent(String subject, String content); //위와 같이 리포지터리의 메서드 명은 데이터를 조회하는 쿼리문의 where 조건을 결정하는 역할을 한다.
	List<Question> findBySubjectLike(String subject); //응답 결과가 여러건인 경우에는 리턴 타입을 Question이 아닌 List<Question>으로 한다
	Page<Question> findAll(Pageable pageable); //Pageable 객체를 입력으로 받아 page<Question> 타입 객체를 리턴
	Page<Question> findAll(Specification<Question> spec, Pageable pageable); // Specification객체와 Pageable 객체를 입력으로 Question 엔티티 조회
	
	/* 자바코드가 아닌 직접 쿼리를 작성하여 검색하는 경우
	@Query("select "
            + "distinct q "
            + "from Question q " 
            + "left outer join SiteUser u1 on q.author=u1 "
            + "left outer join Answer a on a.question=q "
            + "left outer join SiteUser u2 on a.author=u2 "
            + "where "
            + "   q.subject like %:kw% "
            + "   or q.content like %:kw% "
            + "   or u1.username like %:kw% "
            + "   or a.content like %:kw% "
            + "   or u2.username like %:kw% ")
    Page<Question> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
    
    @Query를 작성할 때에는 반드시 테이블 기준이 아닌 엔티티 기준으로 작성
    (site_user와 같은 테이블 명대신 Siteuser 엔티티명 사용, q.author_id=u1.id와 같은 컬럼명 대신 q.author=u1처럼 엔티티의 속성명을 사용)
    @Query에 파라미터로 전달한 kw 문자열은 메서드의 매개변수에 @Param("kw")처럼 @Param 사용, 검색어를 의미하는 kw 문자열은 @Query 안에서 :kw로 참조
	*/
}
