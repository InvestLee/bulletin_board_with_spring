package com.mysite.sbb.answer;

import com.mysite.sbb.user.SiteUser; //답변 저장시 작성자를 저장할 수 있게 함
import com.mysite.sbb.question.Question;
import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    //답변 생성을 위한 create 메서드, 입력으로 받은 question과 content를 사용하여 Answer 객체를 생성하여 저장
    public Answer create(Question question, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        this.answerRepository.save(answer);
        return answer; //컨트롤러에서 답변이 등록된 위치로 이동하기 위해 답변 객체 필요(앵커 기능을 위해)
    }
    
    //답변 아이디로 답변 조회
    public Answer getAnswer(Integer id) {
        Optional<Answer> answer = this.answerRepository.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    //답변의 내용으로 답변을 수정
    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }
    
    //입력으로 받은 Answer 객체를 사용하여 답변을 삭제하는 delete 메서드
    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }
    
    //추천인을 저장(Answer 엔티티에 사용자를 추천인으로 저장하는 vote 메서드)
    public void vote(Answer answer, SiteUser siteUser) {
        if (answer.getVoter().contains(siteUser)) {
        	answer.getVoter().remove(siteUser);
        } else {
        	answer.getVoter().add(siteUser);
        }
        this.answerRepository.save(answer);
    }
    
    public Page<Answer> getList(Question question, int page) {
    	List<Sort.Order> sorts = new ArrayList<>(); //Sort.Order 객체로 구성된 리스트에 Sort.Order 객체를 추가하고 Sort.by(소트리스트)로 소트 객체 생성
    	sorts.add(Sort.Order.desc("Voter")); //추천이 많은 순으로 정렬
    	sorts.add(Sort.Order.desc("createDate")); //날짜기준으로 역순으로 정렬(최신 댓글이 1페이지)
        //아래 문장을 통해 데이터 전체를 조회하지 않고 해당 페이지의 데이터만 조회하도록 쿼리가 변경됨
        //PageRequest.of(page, 10, Sort.by(sorts)) = (조회할 페이지 번호, 한 페이지에 보여줄 게시물의 갯수, Sort 객체 전달)
    	Pageable pageable = PageRequest.of(page, 3, Sort.by(sorts)); 
    	return this.answerRepository.findAllByQuestion(question, pageable);
    }
}
