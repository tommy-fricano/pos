package com.pos.pos.listeners;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .create();
        return gson.toJson(basket);
    }
}
