package com.example.firstservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/first-service")
public class FirstServiceController {
    @Value("${server.port}")
    private String serverPort; // 서버 포트를 출력해보기 위해 사용하는 변수
                                // 환경 변수를 사용하려면 @Value 어노테이션을 사용해야 함

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the First Service";
    }
    @GetMapping("/message")
    public String meesage(@RequestHeader("first-request") String header) {
        System.out.println(header); // 헤더 값을 출력
        return "Hello World in first Service";
    }
    @GetMapping("/check")
    public String check() {
        return "Hi, This is a message from First Service, port: " + serverPort;
    }
}
