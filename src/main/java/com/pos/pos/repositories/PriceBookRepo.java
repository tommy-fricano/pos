package com.pos.pos.repositories;

import com.pos.pos.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceBookRepo extends JpaRepository<Item, Long> {
    @Query(value = "SELECT e FROM Item e ORDER BY RAND() LIMIT 30")
    List<Item> findRandomItems();

    Item findItemByCode(long code);
}
