package com.example.secondservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/second-service")
public class SecondServiceController {
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the Second Service";
    }

    @GetMapping("/message")
    public String meesage(@RequestHeader("second-request") String header) {
        System.out.println(header); // 헤더 값을 출력
        return "Hello World in Second Service";
    }

    @GetMapping("/check")
    public String check() {
        return "Hi, This is a message from Second Service";
    }
}
