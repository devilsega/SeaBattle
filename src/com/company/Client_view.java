package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Client_view implements Runnable{

    private final String VERSION;
    private JButton[][] playerArray = new JButton[10][10];
    private JButton[][] enemyArray = new JButton[10][10];
    private String [] shipNamesRu = {"5-палубный","4-палубный","3-палубный","3-палубный","2-палубный","2-палубный"};
    private String [] shipNamesEn = {"fiveDeck1","fourDeck1","threeDeck1","threeDeck2","twoDeck1","twoDeck2"};
    private JButton [] ship = new JButton[6];
    private JFrame mainFrame = new JFrame();
    private FlowLayout fl = new FlowLayout(FlowLayout.CENTER, 5, 5);
    private GridLayout gl = new GridLayout(10, 10);
    private JPanel gameField = new JPanel();
    private JPanel enemyField = new JPanel();
    private JPanel playerField = new JPanel();
    private JPanel main = new JPanel();
    private JPanel setupField = new JPanel();
    private JPanel setupButtonsField = new JPanel();
    private Client_listener clientListener;
    private JPanel messagePanel = new JPanel();
    private JLabel messageLabel = new JLabel();

    Client_view(Client_listener paramListener, String paramVer){
        clientListener = paramListener;
        VERSION=paramVer;
        mainFrame.setTitle(VERSION);
    }

    public void run(){
        initField();
    }

    //метод первичной отрисовки игрового поля
    private void initField()
    {
        gameField.add(playerField);
        for (int i=0; i<10; i++){
            for (int j=0; j<10; j++){
                playerArray[i][j] = new JButton();
                playerArray[i][j].setPreferredSize(new Dimension(40, 40));
                playerArray[i][j].setName(Integer.toString(i)+Integer.toString(j));
                playerArray[i][j].addActionListener(clientListener);
                playerField.add(playerArray[i][j]);
            }
        }

        gameField.add(enemyField);
        for (int i=0; i<10; i++){
            for (int j=0; j<10; j++){
                enemyArray[i][j] = new JButton();
                enemyArray[i][j].setPreferredSize(new Dimension(40, 40));
                enemyArray[i][j].setName(Integer.toString(i)+Integer.toString(j));
                enemyArray[i][j].addActionListener(clientListener);
                enemyField.add(enemyArray[i][j]);
            }
        }
        enemyField.setVisible(false);

        for (int i=0; i<shipNamesRu.length; i++){
            ship[i] = new JButton(shipNamesRu[i]);
            ship[i].setName(shipNamesEn[i]);
            ship[i].addActionListener(clientListener);
            setupField.add(ship[i]);
        }

        JButton reset = new JButton("Сброс");
        reset.setName("reset");
        JButton startGame = new JButton("Старт");
        startGame.setName("startGame");
        setupButtonsField.add(reset);
        setupButtonsField.add(startGame);

        //слушатель кнопки СБРОС
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i=0; i<10; i++){
                    for (int j=0; j<10; j++){
                        playerArray[i][j].setBackground(null);
                        playerArray[i][j].setEnabled(true);
                    }
                }
                for (int i=0; i<ship.length; i++){
                    ship[i].setEnabled(true);
                }
                clientListener.resetShipCoordinate();
            }
        });

        //слушатель кнопки СТАРТ
        startGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (clientListener.endOfPlacing){
                    setMessageField("Ожидаем пока оппонент завершит расстановку",0);
                    setupField.setEnabled(false);
                    setupField.setVisible(false);
                    setupButtonsField.setEnabled(false);
                    setupButtonsField.setVisible(false);
                    for (int i=0; i<10; i++){
                        for (int j=0; j<10; j++){
                            playerArray[i][j].setEnabled(false);
                        }
                    }
                    clientListener.sendServerMessageReadyToStart();
                }
            }
        });

        messagePanel.add(messageLabel);
        gameField.setLayout(fl);
        setupField.setLayout(fl);
        enemyField.setLayout(gl);
        playerField.setLayout(gl);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.add(messagePanel);
        main.add(gameField);
        main.add(setupField);
        main.add(setupButtonsField);

        mainFrame.getContentPane().add(main);
        mainFrame.pack();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    //метод для рисования корабликов на поле
    void setButton(int i, int j, int side, int stage, int hit){
        if (side==0 && stage==0){
            playerArray[i][j].setEnabled(false);
            playerArray[i][j].setBackground(Color.blue);
        }
        if (side==1 && stage==0){
            enemyArray[i][j].setEnabled(false);
            enemyArray[i][j].setBackground(Color.magenta);
        }
        if (side==0 && stage==1 && hit==0){
            playerArray[i][j].setEnabled(false);
            playerArray[i][j].setBackground(Color.cyan);
        }
        if (side==1 && stage==1 && hit==0){
            enemyArray[i][j].setEnabled(false);
            enemyArray[i][j].setBackground(Color.cyan);
        }
        if (side==0 && stage==1 && hit==1){
            playerArray[i][j].setEnabled(false);
            playerArray[i][j].setBackground(Color.red);
        }
        if (side==1 && stage==1 && hit==1){
            enemyArray[i][j].setEnabled(false);
            enemyArray[i][j].setBackground(Color.red);
        }
        if (side==0 && stage==2){
            playerArray[i][j].setBackground(Color.black);
        }
        if (side==1 && stage==2){
            enemyArray[i][j].setBackground(Color.black);
        }
    }

    void setMessageField(String paramMessage, int paramColor){
        if (paramColor==0){
            messageLabel.setForeground(Color.magenta);
            messageLabel.setText(paramMessage);
        }
        if (paramColor==1){
            messageLabel.setForeground(Color.blue);
            messageLabel.setText(paramMessage);
        }
        if (paramColor==2){
            messageLabel.setForeground(Color.red);
            messageLabel.setText(paramMessage);
        }
    }

    void startGamePlay(){
        if (clientListener.endOfPlacing && clientListener.serverReadyToStartGame){
            fl.setHgap(20);
            fl.setVgap(20);
            enemyField.setVisible(true);
            enemyField.setEnabled(true);
            mainFrame.pack();
            mainFrame.setLocationRelativeTo(null);
        }
    }
    void setGameEnded(){
        for (int i=0; i<10; i++){
            for (int j=0; j<10; j++){
                playerArray[i][j].setEnabled(false);
                enemyArray[i][j].setEnabled(false);
            }
        }
    }
}