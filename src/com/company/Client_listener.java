package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

class Client_listener implements ActionListener {
    private final int GAMETYPE;
    private final String ADDRESS;
    private final int PORT;
    private final String VERSION;
    private Socket socket;

    private Client_view clientView;
    private Client_NetListener clientNetListener;
    private String playerID=null;
    private PrintWriter writer = null;
    private int numOfAttempt = 0;
    private String playerNum= "00";

    private int gameStage =0;
    private boolean shipIsSelected;
    private int shipType;
    boolean endOfPlacing = false;
    boolean serverReadyToStartGame = false;
    private boolean buttonPressIsProcessed=false;

    Client_listener(int paramGameType, String paramAddress, int paramPort, String paramVer) {
        GAMETYPE = paramGameType;
        ADDRESS = paramAddress;
        PORT = paramPort;
        VERSION = paramVer;

        //инициализация сетевой части
        netInit();
        clientNetListener = new Client_NetListener(socket, this);
        Thread clientNetListenerThread = new Thread(clientNetListener);
        clientNetListenerThread.start();
        //первичный обмен с сервером, запрос на айди
        netSend("0000", "00", 0, 0);
        //инициализация ГУИ
        clientView = new Client_view(this, VERSION);
        Thread clientViewThread = new Thread(clientView);
        clientViewThread.start();
        clientView.setMessageField("Расстановка кораблей", 0);
    }

    public void actionPerformed(ActionEvent e) {
        JButton ClickedButton;
        ClickedButton = (JButton) e.getSource();
        if (gameStage ==0)gameSetup(ClickedButton);
        if (gameStage ==1)gamePlay(ClickedButton);
    }

    private void netInit(){
        try {
            numOfAttempt++;
            socket = new Socket(ADDRESS,PORT);
            writer = new PrintWriter(socket.getOutputStream());
            System.out.println("Client connection established");
        }
        catch (IOException e){
            System.out.println("can't init socket, trying to reinit");
            try{
                if (numOfAttempt<6){
                    sleep(2000);
                    netInit();
                }
                else System.exit(1);
            }
            catch (InterruptedException ie){
            }
        }
    }

    //отправляет в соккет подготовленный String, где xxxx (clientID) + xx (координаты) + х (параметр1(ShipPlacementStage)) +
    // + х (параметр2(ShipCounter)) + х (спец.параметр, для размещения: 0-null,1-reset,2-endshipplacement)
    private void netSend(String paramID, String paramCoords, int paramOne, int paramSpecial) {
        writer.println(paramID + "" + paramCoords + "" + Integer.toString(paramOne) + ""  + Integer.toString(paramSpecial));
        writer.flush();
    }

    //ковертация координат (стринг из N цифр, четный) в массив Arraylist<>, где [0]=x [1]=y
    private ArrayList<Integer> convertStringToCoordinates(String paramString){
        ArrayList<Integer>temp = new ArrayList<>();;
        for(int i=0; i<=paramString.length()-1; i++){
            temp.add(Character.getNumericValue(paramString.charAt(i)));
        }
        return temp;
    }

    //конвертация полученной в BufferedReader строки в массив String[3], где [0] - айди, [1] - спец.параметр, [3] - координаты Х У
    private String[] convertReceivedNetData(String temp){
        String[] arrayOfData = new String[3];
        StringBuilder notConverted = new StringBuilder(temp);
        StringBuilder paramID = new StringBuilder();
        StringBuilder paramOne = new StringBuilder();
        StringBuilder paramCords= new StringBuilder();
        paramID.append(notConverted.charAt(0)).append(notConverted.charAt(1)).append(notConverted.charAt(2)).append(notConverted.charAt(3));
        paramOne.append(notConverted.charAt(4));
        arrayOfData[0]=paramID.toString();
        arrayOfData[1]=paramOne.toString();
        if (Character.toString(temp.charAt(5)).equals("x"))arrayOfData[2]="-1";
        else {
            for(int i = 5; i<=temp.length()-1; i++){
                paramCords.append(temp.charAt(i));
            }
            arrayOfData[2]=paramCords.toString();
        }
        return arrayOfData;
    }

    private void setPlayerID(String temp){
        playerID=temp;
    }

    void setErrorMessage(){
        clientView.setMessageField("Ошибка соединения!!!",0);
    }

    //сброс на стартовую позицию как в начале расстановки
    void resetShipCoordinate(){
        shipIsSelected = false;
        netSend(playerID,"00",0,1);
        buttonPressIsProcessed=false;
        endOfPlacing=false;
    }

    void sendServerMessageReadyToStart(){
        netSend(playerID,"00", 0, 2);
    }

    //метод обработки информации пришедшей из bufferedReader
    synchronized void processReceivedData(String paramReceivedData){
        String[]finalData;

        finalData = convertReceivedNetData(paramReceivedData);

        if (finalData[0].equals("9999")){
            System.out.println("На этом сервере все места заняты!");
            setErrorMessage();
        }
        if (finalData[0].equals(playerID)){
            /*
            * ПАЙПЛАЙН ДЛЯ ВТОРОЙ СТАДИИ (ПОХОДОВАЯ ИГРА)
            */
            if(gameStage==1){
                if (finalData[1].equals("0")){
                    clientView.setButton(convertStringToCoordinates(finalData[2]).get(0), convertStringToCoordinates(finalData[2]).get(1),0,1,0);
                    clientView.setMessageField("Ваш ход",1);
                    buttonPressIsProcessed = false;
                }
                if (finalData[1].equals("1")){
                    if (finalData[2].length()<3){
                        clientView.setButton(convertStringToCoordinates(finalData[2]).get(0), convertStringToCoordinates(finalData[2]).get(1),0,1,1);
                    }
                    if (finalData[2].length()>2){
                        for(int i = 0; i <finalData[2].length(); i=i+2){
                            clientView.setButton(convertStringToCoordinates(finalData[2]).get(i), convertStringToCoordinates(finalData[2]).get(i+1),0,2,0);
                        }
                    }
                    clientView.setMessageField("Ваш ход",1);
                    buttonPressIsProcessed = false;
                }
                if (finalData[1].equals("2")){
                    clientView.setButton(convertStringToCoordinates(finalData[2]).get(0), convertStringToCoordinates(finalData[2]).get(1),1,1,0);
                }
                if (finalData[1].equals("3")){
                    if (finalData[2].length()<3){
                        clientView.setButton(convertStringToCoordinates(finalData[2]).get(0), convertStringToCoordinates(finalData[2]).get(1),1,1,1);
                    }
                    if (finalData[2].length()>2){
                        for(int i = 0; i <finalData[2].length(); i=i+2){
                            clientView.setButton(convertStringToCoordinates(finalData[2]).get(i), convertStringToCoordinates(finalData[2]).get(i+1),1,2,0);
                        }
                    }
                }
                if (finalData[1].equals("4")){
                    clientView.setGameEnded();
                    gameStage++;
                    JOptionPane.showMessageDialog(null, "Вы проиграли!", "Конец игры", JOptionPane.PLAIN_MESSAGE);
                }
                if (finalData[1].equals("5")){
                    clientView.setGameEnded();
                    gameStage++;
                    JOptionPane.showMessageDialog(null, "Вы выиграли!", "Конец игры", JOptionPane.PLAIN_MESSAGE);
                }
            }
            /*
            * ПАЙПЛАЙН ДЛЯ ПЕРВОЙ СТАДИИ (РАССТАНОВКА)
            */
            if(gameStage==0){
                if (finalData[1].equals("0")){
                    if(finalData[2].equals("-1")){
                        buttonPressIsProcessed = false;
                    }
                    else{
                        clientView.setButton(convertStringToCoordinates(finalData[2]).get(0), convertStringToCoordinates(finalData[2]).get(1),0,0,0);
                        buttonPressIsProcessed = false;
                    }
                }
                if (finalData[1].equals("1")){
                    shipIsSelected=false;
                    buttonPressIsProcessed = false;
                }
                if (finalData[1].equals("2")){
                    endOfPlacing=true;
                }

                //переход ко второй стадии по команде сервера.
                if (finalData[1].equals("3")){
                    serverReadyToStartGame=true;
                    gameStage++;
                    clientView.startGamePlay();
                    if(playerNum.equals("01"))clientView.setMessageField("Ваш ход",1);
                    if(playerNum.equals("02"))clientView.setMessageField("Ход оппонента",2);
                }
            }
        }

        if (!finalData[0].equals(playerID)){
            setPlayerID(finalData[0]);
            playerNum=finalData[2];
        }

    }

    //обработка нажатий по кнопкам интерфейса
    private void gameSetup(JButton ClickedButton) {
        if (ClickedButton.getName().equals("fiveDeck1") && !shipIsSelected && !buttonPressIsProcessed){
            shipIsSelected=true;
            shipType = 5;
            ClickedButton.setEnabled(false);
        }
        if (ClickedButton.getName().equals("fourDeck1") && !shipIsSelected && !buttonPressIsProcessed){
            shipIsSelected=true;
            shipType = 4;
            ClickedButton.setEnabled(false);
        }
        if (ClickedButton.getName().equals("threeDeck1") && !shipIsSelected && !buttonPressIsProcessed){
            shipIsSelected=true;
            shipType = 3;
            ClickedButton.setEnabled(false);
        }
        if (ClickedButton.getName().equals("threeDeck2") && !shipIsSelected && !buttonPressIsProcessed){
            shipIsSelected=true;
            shipType = 3;
            ClickedButton.setEnabled(false);
        }
        if (ClickedButton.getName().equals("twoDeck1") && !shipIsSelected && !buttonPressIsProcessed){
            shipIsSelected=true;
            shipType = 2;
            ClickedButton.setEnabled(false);
        }
        if (ClickedButton.getName().equals("twoDeck2") && !shipIsSelected && !buttonPressIsProcessed){
            shipIsSelected=true;
            shipType = 2;
            ClickedButton.setEnabled(false);
        }

        if (!buttonPressIsProcessed && shipIsSelected &&!(ClickedButton.getName().equals("fiveDeck1"))&&!(ClickedButton.getName().equals("fourDeck1"))
                &&!(ClickedButton.getName().equals("threeDeck1"))&&!(ClickedButton.getName().equals("threeDeck2"))&&!(ClickedButton.getName().equals("twoDeck1"))
                &&!(ClickedButton.getName().equals("twoDeck2"))&&!(ClickedButton.getName().equals("reset"))&&!(ClickedButton.getName().equals("startGame"))) {
            netSend(playerID, ClickedButton.getName(), shipType,0);
            buttonPressIsProcessed = true;

        }
    }

    private void gamePlay(JButton ClickedButton) {
        if (!buttonPressIsProcessed){
            netSend(playerID, ClickedButton.getName(),0,0);
            clientView.setMessageField("Ход оппонента",2);
            buttonPressIsProcessed = true;
        }
    }
}