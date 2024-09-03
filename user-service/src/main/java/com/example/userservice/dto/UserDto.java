package com.example.userservice.dto;

import com.example.userservice.valueobject.ResponseOrder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserDto {
    private String email;
    private String name;
    private String pwd;
    private String userId;
    private Date createdAt;

    private String encryptedPwd; // 암호화된 비밀번호

    private List<ResponseOrder> orders;
}
