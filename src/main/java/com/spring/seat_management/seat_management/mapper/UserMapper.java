package com.spring.seat_management.seat_management.mapper;

import com.spring.seat_management.seat_management.dto.request.UserLoginReq;
import com.spring.seat_management.seat_management.dto.request.UserSignupReq;
import com.spring.seat_management.seat_management.dto.response.UserLoginRes;
import com.spring.seat_management.seat_management.dto.response.UserSignupRes;
import com.spring.seat_management.seat_management.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntityFromSignUpRequest(UserSignupReq request);

    UserSignupRes toSignUpResponse(User user);
    UserLoginRes toLoginResponse(User user);
}
