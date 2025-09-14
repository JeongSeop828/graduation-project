package com.example._th_project.log;

import com.example._th_project.domain.table.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_access_log", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "access_date"})  // 같은 날 중복 방지
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "access_date", nullable = false)
    private LocalDate accessDate;
}
