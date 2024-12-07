package com.Lobo.KakaoLoginTest.JPA.Service;

import com.Lobo.KakaoLoginTest.JPA.Dto.DataUsageDto;
import com.Lobo.KakaoLoginTest.JPA.Entity.HourlyDataUsage;
import com.Lobo.KakaoLoginTest.JPA.Entity.Device;
import com.Lobo.KakaoLoginTest.JPA.Repository.HourlyDataUsageRepository;

import com.Lobo.KakaoLoginTest.JPA.Repository.DeviceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HourlyDataUsageService {

    @Autowired
    private HourlyDataUsageRepository dataUsageRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Transactional
    public void saveDataUsages(String device_id, List<DataUsageDto> requests) {

        // device_id로 Device 엔티티 조회
        Optional<Device> optDevice = deviceRepository.findById(device_id);

        if(optDevice.isEmpty())
            return;

        // 요청에서 사용 기록 시간 추출
        List<LocalDateTime> usageTimes = requests.stream()
                .map(DataUsageDto::getUsageTime)
                .toList();

        // 데이터베이스에서 이미 존재하는 기록 가져오기
        List<Object[]> rawExistingUsageTimes = dataUsageRepository.findByDevice_deviceIdAndUsageTimes(device_id, usageTimes);

        List<LocalDateTime> existingUsageTimes = rawExistingUsageTimes.stream()
                .map(row -> ((Timestamp) row[0]).toLocalDateTime())
                .toList();

        // 중복되지 않은 사용 기록 필터링
        List<HourlyDataUsage> newUsages = requests.stream()
                .filter(request -> !existingUsageTimes.contains(request.getUsageTime()))
                .map(request -> HourlyDataUsage.builder()
                        .deviceId(optDevice.get())
                        .usageTime(request.getUsageTime())
                        .dataUsed(request.getDataUsed())
                        .wifiUsed(request.getWifiUsed())
                        .build())
                .toList();

        dataUsageRepository.saveAll(newUsages);
    }

    public List<HourlyDataUsage> loadDataUsage(String device_id) {
        return dataUsageRepository.findAllByDevice_deviceId(device_id);
    }
}
