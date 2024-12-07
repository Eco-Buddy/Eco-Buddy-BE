package com.Lobo.KakaoLoginTest.JPA.Repository;

import com.Lobo.KakaoLoginTest.JPA.Entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    List<Device> findAllByUserId(String userId);
}
