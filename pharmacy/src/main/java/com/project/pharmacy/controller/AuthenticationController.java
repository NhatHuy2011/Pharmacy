package com.project.pharmacy.controller;

import com.nimbusds.jose.JOSEException;
import com.project.pharmacy.dto.request.AuthenticateRequest;
import com.project.pharmacy.dto.request.InstrospectTokenRequest;
import com.project.pharmacy.dto.request.LogoutRequest;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.AuthenticationResponse;
import com.project.pharmacy.dto.response.IntrospectTokenResponse;
import com.project.pharmacy.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/outbound/authentication")
    public ApiResponse<AuthenticationResponse> outbound(@RequestParam("code") String code){

        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.outboundAuthenticate(code))
                .build();
    }

    @PostMapping("/log-in")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticateRequest request){
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.authenticate(request))
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectTokenResponse> introspectToken(@RequestBody InstrospectTokenRequest request) throws ParseException, JOSEException {
        return ApiResponse.<IntrospectTokenResponse>builder()
                .result(authenticationService.introspectToken(request))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .message("Log-out successfull")
                .build();
    }
}
