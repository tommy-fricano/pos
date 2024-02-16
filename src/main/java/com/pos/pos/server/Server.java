package com.pos.pos.server;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


@Component
public class Server {

    static List<ClientHandler> clients;
    public Server(){
        clients = new ArrayList<>();
    }

    public void start(int port) throws IOException{
        ServerSocket serverSocket = new ServerSocket(port);
        while(true){
            Socket s = null;
            try{
                s = serverSocket.accept();
                System.out.println("new client connected "+ s);
                connect(s);
            }
            catch (Exception e){
                s.close();
                System.out.println(e.getStackTrace());
            }
        }
    }

    public void broadcast(final String message){
        for(ClientHandler client: clients){
            try{
                client.write(message);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void connect(Socket socket) {
        ClientHandler client = new ClientHandler(this, socket);
        clients.add(client);
    }

    public void disconnect(ClientHandler clientHandler){
        clients.remove(clientHandler);
        clientHandler.stop();
    }
}

