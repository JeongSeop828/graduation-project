package com.example._th_project.domain.table;

import com.example._th_project.domain.InquiryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "inquiries")
@AllArgsConstructor
@Builder
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long inquiryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admins admin;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Lob
    @Column(name = "reply", columnDefinition = "LONGTEXT")
    private String reply;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private InquiryStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Builder
    public Inquiry(Users user, String title, String content){
        this.user = user;
        this.admin = null;
        this.title = title;
        this.content = content;
        this.reply = null;
        this.status = InquiryStatus.PENDING;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}