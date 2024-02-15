package com.pos.pos.util;

import com.pos.pos.models.Item;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader {


    public static List<Item> loadItems(File file) throws Exception {

    List<Item> itemItems = new ArrayList<>();
        Scanner scanner;
        try {
            scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                String [] itemInfo = line.split("\t");
                for(int i=0;i< itemInfo.length; i++){
                    itemInfo[i] = itemInfo[i].trim();
                }
                itemItems.add(new Item(Long.parseLong(itemInfo[0]),itemInfo[1], BigDecimal.valueOf(Double.parseDouble(itemInfo[2]))));
            }
            scanner.close();
        } catch (NullPointerException exception) {
            throw new Exception("File not found");
        }


        return itemItems;
    }
}
