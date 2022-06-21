package com.mysite.sbb.question;
//Spring Boot Validation, 화면에서 전달되는 입력 값을 검증하기 위한 폼 클래스, 폼 클래스는 입력 값의 검증 및 화면에서 전달한 입력 값을 바인딩할 때 사용
//매개변수로 바인딩한 객체는 Model 객체로 전달하지 않아도 템플릿에서 사용 가능

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {
    @NotEmpty(message="제목은 필수항목입니다.") //해당 값이 Null 또는 빈 문자열을 허용하지 않음, 검증에 실패할 경우 message를 통해 화면에 오류 메세지 표시
    @Size(max=200) //최대 길이가 200 바이트를 넘으면 안된다는 의미
    private String subject;

    @NotEmpty(message="내용은 필수항목입니다.")
    private String content;
}