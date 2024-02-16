package com.pos.pos.services;

import com.pos.pos.models.Item;
import com.pos.pos.repositories.PriceBookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceBookService {

    private final PriceBookRepo priceBookRepo;

    @Autowired
    public PriceBookService(PriceBookRepo priceBookRepo) {
        this.priceBookRepo = priceBookRepo;
    }

    public List<Item> getPriceBook(){
        return priceBookRepo.findRandomItems();
    }

    public Item getItem(long code){
        return priceBookRepo.findItemByCode(code);
    }
}
