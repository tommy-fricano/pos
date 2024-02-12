package com.pos.pos.util;

import com.pos.pos.models.PriceBook;
import org.springframework.core.io.Resource;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader {


    public static List<PriceBook> loadItems(File file) throws Exception {

    List<PriceBook> priceBookItems = new ArrayList<>();
        Scanner scanner;
        try {
            scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                String [] itemInfo = line.split("\t");
                for(int i=0;i< itemInfo.length; i++){
                    itemInfo[i] = itemInfo[i].trim();
                }
                priceBookItems.add(new PriceBook(Long.parseLong(itemInfo[0]),itemInfo[1], BigDecimal.valueOf(Double.parseDouble(itemInfo[2]))));
            }

        } catch (NullPointerException exception) {
            throw new Exception("File not found");
        }

        return priceBookItems;
    }
}
