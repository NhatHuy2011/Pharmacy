package com.project.pharmacy.mapper;

import com.project.pharmacy.dto.request.auth.SignUpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.project.pharmacy.dto.request.user.UserUpdateBio;
import com.project.pharmacy.dto.response.entity.UserResponse;
import com.project.pharmacy.entity.User;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mapping(target = "noPassword", ignore = true)
    UserResponse toUserResponse(User user);
}
