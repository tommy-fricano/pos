package com.pos.pos.controllers;

import com.pos.pos.listener.ScannedEventListener;
import com.pos.pos.models.Basket;
import com.pos.pos.models.LineItem;
import com.pos.pos.models.PriceBook;
import com.pos.pos.service.PriceBookService;
import com.pos.pos.service.VirtualJournal;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Register implements ScannedEventListener {

    private final PriceBookService priceBookService;
    private final VirtualJournal virtualJournal;

    @Getter
    private final BarcodeScanner barcodeScanner;

    @Getter
    private Basket basket;

    boolean isBasket;

    @Autowired
    public Register(PriceBookService priceBookService, VirtualJournal virtualJournal, BarcodeScanner barcodeScanner) {
        this.priceBookService = priceBookService;
        this.virtualJournal = virtualJournal;
        this.barcodeScanner = barcodeScanner;
        startBasket();
    }

    @PostConstruct
    private void begin(){
        barcodeScanner.addScannedEventListener(this);
    }
    @Override
    public void onScanned(String scannedData) {
        // Handle the scanned data in this method
        System.out.println("Scanned data received: " + scannedData);
        itemAdded(scannedItem(scannedData));
    }

    public void startBasket(){
        isBasket = true;
        basket = new Basket();
        basket.setLineItems(new ArrayList<>());
        virtualJournal.basketInitialized();
    }

    public boolean itemAdded(LineItem lineItem){
        if(basket == null){
            startBasket();
        }
        virtualJournal.itemAddedLog(lineItem);
        return basket.appendLineItem(lineItem);
    }

    public void itemVoided(LineItem lineItem){
        virtualJournal.itemVoidedLog(lineItem);
        basket.voidLineItem();
    }

    public void basketVoided(){
        virtualJournal.basketVoidedLog(basket);
        basket = null;
    }

    public void endBasket(){
        virtualJournal.basketComplete(basket);
        basket = null;
    }

    public List<PriceBook> sendPriceBook(){
        return priceBookService.getPriceBook();
    }

    public LineItem scannedItem(String scannedData){
        PriceBook item = priceBookService.getPriceBookItem(Long.parseLong(scannedData));
        return LineItem.builder()
                .name(item.getItemName())
                .quantity(1)
                .value(item.getPrice())
                .voided(false)
                .build();
    }

}
