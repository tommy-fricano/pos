package com.pos.pos.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class Basket {

    private final BigDecimal TAX = BigDecimal.valueOf(.07);

    private List<LineItem> lineItems;

    private boolean voided = false;

    private BigDecimal subtotal = BigDecimal.valueOf(0);

    private BigDecimal total= BigDecimal.valueOf(0);

    private BigDecimal voidTotal = BigDecimal.valueOf(0);

    public boolean appendLineItem(LineItem lineItem){
        boolean newItem = true;
        for(LineItem itemInBasket : lineItems){
            if(itemInBasket.getName().equals(lineItem.getName())){
                itemInBasket.setQuantity(itemInBasket.getQuantity() +1);
                itemInBasket.setValue(itemInBasket.getValue().add(lineItem.getValue()));
                newItem = false;
            }
        }
        if(newItem){ lineItems.add(lineItem);}

        updateTotals(lineItem, true);
        total = subtotal.add(subtotal.multiply(TAX));
        voidTotal = subtotal;
        return newItem;
    }

    public void voidLineItem(){
        if(lineItems.isEmpty()){
            return;
        }
        LineItem last = lineItems.get(lineItems.size()-1);
        last.setVoided(true);
        if(last.getQuantity() >= 3){
            last.setQuantity(last.getQuantity()-1);
            last.setValue(last.getValue().subtract(last.getValue().divide(BigDecimal.valueOf(last.getQuantity()))));
        } else if(last.getQuantity() == 2){
            last.setQuantity(last.getQuantity()-1);
            last.setValue(last.getValue().subtract(last.getValue()));
        } else{
            lineItems.get(lineItems.size()-1).setVoided(true);
        }

        updateTotals(last, false);
    }

    private void updateTotals(LineItem item, boolean add){
        if(add){
            subtotal = subtotal.add(item.getValue());
            total = subtotal.add(subtotal.multiply(TAX));
            voidTotal = subtotal;
        }else{
            subtotal = subtotal.subtract(item.getValue());
            total = subtotal.add(subtotal.multiply(TAX));
        }
    }
}
