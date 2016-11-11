package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class Server_NetInteraction implements Runnable{
    private Socket socket;
    private Server server;

    Server_NetInteraction (Server tempServer, Socket tempSocket){
        server=tempServer;
        socket=tempSocket;
    }

    public void run() {
        BufferedReader reader = null;
        System.out.println("Server net listener is starting...");
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true){
                String receivedData = reader.readLine();
                server.processReceivedData(socket,receivedData);
            }
        }
        catch (IOException ex){
            System.out.println("Error in server net read");
        }
    }
}