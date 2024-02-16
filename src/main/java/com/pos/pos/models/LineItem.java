package com.pos.pos.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class LineItem {

    private Item item;
    private BigDecimal price;
    private int quantity;
    private boolean voided;

    public LineItem(){

    }

    @JsonCreator
    public LineItem(@JsonProperty("item") Item item, @JsonProperty("price") BigDecimal price, @JsonProperty("quantity") int quantity, @JsonProperty("voided") boolean voided) {
        this.item = item;
        this.price = price;
        this.quantity = quantity;
        this.voided = voided;
    }
}
