package com.example._th_project.controller;

import com.example._th_project.domain.dto.*;
import com.example._th_project.domain.table.Inquiry;
import com.example._th_project.services.InquiryService;
import com.example._th_project.status.DefaultRes;
import com.example._th_project.status.ResponseMessage;
import com.example._th_project.status.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;


    @PostMapping("/users/{userId}/inquiries")
    public ResponseEntity<DefaultRes<String>> registerInquiry(@PathVariable Long userId,
                                                              @RequestBody InquiryRegisterDTO dto){
        if(inquiryService.registerInquiry(userId, dto)){
            return ResponseEntity.ok(DefaultRes.res(StatusCode.OK, "문의 등록 성공", "사용자님의 문의가 정상적으로 등록되었습니다."));
        }
        else{
            return ResponseEntity.ok(DefaultRes.res(StatusCode.BAD_REQUEST, "문의 등록 실패", "사용자님의 문의 등록이 실패되었습니다."));
        }
    }


    @GetMapping("/users/{userId}/inquiries")
    public ResponseEntity<DefaultRes<InquiryListResponseDTO>> inquiries(@PathVariable Long userId){

        List<InquiryDTO> inquiryDTOS = inquiryService.inquiryList(userId);
        InquiryListResponseDTO dto = new InquiryListResponseDTO(inquiryDTOS);


        return new ResponseEntity<>(DefaultRes.res(StatusCode.OK, ResponseMessage.TRANSMISSION_SUCCESS, dto), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/{inquiryId}")
    public ResponseEntity<DefaultRes<InquiryDetailDTO>> inquiryDetail(@PathVariable Long inquiryId){

        InquiryDetailDTO dto = inquiryService.inquiryData(inquiryId);

        return new ResponseEntity<>(DefaultRes.res(StatusCode.OK, ResponseMessage.TRANSMISSION_SUCCESS, dto), HttpStatus.OK);
    }

    @DeleteMapping("/inquiries/{inquiryId}")
    public ResponseEntity<DefaultRes<String>> deleteInquiry(@PathVariable Long inquiryId){
        try {
            boolean deleted = inquiryService.delete(inquiryId);
            if(deleted){
                return ResponseEntity.ok(DefaultRes.res(StatusCode.OK, "문의 내역 삭제 성공", "문의 내역이 정상적으로 삭제되었습니다."));
            }
            else{
                return new ResponseEntity<>(DefaultRes.res(StatusCode.INTERNAL_SERVER_ERROR, "문의 정보 삭제 실패", null), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }catch (Exception e){
            return new ResponseEntity<>(DefaultRes.res(StatusCode.NOT_FOUND, "문의 정보 없음", null), HttpStatus.NOT_FOUND);
        }
    }
}
