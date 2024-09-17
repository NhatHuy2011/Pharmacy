package com.project.pharmacy.mapper;

import com.project.pharmacy.dto.request.UserCreateRequest;
import com.project.pharmacy.dto.response.UserResponse;
import com.project.pharmacy.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreateRequest request);

    UserResponse toUserResponse(User user);
}
