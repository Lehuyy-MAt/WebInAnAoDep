package com.example.webinanao.Dto.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String fullName;
    private String phoneNumber;
    private String address;
}