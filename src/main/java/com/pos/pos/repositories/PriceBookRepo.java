package com.pos.pos.repositories;

import com.pos.pos.models.PriceBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceBookRepo extends JpaRepository<PriceBook, Long> {
    @Query(value = "SELECT e FROM PriceBook e ORDER BY RAND() LIMIT 30")
    List<PriceBook> findRandomItems();

    PriceBook findPriceBookByCode(long code);
}
