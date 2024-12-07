package com.Lobo.KakaoLoginTest.JPA.Service;

import com.Lobo.KakaoLoginTest.JPA.Entity.Device;
import com.Lobo.KakaoLoginTest.JPA.Repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    public List<String> getDevicesByUserId(String userId) {
        // userId로 모든 Device 조회
        List<Device> devices =  deviceRepository.findAllByUserId(userId);
        ArrayList<String> result = new ArrayList<>();

        for(Device device : devices) {
            result.add(device.getDeviceId());
        }

        return result;
    }
}
