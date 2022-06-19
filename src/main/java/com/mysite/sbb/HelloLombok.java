package com.mysite.sbb;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor //해당 속성을 필요로 하는 생성자가 롬복에 의해 자동 생성(final이 없는 속성은 생성자에 포함되지 않음) DI
@Getter//final을 적용하면 @Setter가 의미가 없으며 Setter 메서드도 사용 불가능(final은 한번 설정한 값을 변경할 수 없음)
public class HelloLombok {
	
	private final String hello;
	private final int  lombok;
	
	public static void main(String[] args) {
		HelloLombok helloLombok = new HelloLombok("헬로", 5);
		System.out.println(helloLombok.getHello());
		System.out.println(helloLombok.getLombok());
	}
}
