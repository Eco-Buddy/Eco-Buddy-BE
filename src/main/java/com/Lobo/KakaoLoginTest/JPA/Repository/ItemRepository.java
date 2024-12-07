package com.Lobo.KakaoLoginTest.JPA.Repository;

import com.Lobo.KakaoLoginTest.JPA.Entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByUserIdAndItemIdBetween(String userId, Integer startItemId, Integer endItemId);

}
