package com.pos.pos.models;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
public class Basket {

    private static final BigDecimal TAX = BigDecimal.valueOf(.07);

    private List<LineItem> lineItems;

    private boolean voided = false;

    private BigDecimal subtotal = BigDecimal.ZERO;

    private BigDecimal total= BigDecimal.ZERO;

    private String registerId;

    private String cashierId;

    private String createdTimestamp;

    private BigDecimal discount = BigDecimal.ZERO;

    // try with resources


    public void appendLineItem(LineItem lineItem){
        lineItems.add(lineItem);

        subtotal = (subtotal.add(lineItem.getPrice())).setScale(2, RoundingMode.HALF_UP);
        total = (subtotal.add(subtotal.multiply(TAX))).setScale(2, RoundingMode.HALF_UP);
    }

    public void voidLineItem(){
        if(lineItems.isEmpty()){
            return;
        }
        List<LineItem> nonVoidedLineItems = this.getNonVoidedLineItems();
        LineItem last = nonVoidedLineItems.get(nonVoidedLineItems.size()-1);
        last.setVoided(true);

        subtotal = (subtotal.subtract(last.getPrice())).setScale(2, RoundingMode.HALF_UP);
        total = (subtotal.add(subtotal.multiply(TAX))).setScale(2, RoundingMode.HALF_UP);
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
