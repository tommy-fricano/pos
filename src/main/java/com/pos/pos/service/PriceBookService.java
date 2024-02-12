package com.pos.pos.service;

import com.pos.pos.models.PriceBook;
import com.pos.pos.repositories.PriceBookRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceBookService {

    private final PriceBookRepo priceBookRepo;

    @Autowired
    public PriceBookService(PriceBookRepo priceBookRepo) {
        this.priceBookRepo = priceBookRepo;
    }

    public List<PriceBook> getPriceBook(){
        return priceBookRepo.findRandomItems();
    }

    public PriceBook getPriceBookItem(long code){
        return priceBookRepo.findPriceBookByCode(code);
    }
}
