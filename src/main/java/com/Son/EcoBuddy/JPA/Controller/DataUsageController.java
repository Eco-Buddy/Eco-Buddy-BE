package com.Son.EcoBuddy.JPA.Controller;

import com.Son.EcoBuddy.JPA.Dto.DataUsageDto;
import com.Son.EcoBuddy.Utility.TokenValidationResult;
import com.Son.EcoBuddy.JPA.Entity.DailyDataUsage;
import com.Son.EcoBuddy.JPA.Entity.HourlyDataUsage;
import com.Son.EcoBuddy.JPA.Service.DailyDataUsageService;
import com.Son.EcoBuddy.JPA.Service.HourlyDataUsageService;
import com.Son.EcoBuddy.JPA.Service.TokenValidationService;
import com.Son.EcoBuddy.Utility.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/dataUsage")
public class DataUsageController {

    @Autowired
    private HourlyDataUsageService hourlyDataUsageService;

    @Autowired
    private DailyDataUsageService dailyDataUsageService;

    @Autowired
    private TokenValidationService tokenValidationService;

    String new_accessToken;

    @PostMapping("/save/hourly")
    public ResponseEntity<Object> saveHourlyDataUsage(
            @RequestHeader("authorization") String accessToken,
            @RequestHeader("deviceId") String device_id,
            @RequestHeader("userId") String user_id,
            @RequestBody List<DataUsageDto> requests) {

        log.info(device_id + "sends hourly data.");

        try {
            TokenValidationResult result = tokenValidationService.validateAccessToken(user_id, device_id, accessToken);

            new_accessToken = AESUtil.decrypt(result.getSavedToken().getAccessToken());

            hourlyDataUsageService.saveDataUsages(device_id, requests);
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

    @PostMapping("/load/hourly")
    public ResponseEntity<Object> loadHourlyDataUsage(
            @RequestHeader("authorization") String accessToken,
            @RequestHeader("deviceId") String device_id,
            @RequestHeader("userId") String user_id
            ) {
        Map<String, Object> responseBody = new HashMap<>();

        try {
            TokenValidationResult result = tokenValidationService.validateAccessToken(user_id, device_id, accessToken);

            new_accessToken = AESUtil.decrypt(result.getSavedToken().getAccessToken());
            List<HourlyDataUsage> usages = hourlyDataUsageService.loadDataUsage(device_id);

            responseBody.put("new_accessToken", new_accessToken);
            responseBody.put("usage", usages);

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

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/save/daily")
    public ResponseEntity<Object> saveDailyDataUsage(
            @RequestHeader("authorization") String accessToken,
            @RequestHeader("deviceId") String device_id,
            @RequestHeader("userId") String user_id,
            @RequestBody List<DataUsageDto> requests) {

        log.info(device_id + "sends daily data.");

        try {
            TokenValidationResult result = tokenValidationService.validateAccessToken(user_id, device_id, accessToken);

            new_accessToken = AESUtil.decrypt(result.getSavedToken().getAccessToken());

            dailyDataUsageService.saveDataUsages(device_id, requests);

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

    @PostMapping("/load/daily")
    public ResponseEntity<Object> loadDailyDataUsage(
            @RequestHeader("authorization") String accessToken,
            @RequestHeader("deviceId") String device_id,
            @RequestHeader("userId") String user_id
    ) {
        Map<String, Object> responseBody = new HashMap<>();

        try {
            TokenValidationResult result = tokenValidationService.validateAccessToken(user_id, device_id, accessToken);

            new_accessToken = AESUtil.decrypt(result.getSavedToken().getAccessToken());
            List<DailyDataUsage> usages = dailyDataUsageService.loadDataUsage(device_id);

            responseBody.put("new_accessToken", new_accessToken);
            responseBody.put("usage", usages);

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

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/load/other/hourly")
    public ResponseEntity<Object> loadOtherHourlyDataUsage(
            @RequestHeader("authorization") String accessToken,
            @RequestHeader("deviceId") String device_id,
            @RequestHeader("userId") String user_id,
            @RequestHeader("searchDeviceId") String search_device_id
    ) {
        Map<String, Object> responseBody = new HashMap<>();

        try {
            TokenValidationResult result = tokenValidationService.validateAccessToken(user_id, device_id, accessToken);

            new_accessToken = AESUtil.decrypt(result.getSavedToken().getAccessToken());
            List<HourlyDataUsage> usages = hourlyDataUsageService.loadDataUsage(search_device_id);

            responseBody.put("new_accessToken", new_accessToken);
            responseBody.put("usage", usages);

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

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/load/other/daily")
    public ResponseEntity<Object> loadOtherDailyDataUsage(
            @RequestHeader("authorization") String accessToken,
            @RequestHeader("deviceId") String device_id,
            @RequestHeader("userId") String user_id,
            @RequestHeader("searchDeviceId") String search_device_id
    ) {
        Map<String, Object> responseBody = new HashMap<>();

        try {
            TokenValidationResult result = tokenValidationService.validateAccessToken(user_id, device_id, accessToken);

            new_accessToken = AESUtil.decrypt(result.getSavedToken().getAccessToken());
            List<DailyDataUsage> usages = dailyDataUsageService.loadDataUsage(search_device_id);

            responseBody.put("new_accessToken", new_accessToken);
            responseBody.put("usage", usages);

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

        return ResponseEntity.ok(responseBody);
    }
}
