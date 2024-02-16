package com.pos.pos.listeners;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.pos.models.Basket;
import com.pos.pos.models.LineItem;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterEvent {

    private RegisterEventEnums action;
    private Basket basket;

    public String buildEventString(){
        StringBuilder message = new StringBuilder();
        message.append("Action: ").append(action).append(", Basket: ").append(this.toJson());
        return message.toString();
    }

    public String buildLineItemString(LineItem lineItem){
        return  action + ": " + lineItem.getItem().getName() + " "+ lineItem.getQuantity() +" "+ lineItem.getItem().getPrice();
    }

    public LineItem getLastItem(){
        return basket.getLineItems().get(basket.getLineItems().size()-1);
    }

    public String toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(basket);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert BasketDTO to JSON", e);
        }
    }
}
