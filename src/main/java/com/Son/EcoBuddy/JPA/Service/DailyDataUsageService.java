package com.Son.EcoBuddy.JPA.Service;

import com.Son.EcoBuddy.JPA.Dto.DataUsageDto;
import com.Son.EcoBuddy.JPA.Entity.DailyDataUsage;
import com.Son.EcoBuddy.JPA.Entity.Device;
import com.Son.EcoBuddy.JPA.Repository.DeviceRepository;
import com.Son.EcoBuddy.JPA.Repository.DailyDataUsageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DailyDataUsageService {

    @Autowired
    private DailyDataUsageRepository dataUsageRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Transactional
    public void saveDataUsages(String device_id, List<DataUsageDto> requests) {

        Optional<Device> optDevice = deviceRepository.findById(device_id);

        if(optDevice.isEmpty())
            return;

        List<LocalDateTime> usageTimes = requests.stream()
                .map(DataUsageDto::getUsageTime)
                .toList();

        List<Object[]> rawExistingUsageTimes = dataUsageRepository.findByDevice_deviceIdAndUsageTimes(device_id, usageTimes);

        List<LocalDateTime> existingUsageTimes = rawExistingUsageTimes.stream()
                .map(row -> ((Timestamp) row[0]).toLocalDateTime())
                .toList();

        List<DailyDataUsage> newUsages = requests.stream()
                .filter(request -> !existingUsageTimes.contains(request.getUsageTime()))
                .map(request -> DailyDataUsage.builder()
                        .deviceId(optDevice.get())
                        .usageTime(request.getUsageTime())
                        .dataUsed(request.getDataUsed())
                        .wifiUsed(request.getWifiUsed())
                        .build())
                .toList();

        dataUsageRepository.saveAll(newUsages);
    }

    public List<DailyDataUsage> loadDataUsage(String device_id) {
        return dataUsageRepository.findAllByDevice_deviceId(device_id);
    }
}
