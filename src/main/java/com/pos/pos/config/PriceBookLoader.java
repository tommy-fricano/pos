package com.pos.pos.config;

import com.pos.pos.repositories.PriceBookRepo;
import com.pos.pos.util.FileReader;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;

@Configuration
public class PriceBookLoader {

    private final PriceBookRepo priceBookRepo;

    @Autowired
    public PriceBookLoader(PriceBookRepo priceBookRepo) throws Exception {
        this.priceBookRepo = priceBookRepo;
        saveInH2();
    }

    @Transactional
    protected void saveInH2() throws Exception {
        File file = ResourceUtils.getFile("classpath:pricebook.tsv");
        priceBookRepo.saveAll(FileReader.loadItems(file));
    }

}
