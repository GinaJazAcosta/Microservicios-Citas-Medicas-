package com.gina.authorization.services;

import com.gina.authorization.dto.LoginRequest;
import com.gina.authorization.dto.TokenResponse;

public interface AuthService {

    TokenResponse autenticar(LoginRequest request) throws Exception;
}

