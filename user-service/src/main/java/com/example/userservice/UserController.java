package com.example.userservice;

import com.example.userservice.valueobject.Greeting;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor // final로 선언된 필드에 대한 생성자 생성
public class UserController {

    private final Environment evn; // 환경 변수 사용
    // Environment 객체를 사용하여 환경 변수 값을 가져오는 방법
    // 필드 주입 방식보다 생성자 하나 만드는 게 좋으므로 생성자 생성

    private final Greeting greeting;  // Greeting 객체 주입

    @GetMapping("/heath_check")
    public String hello() {
        return ("applicaion is running");
    }

    @GetMapping("/welecome")
    public String welecome() {
        //return evn.getProperty("greeting.message"); // application.yml에 정의한 greeting.message 값 반환
        return greeting.getMessage();
    }
}
