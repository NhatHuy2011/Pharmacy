package com.project.pharmacy.controller.auth;

import java.text.ParseException;

import com.project.pharmacy.dto.request.auth.InstrospectTokenRequest;
import com.project.pharmacy.dto.request.auth.SignInRequest;
import com.project.pharmacy.dto.request.auth.SignOutRequest;
import com.project.pharmacy.dto.request.auth.RefreshTokenRequest;
import com.project.pharmacy.dto.request.oauth.OutboundAuthenticationAndroid;
import org.springframework.web.bind.annotation.*;

import com.nimbusds.jose.JOSEException;
import com.project.pharmacy.dto.response.common.ApiResponse;
import com.project.pharmacy.dto.response.auth.AuthenticationResponse;
import com.project.pharmacy.dto.response.auth.IntrospectTokenResponse;
import com.project.pharmacy.service.auth.AuthenticationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    // Role USER
    @PostMapping("/outbound/authentication/web")
    public ApiResponse<AuthenticationResponse> outboundLoginGoogleWeb(@RequestParam("code") String code) {

        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.outboundAuthenticateWeb(code))
                .build();
    }

    @PostMapping("/outbound/authentication/android")
    public ApiResponse<AuthenticationResponse> outboundLoginGoogleAndroid(@RequestBody OutboundAuthenticationAndroid request){
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.outboundAuthenticationAndroid(request))
                .build();
    }

    @PostMapping("/log-in")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody SignInRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.authenticate(request))
                .message("Đăng nhập thành công!")
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectTokenResponse> introspectToken(@RequestBody InstrospectTokenRequest request)
            throws ParseException, JOSEException {
        return ApiResponse.<IntrospectTokenResponse>builder()
                .result(authenticationService.introspectToken(request))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody SignOutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .message("Log-out successfull")
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request)
            throws ParseException, JOSEException {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.refreshToken(request))
                .build();
    }
}
