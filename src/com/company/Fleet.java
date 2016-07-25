package com.company;

import java.util.ArrayList;

public class Fleet {
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

    void setShipCoordinate (int[] coord, int shipBlockCounter){
        boolean coordIsSet = false;
        ArrayList<int[]> tempShipBlock = new ArrayList<>();
        tempShipBlock.add(coord);
        if (isFirstCoord){
            detailedShipCoordinates.add(tempShipBlock);
            isFirstCoord=false;
            coordIsSet=true;
            shipCoordinatesChecks.addShipCoordinate();
        }
        if (shipBlockCounter!=0 && !coordIsSet){
            detailedShipCoordinates.get(ship).add(tempShipBlock.get(0));
            coordIsSet=true;
            shipCoordinatesChecks.addShipCoordinate();
        }
        if (shipBlockCounter==0 && !coordIsSet){
            detailedShipCoordinates.get(ship).add(tempShipBlock.get(0));
            ship++;
            coordIsSet=true;
            isFirstCoord=true;
            shipCoordinatesChecks.addShipCoordinate();
        }
    }

    void clearShipCoordinate (){
        detailedShipCoordinates.clear();
        isFirstCoord=true;
        ship=0;
    }

    ArrayList<ArrayList<int[]>>getDetailedShipCoordinates (){
        return detailedShipCoordinates;
    }

    int deleteOneShipBlock (int[] coord){
        int shipIsDestroyed=0;
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
                shipIsDestroyed=1;
            }
        }
        return shipIsDestroyed;
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
