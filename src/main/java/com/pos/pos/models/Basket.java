package com.pos.pos.models;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Basket {

    private static final BigDecimal TAX = BigDecimal.valueOf(.07);

    private List<LineItem> lineItems;

    private boolean voided = false;

    private BigDecimal subtotal = BigDecimal.valueOf(0);

    private BigDecimal total= BigDecimal.valueOf(0);

    private String registerId;

    private String cashierId;

    private String createdTimestamp;


    public void appendLineItem(LineItem lineItem){
        lineItems.add(lineItem);

        subtotal = subtotal.add(lineItem.getPrice());
        total = subtotal.add(subtotal.multiply(TAX));
    }

    public void voidLineItem(){
        if(lineItems.isEmpty()){
            return;
        }
        List<LineItem> nonVoidedLineItems = this.getNonVoidedLineItems();
        LineItem last = nonVoidedLineItems.get(nonVoidedLineItems.size()-1);
        last.setVoided(true);

        subtotal = subtotal.subtract(last.getPrice());
        total = subtotal.add(subtotal.multiply(TAX));
    }

    public List<LineItem> getNonVoidedLineItems(){
        List<LineItem> result = new ArrayList<>();
        for(LineItem lineItem: lineItems){
            if(!lineItem.isVoided()){
                result.add(lineItem);
            }
        }
        return result;
    }
}
