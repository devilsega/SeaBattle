package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

class GameField extends JFrame{

    private JButton[][] playerArray = new JButton[10][10];
    private JButton[][] enemyArray = new JButton[10][10];
    private String [] shipNamesRu = {"5-палубный","4-палубный","3-палубный","3-палубный","2-палубный","2-палубный"};
    private String [] shipNamesEn = {"fiveDeck1","fourDeck1","threeDeck1","threeDeck2","twoDeck1","twoDeck2"};
    private JButton [] ship = new JButton[6];
    private JFrame mainFrame = new JFrame("Морской Бой v.0.1");
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
    private SetupInteraction setupListener = new SetupInteraction();
    private GameInteraction gameListener = new GameInteraction();
    /*private ArrayList<int[]> firstPlayerShipCoordinate = new ArrayList<>();
    private ArrayList<int[]> secontPlayerShipCoordinate = new ArrayList<>();
    private ArrayList<ArrayList<int[]>> detailedShipCoordinatesFirstPlayer = new ArrayList<>();
    private ArrayList<ArrayList<int[]>> detailedShipCoordinatesSecontPlayer = new ArrayList<>();*/
    private Fleet firstPlayerCoords,secontPlayerCoords;

    void initField()
    {
        gameField.setLayout(fl);
        computerField.setLayout(gl);
        playerField.setLayout(gl);
        setupField.setLayout(fl);
        setupButtonsField.setLayout(fl);
        main.setLayout(gbl);

        setupListener.setGameField(this);                               //привязка классов друг к другу

        gameField.add(playerField);
        for (int i=0; i<10; i++){
            for (int j=0; j<10; j++){
                playerArray[i][j] = new JButton();
                playerArray[i][j].setPreferredSize(new Dimension(40, 40));
                playerArray[i][j].setName(Integer.toString(i)+Integer.toString(j));
                playerArray[i][j].addActionListener(setupListener);
                playerField.add(playerArray[i][j]);
            }
        }

        gameField.add(computerField);
        for (int i=0; i<10; i++){
            for (int j=0; j<10; j++){
                enemyArray[i][j] = new JButton();
                enemyArray[i][j].setPreferredSize(new Dimension(40, 40));
                enemyArray[i][j].setName(Integer.toString(i)+Integer.toString(j));
                enemyArray[i][j].addActionListener(gameListener);
                computerField.add(enemyArray[i][j]);
            }
        }
        computerField.setVisible(false);

        for (int i=0; i<shipNamesRu.length; i++){
            ship[i] = new JButton(shipNamesRu[i]);
            ship[i].setName(shipNamesEn[i]);
            ship[i].addActionListener(setupListener);
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
                setupListener.resetShipCoordinate();
                firstPlayerCoords.clearShipCoordinate();
                secontPlayerCoords.clearShipCoordinate();
            }
        });

        startGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int endOfPlacing = setupListener.getFinishShipPlacement();

                //System.out.println("detailedShipCoordinatesFirstPlayer: "+detailedShipCoordinatesFirstPlayer.size());
                //System.out.println("удаляем позицию");
                //detailedShipCoordinatesFirstPlayer.get(0).remove(0);
                //detailedShipCoordinatesFirstPlayer.get(4).remove(0);
                //System.out.println("detailedShipCoordinatesFirstPlayer: "+detailedShipCoordinatesFirstPlayer.size());


                if (endOfPlacing == 1){
                    /*try
                    {
                        TimeUnit.SECONDS.sleep(2);
                    }
                    catch(InterruptedException z)
                    {
                    }*/

                    //ShipCoordinate = setupListener.getShipCoordinate();
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

                    playerAi();
                    System.out.println("");
                    System.out.print("END First player coords:  ");
                    for (int[] arr : firstPlayerCoords.getShipCoordinates()) {
                        System.out.print(Arrays.toString(arr));
                    }
                    System.out.println("");
                    System.out.print("END Secont player coords: ");
                    for (int[] arr : secontPlayerCoords.getShipCoordinates()) {
                        System.out.print(Arrays.toString(arr));
                    }
                    System.out.println("");
                }
                if (endOfPlacing != 1){
                        System.out.println("Can NOT start");
                }
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
    }
    private void playerAi(){                            //метод инициализации логики ИИ
        PlayerAI computer = new PlayerAI();
        computer.setGameField(this);
        gameListener.setGameField(this);
        computer.initAiGame();
        gameListener.setPlayerAI(computer);
        gameListener.linkToShipCoords(firstPlayerCoords,secontPlayerCoords);
        computer.linkToEnemyShipCoords(secontPlayerCoords);
    }

    void setFleet(Fleet temp, int side){
        if (side==0){
            firstPlayerCoords = temp;
        }
        if (side==1){
            secontPlayerCoords = temp;
        }
    }
}