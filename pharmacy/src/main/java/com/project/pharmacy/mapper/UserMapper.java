package com.project.pharmacy.mapper;

import com.project.pharmacy.dto.request.UserCreateRequest;
import com.project.pharmacy.dto.request.UserUpdateBio;
import com.project.pharmacy.dto.response.OutboundUserResponse;
import com.project.pharmacy.dto.response.UserResponse;
import com.project.pharmacy.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreateRequest request);

    UserResponse toUserResponse(User user);

    void updateBio(@MappingTarget User user, UserUpdateBio request);
}
