package com.Son.EcoBuddy.Utility;

import com.Son.EcoBuddy.JPA.Repository.PetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MissionScheduler {

    @Autowired
    private PetRepository petRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void missionRemainInit() {
        log.info("미션 횟수 초기화");
        petRepository.resetMission();
    }
}
