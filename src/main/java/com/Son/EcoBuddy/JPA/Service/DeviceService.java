package com.Son.EcoBuddy.JPA.Service;

import com.Son.EcoBuddy.JPA.Entity.Device;
import com.Son.EcoBuddy.JPA.Repository.DeviceRepository;
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
