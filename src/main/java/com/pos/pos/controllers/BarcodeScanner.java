package com.pos.pos.controllers;

import com.pos.pos.listeners.ScannedEventListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

@Component
public class BarcodeScanner implements KeyEventDispatcher {

    private final StringBuilder scannedData = new StringBuilder();

    private final List<ScannedEventListener> listeners;

    public BarcodeScanner() {
        this.listeners = new ArrayList<>();
    }

    public void addScannedEventListener(ScannedEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_TYPED) {
            char typedChar = e.getKeyChar();
            if (typedChar == '\n') {
                handleScannedData(scannedData.toString());
                scannedData.setLength(0);
            } else {
                scannedData.append(typedChar);
            }
        }

        return false;
    }

    private void handleScannedData(String scannedData) {
        for (ScannedEventListener listener : listeners) {
            listener.onScanned(scannedData);
        }
    }
}
