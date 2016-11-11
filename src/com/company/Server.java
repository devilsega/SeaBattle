package com.company;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

class Server implements Runnable{
    private final int GAMETYPE;
    private final int PORT;
    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private Ship_controller firstPlayer,secondPlayer;
    private int gameStage=0;
    private boolean firstPlayerEndSetup=false,secondPlayerEndSetup=false;
    private boolean firstPlayerTurnIsGoing=false,secondPlayerTurnIsGoing=false;
    private ArrayList<String> playerID = new ArrayList<>();
    private ArrayList<Socket> clientsList = new ArrayList<>();
    private Server_NetInteraction serverNetInteraction;

    Server (int paramGameType, int paramPort){
        GAMETYPE=paramGameType;
        PORT=paramPort;
    }

    public void run(){
        serverSocket = null;
        firstPlayer = new Ship_controller("543322");
        secondPlayer = new Ship_controller("543322");
        System.out.println("Server is starting...");
        //тело сервера
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println(serverSocket);
        }
        catch (IOException ex){
            System.out.println("Couldn't init server port");
            ex.printStackTrace();
            System.exit(-1);
        }
        while (true){
            try {
                socket = serverSocket.accept();
                System.out.println(socket);
                serverNetInteraction = new Server_NetInteraction(this,socket);
                new Thread(serverNetInteraction).start();
            }
            catch (IOException ex){
                System.out.println("Error in server accept");
                ex.printStackTrace();
                System.exit(0);
            }
        }
    }

    synchronized void processReceivedData(Socket paramActiveSocket, String paramReceivedData){
        String[]finalData;
        int canPlaceShipBlockHere;

        finalData = convertReceivedNetData(paramReceivedData);
        /*
        * ПАЙПЛАЙН ДЛЯ ВТОРОЙ СТАДИИ (ПОХОДОВАЯ ИГРА)
        */
        if(gameStage==1){
            if (finalData[0].equals(playerID.get(0)) && firstPlayerTurnIsGoing){
                int checkHit = secondPlayer.gameplayActions(transformCoordinatesStringToInt(finalData[1]));
                if (checkHit==0){
                    netSend(paramActiveSocket,finalData[0],2,finalData[1]);            //отправка данных атакующему
                    netSend(clientsList.get(1),playerID.get(1),0,finalData[1]);        //отправка данных атакованному
                }
                if (checkHit==1){
                    netSend(paramActiveSocket,finalData[0],3,finalData[1]);            //отправка данных атакующему
                    netSend(clientsList.get(1),playerID.get(1),1,finalData[1]);        //отправка данных атакованному
                    String destroyedCoords = secondPlayer.checkDestroyedShips();       //проверка не полное потопление корабля
                    if (destroyedCoords!=null){
                        netSend(paramActiveSocket,finalData[0],3,destroyedCoords);
                        netSend(clientsList.get(1),playerID.get(1),1,destroyedCoords);
                    }
                }
                if(secondPlayer.shipsSize()<1){
                    netSend(clientsList.get(0),playerID.get(0),5,"00");
                    netSend(clientsList.get(1),playerID.get(1),4,"00");
                }
                firstPlayerTurnIsGoing=false;
                secondPlayerTurnIsGoing=true;
            }
            if (finalData[0].equals(playerID.get(1)) && secondPlayerTurnIsGoing){
                int checkHit = firstPlayer.gameplayActions(transformCoordinatesStringToInt(finalData[1]));
                if (checkHit==0){
                    netSend(paramActiveSocket,finalData[0],2,finalData[1]);            //отправка данных атакующему
                    netSend(clientsList.get(0),playerID.get(0),0,finalData[1]);        //отправка данных атакованному
                }
                if (checkHit==1){
                    netSend(paramActiveSocket,finalData[0],3,finalData[1]);            //отправка данных атакующему
                    netSend(clientsList.get(0),playerID.get(0),1,finalData[1]);        //отправка данных атакованному
                    String destroyedCoords = firstPlayer.checkDestroyedShips();        //проверка не полное потопление корабля
                    if (destroyedCoords!=null){
                        netSend(paramActiveSocket,finalData[0],3,destroyedCoords);
                        netSend(clientsList.get(0),playerID.get(0),1,destroyedCoords);
                    }
                }
                if(firstPlayer.shipsSize()<1){
                    netSend(clientsList.get(0),playerID.get(0),4,"00");
                    netSend(clientsList.get(1),playerID.get(1),5,"00");
                }
                secondPlayerTurnIsGoing=false;
                firstPlayerTurnIsGoing=true;
            }
        }
        /*
        * ПАЙПЛАЙН ДЛЯ ПЕРВОЙ СТАДИИ (РАССТАНОВКА)
        */
        if(gameStage==0){
            //если клиент регистрируется
            if (finalData[0].equals("0000")){
                if(playerID.size()>=2){
                    netSend(paramActiveSocket,"9999",0,"00");
                }
                if(playerID.size()<2){
                    String tempID = generateID();
                    playerID.add(tempID);
                    clientsList.add(paramActiveSocket);
                    if (playerID.size()==1)netSend(paramActiveSocket,tempID,0,"01");
                    else netSend(paramActiveSocket,tempID,0,"02");
                }
            }
            //если клиент имеет айди
            else{
                if (playerID.size()==2){
                    if (finalData[0].equals(playerID.get(0))){
                        if (finalData[3].equals("2")) {
                            firstPlayerEndSetup=true;
                            setGameStage();
                        }
                        if (finalData[3].equals("1")){
                            firstPlayerEndSetup=false;
                            firstPlayer.resetShips();
                        }
                        if (finalData[3].equals("0")){
                            canPlaceShipBlockHere=firstPlayer.setupActions(Integer.parseInt(finalData[2]),transformCoordinatesStringToInt(finalData[1]));
                            if (canPlaceShipBlockHere==0){
                                netSend(paramActiveSocket,finalData[0],0,"x");
                            }
                            if (canPlaceShipBlockHere==1){
                                netSend(paramActiveSocket,finalData[0],0,finalData[1]);
                            }
                            if(canPlaceShipBlockHere==1 && firstPlayer.getactiveShipPlacingStatus())netSend(paramActiveSocket,finalData[0],1,finalData[1]);
                            if (firstPlayer.shipsSize()==19)netSend(paramActiveSocket,finalData[0],2,finalData[1]);
                        }
                    }
                    else if (finalData[0].equals(playerID.get(1))){
                        if (finalData[3].equals("2")) {
                            secondPlayerEndSetup=true;
                            setGameStage();
                        }
                        if (finalData[3].equals("1")){
                            secondPlayerEndSetup=false;
                            secondPlayer.resetShips();
                        }
                        if (finalData[3].equals("0")){
                            canPlaceShipBlockHere=secondPlayer.setupActions(Integer.parseInt(finalData[2]),transformCoordinatesStringToInt(finalData[1]));
                            if (canPlaceShipBlockHere==0){
                                netSend(paramActiveSocket,finalData[0],0,"x");
                            }
                            if (canPlaceShipBlockHere==1){
                                netSend(paramActiveSocket,finalData[0],0,finalData[1]);
                            }
                            if(canPlaceShipBlockHere==1 && secondPlayer.getactiveShipPlacingStatus())netSend(paramActiveSocket,finalData[0],1,finalData[1]);
                            if (secondPlayer.shipsSize()==19)netSend(paramActiveSocket,finalData[0],2,finalData[1]);
                        }
                    }
                    else {
                        netSend(paramActiveSocket,"9999",0,"00");
                    }
                }
            }
        }
    }

    /*отправка по сети данных, где ХХХХ - айди, Х - параметр, ХХ...Xn-1,Хn - координаты (чётное!)
    ** для первой стадии параметр Х: 0 - игнорировать параметр, 1 - корабль выставлен, 2 - все корабли выставлены, 3 - переход на следующую стадию
    ** для второй стадии параметр Х: 0 - своё поле, промах, 1 - своё поле, попадание, 2 - чужое поле, пропах, 3 - чужое поле, попадание, 4 - проигрыш, 5 - победа
    */
    private void netSend(Socket paramSocket, String paramID, int paramOne, String paramCoords) {
        try {
            PrintWriter writer = new PrintWriter(paramSocket.getOutputStream());
            writer.println(paramID+""+Integer.toString(paramOne) +""+paramCoords);
            writer.flush();
        } catch (IOException ex) {
            System.out.println("can't send data");
            ex.printStackTrace();
            System.exit(0);
        }
    }

    //ковертация координат (стринг из 2х цифр) в массив int[2], где [0]=x [1]=y
    private int[] transformCoordinatesStringToInt (String temp){
        int[] arrayOfCoordinates = new int[2];
        StringBuilder bld = new StringBuilder(temp);
        arrayOfCoordinates[0]=Character.getNumericValue(bld.charAt(0));
        arrayOfCoordinates[1]=Character.getNumericValue(bld.charAt(1));
        return arrayOfCoordinates;
    }

    //генерация уникального айди для игрока
    private String generateID(){
        Random random = new Random();
        return Integer.toString(random.nextInt(8999)+1000);
    }

    private void setGameStage(){
        if (gameStage==0 && firstPlayerEndSetup && secondPlayerEndSetup){
            gameStage++;
            System.out.println(clientsList);
            netSend(clientsList.get(0),playerID.get(0),3,"00");
            netSend(clientsList.get(1),playerID.get(1),3,"00");
            firstPlayerTurnIsGoing=true;
        }
    }

    private String[] convertReceivedNetData(String temp){
        String[] arrayOfData = new String[4];
        StringBuilder notConverted = new StringBuilder(temp);
        StringBuilder paramID = new StringBuilder();
        StringBuilder paramCoords = new StringBuilder();
        StringBuilder paramOne = new StringBuilder();
        StringBuilder paramSpecial = new StringBuilder();
        paramID.append(notConverted.charAt(0)).append(notConverted.charAt(1)).append(notConverted.charAt(2)).append(notConverted.charAt(3));
        paramCoords.append(notConverted.charAt(4)).append(notConverted.charAt(5));
        paramOne.append(notConverted.charAt(6));
        paramSpecial.append(notConverted.charAt(7));
        arrayOfData[0]=paramID.toString();
        arrayOfData[1]=paramCoords.toString();
        arrayOfData[2]=paramOne.toString();
        arrayOfData[3]=paramSpecial.toString();
        return arrayOfData;
    }
}