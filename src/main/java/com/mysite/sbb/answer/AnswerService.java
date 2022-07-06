package com.mysite.sbb.answer;

import com.mysite.sbb.user.SiteUser; //답변 저장시 작성자를 저장할 수 있게 함
import com.mysite.sbb.question.Question;
import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    //답변 생성을 위한 create 메서드, 입력으로 받은 question과 content를 사용하여 Answer 객체를 생성하여 저장
    public void create(Question question, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        this.answerRepository.save(answer);
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
}
