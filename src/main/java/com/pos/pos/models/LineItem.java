package com.pos.pos.models;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class LineItem {

    private String name;
    private BigDecimal value;
    private int quantity;
    private boolean voided;


}
