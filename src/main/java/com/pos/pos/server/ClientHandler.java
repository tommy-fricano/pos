package com.pos.pos.server;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private BufferedReader reader;
    private PrintWriter writer;
    private Socket s;
    private Server server;

    private boolean stopped = false;

    public ClientHandler(Server server, Socket socket){
        try{
            this.server = server;
            this.s = socket;
            this.reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.writer = new PrintWriter(s.getOutputStream(),true);
            Thread t = new Thread(this);
            t.start();
        }catch(Exception e){
            System.out.println(e);
        }
    }
    @Override
    public void run() {
        while(!stopped){
            try{
                char c = (char)reader.read();
                if(c == '\uFFFF'){
                    break;
                }
                System.out.print(c);
            }
            catch(Exception e){
                System.out.println(e);
                break;
            }
        }
        server.disconnect(this);
    }

    public void write(String message) {
        writer.println(message);
    }

    public void stop(){
        this.write("Client Disconnected");
        this.stopped = true;
    }
}
