package com.Lobo.KakaoLoginTest.JPA.Repository;

import com.Lobo.KakaoLoginTest.JPA.Entity.HourlyDataUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HourlyDataUsageRepository extends JpaRepository<HourlyDataUsage, Long> {

    List<HourlyDataUsage> findAllByDevice_deviceId(String deviceId);

    @Query(value = "SELECT h.usage_time FROM hourly_data_usage h WHERE h.device_id = :deviceId AND h.usage_time IN :usageTimes", nativeQuery = true)
    List<Object[]> findByDevice_deviceIdAndUsageTimes(@Param("deviceId") String deviceId, @Param("usageTimes") List<LocalDateTime> usageTimes);
}
