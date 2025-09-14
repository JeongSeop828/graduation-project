package com.example._th_project.controller;

import com.example._th_project.domain.dto.*;
import com.example._th_project.domain.table.Pets;
import com.example._th_project.domain.table.Users;
import com.example._th_project.services.PetService;
import com.example._th_project.services.UserService;
import com.example._th_project.status.DefaultRes;
import com.example._th_project.status.ResponseMessage;
import com.example._th_project.status.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class PetController {

    private void errorPrint(Exception e) {
        ZonedDateTime zId = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        System.out.println( "[발생 시간] : " + zId.format(formatter) + "  에러 발생 : " + e.getMessage());
    }

    @Autowired
    private UserService userService;
    @Autowired
    private PetService petService;

    @PostMapping(value = "/users/{userId}/pets", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<DefaultRes<String>> registerPet(@PathVariable Long userId,
                                                          @RequestPart("petInfo") PetRegisterDTO petInfo,
                                                          @RequestPart("image") MultipartFile imageFile) throws IOException {

        petService.create(userId, petInfo, imageFile);

        return ResponseEntity.ok(DefaultRes.res(StatusCode.OK, "반려동물 등록 성공", "반려동물 정보가 정상적으로 등록되었습니다."));
    }

    @DeleteMapping("/users/pets/{petId}")
    public ResponseEntity<DefaultRes<String>> delPet(@PathVariable Long petId){
        try {
            boolean deleted = petService.deletePetById(petId);
            if (deleted) {
                return ResponseEntity.ok(DefaultRes.res(StatusCode.OK, "반려동물 정보 삭제 성공", "반려동물 정보가 정상적으로 삭제되었습니다."));
            } else {
                return new ResponseEntity<>(DefaultRes.res(StatusCode.INTERNAL_SERVER_ERROR, "반려동물 정보 삭제 실패", null), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(DefaultRes.res(StatusCode.NOT_FOUND, "반려동물 정보 없음", null), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "users/{userId}/pets/{petId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<DefaultRes<String>> changePet(@PathVariable Long userId,
                                                        @PathVariable Long petId,
                                                        @RequestPart("petInfo") PetRegisterDTO petInfo,
                                                        @RequestPart("image") MultipartFile imageFile) throws IOException{
        try {
            boolean changed = petService.changePet(petId, petInfo, imageFile);
            if (changed) {
                return ResponseEntity.ok(DefaultRes.res(StatusCode.OK, "반려동물 정보 수정 성공", "반려동물 정보가 정상적으로 수정되었습니다."));
            } else {
                return new ResponseEntity<>(DefaultRes.res(StatusCode.NOT_FOUND, "반려동물 정보 없음", null), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(DefaultRes.res(StatusCode.INTERNAL_SERVER_ERROR, "반려동물 정보 수정 실패", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{userId}/pets")
    public ResponseEntity<DefaultRes<PetListResponseDTO>> userPets(@PathVariable Long userId){

        List<PetList> petLists = petService.petLists(userId);
        PetListResponseDTO dto = new PetListResponseDTO(petLists);


        return new ResponseEntity<>(DefaultRes.res(StatusCode.OK, ResponseMessage.TRANSMISSION_SUCCESS, dto), HttpStatus.OK);

    }

    @GetMapping("/users/{userId}/pets/{petId}")
    public ResponseEntity<DefaultRes<PetDataResponseDTO>> petDetail(@PathVariable Long petId){

        PetDataResponseDTO dto = petService.petData(petId);

        return new ResponseEntity<>(DefaultRes.res(StatusCode.OK, ResponseMessage.TRANSMISSION_SUCCESS, dto), HttpStatus.OK);
    }

}
