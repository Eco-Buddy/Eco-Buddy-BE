package com.Lobo.KakaoLoginTest.JPA.Controller;

import com.Lobo.KakaoLoginTest.JPA.Service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @RequestMapping("/devices")
    public ResponseEntity<?> getDevices(@RequestHeader("userId") String user_id) {
        return ResponseEntity.ok(Map.of("deviceIds", deviceService.getDevicesByUserId(user_id)));
    }
}
