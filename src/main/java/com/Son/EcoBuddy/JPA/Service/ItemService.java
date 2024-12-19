package com.Son.EcoBuddy.JPA.Service;

import com.Son.EcoBuddy.JPA.Entity.Item;
import com.Son.EcoBuddy.JPA.Repository.ItemRepository;
import com.Son.EcoBuddy.JPA.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    public void addItem(String userId, int itemId) {
        Item newItem = Item.builder()
                .user(userRepository.findById(userId).get())
                .itemId(itemId)
                .build();

        itemRepository.save(newItem);
    }

    public List<Item> loadItem(String userId, int range) {
        List<Item> ItemList = itemRepository.findAllByUserIdAndItemIdBetween(userId, range, range + 999);

        if(ItemList.isEmpty()) {
            throw new IllegalArgumentException("Item not found for ID: " + userId);
        }

        return ItemList;
    }
}
