package com.example._th_project.services;

import com.example._th_project.domain.dto.LoginRequestDTO;
import com.example._th_project.domain.dto.TokenResponseDTO;
import com.example._th_project.domain.table.Users;
import com.example._th_project.jwt.JwtUtil;
import com.example._th_project.repository.UsersRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PersistenceContext
    private EntityManager entityManager; // EntityManager는 @PersistenceContext로 주입

    @Transactional
    public Users userFindByUserName(String userName) {
        return usersRepository.findByUsername(userName).orElse(null);
    }

    @Transactional
    public Users userFindByUserId(Long userid) {
        return usersRepository.findById(userid).orElse(null);
    }

    public Users create(Users user) {
        usersRepository.save(user);
        return user;
    }

    @Transactional
    public Long getUserIdByUsername(String username) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        return user.getId();
    }

    @Transactional
    public boolean deleteUserById(Long userId) {
        try {
            // 외래 키 제약을 일시적으로 비활성화
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

            // 사용자 삭제 (CascadeType.ALL로 연결된 엔티티도 자동 삭제)
            Users user = usersRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

            usersRepository.delete(user);
            entityManager.flush(); // 즉시 반영

            // 외래 키 제약 다시 활성화
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean changePassword(Long userId, String newPassword) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자 없음"));

        user.setPassword(passwordEncoder.encode(newPassword));
        return true;
    }

    @Transactional
    public boolean verifyPassword(Long userId, String currentPassword) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자 없음"));

        return passwordEncoder.matches(currentPassword, user.getPassword());
    }

    @Transactional
    public boolean changeNickname(Long userId, String newNickname) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자 없음"));

        user.setNickname(newNickname);
        return true;
    }

    @Transactional
    public TokenResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Users user = usersRepository.findByUsername(loginRequestDTO.getUsername())
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), "ROLE_USER");
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), "ROLE_USER");

        return new TokenResponseDTO(accessToken, refreshToken, user.getId());
    }
}
