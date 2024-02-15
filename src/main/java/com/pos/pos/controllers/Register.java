package com.pos.pos.controllers;

import com.pos.pos.listeners.*;
import com.pos.pos.models.Basket;
import com.pos.pos.models.Item;
import com.pos.pos.models.LineItem;
import com.pos.pos.service.PriceBookService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class Register implements ScannedEventListener, RegisterEventListener{

    private final List<RegisterEventListener> listeners;

    private final PriceBookService priceBookService;

    @Getter
    private final BarcodeScanner barcodeScanner;



    @Getter
    private Basket basket;

    boolean isBasket;

    @Autowired
    public Register(PriceBookService priceBookService, BarcodeScanner barcodeScanner) {
        this.priceBookService = priceBookService;
        this.barcodeScanner = barcodeScanner;
        this.listeners = new ArrayList<>();
    }

    @PostConstruct
    private void begin(){
        barcodeScanner.addScannedEventListener(this);
    }

    @Override
    public void onScanned(String scannedData) {
        this.itemAdded(getItemFromScan(scannedData));
    }

    public void addRegisterEventListener(RegisterEventListener listener) {
        listeners.add(listener);
    }


    @Override
    public void updateListeners(RegisterEvent event) {
        for (RegisterEventListener registerEventListener : listeners) {
            registerEventListener.updateListeners(event);
        }
    }

    public void startBasket(){
        isBasket = true;
        this.basket = new Basket();
        this.basket.setLineItems(new ArrayList<>());
        this.basket.setRegisterId("404404");
        this.basket.setCashierId("201201");
        this.basket.setCreatedTimestamp(String.valueOf(ZonedDateTime.now(ZoneId.systemDefault())));
        this.updateListeners(
                RegisterEvent.builder()
                .action(RegisterEventEnums.STARTBASKET)
                .basket(this.basket)
                .build()
        );
    }

    public void itemAdded(LineItem lineItem){
        if(this.basket == null){
            this.startBasket();
        }
        this.basket.appendLineItem(lineItem);

        this.updateListeners(
                RegisterEvent.builder()
                        .action(RegisterEventEnums.ADDITEM)
                        .basket(this.basket)
                        .build()
        );
    }

    public void itemVoided(){
        this.basket.voidLineItem();

        this.updateListeners(
                RegisterEvent.builder()
                .action(RegisterEventEnums.VOIDITEM)
                .basket(this.basket)
                .build());
    }


    public void endBasket(RegisterEventEnums eventEnums){
        this.updateListeners(
                RegisterEvent.builder()
                .action(eventEnums)
                .basket(this.basket)
                .build()
        );
//        this.updateListeners(
//                RegisterEvent.builder()
//                .action(RegisterEventEnums.ENDBASKET)
//                        .basket(this.basket)
//                        .build()
//        );
        this.basket = null;
    }

    public List<Item> sendPriceBook(){
        return priceBookService.getPriceBook();
    }

    public LineItem getItemFromScan(String scannedData){
        Item item = priceBookService.getItem(Long.parseLong(scannedData));
        return LineItem.builder()
                .item(item)
                .quantity(1)
                .price(item.getPrice())
                .voided(false)
                .build();
    }

}
