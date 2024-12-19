package com.Son.EcoBuddy.JPA.Repository;

import com.Son.EcoBuddy.JPA.Entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByUserIdAndItemIdBetween(String userId, Integer startItemId, Integer endItemId);

}
