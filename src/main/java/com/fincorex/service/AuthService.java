package com.fincorex.service;

import com.fincorex.dto.request.LoginRequest;
import com.fincorex.dto.request.RegisterRequest;
import com.fincorex.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
