package com.example._th_project.controller;

import com.example._th_project.domain.dto.*;
import com.example._th_project.services.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inquiries")
@RequiredArgsConstructor
public class AdminInquiryController {

    private final InquiryService inquiryService;

    @GetMapping
    public ResponseEntity<List<InquiryResponseDto>> getAllInquiries() {
        return ResponseEntity.ok(inquiryService.findAllInquiries());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InquiryResponseDto> getInquiry(@PathVariable Long id) {
        return ResponseEntity.ok(inquiryService.findInquiryById(id));
    }

    @PutMapping("/{id}/reply")
    public ResponseEntity<String> replyInquiry(
            @PathVariable Long id,
            @RequestBody AdminReplyRequestDto dto) {
        inquiryService.replyInquiry(id, dto);
        return ResponseEntity.ok("답변 완료");
    }
}
