package com.pos.pos.service;

import com.pos.pos.controllers.Register;
import com.pos.pos.listeners.RegisterEvent;
import com.pos.pos.listeners.RegisterEventListener;
import com.pos.pos.server.Server;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class VirtualJournalService implements RegisterEventListener {

    public static final String VIRTUAL_JOURNAL = "Virtual Journal: ";
    public static final String REGISTER_ID = ": Register Id: ";
    private final Server server;
    private final Register register;

    private ZonedDateTime currentZonedDateTime;

//    private int cashierId;
    private int registerId;
    private final Random r = new Random();
    @PostConstruct
    private void begin(){
        register.addRegisterEventListener(this);
        currentZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        registerId = r.nextInt(50);
    }


    @Override
    public void updateListeners(RegisterEvent event) {
        switch (event.getAction()) {
            case STARTBASKET -> basketInitializedLog(event);
            case ADDITEM -> itemLog(event);
            case VOIDITEM -> itemLog(event);
            default -> basketCompleteLog(event);
        }
    }

    public void itemLog(RegisterEvent registerEvent) {
        server.broadcast(VIRTUAL_JOURNAL + currentZonedDateTime + REGISTER_ID + registerId + ": " + registerEvent.buildLineItemString(registerEvent.getLastItem()));
        System.out.println(VIRTUAL_JOURNAL+ currentZonedDateTime + REGISTER_ID + registerId + ": "  + registerEvent.buildLineItemString(registerEvent.getLastItem()));
    }

    public void basketInitializedLog(RegisterEvent event){
        server.broadcast(VIRTUAL_JOURNAL + currentZonedDateTime + REGISTER_ID + registerId + ": "  + event.getAction() );
        System.out.println(VIRTUAL_JOURNAL + currentZonedDateTime + REGISTER_ID + registerId + ": "  + event.getAction());
    }

    public void basketCompleteLog(RegisterEvent event){
        StringBuilder log = new StringBuilder();
        log.append(VIRTUAL_JOURNAL).append(currentZonedDateTime).append(REGISTER_ID).append(registerId).append(": ").append(event.buildEventString());
        server.broadcast(String.valueOf(log));
        System.out.println(log);
    }

}
