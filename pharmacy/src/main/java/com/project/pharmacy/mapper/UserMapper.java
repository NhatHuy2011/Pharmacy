package com.project.pharmacy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.project.pharmacy.dto.request.UserCreateRequest;
import com.project.pharmacy.dto.request.UserUpdateBio;
import com.project.pharmacy.dto.response.UserResponse;
import com.project.pharmacy.entity.User;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User toUser(UserCreateRequest request);

    @Mapping(target = "noPassword", ignore = true)
    UserResponse toUserResponse(User user);

    void updateBio(@MappingTarget User user, UserUpdateBio request);
}
