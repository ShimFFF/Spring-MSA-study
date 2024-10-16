package com.example.userservice;

import com.example.userservice.error.FeignErrorDecoder;
import feign.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@EnableDiscoveryClient // Eureka Server에 서비스 등록
@SpringBootApplication
@EnableFeignClients
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
    @LoadBalanced // spring cloud eureka에 등록된 서비스 이름으로 호출하기 위해
	// UserServiceApplication에서 빈을 등록해주는 이유는
	// Order Service를 호출하기 위해
	// RestTemplate을 Bean으로 등록
	// Bean으로 등록한 RestTemplate은 @Autowired로 주입받아 사용 가능
	public RestTemplate getRestTemplate() {
		int TIMEOUT = 5000;

		RestTemplate restTemplate = new RestTemplateBuilder()
				.setConnectTimeout(Duration.ofMillis(TIMEOUT))
				.setReadTimeout(Duration.ofMillis(TIMEOUT))
				.build();

		return restTemplate;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder()
	{
		return new BCryptPasswordEncoder();
	}

	@Bean
	public Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}
//    @Bean 컴포넌트로 빈을 등록시킨 상태이기 때문에 굳이 빈으로 등록할 필요가 없어서 주석 처리함
//    public FeignErrorDecoder getFeignErrorDecoder() {
//		return new FeignErrorDecoder();
//    }
}
