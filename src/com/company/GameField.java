package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

class GameField extends JFrame{

    private JButton[][] playerArray = new JButton[10][10];
    private JButton[][] enemyArray = new JButton[10][10];
    private String [] shipNamesRu = {"5-палубный","4-палубный","3-палубный","3-палубный","2-палубный","2-палубный"};
    private String [] shipNamesEn = {"fiveDeck1","fourDeck1","threeDeck1","threeDeck2","twoDeck1","twoDeck2"};
    private JButton [] ship = new JButton[6];
    private JFrame mainFrame = new JFrame("Морской Бой v.0.5");
    private FlowLayout fl = new FlowLayout(FlowLayout.LEADING, 5, 5);
    private GridLayout gl = new GridLayout(10, 10);
    private GridBagLayout gbl = new GridBagLayout();
    private GridBagConstraints c = new GridBagConstraints();
    private JPanel gameField = new JPanel();
    private JPanel computerField = new JPanel();
    private JPanel playerField = new JPanel();
    private JPanel main = new JPanel();
    private JPanel setupField = new JPanel();
    private JPanel setupButtonsField = new JPanel();
    private PlayerHuman playerHuman;
    private Fleet firstPlayerCoords, secondPlayerCoords;
    private PlayerAI computer;
    private boolean gameEnded=false;
    private boolean firstPlayerTurnIsGoing=true, secontPlayerTurnIsGoing=false;
    private int typeOfGame;
    private int gamestage=0;

    GameField (int type){
        typeOfGame=type;
        if (typeOfGame==0){
            playerHuman= new PlayerHuman(0);
            playerHuman.setGameField(this);
        }
        initField();
    }

    private void initField()
    {
        gameField.setLayout(fl);
        computerField.setLayout(gl);
        playerField.setLayout(gl);
        setupField.setLayout(fl);
        setupButtonsField.setLayout(fl);
        main.setLayout(gbl);

        playerHuman.setGameField(this);                               //привязка классов друг к другу

        gameField.add(playerField);
        for (int i=0; i<10; i++){
            for (int j=0; j<10; j++){
                playerArray[i][j] = new JButton();
                playerArray[i][j].setPreferredSize(new Dimension(40, 40));
                playerArray[i][j].setName(Integer.toString(i)+Integer.toString(j));
                playerArray[i][j].addActionListener(playerHuman);
                playerField.add(playerArray[i][j]);
            }
        }

        gameField.add(computerField);
        for (int i=0; i<10; i++){
            for (int j=0; j<10; j++){
                enemyArray[i][j] = new JButton();
                enemyArray[i][j].setPreferredSize(new Dimension(40, 40));
                enemyArray[i][j].setName(Integer.toString(i)+Integer.toString(j));
                enemyArray[i][j].addActionListener(playerHuman);
                computerField.add(enemyArray[i][j]);
            }
        }
        computerField.setVisible(false);

        for (int i=0; i<shipNamesRu.length; i++){
            ship[i] = new JButton(shipNamesRu[i]);
            ship[i].setName(shipNamesEn[i]);
            ship[i].addActionListener(playerHuman);
            setupField.add(ship[i]);
        }

        JButton reset = new JButton("Сброс");
        reset.setName("reset");
        JButton startGame = new JButton("Старт");
        startGame.setName("startGame");
        setupButtonsField.add(reset);
        setupButtonsField.add(startGame);

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
                playerHuman.resetShipCoordinate();
                firstPlayerCoords.clearShipCoordinate();
            }
        });

        startGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int endOfPlacing = playerHuman.getFinishShipPlacement();

                if (endOfPlacing == 1){
                    /*try
                    {
                        TimeUnit.SECONDS.sleep(2);
                    }
                    catch(InterruptedException z)
                    {
                    }*/
                    setupField.setEnabled(false);
                    setupField.setVisible(false);
                    setupButtonsField.setEnabled(false);
                    setupButtonsField.setVisible(false);
                    for (int i=0; i<10; i++){
                        for (int j=0; j<10; j++){
                            playerArray[i][j].setEnabled(false);
                        }
                    }
                    fl.setHgap(20);
                    fl.setVgap(20);
                    computerField.setVisible(true);
                    computerField.setEnabled(true);
                    mainFrame.pack();
                    mainFrame.setLocationRelativeTo(null);
                    gamestage=1;
                    playerAi();
                }
                /*if (endOfPlacing != 1){
                        System.out.println("Can NOT start");
                }*/
            }
        });

        c.insets = new Insets(5, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        main.add(gameField, c);
        c.gridx = 0;
        c.gridy = 1;
        main.add(setupField, c);
        c.gridx = 0;
        c.gridy = 2;
        main.add(setupButtonsField, c);
        mainFrame.getContentPane().add(main);
        mainFrame.pack();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

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
    private void playerAi(){                            //метод инициализации логики ИИ
        computer = new PlayerAI(1);
        computer.setGameField(this);
        computer.initAiGame();
        playerHuman.linkToShipCoords(firstPlayerCoords, secondPlayerCoords);
        computer.linkToEnemyShipCoords(firstPlayerCoords);

        /*System.out.println("");
        System.out.println("END First player coords:  ");
        for (ArrayList<int[]> arr : firstPlayerCoords.getDetailedShipCoordinates()) {
            for(int[] zzz : arr){
                System.out.print(Arrays.toString(zzz));
            }System.out.println("");
        }

        System.out.println("");
        System.out.println("END Secont player coords: ");
        for (ArrayList<int[]> arr : secondPlayerCoords.getDetailedShipCoordinates()) {
            for(int[] zzz : arr){
                System.out.print(Arrays.toString(zzz));
            }System.out.println("");
        }*/
    }

    void runTheSinglePlayerGame(){                      //метод процесса игры
        if (!gameEnded){
            computer.playAi();
        }
    }

    void setFleet(Fleet temp, int side){
        if (side==0){
            firstPlayerCoords = temp;
        }
        if (side==1){
            secondPlayerCoords = temp;
        }
    }

    void setGameEnded(){
        for (int i=0; i<10; i++){
            for (int j=0; j<10; j++){
                playerArray[i][j].setEnabled(false);
                enemyArray[i][j].setEnabled(false);
            }
        }
        gameEnded=true;
    }

    boolean getTurnState(int side){
        boolean temp=false;
        switch (side){
            case (0):{
                temp=firstPlayerTurnIsGoing;
                break;
            }
            case (1):{
                temp=secontPlayerTurnIsGoing;
                break;
            }
        }
        return temp;
    }

    void setTurnState(boolean temp, int side){
        switch (side){
            case (0):{
                firstPlayerTurnIsGoing=temp;
                break;
            }
            case (1):{
                secontPlayerTurnIsGoing=temp;
                break;
            }
        }
    }

    int getGamestage(){
        return gamestage;
    }

    boolean getGameEnded(){
        return gameEnded;
    }
}
