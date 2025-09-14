package com.example._th_project.controller;


import com.example._th_project.domain.dto.AdminLoginDto;
import com.example._th_project.domain.dto.AdminSignupDto;
import com.example._th_project.jwt.JwtUtil;
import com.example._th_project.jwt.TokenResponseDto;
import com.example._th_project.services.AdminAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final JwtUtil jwtUtil;

    // ✅ 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody AdminLoginDto requestDto) {
        TokenResponseDto response = adminAuthService.login(requestDto);
        return ResponseEntity.ok(response);
    }

    // ✅ 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("invalid refresh token");
        }

        String username = jwtUtil.getUsername(refreshToken);

        try {
            TokenResponseDto newTokens = adminAuthService.reissue(username, refreshToken);
            return ResponseEntity.ok(newTokens);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // ✅ 아이디 중복 확인
    @GetMapping("/check-id")
    public ResponseEntity<?> checkDuplicateId(@RequestParam String username) {
        boolean exists = adminAuthService.isUsernameTaken(username);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // ✅ 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("토큰 누락");
        }

        String token = bearerToken.substring(7);
        String username = jwtUtil.getUsername(token);

        adminAuthService.logout(username, token); // ✅ access 토큰도 같이 전달

        return ResponseEntity.ok("로그아웃 완료");
    }

    // ✅ 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody AdminSignupDto dto) {
        adminAuthService.signup(dto);
        return ResponseEntity.ok("관리자 회원가입 성공");
    }
}
