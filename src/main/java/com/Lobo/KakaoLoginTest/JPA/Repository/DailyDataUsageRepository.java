package com.Lobo.KakaoLoginTest.JPA.Repository;

import com.Lobo.KakaoLoginTest.JPA.Entity.DailyDataUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DailyDataUsageRepository extends JpaRepository<DailyDataUsage, Long> {

    List<DailyDataUsage> findAllByDevice_deviceId(String deviceId);

    @Query(value = "SELECT d.usage_time FROM daily_data_usage d WHERE d.device_id = :deviceId AND d.usage_time IN :usageTimes", nativeQuery = true)
    List<Object[]> findByDevice_deviceIdAndUsageTimes(@Param("deviceId") String deviceId, @Param("usageTimes") List<LocalDateTime> usageTimes);
}
