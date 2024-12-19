package com.Son.EcoBuddy.JPA.Controller;

import com.Son.EcoBuddy.JPA.Dto.PetDto;
import com.Son.EcoBuddy.Utility.TokenValidationResult;
import com.Son.EcoBuddy.JPA.Entity.Pet;
import com.Son.EcoBuddy.JPA.Service.PetService;
import com.Son.EcoBuddy.JPA.Service.TokenValidationService;
import com.Son.EcoBuddy.Utility.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private TokenValidationService tokenValidationService;

    String new_accessToken;

    @PostMapping("/create")
    public ResponseEntity<Object> createPet(
            @RequestHeader("authorization") String accessToken,
            @RequestHeader("deviceId") String device_id,
            @RequestHeader("userId") String user_id,
            @RequestParam String petName) {

        try {
            TokenValidationResult result = tokenValidationService.validateAccessToken(user_id, device_id, accessToken);

            new_accessToken = AESUtil.decrypt(result.getSavedToken().getAccessToken());
            petService.createPet(user_id, petName);
        } catch (IllegalArgumentException e) {
            // 사용자 ID나 토큰이 없는 경우
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            // 유효하지 않은 access_token
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (IllegalStateException e) {
            // 토큰 갱신 간 문제 발생
            return ResponseEntity.status(500).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(Map.of("new_accessToken", new_accessToken));
    }

    @PostMapping("/save")
    public ResponseEntity<Object> savePet(
            @RequestHeader("authorization") String accessToken,
            @RequestHeader("deviceId") String device_id,
            @RequestHeader("userId") String user_id,
            @RequestBody PetDto request) {

        try {
            TokenValidationResult result = tokenValidationService.validateAccessToken(user_id, device_id, accessToken);

            new_accessToken = AESUtil.decrypt(result.getSavedToken().getAccessToken());
            petService.savePet(user_id, request);
        } catch (IllegalArgumentException e) {
            // 사용자 ID나 토큰, 펫이 없는 경우
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            // 유효하지 않은 access_token
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (IllegalStateException e) {
            // 토큰 갱신 간 문제 발생
            return ResponseEntity.status(500).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(Map.of("new_accessToken", new_accessToken));
    }

    @PostMapping("/load")
    public ResponseEntity<Object> loadPet(
        @RequestHeader("authorization") String accessToken,
        @RequestHeader("deviceId") String device_id,
        @RequestHeader("userId") String user_id
    ) {
        Map<String, Object> responseBody = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Content-Type", "application/json;charset=UTF-8");

        try {
            TokenValidationResult result = tokenValidationService.validateAccessToken(user_id, device_id, accessToken);

            new_accessToken = AESUtil.decrypt(result.getSavedToken().getAccessToken());

            Pet pet_info = petService.loadPet(user_id);
            PetDto petDto = PetDto.builder()
                    .petName(pet_info.getPetName())
                    .petLevel(pet_info.getPetLevel())
                    .experience(pet_info.getExperience())
                    .points(pet_info.getPoints())
                    .background(pet_info.getBackground())
                    .floor(pet_info.getFloor())
                    .mission(pet_info.getMission())
                    .build();

            responseBody.put("new_accessToken", new_accessToken);
            responseBody.put("pet", petDto);

        } catch (IllegalArgumentException e) {
            // 사용자 ID나 토큰, 펫이 없는 경우
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            // 유효하지 않은 access_token
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (IllegalStateException e) {
            // 토큰 갱신 간 문제 발생
            return ResponseEntity.status(500).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);
    }

    @PostMapping("/mission")
    public ResponseEntity<Object> missionRequest(
            @RequestHeader("authorization") String accessToken,
            @RequestHeader("deviceId") String device_id,
            @RequestHeader("userId") String user_id
    ) {
        try {
            TokenValidationResult result = tokenValidationService.validateAccessToken(user_id, device_id, accessToken);
            new_accessToken = AESUtil.decrypt(result.getSavedToken().getAccessToken());

            petService.petMission(user_id);

        } catch (IllegalArgumentException e) {
            // 사용자 ID나 토큰, 펫이 없는 경우
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            // 유효하지 않은 access_token
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (IllegalAccessException e) {
            // 남은 미션 횟수가 0
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalStateException e) {
            // 토큰 갱신 간 문제 발생
            return ResponseEntity.status(500).body(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(Map.of("new_accessToken", new_accessToken));
    }
}
