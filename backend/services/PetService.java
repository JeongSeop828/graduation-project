package com.example._th_project.services;

import com.example._th_project.domain.dto.PetDataResponseDTO;
import com.example._th_project.domain.dto.PetList;
import com.example._th_project.domain.dto.PetRegisterDTO;
import com.example._th_project.domain.table.Pets;
import com.example._th_project.domain.table.Users;
import com.example._th_project.repository.PetRepository;
import com.example._th_project.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Service
public class PetService {

    private PetRepository petRepository;
    private UsersRepository usersRepository;

    public Pets create(Long userId, PetRegisterDTO dto, MultipartFile img) throws IOException {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        byte[] imgData = img.getBytes();
        Pets pet = new Pets(user, dto, imgData);

        petRepository.save(pet);

        return pet;
    }

    public boolean deletePetById(Long petId){
        if(petRepository.existsById(petId)){
            petRepository.deleteById(petId);
            return true;
        }
        else {
            return false;
        }
    }

    @Transactional
    public boolean changePet(Long petId, PetRegisterDTO pet, MultipartFile img) throws IOException{
        Optional<Pets> optionalPet = petRepository.findById(petId);

        if(optionalPet.isEmpty()){
            throw new NoSuchElementException("반려동물 정보 없음");
        }

        byte[] imgData = img.getBytes();

        Pets findPet = optionalPet.get();

        findPet.setPetName(pet.getPetName());
        findPet.setAge(pet.getAge());
        findPet.setBreed(pet.getBreed());
        findPet.setWeight(pet.getWeight());
        findPet.setSpecies(pet.getSpecies());
        findPet.setGender(pet.getGender());
        findPet.setPetImg(imgData);

        return true;
    }

    @Transactional
    public List<PetList> petLists(Long userId){
        Optional<Users> optionalUser = usersRepository.findById(userId);

        if(optionalUser.isEmpty()){
            throw new NoSuchElementException("유저정보 없음");
        }

        List<Long> petIds = petRepository.findByUserId(userId);

        List<PetList> petLists = new ArrayList<>();

        for( Long l : petIds){
            PetList petlist = petRepository.findPetListByPetId(l);

            petLists.add(petlist);
        }

        return petLists;
    }

    @Transactional
    public PetDataResponseDTO petData(Long petId){
        Optional<Pets> optionalPet = petRepository.findById(petId);

        if(optionalPet.isEmpty()){
            throw new NoSuchElementException("반려동물 정보 없음");
        }

        Pets finder = optionalPet.get();

        return new PetDataResponseDTO(finder);
    }
}
