package com.company;

import java.util.ArrayList;

class ShipCoordinatesChecks {

    private int pressedButtonCoordI,pressedButtonCoordJ;
    private Fleet fleet;
    private ArrayList<int[]> shipCoordinate = new ArrayList<>();
    private int firstBlockCoordI = 0,firstBlockCoordJ = 0;

    void setFleet(Fleet tempFleet){
        fleet = tempFleet;
    }

    void setPressedButtonCoordinates(int[] temp){
        pressedButtonCoordI = temp[0];
        pressedButtonCoordJ = temp[1];
    }

     int getCanPlaceShipBlockHere(int shipPlacementStage) {
        int a=0;

        if (shipPlacementStage==0) {
            shipCoordinate.clear();
            firstBlockCoordI = pressedButtonCoordI;
            firstBlockCoordJ = pressedButtonCoordJ;
            a = checkAllDirections(shipPlacementStage);
        }
        if (shipPlacementStage>0){
            a = checkAllDirections(shipPlacementStage);
        }
        if (a==1){
            return 1;
        }
        else return 0;
    }

    private int checkAllDirections (int shipPlacementStage){
        int north=0,south=0,west=0,east=0;
        int[] temp = new int[2];
        boolean hasBlockBeside=false;
        int [] tempB = new int[2];
        if (shipPlacementStage>=2){
            tempB[0] = shipCoordinate.get(1)[0];
            tempB[1] = shipCoordinate.get(1)[1];
        }
        try {
            temp[0]= (pressedButtonCoordI - 1);
            temp[1]= (pressedButtonCoordJ);
            north = fleet.checkThisBlockisEmpty(pressedButtonCoordI - 1, pressedButtonCoordJ);
            for (int[] value : shipCoordinate) {
                if (value[0]==temp[0] && value[1]==temp[1]) {
                    north = 0;
                    if (shipPlacementStage<2)hasBlockBeside=true;
                    if (shipPlacementStage>=2){
                        if ((firstBlockCoordI==tempB[0] && firstBlockCoordI==pressedButtonCoordI) || (firstBlockCoordJ==tempB[1] && firstBlockCoordJ==pressedButtonCoordJ))hasBlockBeside=true;
                    }
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            north=0;
        }
        try {
            temp[0]= (pressedButtonCoordI);
            temp[1]= (pressedButtonCoordJ-1);
            west = fleet.checkThisBlockisEmpty(pressedButtonCoordI,pressedButtonCoordJ-1);
            for (int[] value : shipCoordinate) {
                if (value[0]==temp[0] && value[1]==temp[1]) {
                    west = 0;
                    if (shipPlacementStage<2)hasBlockBeside=true;
                    if (shipPlacementStage>=2){
                        if ((firstBlockCoordI==tempB[0] && firstBlockCoordI==pressedButtonCoordI) || (firstBlockCoordJ==tempB[1] && firstBlockCoordJ==pressedButtonCoordJ))hasBlockBeside=true;
                    }
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            west=0;
        }
        try {
            temp[0]= (pressedButtonCoordI+1);
            temp[1]= (pressedButtonCoordJ);
            south = fleet.checkThisBlockisEmpty(pressedButtonCoordI+1,pressedButtonCoordJ);
            for (int[] value : shipCoordinate) {
                if (value[0]==temp[0] && value[1]==temp[1]) {
                    south = 0;
                    if (shipPlacementStage<2)hasBlockBeside=true;
                    if (shipPlacementStage>=2){
                        if ((firstBlockCoordI==tempB[0] && firstBlockCoordI==pressedButtonCoordI) || (firstBlockCoordJ==tempB[1] && firstBlockCoordJ==pressedButtonCoordJ))hasBlockBeside=true;
                    }
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            south=0;
        }
        try {
            temp[0]= (pressedButtonCoordI);
            temp[1]= (pressedButtonCoordJ+1);
            east = fleet.checkThisBlockisEmpty(pressedButtonCoordI,pressedButtonCoordJ+1);
            for (int[] value : shipCoordinate) {
                if (value[0]==temp[0] && value[1]==temp[1]) {
                    east = 0;
                    if (shipPlacementStage<2)hasBlockBeside=true;
                    if (shipPlacementStage>=2){
                        if ((firstBlockCoordI==tempB[0] && firstBlockCoordI==pressedButtonCoordI) || (firstBlockCoordJ==tempB[1] && firstBlockCoordJ==pressedButtonCoordJ))hasBlockBeside=true;
                    }
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            east=0;
        }
        if ((west+north+east+south==0) && shipPlacementStage==0) return 1;
        if ((west+north+east+south==0) && hasBlockBeside && shipPlacementStage>0) return 1;
        else return 0;
    }

    void addShipCoordinate(){
        int[] temp = {pressedButtonCoordI,pressedButtonCoordJ};
        shipCoordinate.add(temp);
    }
}