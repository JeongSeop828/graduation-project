package com.example._th_project.services;

import com.example._th_project.domain.InquiryStatus;
import com.example._th_project.domain.dto.*;
import com.example._th_project.domain.table.Admins;
import com.example._th_project.domain.table.Inquiry;
import com.example._th_project.domain.table.Users;
import com.example._th_project.repository.AdminRepository;
import com.example._th_project.repository.InquiryRepository;
import com.example._th_project.repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private AdminRepository adminRepository;

    @Transactional
    public boolean registerInquiry(Long userId, InquiryRegisterDTO dto){
        if(usersRepository.existsById(userId)){
            Users user = usersRepository.findUsersByUserId(userId);
            inquiryRepository.save(new Inquiry(user, dto.getTitle(), dto.getContent()));
            return true;
        }
        else{
            throw new NoSuchElementException("유저를 찾을 수 없습니다.");
        }
    }

    @Transactional
    public List<InquiryDTO> inquiryList(Long userId){
        Optional<Users> optionalUser = usersRepository.findById(userId);

        if(optionalUser.isEmpty()){
            throw new NoSuchElementException("유저정보 없음");
        }

        List<InquiryDTO> dto = inquiryRepository.findInquiryDTObyUserId(userId);

        return dto;
    }

    @Transactional
    public InquiryDetailDTO inquiryData(@PathVariable Long inquiryId){
        Optional<Inquiry> optionalInquiry = inquiryRepository.findById(inquiryId);

        if(optionalInquiry.isEmpty()){
            throw new NoSuchElementException("문의 정보 없음");
        }

        Inquiry finder = optionalInquiry.get();

        InquiryDetailDTO dto = new InquiryDetailDTO(finder);

        dto.setStatus(finder.getStatus().getDescription());

        return dto;

    }

    public boolean delete(Long inquiryId) {
        if(inquiryRepository.existsById(inquiryId)){
            inquiryRepository.deleteById(inquiryId);

            return true;
        }
        else{
            return false;
        }
    }

    public List<InquiryResponseDto> findAllInquiries() {
        return inquiryRepository.findAllWithUser().stream()
                .map(InquiryResponseDto::new)
                .toList();
    }

    // 단건 문의 조회 (User 조인 포함)
    public InquiryResponseDto findInquiryById(Long id) {
        Inquiry inquiry = inquiryRepository.findByIdWithUser(id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));
        return new InquiryResponseDto(inquiry);
    }

    // 문의 답변 처리
    public void replyInquiry(Long inquiryId, AdminReplyRequestDto dto) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));

        Admins admin = adminRepository.findById(dto.getAdminId()) // ✅ 영속 객체 조회
                .orElseThrow(() -> new RuntimeException("관리자가 존재하지 않습니다."));

        inquiry.setReply(dto.getReply());
        inquiry.setAdmin(admin); // ✅ 정상적으로 설정
        inquiry.setStatus(InquiryStatus.COMPLETED);

        inquiryRepository.save(inquiry);
    }
}
