package com.example.userservice.security;

import com.example.userservice.UserService;
import com.example.userservice.dto.UserDto;
import com.example.userservice.valueobject.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.User;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

// 사용자 인증 필터
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;
    private final Environment environment;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                   UserService userService, Environment environment) {
        super(authenticationManager);
        this.userService = userService;
        this.environment = environment;
    }

    @Override
    // 사용자 인증 시도
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {
            // 인풋 파라미터로 받은 정보를 인증 정보로 만들 것임
            RequestLogin creds = new ObjectMapper().readValue(req.getInputStream(), RequestLogin.class);

            return getAuthenticationManager().authenticate(
                    // userName, Password를 가지고 UsernamePasswordAuthenticationToken 객체 생성
                    // 그래야지 시큐리티에서 인증을 할 수 있음
                    // UsernamePasswordAuthenticationToken 객체를 생성하면
                    // AuthenticationManager에 인증을 요청 (getAuthenticationManager().authenticate)
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    // 인증 성공 시 실행
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        // 로그인 성공 했을 때, 어떤 것을 해줄 것인지?

        // 로그인 성공 시, 토큰을 만들어서 헤더에 담아서 보내 줄 것이냐?
        // 로긍니 했을 때, 반환 값으로 무엇을 줄건지 등등의 작업을 하면 됨

        String userName = ((User) auth.getPrincipal()).getUsername();
        UserDto userDetails = userService.getUserDetailsByEmail(userName);

        byte[] secretKeyBytes = Base64.getEncoder().encode(
                Objects.requireNonNull(environment.getProperty("token.secret")).getBytes());

        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        Instant now = Instant.now();

        String token = Jwts.builder()
                .setSubject(userDetails.getUserId())
                .setExpiration(Date.from(now.plusMillis(
                        Long.parseLong(Objects.requireNonNull(
                                environment.getProperty("jwt.expiration"))))))
                .setIssuedAt(Date.from(now))
                .signWith(secretKey)
                .compact();

        res.addHeader("token", token);
        res.addHeader("userId", userDetails.getUserId());
    }
}