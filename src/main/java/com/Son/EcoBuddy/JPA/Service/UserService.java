package com.Son.EcoBuddy.JPA.Service;

import com.Son.EcoBuddy.JPA.Entity.User;
import com.Son.EcoBuddy.JPA.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findOrRegisterUser(User user) {
        // 기존 유저가 있으면 반환, 없으면 새 유저를 저장 후 반환
        return userRepository.findById(user.getId())
                .orElseGet(() -> userRepository.save(user));
    }
}
