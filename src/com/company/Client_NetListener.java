package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client_NetListener implements Runnable{
    private Socket socket;
    private Client_listener clientListener;


    Client_NetListener(Socket paramSocket, Client_listener paramListener){
        socket = paramSocket;
        clientListener = paramListener;
    }

    public void run(){
        String receivedData;
        System.out.println("Client net listener is starting...");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true){
                receivedData = reader.readLine();
                clientListener.processReceivedData(receivedData);
            }
        }
        catch (IOException ex){
            System.out.println("Error in client net listener");
            clientListener.setErrorMessage();
        }
    }
}