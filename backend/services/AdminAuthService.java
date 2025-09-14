package com.example._th_project.services;


import com.example._th_project.domain.AdminRole;
import com.example._th_project.domain.dto.AdminLoginDto;
import com.example._th_project.domain.dto.AdminSignupDto;
import com.example._th_project.domain.table.Admins;
import com.example._th_project.jwt.JwtProperties;
import com.example._th_project.jwt.JwtUtil;
import com.example._th_project.jwt.TokenResponseDto;
import com.example._th_project.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    /* 로그인 */
    public TokenResponseDto login(AdminLoginDto requestDto) {
        Admins admin = adminRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String username = admin.getUsername();
        String role     = admin.getRole().name();

        String accessToken  = jwtUtil.generateAccessToken(username, role);
        String refreshToken = jwtUtil.generateRefreshToken(username, role);

        redisTemplate.opsForValue().set(
                "refresh:" + username + ":latest",
                refreshToken,
                jwtProperties.getRefreshTokenExpiration(),
                TimeUnit.MILLISECONDS
        );

        return new TokenResponseDto(accessToken, refreshToken, admin.getId());
    }

    /* 회원가입 (기본 권한 USER) */
    public void signup(AdminSignupDto dto) {
        if (adminRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 관리자 아이디입니다.");
        }

        Admins admin = Admins.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .role(AdminRole.USER)
                .build();

        adminRepository.save(admin);
    }

    /* 로그아웃 */
    public void logout(String username, String accessToken) {
        redisTemplate.delete("refresh:" + username + ":latest");
        redisTemplate.delete("refresh:" + username + ":prev");

        long remainingMillis = jwtUtil.getRemainingMillis(accessToken);
        if (remainingMillis > 0) {
            redisTemplate.opsForValue().set(
                    "blacklist:" + accessToken,
                    "logout",
                    remainingMillis,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    /* 토큰 재발급 */
    public TokenResponseDto reissue(String username, String requestRefreshToken) {
        String latestKey = "refresh:" + username + ":latest";
        String prevKey   = "refresh:" + username + ":prev";

        String latest = redisTemplate.opsForValue().get(latestKey);
        String prev   = redisTemplate.opsForValue().get(prevKey);

        if (!(requestRefreshToken.equals(latest) || requestRefreshToken.equals(prev))) {
            throw new IllegalArgumentException("Refresh Token이 유효하지 않습니다.");
        }

        Admins admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));

        String role = admin.getRole().name();

        String newAccessToken  = jwtUtil.generateAccessToken(username, role);
        String newRefreshToken = jwtUtil.generateRefreshToken(username, role);

        if (latest != null) {
            redisTemplate.opsForValue().set(prevKey, latest, 10, TimeUnit.SECONDS);
        }

        redisTemplate.opsForValue().set(
                latestKey,
                newRefreshToken,
                jwtProperties.getRefreshTokenExpiration(),
                TimeUnit.MILLISECONDS
        );

        return new TokenResponseDto(newAccessToken, newRefreshToken, admin.getId());
    }

    public boolean isUsernameTaken(String username) {
        return adminRepository.existsByUsername(username);
    }
}
