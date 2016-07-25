package com.company;

import javax.swing.*;
import java.util.Random;
import java.util.ArrayList;

class PlayerAI {
    private ArrayList<int[]>usedCoordinates = new ArrayList<>();
    private ArrayList<int[]>damagedShipCoordinates = new ArrayList<>();
    private int shipPlacementStage;
    private GameField gameField;
    private Fleet aiShips = new Fleet(1);
    private Fleet enemyShips;
    private int[]shipCounter = {5,4,3,3,2,2};
    private boolean resetShipPlacement, shipIsHit;
    private int shipHits=0;

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
                        for (ArrayList<int[]> ship:aiShips.getDetailedShipCoordinates()){
                            for(int[] value:ship){
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
            if (aiShips.checkThisBlockisEmpty(temp[0],temp[1])==1)repeat=1;
        }
        if (side==1){
            if (enemyShips.checkThisBlockisEmpty(temp[0],temp[1])==1)repeat=1;
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
        for (ArrayList<int[]> ship:aiShips.getDetailedShipCoordinates()){
            for(int[] shipBlock:ship){
                gameField.setButton(shipBlock[0],shipBlock[1],1,0,0);
            }
        }
    }

    private void sortDamageCoordinates(){
        if (damagedShipCoordinates.size()>1){
            if (damagedShipCoordinates.get(damagedShipCoordinates.size()-1)[0]==damagedShipCoordinates.get(0)[0]){
                for (int i = damagedShipCoordinates.size() - 1; i >= 0; i--) {
                    for (int j = 0; j < i; j++) {
                        if (damagedShipCoordinates.get(j)[1] > damagedShipCoordinates.get(j+1)[1]) {
                            int t = damagedShipCoordinates.get(j)[1];
                            damagedShipCoordinates.get(j)[1] = damagedShipCoordinates.get(j+1)[1];
                            damagedShipCoordinates.get(j+1)[1] = t;
                        }
                    }
                }
            }
            if (damagedShipCoordinates.get(damagedShipCoordinates.size()-1)[1]==damagedShipCoordinates.get(0)[1]){
                for (int i = damagedShipCoordinates.size() - 1; i >= 0; i--) {
                    for (int j = 0; j < i; j++) {
                        if (damagedShipCoordinates.get(j)[0] > damagedShipCoordinates.get(j+1)[0]) {
                            int t = damagedShipCoordinates.get(j)[0];
                            damagedShipCoordinates.get(j)[0] = damagedShipCoordinates.get(j+1)[0];
                            damagedShipCoordinates.get(j+1)[0] = t;
                        }
                    }
                }
            }
        }
    }

    void playAi(){
        Random random = new Random();
        boolean endOfTurn=false;
        int[] rand = new int[2];
        ArrayList<int[]> avaibleOptions = new ArrayList<>();
        while (!endOfTurn){
            int randI = random.nextInt(10);
            int randJ = random.nextInt(10);
            rand[0]=randI;
            rand[1]=randJ;
            if (checkIfCoordinateIsUnique(rand,(byte)2)==0 && !shipIsHit){                      //попытка попасть в НОВЫЙ корабль
                if (checkIfCoordinateIsUnique(rand,(byte)1)==1){                                //hit
                    gameField.setButton(rand[0],rand[1],0,1,1);
                    enemyShips.deleteOneShipBlock(rand);
                    usedCoordinates.add(rand);
                    endOfTurn=true;
                    shipIsHit=true;
                    damagedShipCoordinates.add(rand);
                    shipHits++;
                }
                if (checkIfCoordinateIsUnique(rand,(byte)1)==0 && !endOfTurn){                  //miss
                    gameField.setButton(rand[0],rand[1],0,1,0);
                    usedCoordinates.add(rand);
                    endOfTurn=true;
                }
            }
            if (shipIsHit && shipHits==1 && !endOfTurn){                                                                                        //Первая попытка добить УЖЕ ПОДБИТЫЙ корабль
                int [] option1={damagedShipCoordinates.get(0)[0]-1,damagedShipCoordinates.get(0)[1]};
                int [] option2={damagedShipCoordinates.get(0)[0]+1,damagedShipCoordinates.get(0)[1]};
                int [] option3={damagedShipCoordinates.get(0)[0],damagedShipCoordinates.get(0)[1]-1};
                int [] option4={damagedShipCoordinates.get(0)[0],damagedShipCoordinates.get(0)[1]+1};
                avaibleOptions.add(option1);
                avaibleOptions.add(option2);
                avaibleOptions.add(option3);
                avaibleOptions.add(option4);
                int randOption = random.nextInt(4);
                if (avaibleOptions.get(randOption)[0]>=0 && avaibleOptions.get(randOption)[1]>=0 && avaibleOptions.get(randOption)[0]<10 && avaibleOptions.get(randOption)[1]<10){
                    if (checkIfCoordinateIsUnique(avaibleOptions.get(randOption),(byte)2)==0){
                        if (checkIfCoordinateIsUnique(avaibleOptions.get(randOption),(byte)1)==1){                                          //hit
                            gameField.setButton(avaibleOptions.get(randOption)[0],avaibleOptions.get(randOption)[1],0,1,1);
                            damagedShipCoordinates.add(avaibleOptions.get(randOption));
                            int shipIsDestroyed = enemyShips.deleteOneShipBlock(avaibleOptions.get(randOption));
                            if (shipIsDestroyed==1){
                                for (int[] value:damagedShipCoordinates){
                                    gameField.setButton(value[0],value[1],0,2,1);
                                }
                                shipIsHit=false;
                                shipHits=0;
                                damagedShipCoordinates.clear();
                            }
                            else{
                                sortDamageCoordinates();
                                shipHits++;
                            }
                            usedCoordinates.add(avaibleOptions.get(randOption));
                            endOfTurn=true;
                        }
                        if (checkIfCoordinateIsUnique(avaibleOptions.get(randOption),(byte)1)==0 && !endOfTurn){                            //miss
                            gameField.setButton(avaibleOptions.get(randOption)[0],avaibleOptions.get(randOption)[1],0,1,0);
                            usedCoordinates.add(avaibleOptions.get(randOption));
                            endOfTurn=true;
                        }
                    }
                }
            }
            if (shipIsHit && shipHits>1 && !endOfTurn){                                                                                        //Остальные попытки добить УЖЕ ПОДБИТЫЙ корабль
                if (damagedShipCoordinates.get(damagedShipCoordinates.size()-1)[0]==damagedShipCoordinates.get(0)[0]){
                    if (damagedShipCoordinates.get(0)[1]<damagedShipCoordinates.get(damagedShipCoordinates.size()-1)[1]){
                        int [] option1={damagedShipCoordinates.get(0)[0],damagedShipCoordinates.get(damagedShipCoordinates.size()-1)[1]+1};
                        int [] option2={damagedShipCoordinates.get(0)[0],damagedShipCoordinates.get(0)[1]-1};
                        avaibleOptions.add(option1);
                        avaibleOptions.add(option2);
                    }
                    else{
                        int [] option1={damagedShipCoordinates.get(0)[0],damagedShipCoordinates.get(damagedShipCoordinates.size()-1)[1]-1};
                        int [] option2={damagedShipCoordinates.get(0)[0],damagedShipCoordinates.get(0)[1]+1};
                        avaibleOptions.add(option1);
                        avaibleOptions.add(option2);
                    }
                }
                if (damagedShipCoordinates.get(damagedShipCoordinates.size()-1)[1]==damagedShipCoordinates.get(0)[1]){
                    if(damagedShipCoordinates.get(0)[0]<damagedShipCoordinates.get(damagedShipCoordinates.size()-1)[0]){
                        int [] option1={damagedShipCoordinates.get(damagedShipCoordinates.size()-1)[0]+1,damagedShipCoordinates.get(0)[1]};
                        int [] option2={damagedShipCoordinates.get(0)[0]-1,damagedShipCoordinates.get(0)[1]};
                        avaibleOptions.add(option1);
                        avaibleOptions.add(option2);
                    }
                    else{
                        int [] option1={damagedShipCoordinates.get(damagedShipCoordinates.size()-1)[0]-1,damagedShipCoordinates.get(0)[1]};
                        int [] option2={damagedShipCoordinates.get(0)[0]+1,damagedShipCoordinates.get(0)[1]};
                        avaibleOptions.add(option1);
                        avaibleOptions.add(option2);
                    }
                }
                int randOption = random.nextInt(2);
                if (avaibleOptions.get(randOption)[0]>=0 && avaibleOptions.get(randOption)[1]>=0 && avaibleOptions.get(randOption)[0]<10 && avaibleOptions.get(randOption)[1]<10){
                    if (checkIfCoordinateIsUnique(avaibleOptions.get(randOption),(byte)2)==0){
                        if (checkIfCoordinateIsUnique(avaibleOptions.get(randOption),(byte)1)==1){                                      //hit
                            gameField.setButton(avaibleOptions.get(randOption)[0],avaibleOptions.get(randOption)[1],0,1,1);
                            damagedShipCoordinates.add(avaibleOptions.get(randOption));
                            int shipIsDestroyed = enemyShips.deleteOneShipBlock(avaibleOptions.get(randOption));
                            if (shipIsDestroyed==1){
                                for (int[] value:damagedShipCoordinates){
                                    gameField.setButton(value[0],value[1],0,2,1);
                                }
                                shipIsHit=false;
                                shipHits=0;
                                damagedShipCoordinates.clear();
                            }
                            else{
                                sortDamageCoordinates();
                                shipHits++;
                            }
                            usedCoordinates.add(avaibleOptions.get(randOption));
                            endOfTurn=true;
                        }
                        if (checkIfCoordinateIsUnique(avaibleOptions.get(randOption),(byte)1)==0 && !endOfTurn){                        //miss
                            gameField.setButton(avaibleOptions.get(randOption)[0],avaibleOptions.get(randOption)[1],0,1,0);
                            usedCoordinates.add(avaibleOptions.get(randOption));
                            endOfTurn=true;
                        }
                    }
                }
            }
        }
        if (enemyShips.getSize()<1){
            gameField.setGameEnded();
            JOptionPane.showMessageDialog(null,
                    "Победил компьютер!", "Конец игры",
                    JOptionPane.PLAIN_MESSAGE);
        }
    }
}
