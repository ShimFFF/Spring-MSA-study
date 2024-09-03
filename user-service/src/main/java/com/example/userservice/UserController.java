package com.example.userservice;

import com.example.userservice.dto.UserDto;
import com.example.userservice.valueobject.Greeting;
import com.example.userservice.valueobject.RequestUser;
import com.example.userservice.valueobject.ResponseUser;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor // final로 선언된 필드에 대한 생성자 생성
public class UserController {

    private final Environment evn; // 환경 변수 사용
    // Environment 객체를 사용하여 환경 변수 값을 가져오는 방법
    // 필드 주입 방식보다 생성자 하나 만드는 게 좋으므로 생성자 생성

    private final Greeting greeting;  // Greeting 객체 주입

    private final UserService userService;

    @GetMapping("/heath_check")
    public String hello() {
        return ("applicaion is running");
    }

    @GetMapping("/welecome")
    public String welecome() {
        //return evn.getProperty("greeting.message"); // application.yml에 정의한 greeting.message 값 반환
        return greeting.getMessage();
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {
        ModelMapper mapper = new ModelMapper(); // 모델 맵퍼 객체 생성
        // 모델 맵퍼를 사용해 RequestUser 객체를 UserDto 객체로 변환
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

}
