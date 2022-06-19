package com.mysite.sbb;

import static org.junit.jupiter.api.Assertions.assertEquals; //assertEquals(기대값, 실제값) 기대값과 실제값이 동일하지 않다면 테스트 실패 처리
import static org.junit.jupiter.api.Assertions.assertTrue; //값이 true인지를 테스트한다.

import java.util.List;
import java.util.Optional; //null 처리를 유연하게 처리하기 위해 사용 isPresent로 null이 아닌지 확인 후 get으로 실제 Question 객체 값을 얻어야 함

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import com.mysite.sbb.question.QuestionService;

@SpringBootTest //해당 클래스가 스트링부트 테스트 클래스임을 의미
class SbbApplicationTests {
	
	//스프링의 DI기능으로 questionService 객체를 스프링이 자동으로 생성한다. (DI : 스프링이 객체를 대신 생성하여 주입)
    @Autowired //순환참조 문제와 같은 이유로 @Autowired보다는 생성자를 통한 객체 주입방식이 권장됨 하지만 테스트 코드는 생성자를 통한 객체 주입이 불가능 하므로 테스트 코드 작성시에만 Autowired 사용
    private QuestionService questionService;

    @Test //@Test는 testJpa가 테스트 메서드임을 나타냄
    void testJpa() { //이 클래스를 JUnit(테스트 코드를 작성하고 실행하기 위한 자바의 테스트 프레임워크)으로 실행하면 @Test가 붙은 메서드가 실행
        for (int i = 1; i <= 300; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
            this.questionService.create(subject, content);
		}
	}
	
    //findAll 메서드 : 데이터를 조회할 때 사용하는 메서드
    //findByID 메서드 : 리턴 타임은 Question이 아닌 Optional, ID값으로 테이터 조회(DB세션이 끊어짐), DB세현을 유지하기 위해 @Transactional 사용
    //save 메서드 : 변경된 Question 데이터 저장, update문과 관련 있음
    //count 메서드 : 리포지터리의 총 데이터 건수를 리턴
    //필요한 시점에 데이터를 가져오는 방식 Lazy 방식
    //q 객체를 조회할 때 답변 리스트 모두 가져오는 방식 Eager 방식
    
	/*
	@Autowired
	private QuestionRepository questionRepository;
	
	@Autowired
	private AnswerRepository answerRepository;
	
	@Transactional
	@Test
	void testJpa() {
		Optional<Question> oq = this.questionRepository.findById(2);
        assertTrue(oq.isPresent());
        Question q = oq.get();
        
        List<Answer> answerList = q.getAnswerList();
        
        assertEquals(1, answerList.size());
        assertEquals("�� �ڵ����� �����˴ϴ�.", answerList.get(0).getContent());
        
	}
	*/
}
