package com.example._th_project.controller;

import com.example._th_project.domain.dto.*;
import com.example._th_project.domain.table.Users;
import com.example._th_project.jwt.JwtAuthenticationFilter;
import com.example._th_project.jwt.JwtUtil;
import com.example._th_project.log.AccessLogService;
import com.example._th_project.repository.UsersRepository;
import com.example._th_project.services.UserService;
import com.example._th_project.status.DefaultRes;
import com.example._th_project.status.ResponseMessage;
import com.example._th_project.status.StatusCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService usersService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private AccessLogService accessLogService;

    private void errorPrint(Exception e) {
        ZonedDateTime zId = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        System.out.println( "[발생 시간] : " + zId.format(formatter) + "  에러 발생 : " + e.getMessage());
    }

    //아이디 중복 확인
    @PostMapping("/DuplicateTest")
    public ResponseEntity<StatusDTO> DuplicateTest(@RequestBody Users user)
    {
        StatusDTO dto = new StatusDTO();

        if(usersService.userFindByUserName(user.getUsername()) != null)
            dto.setStatus(0);
        else
            dto.setStatus(1);
        try
        {
            switch (dto.getStatus())
            {
                case 0:
                    dto.setMessage("중복된 아이디입니다.");
                    return new ResponseEntity(DefaultRes.res(StatusCode.DUPLICATE_ACCOUNT, ResponseMessage.ID_DUPLICATETEST_FAIL, dto), HttpStatus.OK);
                case 1:
                    dto.setMessage("가입 가능한 아이디입니다.");
                    break;
                default:
                    dto.setMessage("오류 발생");
                    dto.setErrCode("UserAPIController DuplicateTest쪽 확인바람");
                    return new ResponseEntity(DefaultRes.res(StatusCode.DUPLICATE_ACCOUNT, ResponseMessage.INTERNAL_SERVER_ERROR, dto), HttpStatus.OK);

            }
        }
        catch (Exception e)
        {
            errorPrint(e);
        }
        return new ResponseEntity(DefaultRes.res(StatusCode.OK,ResponseMessage.ID_DUPLICATETEST_SUCCESS, dto), HttpStatus.OK);
    }

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<DefaultRes<StatusDTO>> signup(@RequestBody Users user) {
        StatusDTO dto = new StatusDTO();

        try {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);

            usersService.create(user);
            dto.setStatus(1);
            dto.setMessage("회원가입 성공");
        } catch (Exception e) {
            errorPrint(e);
            dto.setStatus(0);
            dto.setMessage("회원가입 실패");
            dto.setErrCode("서버쪽 오류");
            return new ResponseEntity<>(DefaultRes.res(StatusCode.OK, ResponseMessage.CREATED_USER_FAIL, dto), HttpStatus.OK);
        }

        return new ResponseEntity<>(DefaultRes.res(StatusCode.OK, ResponseMessage.CREATED_USER, dto), HttpStatus.OK);
    }

    //사용자 상세 정보
    @GetMapping("/{userId}")
    public ResponseEntity<DefaultRes<UserResponseDto>> getUserById(@PathVariable Long userId) {
        Users user = usersService.userFindByUserId(userId);
        UserResponseDto  userResponseDto = new UserResponseDto(user);

        if (user != null) {
            return new ResponseEntity<>(DefaultRes.res(StatusCode.OK, "사용자 정보 조회 성공", userResponseDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(DefaultRes.res(StatusCode.NOT_FOUND, "해당 사용자를 찾을 수 없습니다.", null), HttpStatus.NOT_FOUND);
        }
    }

    //사용자 회원탈퇴
    @DeleteMapping("/{userId}")
    public ResponseEntity<DefaultRes<String>> delUser(@PathVariable Long userId) {
        try {
            boolean deleted = usersService.deleteUserById(userId);
            if (deleted) {
                return ResponseEntity.ok(DefaultRes.res(StatusCode.OK, "회원 탈퇴 성공", "회원이 정상적으로 삭제되었습니다."));
            } else {
                return new ResponseEntity<>(DefaultRes.res(StatusCode.NOT_FOUND, "회원 정보 없음", null), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(DefaultRes.res(StatusCode.INTERNAL_SERVER_ERROR, "회원 탈퇴 실패", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PatchMapping("/{userId}/password")
    public ResponseEntity<DefaultRes<String>> changePassword(
            @PathVariable Long userId,
            @RequestBody PasswordChangeRequestDto dto) {

        try {
            usersService.changePassword(userId, dto.getNewPassword());
            return ResponseEntity.ok(DefaultRes.res(StatusCode.OK, "비밀번호 변경 성공", null));
        } catch (Exception e) {
            return new ResponseEntity<>(DefaultRes.res(StatusCode.INTERNAL_SERVER_ERROR, "비밀번호 변경 중 오류 발생", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{userId}/verify-password")
    public ResponseEntity<DefaultRes<String>> verifyPassword(
            @PathVariable Long userId,
            @RequestBody PasswordVerifyRequestDto dto) {

        try {
            boolean match = usersService.verifyPassword(userId, dto.getCurrentPassword());

            if (match) {
                return ResponseEntity.ok(DefaultRes.res(StatusCode.OK, "비밀번호 일치", null));
            } else {
                return new ResponseEntity<>(DefaultRes.res(StatusCode.UNAUTHORIZED, "비밀번호 불일치", null), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(DefaultRes.res(StatusCode.INTERNAL_SERVER_ERROR, "비밀번호 확인 중 오류 발생", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{userId}/nickName")
    public ResponseEntity<DefaultRes<String>> changeNickName(
            @PathVariable Long userId,
            @RequestBody NickNameChangeRequestDTO dto) {

        try {
            boolean success = usersService.changeNickname(userId, dto.getNewNickname());

            if (success) {
                return ResponseEntity.ok(DefaultRes.res(StatusCode.OK, "닉네임 변경 성공", null));
            } else {
                return new ResponseEntity<>(DefaultRes.res(StatusCode.UNAUTHORIZED, "입력 오류", null), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(DefaultRes.res(StatusCode.INTERNAL_SERVER_ERROR, "닉네임 변경 중 오류 발생", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<DefaultRes<TokenResponseDTO>> login(@RequestBody LoginRequestDTO loginRequestDTO){
        Users user = usersRepository.findByUsername(loginRequestDTO.getUsername())
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        if(!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())){
            return new ResponseEntity<>(DefaultRes.res(StatusCode.UNAUTHORIZED, "아이디/비밀번호를 확인해주세요.", null), HttpStatus.UNAUTHORIZED);
        }

        accessLogService.saveAccessLog(user);

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), "USER");
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), "USER");

        redisTemplate.opsForValue().set(loginRequestDTO.getUsername(), refreshToken);
        System.out.println("로그인한 사용자 ID: " + user.getId());
        TokenResponseDTO dto = new TokenResponseDTO(accessToken, refreshToken, user.getId());

        return new ResponseEntity<>(DefaultRes.res(StatusCode.OK, "로그인 성공", dto), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<DefaultRes<String>> logout(HttpServletRequest request){
        String token = jwtUtil.resolveToken(request);
        String username = jwtUtil.getUsername(token);

        long remaining = jwtUtil.getRemainingMillis(token);
        redisTemplate.opsForValue().set("BLACKLIST:" + token, "logout", remaining, TimeUnit.MILLISECONDS);

        redisTemplate.delete(username);

        return new ResponseEntity<>(DefaultRes.res(StatusCode.OK, "로그아웃 성공", null), HttpStatus.OK);
    }
}
