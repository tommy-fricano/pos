package com.pos.pos.service;

import com.pos.pos.models.Basket;
import com.pos.pos.models.LineItem;
import org.springframework.stereotype.Component;

@Component
public class VirtualJournal {

    // todo move to virtual journal

//    private Date date;
//    private int cashierId;
//    private int registerId;
//    private String location;
    public void basketInitialized(){
        System.out.println("Virtual Journal: Basket Started.");
    }
    public void itemAddedLog(LineItem item){
        System.out.println("Virtual Journal: Added Item:  "+ item.getName() + " "+ item.getQuantity() +" "+item.getValue());
    }

    public void itemVoidedLog(LineItem item){
        System.out.println("Virtual Journal: Voided Item:  "+ item.getName() + " "+ item.getQuantity() +" "+item.getValue());
    }

    public void basketVoidedLog(Basket basket){
        StringBuilder log = new StringBuilder();
        log.append("Virtual Journal: Basket Voided: ");
        for (LineItem item : basket.getLineItems()) {
            log.append(item.getName()).append(" ").append(item.getQuantity()).append(" ").append(item.getValue()).append(", ");
        }
        log.append(" Total: ").append(basket.getVoidTotal()).append(".");

        System.out.println(log);
    }

    public void basketComplete(Basket basket){
        StringBuilder log = new StringBuilder();
        log.append("Virtual Journal: Transaction complete. Basket: ");
        for (LineItem item : basket.getLineItems()) {
            log.append(item.getName()).append(" ").append(item.getQuantity()).append(" ").append(item.getValue()).append(" voided: ").append(item.isVoided()).append(", ");
        }
        log.append(" Subtotal: ").append(basket.getSubtotal()).append(" Total: ").append(basket.getTotal()).append(".");

        System.out.println(log);
    }
}
