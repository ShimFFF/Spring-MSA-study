package com.example.userservice.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class FeignErrorDecoder implements ErrorDecoder {
    Environment env; //환경 변수 사용

    @Autowired //의존성 주입 -> FeignErrorDecoder 클래스를 Spring 빈으로 등록
    public FeignErrorDecoder(Environment env) {
        this.env = env;
    }

    @Override // ErrorDecoder 인터페이스의 decode 메소드 구현
    // FeignClient를 통해 호출한 서비스의 응답 코드에 따라 예외 처리
    //HTTP 응답 코드에 따라 예외 처리
    public Exception decode(String methodKey, Response response) {
        switch(response.status()) {
            case 400:
                break;
            case 404:
                // getOrders 메소드 호출 시에 발생한 404 에러라면
                if (methodKey.contains("getOrders")) {
                    return new ResponseStatusException(HttpStatus.valueOf(response.status()),
                            "User's orders is empty");
                }
                break;
            default:
                return new Exception(response.reason());
        }

        return null;
    }
}
