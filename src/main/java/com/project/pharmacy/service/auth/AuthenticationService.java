package com.project.pharmacy.service.auth;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.project.pharmacy.dto.request.auth.InstrospectTokenRequest;
import com.project.pharmacy.dto.request.auth.SignInRequest;
import com.project.pharmacy.dto.request.auth.SignOutRequest;
import com.project.pharmacy.dto.request.auth.RefreshTokenRequest;
import com.project.pharmacy.dto.request.oauth.ExchangeTokenRequest;
import com.project.pharmacy.dto.request.oauth.OutboundAuthenticationAndroid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.project.pharmacy.dto.response.auth.AuthenticationResponse;
import com.project.pharmacy.dto.response.oauth.ExchangeTokenResponse;
import com.project.pharmacy.dto.response.auth.IntrospectTokenResponse;
import com.project.pharmacy.dto.response.oauth.OutboundUserResponse;
import com.project.pharmacy.entity.InvalidatedToken;
import com.project.pharmacy.entity.Role;
import com.project.pharmacy.entity.User;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.InvalidatedTokenRepository;
import com.project.pharmacy.repository.RoleRepository;
import com.project.pharmacy.repository.UserRepository;
import com.project.pharmacy.repository.httpclient.OutboundIdentityClient;
import com.project.pharmacy.repository.httpclient.OutboundUserClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    UserRepository userRepository;

    InvalidatedTokenRepository invalidatedTokenRepository;

    OutboundIdentityClient outboundIdentityClient;

    OutboundUserClient outboundUserClient;

    RoleRepository roleRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @NonFinal
    @Value("${outbound.identity.client-id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${outbound.identity.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${outbound.identity.redirect-uri}")
    protected String REDIRECT_URI;

    @NonFinal
    protected String GRANT_TYPE = "authorization_code";

    //Outbound authenticate login with google in web
    public AuthenticationResponse outboundAuthenticateWeb(String code) {
        // ExchangeToken from code
        ExchangeTokenResponse response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());

        // Get User Info
        OutboundUserResponse userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());

        /*`https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=${accessToken}`*/

        // On board User
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = userRepository
                .findByUsername(userInfo.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .username(userInfo.getEmail())
                        .firstname(userInfo.getGivenName())
                        .lastname(userInfo.getFamilyName())
                        .status(true)
                        .isVerified(true)
                        .roles(roles)
                        .build()));
        log.info("User Info: {}", userInfo);

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    //Out bound authenticate login with google on android
    public AuthenticationResponse outboundAuthenticationAndroid(OutboundAuthenticationAndroid request){
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = userRepository
                .findByUsername(request.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .username(request.getEmail())
                        .firstname(request.getGivenName())
                        .lastname(request.getFamilyName())
                        .image(request.getPhoto())
                        .status(true)
                        .isVerified(true)
                        .roles(roles)
                        .build()));

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    // Log-in
    public AuthenticationResponse authenticate(SignInRequest request) { // login
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)); // Check username

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword()); // Check password
        if (!authenticated)
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);

        if (!user.getStatus())
            throw new AppException(ErrorCode.USER_HAS_BEEN_BAN);

        if (!user.getIsVerified()) {
            throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    private String generateToken(User user) { // 1 TOKEN Gom: Header, Payload, Signature
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512); // Header(Chua thuat toan dung de ki cho Signature)

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder() // Tao Data cho Payload - Data trong Payload goi la Claim
                .subject(user.getUsername()) // Dai dien cho user dang dang nhap
                .issuer("pharmacy.com") // Xac dinh token duoc issue tu ai, thuong se dung domain cua service
                .issueTime(new Date()) // Thoi diem tao ra token
                .expirationTime(new Date(Instant.now()
                        .plus(VALID_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli())) // Token het han sau 1 gio
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject()); // Payload(Chua thong tin Token goi di nhu username, role)

        JWSObject jwsObject = new JWSObject(header, payload); // Tao moi 1 JWT

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject
                    .serialize(); // Thuat toan ki nay co key ma hoa va key giai ma giong nhau, can 1 Secret la 1 chuoi
            // 32 bytes
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) { // Lay ra role cua user de cho vao scope trong token
        StringJoiner stringJoiner = new StringJoiner(" ");
        log.info("User roles: " + user.getRoles());

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
            });
        }

        return stringJoiner.toString();
    }

    // Introspect Token
    public IntrospectTokenResponse introspectToken(InstrospectTokenRequest request)
            throws JOSEException, ParseException { // kiem tra token co con hieu luc
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false; // token het han / user log-out
        }
        return IntrospectTokenResponse.builder().valid(isValid).build();
    }

    // Log-out
    public void logout(SignOutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(expiryTime)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired");
        }
    }

    // Refresh token
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        var signJWT = verifyToken(request.getToken(), true);

        var jti = signJWT.getJWTClaimsSet().getJWTID();
        var expityTime = signJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(expityTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);

        var username = signJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                        .getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        // token in invalidatetoken
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }
}
