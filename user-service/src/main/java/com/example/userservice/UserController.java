package com.example.userservice;

import com.example.userservice.dto.UserDto;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public String status() {
        return String.format("It's Working in Catalog Service on PORT %s"
                , evn.getProperty("local.server.port")
                + " port(server.port) = " + evn.getProperty("server.port")
                + " with token sercret= " + evn.getProperty("token.secret")
                + " with token expiration time= " + evn.getProperty("token.expiration_time")
        );
    }

    @GetMapping("/welecome")
    public String welecome() {
        //return evn.getProperty("greeting.message"); // application.yml에 정의한 greeting.message 값 반환
        return greeting.getMessage();
    }

    @Operation(summary = "사용자 회원 가입을 위한 API", description = "user-service에 회원 가입을 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CREATED"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
    }
    )
    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @Operation(summary = "전체 사용자 목록조회 API", description = "현재 회원 가입 된 전체 사용자 목록을 조회하기 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized (인증 실패 오류)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (권한이 없는 페이지에 엑세스)"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
    }
    )
    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        Iterable<UserEntity> userList = userService.getUserByAll();

        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "사용자 정보 상세조회 API", description = "사용자에 대한 상세 정보조회를 위한 API (사용자 정보 + 주문 내역 확인)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized (인증 실패 오류)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (권한이 없는 페이지에 엑세스)"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND (회원 정보가 없을 겨우)"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
    }
    )
    @GetMapping("/users/{userId}")
    public ResponseEntity getUser(@PathVariable("userId") String userId) {
        //getUserByUserId에서 사용자의 주문 내역도 가져올 예정
        UserDto userDto = userService.getUserByUserId(userId);

        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        ResponseUser returnValue = new ModelMapper().map(userDto, ResponseUser.class);

        EntityModel entityModel = EntityModel.of(returnValue);
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).getUsers());
        entityModel.add(linkTo.withRel("all-users"));

        try {
            return ResponseEntity.status(HttpStatus.OK).body(entityModel);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    @GetMapping("/users/hateoas")
    public ResponseEntity<CollectionModel<EntityModel<ResponseUser>>> getUsersWithHateoas() {
        List<EntityModel<ResponseUser>> result = new ArrayList<>();
        Iterable<UserEntity> users = userService.getUserByAll();

        for (UserEntity user : users) {
            EntityModel entityModel = EntityModel.of(user);
            entityModel.add(linkTo(methodOn(this.getClass()).getUser(user.getUserId())).withSelfRel());

            result.add(entityModel);
        }

        return ResponseEntity.ok(CollectionModel.of(result, linkTo(methodOn(this.getClass()).getUsersWithHateoas()).withSelfRel()));
    }

}
