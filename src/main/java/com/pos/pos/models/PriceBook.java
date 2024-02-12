package com.pos.pos.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Data
@Table
public class PriceBook {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    @Column
    private long code;
    @Column
    private String itemName;
    @Column
    private BigDecimal price;

    public PriceBook(long code, String itemName, BigDecimal price) {
        this.code = code;
        this.itemName = itemName;
        this.price = price;
    }
}
