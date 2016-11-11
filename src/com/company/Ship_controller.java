package com.company;

import java.util.ArrayList;

class Ship_controller {
    private ArrayList <Ship_storage>shipsArray = new ArrayList<>();
    private final String[] sides = {"CENTER","NORTH","SOUTH","WEST","EAST"};
    private boolean shipIsSelected;
    private int shipPlacementStage=0;
    private Ship_storage activeShip;
    private int activeShipsArrayNum;
    private boolean activeShipPlacingIsComplete = false;

    //по умолчанию получает стандартную конфигурацию: 543322
    Ship_controller(String paramTypeOfShips){
        ArrayList<Integer>typeOfShips = new ArrayList<>();
        for(int i = 0; i<paramTypeOfShips.length(); i++){
            int x = Character.getNumericValue(paramTypeOfShips.charAt(i));
            typeOfShips.add(x);
        }

        for (int item:typeOfShips) {
            Ship_storage ship = new Ship_storage(this,item);
            shipsArray.add(ship);
        }
    }

    void resetShips(){
        for (Ship_storage item:shipsArray) {
            item.clearShipCoordinates();
        }
        activeShip = null;
        shipIsSelected=false;
        shipPlacementStage=0;
        activeShipPlacingIsComplete=false;
    }

    boolean getactiveShipPlacingStatus(){
        return activeShipPlacingIsComplete;
    }

    int shipsSize(){
        int size = 0;
        for (Ship_storage item:shipsArray) {
            size=size+item.size();
        }
        return size;
    }

    int setupActions(int paramTypeOfShip, int[]paramCoords){
        int result = 0;
        if(checkCoordinatesAtSetup(paramCoords)==0){
            setShipCoordAtSetup(paramTypeOfShip,paramCoords);
            result=1;
        }
        return result;
    }

    int gameplayActions(int []paramCoords){                         //0 - промах, 1 - попадание
        int result = checkThisBlockIsEmpty(paramCoords);
        if (result==1){
            for (Ship_storage item:shipsArray) {
                item.deleteOneShipCoord(paramCoords);
            }
        }
        return result;
    }

    //отдаёт координаты затопленного на данном ходу корабля, в случае отсутсвия такового отдаёт null
    String checkDestroyedShips(){
        String coords = null;
        for (int i = 0; i<shipsArray.size();i++) {
            if(shipsArray.get(i).getflagDestroyedShip()){
                coords=shipsArray.get(i).getHistoryShipCoords();
                break;
            }
        }
        return coords;
    }

    private int checkThisBlockIsEmpty(int[]paramCoords){                //0 - пусто, 1 - занято
        int result = 0;
        for (Ship_storage item:shipsArray) {
            if (item.checkThisBlockisEmpty(paramCoords)==1){
                result=1;
                break;
            }
        }
        return result;
    }

    private void setShipCoordAtSetup(int paramTypeOfShip, int[]paramCoords){
        if (!shipIsSelected){
            for (int i=0; i<shipsArray.size(); i++){
                if ((shipsArray.get(i).getSIZEOFSHIP()==paramTypeOfShip)&&(shipsArray.get(i).size()==0)){
                    activeShipsArrayNum=i;
                    shipIsSelected=true;
                    activeShip=shipsArray.get(activeShipsArrayNum);
                    activeShipPlacingIsComplete=false;
                    break;
                }
            }
        }
        shipsArray.get(activeShipsArrayNum).set(paramCoords);
        shipPlacementStage++;
        if (shipsArray.get(activeShipsArrayNum).size()==shipsArray.get(activeShipsArrayNum).getSIZEOFSHIP()){
            activeShipPlacingIsComplete=true;
            activeShip=null;
            shipPlacementStage=0;
            shipIsSelected=false;
        }
    }

    //основной метод проверки допустимости установки координат во время фазы растановки
    //0 - ok, 1 - no
    private int checkCoordinatesAtSetup(int[]clickedCoords){
        ArrayList <int[]> possibleMoves = new  ArrayList<>();
        int result = 0;

        if(shipPlacementStage>1){
            result=1;
            int[]tempCordsFirst=null;
            int[]tempCordsLast=null;
            //составление массива всех допустимых вариантов постановки
            //проверка вертикально
            if(activeShip.get(0)[1]==activeShip.get(activeShip.size()-1)[1]){
                if(activeShip.get(0)[0]<activeShip.get(activeShip.size()-1)[0]){
                    tempCordsFirst = modifyCoords(activeShip.get(0),sides[1]);
                    tempCordsLast = modifyCoords(activeShip.get(activeShip.size()-1),sides[2]);
                }
                if(activeShip.get(0)[0]>activeShip.get(activeShip.size()-1)[0]){
                    tempCordsLast = modifyCoords(activeShip.get(0),sides[1]);
                    tempCordsFirst = modifyCoords(activeShip.get(activeShip.size()-1),sides[2]);
                }
                if (checkThisBlockIsEmpty(tempCordsFirst)!=1){
                    if(checkThisBlockIsEmpty(modifyCoords(tempCordsFirst,"WEST"))!=1){
                        if(checkThisBlockIsEmpty(modifyCoords(tempCordsFirst,"NORTH"))!=1){
                            if(checkThisBlockIsEmpty(modifyCoords(tempCordsFirst,"EAST"))!=1){
                                possibleMoves.add(tempCordsFirst);
                            }
                        }
                    }
                }
                if (checkThisBlockIsEmpty(tempCordsLast)!=1){
                    if(checkThisBlockIsEmpty(modifyCoords(tempCordsLast,"WEST"))!=1){
                        if(checkThisBlockIsEmpty(modifyCoords(tempCordsLast,"SOUTH"))!=1){
                            if(checkThisBlockIsEmpty(modifyCoords(tempCordsLast,"EAST"))!=1){
                                possibleMoves.add(tempCordsLast);
                            }
                        }
                    }
                }
                //сравнение допустимых вариантов в с предложенным
                if (possibleMoves.size()!=0){
                    for (int[] tempCoord:possibleMoves) {
                        if(tempCoord[0]==clickedCoords[0] && tempCoord[1]==clickedCoords[1])result=0;
                    }
                }
            }
            //проверка горизонтально
            if(activeShip.get(0)[0]==activeShip.get(activeShip.size()-1)[0]){
                if(activeShip.get(0)[1]<activeShip.get(activeShip.size()-1)[1]){
                    tempCordsFirst = modifyCoords(activeShip.get(0),sides[3]);
                    tempCordsLast = modifyCoords(activeShip.get(activeShip.size()-1),sides[4]);
                }
                if(activeShip.get(0)[0]>activeShip.get(activeShip.size()-1)[0]){
                    tempCordsLast = modifyCoords(activeShip.get(0),sides[3]);
                    tempCordsFirst = modifyCoords(activeShip.get(activeShip.size()-1),sides[4]);
                }
                if (checkThisBlockIsEmpty(tempCordsFirst)!=1){
                    if(checkThisBlockIsEmpty(modifyCoords(tempCordsFirst,"WEST"))!=1){
                        if(checkThisBlockIsEmpty(modifyCoords(tempCordsFirst,"NORTH"))!=1){
                            if(checkThisBlockIsEmpty(modifyCoords(tempCordsFirst,"SOUTH"))!=1){
                                possibleMoves.add(tempCordsFirst);
                            }
                        }
                    }
                }
                if (checkThisBlockIsEmpty(tempCordsLast)!=1){
                    if(checkThisBlockIsEmpty(modifyCoords(tempCordsLast,"NORTH"))!=1){
                        if(checkThisBlockIsEmpty(modifyCoords(tempCordsLast,"SOUTH"))!=1){
                            if(checkThisBlockIsEmpty(modifyCoords(tempCordsLast,"EAST"))!=1){
                                possibleMoves.add(tempCordsLast);
                            }
                        }
                    }
                }
                if (possibleMoves.size()!=0){
                    for (int[] tempCoord:possibleMoves) {
                        if(tempCoord[0]==clickedCoords[0] && tempCoord[1]==clickedCoords[1]){
                            result=0;
                            break;
                        }
                    }
                }
            }
        }
        if (shipPlacementStage==1){
            result=1;
            int[]tempCords=null;
            //составление массива всех допустимых вариантов постановки
            for (int i = 1; i <5; i++){
                tempCords=modifyCoords(activeShip.get(0),sides[i]);
                if(checkThisBlockIsEmpty(tempCords)!=1 && i==1){
                    if(checkThisBlockIsEmpty(modifyCoords(tempCords,"NORTH"))!=1){
                        if(checkThisBlockIsEmpty(modifyCoords(tempCords,"WEST"))!=1){
                            if(checkThisBlockIsEmpty(modifyCoords(tempCords,"EAST"))!=1){
                                possibleMoves.add(tempCords);
                            }
                        }
                    }
                }
                if(checkThisBlockIsEmpty(tempCords)!=1 && i==2){
                    if(checkThisBlockIsEmpty(modifyCoords(tempCords,"SOUTH"))!=1){
                        if(checkThisBlockIsEmpty(modifyCoords(tempCords,"WEST"))!=1){
                            if(checkThisBlockIsEmpty(modifyCoords(tempCords,"EAST"))!=1){
                                possibleMoves.add(tempCords);
                            }
                        }
                    }
                }
                if(checkThisBlockIsEmpty(tempCords)!=1 && i==3){
                    if(checkThisBlockIsEmpty(modifyCoords(tempCords,"NORTH"))!=1){
                        if(checkThisBlockIsEmpty(modifyCoords(tempCords,"SOUTH"))!=1){
                            if(checkThisBlockIsEmpty(modifyCoords(tempCords,"WEST"))!=1){
                                possibleMoves.add(tempCords);
                            }
                        }
                    }
                }
                if(checkThisBlockIsEmpty(tempCords)!=1 && i==4){
                    if(checkThisBlockIsEmpty(modifyCoords(tempCords,"NORTH"))!=1){
                        if(checkThisBlockIsEmpty(modifyCoords(tempCords,"SOUTH"))!=1){
                            if(checkThisBlockIsEmpty(modifyCoords(tempCords,"EAST"))!=1){
                                possibleMoves.add(tempCords);
                            }
                        }
                    }
                }
            }
            //сравнение допустимых вариантов в с предложенным
            if (possibleMoves.size()!=0){
                for (int[] tempCoord:possibleMoves) {
                    if(tempCoord[0]==clickedCoords[0] && tempCoord[1]==clickedCoords[1]){
                        result=0;
                        break;
                    }
                }
            }
        }
        if (shipPlacementStage==0){
            for (int i=0;i<5;i++){
                if (checkThisBlockIsEmpty(modifyCoords(clickedCoords,sides[i]))==1){
                    result=1;
                    break;
                }
            }
        }
        return result;
    }

    private int[] modifyCoords(int[]paramCoords, String paramSide){
        int[]result = new int[2];
        result[0]=paramCoords[0];
        result[1]=paramCoords[1];
        if(paramSide.equals("NORTH"))result[0]--;
        if(paramSide.equals("SOUTH"))result[0]++;
        if(paramSide.equals("EAST"))result[1]++;
        if(paramSide.equals("WEST"))result[1]--;
        return result;
    }
}