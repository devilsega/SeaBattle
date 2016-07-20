package com.company;

import java.util.ArrayList;
import java.util.Arrays;

public class Fleet {
    //private ArrayList<int[]> shipCoordinates = new ArrayList<>();
    private ArrayList<int[]> tempShipCoordinate = new ArrayList<>();
    private ArrayList<ArrayList<int[]>> detailedShipCoordinates = new ArrayList<>();
    private ShipCoordinatesChecks shipCoordinatesChecks = new ShipCoordinatesChecks();
    private GameField gameField;
    private int side, ship=0;
    private boolean isFirstCoord=true;

    Fleet (int tempside){          //0 - first player; 1 - second player;
        side = tempside;
        shipCoordinatesChecks.setFleet(this);
    }

    void setGameField(GameField gameField){
        this.gameField = gameField;
        gameField.setFleet(this,side);
    }

    /*void setShipCoordinate (int[] coord){
        shipCoordinates.add(coord);
    }*/

    void setShipCoordinate (int[] coord, int shipBlockCounter){
        boolean coordIsSet = false;
        ArrayList<int[]> tempShipBlock = new ArrayList<>();
        tempShipBlock.add(coord);
        if (isFirstCoord){
            detailedShipCoordinates.add(tempShipBlock);
            isFirstCoord=false;
            coordIsSet=true;
        }
        if (shipBlockCounter!=0 && !coordIsSet){
            detailedShipCoordinates.get(ship).add(tempShipBlock.get(0));
            coordIsSet=true;
        }
        if (shipBlockCounter==0 && !coordIsSet){
            detailedShipCoordinates.get(ship).add(tempShipBlock.get(0));
            ship++;
            coordIsSet=true;
            isFirstCoord=true;
        }

        /*shipCoordinatesChecks.addShipCoordinate();
        //shipCoordinates.add(coord);
        tempShipCoordinate.add(coord);
        if (shipBlockCounter==0){
            ArrayList <int[]>temp = new ArrayList<>();
            for (int[] m:tempShipCoordinate){
                temp.add(m);
            }
            detailedShipCoordinates.add(temp);
            tempShipCoordinate.clear();
        }*/
    }

    void clearShipCoordinate (){
        //shipCoordinates.clear();
        detailedShipCoordinates.clear();
        tempShipCoordinate.clear();
    }

    /*ArrayList<int[]> getShipCoordinates(){
        return shipCoordinates;
    }*/

    ArrayList<ArrayList<int[]>>getDetailedShipCoordinates (){
        return detailedShipCoordinates;
    }

    void deleteOneShipBlock (int[] coord){
        /*for (int i = 0; i < shipCoordinates.size(); i++) {
            if (shipCoordinates.get(i)[0]==coord[0] && shipCoordinates.get(i)[1]==coord[1]) {
                shipCoordinates.remove(i);
            }
        }*/
        for (ArrayList<int[]>ship:detailedShipCoordinates){
            for (int i = 0; i < ship.size(); i++) {
                if(ship.get(i)[0] ==coord[0] && ship.get(i)[1]==coord[1]){
                    ship.remove(i);
                }
            }
        }
        for (int i=0;i<detailedShipCoordinates.size();i++){
            if (detailedShipCoordinates.get(i).size()<1){
                detailedShipCoordinates.remove(i);
            }
        }
    }

    int checkThisBlockisEmpty(int i, int j){                                //if 1 => занято, if 0 => пусто
        int[]temp={i,j};
        int result = 0;
        boolean outOfBounds=false;

        if (i<-1 || j<-1 || i>10 || j>10){
            outOfBounds=true;
            result=1;
        }
        if (!outOfBounds){
            /*for (int a = 0; a < shipCoordinates.size(); a++) {
                if (temp[0] == shipCoordinates.get(a)[0] && temp[1] == shipCoordinates.get(a)[1]) {
                    result = 1;
                }
            }*/
            for (ArrayList<int[]>ship:detailedShipCoordinates) {
                for (int a = 0; a < ship.size(); a++) {
                    if (ship.get(a)[0] == temp[0] && ship.get(a)[1] == temp[1]) {
                        result = 1;
                    }
                }
            }
        }
        return result;
    }

    void setPressedButtonCoordinates (int[] temp){
        shipCoordinatesChecks.setPressedButtonCoordinates(temp);
    }
    int getCanPlaceShipBlockHere (int shipPlacementStage){
        return shipCoordinatesChecks.getCanPlaceShipBlockHere(shipPlacementStage);
    }
    int getSize(){
        int size=0;
        for (ArrayList<int[]>ship:detailedShipCoordinates) {
            for (int a = 0; a < ship.size(); a++) {
                size++;
            }
        }
        return size;
    }
}
