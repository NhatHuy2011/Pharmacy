package com.project.pharmacy.service;

import com.project.pharmacy.dto.request.UserCreateRequest;
import com.project.pharmacy.dto.response.UserResponse;
import com.project.pharmacy.entity.User;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;

    ImageService imageService;

    public UserResponse createUser(UserCreateRequest request, MultipartFile file) throws IOException {
        if(userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = new User();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String urlImage = imageService.uploadImage(file);

        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullname(request.getFullname());
        user.setDob(request.getDob());
        user.setSex(request.getSex());
        user.setPhone_number(request.getPhone_number());
        user.setEmail(request.getEmail());
        user.setImage(urlImage);
        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .fullname(user.getFullname())
                .dob(user.getDob())
                .sex(user.getSex())
                .phone_number(user.getPhone_number())
                .email(user.getEmail())
                .image(user.getImage())
                .build();
    }
}
