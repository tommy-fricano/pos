package com.pos.pos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("lineItems")
    private List<LineItem> lineItems;

    @JsonProperty("voided")
    private boolean voided = false;

    @JsonProperty("subtotal")
    private BigDecimal subtotal = BigDecimal.ZERO;

    @JsonProperty("total")
    private BigDecimal total= BigDecimal.ZERO;

    @JsonProperty("registerId")
    private String registerId;

    @JsonProperty("cashierId")
    private String cashierId;

    @JsonProperty("createdTimestamp")
    private String createdTimestamp;

    @JsonProperty("discount")
    private BigDecimal discount = BigDecimal.ZERO;

    @JsonIgnore
    List<LineItem> nonVoidedLineItems;


    public void appendLineItem(LineItem lineItem){
        lineItems.add(lineItem);

        subtotal = (subtotal.add(lineItem.getPrice())).setScale(2, RoundingMode.HALF_UP);
        total = (subtotal.add(subtotal.multiply(TAX))).setScale(2, RoundingMode.HALF_UP);
    }

    public void voidLineItem(){
        if(lineItems.isEmpty()){
            return;
        }
        nonVoidedLineItems = this.getNonVoidedLineItems();
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
        return result.isEmpty() ? new ArrayList<>() : result;
    }

    public void applyDiscount() {
        if(discount.compareTo(total) == 1){
            total = BigDecimal.ZERO;
        }
        else{
            total = total.subtract(discount);
        }
    }
}
