package com.zerobase.memberapi.security;

import com.zerobase.memberapi.service.CustomerService;
import com.zerobase.memberapi.service.SellerService;
import com.zerobase.memberapi.util.Aes256Util;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
@RefreshScope // 특정행동 수행시 변경된 설정파일의 설정이 앱의 재배포 과정없이 실시간 반영
public class TokenProvider {

    private static final long TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24; // token 만료시간 : 하루
    private static final String KEY_ROLES = "roles";
    private final CustomerService customerService;
    private final SellerService sellerService;
    public static final String TOKEN_PREFIX = "Bearer ";

    @Value("${spring.jwt.secret}")
    private String secretKey;

    // 토큰 생성
    public String generateToken(Long id, String email, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(Aes256Util.encrypt(email))
                .setId(Aes256Util.encrypt(id.toString())); // 사용자 정보 암호화해 저장
        claims.put(KEY_ROLES, roles); // 권한 정보 저장
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME); // 만료 기간 : 1일


        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 생성 시간
                .setExpiration(expiryDate) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS512) // 사용할 암호화 알고리즘, 비밀키
                .compact();

    }

    // 토큰 이용해 인증 정보 반환
    public Authentication getAuthentication(String token) {
        System.out.println("$$$$$$$$$$$$$$$$$$$");
        System.out.println(this.getUserRole(token));
        UserDetails userDetails;

        if("[ROLE_CUSTOMER]".equals(this.getUserRole(token))) {
            userDetails = customerService.loadUserByUsername(this.getUsernameFromToken(token));
        }else{
            userDetails = sellerService.loadUserByUsername(this.getUsernameFromToken(token));
        }
        log.info("{} {}", userDetails.getAuthorities(), userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // 토큰이용해 유저 이메일 복호화해 리턴
    public String getUsernameFromToken(String token) {
        return Aes256Util.decrypt(this.parseClaims(token).getSubject());
    }

    // 토큰이용해 유저 id 복호화해 리턴
    public Long getUserIdFromToken(String token) {
        String bearer = token.substring(TOKEN_PREFIX.length());
        return Long.valueOf(Objects.requireNonNull(Aes256Util.decrypt(this.parseClaims(bearer).getId())));
    }

    // 토큰이용해 유저 권한 복호화해 리턴
    public String getUserRole(String token) {
        return this.parseClaims(token).get(KEY_ROLES).toString();
    }

    // jwt 파싱해 claims 얻어옴
    private Claims parseClaims(String token) {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Jwts.parserBuilder().setSigningKey(keyBytes).build().parseClaimsJws(token).getBody();

        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // 토큰 만료되었는지 확인 (1일이 지났는지)
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) return false;

        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(keyBytes).build().parseClaimsJws(token);

            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (SecurityException | MalformedJwtException e) {
            throw new SecurityException(e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
