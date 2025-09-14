package com.example._th_project.domain.table;

import com.example._th_project.domain.AdminRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Admins {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AdminRole role = AdminRole.USER;

    @PrePersist
    private void prePersist() {
        if (role == null) role = AdminRole.USER;
    }
}
