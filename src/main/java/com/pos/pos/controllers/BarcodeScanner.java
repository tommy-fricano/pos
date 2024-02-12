package com.pos.pos.controllers;

import com.pos.pos.listener.ScannedEventListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

@Component
public class BarcodeScanner implements KeyEventDispatcher {

    private StringBuilder scannedData = new StringBuilder();

    private List<ScannedEventListener> listeners;

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

            // Check for the Enter key to indicate the end of a scan
            if (typedChar == '\n') {
                handleScannedData(scannedData.toString());
                scannedData.setLength(0); // Clear the scanned data buffer
            } else {
                scannedData.append(typedChar);
            }
        }

        // Continue to dispatch the event to other listeners
        return false;
    }

    private void handleScannedData(String scannedData) {
        // Notify all registered listeners with the scanned data
        for (ScannedEventListener listener : listeners) {
            listener.onScanned(scannedData);
        }
    }
}
