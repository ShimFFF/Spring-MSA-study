package com.example.userservice;

import com.example.userservice.dto.UserDto;
import com.example.userservice.valueobject.ResponseOrder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

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


    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(userEntity, UserDto.class);
        return userDto;
    }

    @Override // UserDetailsService의 추상 메서드 구현, 로그인 시 사용자 정보를 가져오는 메서드
    // UserDetailsService를 상속받아 loadUserByUsername 메서드를 구현하면
    // 스프링 시큐리티가 로그인 요청을 처리할 때 이 메서드를 호출하여 사용자 정보를 가져옴
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByEmail(username);

        if (userEntity == null)
            throw new UsernameNotFoundException(username + ": not found");

        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(),
                true, true, true, true,
                new ArrayList<>());

    }


}
