package com.example.userservice.security;

import com.example.userservice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
public class WebSecurity {

    private final  UserService userService;
    // 패스워드 암호화를 위해 Bcrypt 암호화 방식 사용
    // Bcrypt 암호화 방식이란, 해시 함수를 이용하여 비밀번호를 여러번 암호화하는 방식
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // 비밀번호 암호화
    private final Environment env;

//    @Value("${server.ip}")
//    public static String ALLOWED_IP_ADDRESS;
//    public static final String SUBNET = "/32";
//    public static final IpAddressMatcher ALLOWED_IP_ADDRESS_MATCHER = new IpAddressMatcher(ALLOWED_IP_ADDRESS + SUBNET);

    @Autowired //개발자에 의해 인스턴스가 만들어 지는 것이 아니라, Spring에 의해 자동으로 주입되는 것
    // -> Spring 컨텍스트가 기동이 되면서 자동으로 등록할 수 있는 빈들을 찾아서 메모리에 객체를 생성
    // -> 즉, 자동으로 등록해주는 과정
    // 근데, BCryptPasswordEncoder는 어디에서도 빈을 등록하지 않았는데, 어떻게 주입이 되는가?
    // -> 안됨 ㅇㅅㅇ
    // 때문에 빈을 등록 해줘야함
    // -> UserServiceApplication.java에 빈 등록
    public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.env = env;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Bean //권한 작업 처리
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        // userDetailsService() 메소드를 사용하여 사용자 정보를 가져오는 서비스를 설정
        // 사용자 검색을 해옴
        // passwordEncoder() 메소드를 사용하여 비밀번호를 암호화해서 DB 비번과 비교
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http.csrf(AbstractHttpConfigurer::disable); // csrf 비활성화
//        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((authz) -> authz
                                .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                                    // 인증이 안되어도, 허용된 IP 주소로부터의 요청은 허용
                                .requestMatchers(new AntPathRequestMatcher("/users", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/welcome")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/health-check")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/swagger-resources/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()//                              .requestMatchers("/**").access(this::hasIpAddress)
                                .requestMatchers("/**").access( // 해당 허용된 IP 주소로부터의 요청만 허용
                                        new WebExpressionAuthorizationManager("hasIpAddress('172.25.82.232') or hasIpAddress('127.0.0.1') or hasIpAddress('172.30.96.94')")) // host pc ip address
                                .anyRequest().authenticated()
                )
                .authenticationManager(authenticationManager)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilter(getAuthenticationFilter(authenticationManager));
        http.headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }

//    private AuthorizationDecision hasIpAddress(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
//        return new AuthorizationDecision(ALLOWED_IP_ADDRESS_MATCHER.matches(object.getRequest()));
//    }

    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        // AuthenticationFilter 객체 생성
        return new AuthenticationFilter(authenticationManager, userService, env);
    }

}
