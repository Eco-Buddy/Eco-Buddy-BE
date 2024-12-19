package com.Son.EcoBuddy.JPA.Service;

import com.Son.EcoBuddy.JPA.Dto.PetDto;
import com.Son.EcoBuddy.JPA.Entity.Pet;
import com.Son.EcoBuddy.JPA.Repository.PetRepository;
import com.Son.EcoBuddy.JPA.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemService itemService;

    @Transactional
    public void createPet(String user_id, String pet_name) {
        Pet new_pet = Pet.builder()
                        .petName(pet_name)
                        .experience(0)
                        .petLevel(1)
                        .points(1000)
                        .user(userRepository.findById(user_id).get())
                        .background(1001)
                        .floor(2001)
                        .mission(3)
                        .build();

        itemService.addItem(user_id, 1001);
        itemService.addItem(user_id, 2001);
        petRepository.save(new_pet);
    }

    @Transactional
    public void savePet(String user_id, PetDto request) {

        Optional<Pet> optionalPet = petRepository.findByUserId(user_id);

        if(optionalPet.isEmpty()) {
            throw new IllegalArgumentException("Pet not found for ID: " + user_id);
        }

        Pet pet = optionalPet.get();

        pet.setPetName(request.getPetName());
        pet.setPetLevel(request.getPetLevel());
        pet.setExperience(request.getExperience());
        pet.setPoints(request.getPoints());
        pet.setBackground(request.getBackground());
        pet.setFloor(request.getFloor());

        petRepository.save(pet);
    }

    public Pet loadPet(String userId) {
        Optional<Pet> optionalPet = petRepository.findByUserId(userId);

        if(optionalPet.isEmpty()) {
            throw new IllegalArgumentException("Pet not found for ID: " + userId);
        }

        return optionalPet.get();
    }

    public boolean findPet(String user_id) {
        Optional<Pet> optionalPet = petRepository.findByUserId(user_id);

        return optionalPet.isPresent();
    }

    @Transactional
    public void petMission(String user_id) throws IllegalAccessException {
        Optional<Pet> optionalPet = petRepository.findByUserId(user_id);

        if(optionalPet.isEmpty()) {
            throw new IllegalArgumentException("Pet not found for ID: " + user_id);
        }

        int mission_count = optionalPet.get().getMission();

        if(mission_count <= 0) {
            throw new IllegalAccessException("Remaining mission count is zero");
        }

        optionalPet.get().setMission(mission_count - 1);

        petRepository.save(optionalPet.get());
    }
}
