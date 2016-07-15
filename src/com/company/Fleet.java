package com.company;

import java.util.ArrayList;

public class Fleet {
    private ArrayList<int[]> shipCoordinates = new ArrayList<>();
    private ArrayList<int[]> tempShipCoordinate = new ArrayList<>();
    private ArrayList<ArrayList<int[]>> detailedShipCoordinates = new ArrayList<>();
    private ShipCoordinatesChecks shipCoordinatesChecks = new ShipCoordinatesChecks();
    private GameField gameField;
    private int side;

    Fleet (int tempside){          //0 - first player; 1 - second player;
        side = tempside;
        shipCoordinatesChecks.setFleet(this);
    }

    void setGameField(GameField gameField){
        this.gameField = gameField;
        gameField.setFleet(this,side);
    }

    void setShipCoordinate (int[] coord){
        shipCoordinates.add(coord);
    }

    void setShipCoordinate (int[] coord, int stage){
        shipCoordinates.add(coord);
        tempShipCoordinate.add(coord);
        /*if (stage==0){
            detailedShipCoordinates.add(tempShipCoordinate);
            tempShipCoordinate.clear();

            for (int i=0;i<detailedShipCoordinates.size();i++) {
                //System.out.println(detailedShipCoordinates.get(i).size());
            }
        }*/
    }

    void clearShipCoordinate (){
        shipCoordinates.clear();
        detailedShipCoordinates.clear();
    }

    ArrayList<int[]> getShipCoordinates(){
        return shipCoordinates;
    }

    void deleteOneShipBlock (int[] coord){
        for (int i = 0; i < shipCoordinates.size(); i++) {
            if (shipCoordinates.get(i)[0]==coord[0] && shipCoordinates.get(i)[1]==coord[1]) {
                shipCoordinates.remove(i);
            }
        }
    }

    int checkThisBlockisEmpty(int i, int j){                                //if 1 => занято, if 0 => пусто
        int[]temp={i,j};
        int result = 0;
        boolean hasMinus=false;

        if (i<-1 || j<-1 || i>10 || j>10){
            hasMinus=true;
            result=1;
        }
        if (!hasMinus){
            for (int a = 0; a < shipCoordinates.size(); a++) {
                if (temp[0] == shipCoordinates.get(a)[0] && temp[1] == shipCoordinates.get(a)[1]) {
                    result = 1;
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
    void addShipCoordinate(){
        shipCoordinatesChecks.addShipCoordinate();
    }
}
