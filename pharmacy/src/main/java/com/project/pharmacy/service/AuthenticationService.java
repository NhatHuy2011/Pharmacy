package com.project.pharmacy.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.project.pharmacy.dto.request.AuthenticateRequest;
import com.project.pharmacy.dto.request.InstrospectTokenRequest;
import com.project.pharmacy.dto.response.AuthenticationResponse;
import com.project.pharmacy.dto.response.IntrospectTokenResponse;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    UserRepository userRepository;

    @NonFinal
    @Value("${jwt.signerKey}")//key secret nen dat o file yml de team devops co the thay doi
    protected String SIGNER_KEY;

    public AuthenticationResponse authenticate(AuthenticateRequest request){
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));//Check username

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());//Check password
        if(!authenticated)
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);

        var token = generateToken(request.getUsername());

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    private String generateToken(String username){//1 TOKEN Gom: Header, Payload, Signature
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512); //Header(Chua thuat toan dung de ki cho Signature)

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder() // Tao Data cho Payload - Data trong Payload goi la Claim
                .subject(username)//Dai dien cho user dang dang nhap
                .issuer("pharmacy.com")//Xac dinh token duoc issue tu ai, thuong se dung domain cua service
                .issueTime(new Date())//Thoi diem tao ra token
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))//Token het han sau 1 gio
                .claim("customClaim", "Custom")
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());//Payload(Chua thong tin Token goi di nhu username, role)

        JWSObject jwsObject = new JWSObject(header, payload);//Tao moi 1 JWT

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();//Thuat toan ki nay co key ma hoa va key giai ma giong nhau, can 1 Secret la 1 chuoi 32 bytes
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    public IntrospectTokenResponse introspectToken(InstrospectTokenRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime =  signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return IntrospectTokenResponse.builder()
                .valid(verified && expiryTime.after(new Date()))
                .build();
    }
}
