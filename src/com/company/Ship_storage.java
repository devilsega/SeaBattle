package com.company;

import java.util.ArrayList;

class Ship_storage {
    private Ship_controller shipController;
    private final int SIZEOFSHIP;
    private ArrayList <int[]> shipCoords = new ArrayList<>();
    private ArrayList <int[]> historyShipCoords = new ArrayList<>();
    private int flagDestroyedShip = 0;

    Ship_storage(Ship_controller paramController, int paramSizeOfShip){
        shipController = paramController;
        SIZEOFSHIP = paramSizeOfShip;
    }

    boolean set(int[] paramShipCoords){
        if (shipCoords.size()<SIZEOFSHIP){
            shipCoords.add(paramShipCoords);
            historyShipCoords.add(paramShipCoords);
            //сортировка пузырьком
            if(shipCoords.size()>1){
                for(int i = shipCoords.size()-1 ; i > 0 ; i--){
                    for(int j = 0 ; j < i ; j++){
                        //для вертикального
                        if (shipCoords.get(0)[1]==shipCoords.get(shipCoords.size()-1)[1]){
                            if( shipCoords.get(j)[0] > shipCoords.get(j+1)[0] ){
                                int[] tmp = shipCoords.get(j);
                                shipCoords.set(j,shipCoords.get(j+1));
                                shipCoords.set(j+1,tmp);
                            }
                        }
                        //для горизонтального
                        if (shipCoords.get(0)[0]==shipCoords.get(shipCoords.size()-1)[0]){
                            if( shipCoords.get(j)[1] > shipCoords.get(j+1)[1] ){
                                int[] tmp = shipCoords.get(j);
                                shipCoords.set(j,shipCoords.get(j+1));
                                shipCoords.set(j+1,tmp);
                            }
                        }
                    }
                }
            }
            return true;
        }
        else return false;
    }

    int[] get(int paramIndex){
        return shipCoords.get(paramIndex);
    }

    int size(){
        return shipCoords.size();
    }

    int getSIZEOFSHIP(){
        return SIZEOFSHIP;
    }

    //при первом запросе отдаёт флаг потопленного корабля, в дальнейшем его не отдаёт.
    boolean getflagDestroyedShip(){
        boolean temp = false;
        if (flagDestroyedShip ==1){
            temp=true;
            flagDestroyedShip =2;
        }
        return temp;
    }

    String getHistoryShipCoords(){
        StringBuilder coords = new StringBuilder();
        for (int[] item:historyShipCoords) {
            coords.append(item[0]).append(item[1]);
        }
        return coords.toString();
    }

    void deleteOneShipCoord(int[]paramShipCoords){
        for (int i = 0; i < shipCoords.size(); i++) {
            if(paramShipCoords[0]==shipCoords.get(i)[0] && paramShipCoords[1]==shipCoords.get(i)[1]){
                shipCoords.remove(i);
                break;
            }
        }
        if (size()==0 && flagDestroyedShip<1){
            flagDestroyedShip =1;
        }
    }

    void clearShipCoordinates(){
        shipCoords.clear();
        historyShipCoords.clear();
        flagDestroyedShip =0;
    }

    int checkThisBlockisEmpty(int[]paramShipCoords){        //0 - пусто, 1 - занято
        int result = 0;
        for (int[]item:shipCoords) {
            if(item[0]==paramShipCoords[0] && item[1]==paramShipCoords[1]){
                result=1;
                break;
            }
        }
        return result;
    }
}