package com.pos.pos.models;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class LineItem {

    private Item item;
    private BigDecimal price;
    private int quantity;
    private boolean voided;



}
