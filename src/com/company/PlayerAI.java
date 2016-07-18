package com.company;

import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;

class PlayerAI {
    //private ArrayList<int[]>aiShipCoordinates = new ArrayList<>();
    private ArrayList<int[]>usedCoordinates = new ArrayList<>();
    private int shipPlacementStage;
    private GameField gameField;
    private Fleet aiShips = new Fleet(1);
    private Fleet enemyShips;
    private int[]shipCounter = {5,4,3,3,2,2};
    private boolean resetShipPlacement;

    void setGameField(GameField gameField){
        this.gameField=gameField;
    }

    void linkToEnemyShipCoords(Fleet tempEnemy){
        this.enemyShips=tempEnemy;
    }

    void initAiGame(){
        aiShips.setGameField(gameField);
        do {
            resetShipPlacement=false;
            //aiShipCoordinates.clear();
            aiShips.clearShipCoordinate();
            shipPlacementStage = 0;
            for (int value : shipCounter){
                for (int a=value; a>0; a--){
                    setupAi(a-1);
                    shipPlacementStage++;
                    if (a==1)shipPlacementStage=0;
                }
            }
        }
        while (resetShipPlacement);
        //drawShips();                  //сервисный метод, чтоб визуально убедиться, что ИИ разместил кораблики верно.
    }

    private void setupAi(int shipBlockCounter) {
        boolean coordIsSet = false;
        int CanPlaceShipBlockHere;
        int[] tempRand = new int[2];
        Random random = new Random();
        ArrayList<int[]> avaibleOptions = new ArrayList<>();

            while (!coordIsSet) {
                int randI = random.nextInt(10);
                int randJ = random.nextInt(10);
                tempRand[0]=randI;
                tempRand[1]=randJ;
                int repeat = checkIfCoordinateIsUnique(tempRand,(byte)0);

                if (!resetShipPlacement){
                    if (shipPlacementStage == 0 && repeat == 0) {                                        //выставление первой координаты.
                        aiShips.setPressedButtonCoordinates(tempRand);
                        CanPlaceShipBlockHere = aiShips.getCanPlaceShipBlockHere(shipPlacementStage);
                        if (CanPlaceShipBlockHere == 1) {
                            aiShips.setShipCoordinate(tempRand,shipBlockCounter);
                            coordIsSet = true;
                        }
                    }
                    if (shipPlacementStage > 0 && repeat == 0) {
                        for (int[] value : aiShips.getShipCoordinates()) {
                            for (int a = -1; a < 2; a++) {
                                for (int b = -1; b < 2; b++) {
                                    if ((value[0] + a)>=0 && (value[1] + b)>=0){
                                        int[] temp = new int[2];
                                        temp[0]=value[0] + a;
                                        temp[1]=value[1] + b;
                                        aiShips.setPressedButtonCoordinates(temp);
                                        CanPlaceShipBlockHere = aiShips.getCanPlaceShipBlockHere(shipPlacementStage);
                                        if (CanPlaceShipBlockHere == 1 && checkIfCoordinateIsUnique(temp,(byte)0) == 0){
                                            avaibleOptions.add(temp);
                                        }
                                    }
                                }
                            }
                        }
                        if (avaibleOptions.size() > 0) {
                            int rand = random.nextInt(avaibleOptions.size());
                            aiShips.setPressedButtonCoordinates(avaibleOptions.get(rand));
                            aiShips.setShipCoordinate(avaibleOptions.get(rand),shipBlockCounter);
                            coordIsSet = true;
                        }
                        if (avaibleOptions.size()==0){
                            resetShipPlacement=true;
                            coordIsSet = true;
                        }
                        avaibleOptions.clear();
                    }
                }
                if (resetShipPlacement)coordIsSet=true;
            }
        }

    private int checkIfCoordinateIsUnique(int[]temp, byte side) {
        int repeat=0;

        if (side==0){
            for (int i = 0; i < aiShips.getShipCoordinates().size(); i++) {
                if (aiShips.getShipCoordinates().get(i)[0]==temp[0] && aiShips.getShipCoordinates().get(i)[1]==temp[1]) {
                    repeat=1;
                }
            }
        }
        if (side==1){
            for (int a=0;a<enemyShips.getShipCoordinates().size();a++){
                if (enemyShips.getShipCoordinates().get(a)[0]==temp[0] && enemyShips.getShipCoordinates().get(a)[1]==temp[1]) {
                    repeat=1;
                }
            }
        }
        if (side==2) {
            for (int a=0;a<usedCoordinates.size();a++){
                if (usedCoordinates.get(a)[0]==temp[0] && usedCoordinates.get(a)[1]==temp[1]) {
                    repeat=1;
                }
            }
        }
        return repeat;
    }

    private void drawShips(){
        for (int[] value:aiShips.getShipCoordinates()){
            gameField.setButton(value[0],value[1],(byte)1,(byte)0,(byte)0);
        }
    }

    void playAi(){
        System.out.println("ходит ИИ!");
        System.out.println("");
        System.out.print("First player coords: ");
        for (int[] arr : enemyShips.getShipCoordinates()) {
            System.out.print(Arrays.toString(arr));
        }
        System.out.println("");
        System.out.println("");
        System.out.print("Secont player coords: ");
        for (int[] arr : aiShips.getShipCoordinates()) {
            System.out.print(Arrays.toString(arr));
        }
        System.out.println("");
        Random random = new Random();
        boolean endOfTurn=false;
        int[] rand = new int[2];
        while (!endOfTurn){
            int randI = random.nextInt(10);
            int randJ = random.nextInt(10);
            rand[0]=randI;
            rand[1]=randJ;
            if (checkIfCoordinateIsUnique(rand,(byte)2)==0){
                if (checkIfCoordinateIsUnique(rand,(byte)1)==1){
                    gameField.setButton(rand[0],rand[1],(byte)0,(byte)1,(byte)1);
                    enemyShips.deleteOneShipBlock(rand);
                    usedCoordinates.add(rand);
                    endOfTurn=true;
                    System.out.println("HIT!");
                }
                if (checkIfCoordinateIsUnique(rand,(byte)1)==0 && !endOfTurn){
                    gameField.setButton(rand[0],rand[1],(byte)0,(byte)1,(byte)0);
                    usedCoordinates.add(rand);
                    endOfTurn=true;
                    System.out.println("MISS!");
                }
            }
        }
    }
}
