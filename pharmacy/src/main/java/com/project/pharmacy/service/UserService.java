package com.project.pharmacy.service;

import com.project.pharmacy.dto.request.UserCreateRequest;
import com.project.pharmacy.dto.request.UserUpdateBio;
import com.project.pharmacy.dto.request.UserUpdateRole;
import com.project.pharmacy.dto.response.UserResponse;
import com.project.pharmacy.entity.Role;
import com.project.pharmacy.entity.User;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.UserMapper;
import com.project.pharmacy.repository.RoleRepository;
import com.project.pharmacy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;

    ImageService imageService;

    PasswordEncoder passwordEncoder;

    UserMapper userMapper;

    RoleRepository roleRepository;

    //Role USER
    public UserResponse createUser(UserCreateRequest request, MultipartFile file) throws IOException {
        if(userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        String urlImage = imageService.uploadImage(file);

        Role role = roleRepository.findByName("USER")
                .orElseThrow(()->new AppException(ErrorCode.ROLE_NOT_FOUND));
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setImage(urlImage);
        user.setRoles(roles);
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("returnObject.username == authentication.name")
    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("returnObject.username == authentication.name")
    public UserResponse updateBio(UserUpdateBio request, MultipartFile file) throws IOException{
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        String urlImage = imageService.uploadImage(file);

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateBio(user, request);
        user.setImage(urlImage);
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    //Role ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateRole(UserUpdateRole request){
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(()->new AppException(ErrorCode.ROLE_NOT_FOUND));

        user.getRoles().add(role);
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll(){
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }
}
