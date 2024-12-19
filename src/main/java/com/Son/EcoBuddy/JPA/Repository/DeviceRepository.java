package com.Son.EcoBuddy.JPA.Repository;

import com.Son.EcoBuddy.JPA.Entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    List<Device> findAllByUserId(String userId);
}
