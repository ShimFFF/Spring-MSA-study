package com.example.userservice;

import com.example.userservice.dto.UserDto;
import com.example.userservice.valueobject.ResponseOrder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        // 모델맵퍼 사용
        // UserDto -> UserEntity
        // 모델 맵처 작동 방식: UserDto 객체의 필드와 UserEntity 객체의 필드가 일치하는 경우,
        // 모델 맵퍼는 두 객체 간의 필드 값을 복사하여 객체를 변환
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);

        // 비밀번호 암호화 시켜 저장
        userEntity.setEncryptedPwd("passwordEncoder.encode(userDto.getPwd())");

        userRepository.save(userEntity);

        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);

        return returnUserDto;
    }

    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) {
            //throw new UsernameNotFoundException("User not found");
        }
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        //일단 null로 지정
        List<ResponseOrder> orders = new ArrayList<>();
        userDto.setOrders(orders);

        return userDto;
    }

    // UserDto 객체로 변환 해줘도 상관 없음
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }



}
