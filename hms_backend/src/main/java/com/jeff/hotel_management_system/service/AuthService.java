package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.dto.LoginDto;

public interface AuthService {

    String login(LoginDto loginDto);
}
