package com.Son.EcoBuddy.JPA.Controller;

import com.Son.EcoBuddy.JPA.Entity.Item;
import com.Son.EcoBuddy.JPA.Service.ItemService;
import com.Son.EcoBuddy.JPA.Service.TokenValidationService;
import com.Son.EcoBuddy.Utility.AESUtil;
import com.Son.EcoBuddy.Utility.TokenValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private TokenValidationService tokenValidationService;

    String new_accessToken;

    @PostMapping("/save")
    public ResponseEntity<Object> saveItem(
            @RequestHeader("authorization") String accessToken,
            @RequestHeader("deviceId") String device_id,
            @RequestHeader("userId") String user_id,
            @RequestParam int item_id) {

        try {
            TokenValidationResult result = tokenValidationService.validateAccessToken(user_id, device_id, accessToken);

            new_accessToken = AESUtil.decrypt(result.getSavedToken().getAccessToken());

            itemService.addItem(user_id, item_id);
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

    @PostMapping("/load")
    public ResponseEntity<Object> loadItem(
            @RequestHeader("authorization") String accessToken,
            @RequestHeader("deviceId") String device_id,
            @RequestHeader("userId") String user_id,
            @RequestParam int range) {
        Map<String, Object> responseBody = new HashMap<>();

        try {
            TokenValidationResult result = tokenValidationService.validateAccessToken(user_id, device_id, accessToken);

            new_accessToken = AESUtil.decrypt(result.getSavedToken().getAccessToken());
            List<Item> items = itemService.loadItem(user_id, range);

            responseBody.put("new_accessToken", new_accessToken);
            responseBody.put("items", items);

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
