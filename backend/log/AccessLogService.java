package com.example._th_project.log;

import com.example._th_project.domain.table.Users;
import com.example._th_project.repository.UserAccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AccessLogService {

    private final UserAccessLogRepository logRepository;

    public void saveAccessLog(Users user) {
        System.out.println(">>> AccessLogService 호출, userId=" + user.getId());
        // 서울 타임존으로 날짜 고정
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        if (!logRepository.existsByUserAndAccessDate(user, today)) {
            logRepository.save(UserAccessLog.builder()
                    .user(user)
                    .accessDate(today)
                    .build());
        }
    }
}
