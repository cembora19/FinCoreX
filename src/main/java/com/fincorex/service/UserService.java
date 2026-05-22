package com.fincorex.service;

import com.fincorex.dto.request.CreateUserRequest;
import com.fincorex.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    List<UserResponse> getAllUsers();
}