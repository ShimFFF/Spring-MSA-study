package com.example.userservice.valueobject;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component // 스프링 빈으로 등록
// 용도가 없는 클래스이므로 @Component 어노테이션을 붙여 스프링 빈으로 등록
// Repository, Service, Controller, Configuration 등의 용도가 아니므로 @Component 사용
@Data // getter, setter, toString, equals, hashCode 등의 메소드 자동 생성
public class Greeting {

    @Value("${greeting.message}")
    private String message; // application.yml에 정의한 greeting.message 값 주입


}
