package com.mysite.sbb;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

// @Component로 인해 CommonUtil 클래스는 스프링부트에 관리되는 빈(bean, 자바객체)으로 등록
// 빈(bean)으로 등록된 컴포넌트는 템플릿에서 바로 사용 가능
// markdown 메서드는 마크다운 텍스트를 HTML 문서로 변환하여 리턴
// 즉, 마크다운 문법이 적용된 일반 텍스트를 변환된(소스코드, 기울이기, 굵게, 링크 등) HTML로 리턴
@Component
public class CommonUtil {
    public String markdown(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}
