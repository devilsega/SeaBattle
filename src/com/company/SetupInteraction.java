package com.company;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

class SetupInteraction implements ActionListener{

    private boolean shipIsSelected;
    private int shipCounter, endOfPlacing;
    private int shipPlacementStage = 0;
    private GameField gameField;
    private Fleet playerShips = new Fleet(0);



    public void actionPerformed(ActionEvent e) {
        JButton ClickedButton;
        ClickedButton = (JButton) e.getSource();

        if (ClickedButton.getName().equals("fiveDeck1") & !shipIsSelected){
            shipIsSelected=true;
            shipCounter = 5;
            ClickedButton.setEnabled(false);
        }
        if (ClickedButton.getName().equals("fourDeck1") & !shipIsSelected){
            shipIsSelected=true;
            shipCounter = 4;
            ClickedButton.setEnabled(false);
        }
        if (ClickedButton.getName().equals("threeDeck1") & !shipIsSelected){
            shipIsSelected=true;
            shipCounter = 3;
            ClickedButton.setEnabled(false);
        }
        if (ClickedButton.getName().equals("threeDeck2") & !shipIsSelected){
            shipIsSelected=true;
            shipCounter = 3;
            ClickedButton.setEnabled(false);
        }
        if (ClickedButton.getName().equals("twoDeck1") & !shipIsSelected){
            shipIsSelected=true;
            shipCounter = 2;
            ClickedButton.setEnabled(false);
        }
        if (ClickedButton.getName().equals("twoDeck2") & !shipIsSelected){
            shipIsSelected=true;
            shipCounter = 2;
            ClickedButton.setEnabled(false);
        }

        if (shipIsSelected &!(ClickedButton.getName().equals("fiveDeck1"))&!(ClickedButton.getName().equals("fourDeck1"))
                &!(ClickedButton.getName().equals("threeDeck1"))&!(ClickedButton.getName().equals("threeDeck2"))&!(ClickedButton.getName().equals("twoDeck1"))
                &!(ClickedButton.getName().equals("twoDeck2"))&!(ClickedButton.getName().equals("reset"))&!(ClickedButton.getName().equals("startGame"))){
            if (shipCounter>0){
                playerShips.setPressedButtonCoordinates(transformCoordinatesStringToInt(ClickedButton.getName()));
                int CanPlaceShipBlockHere =playerShips.getCanPlaceShipBlockHere(shipPlacementStage);
                if (CanPlaceShipBlockHere==1){
                    ClickedButton.setEnabled(false);
                    gameField.setButton(transformCoordinatesStringToInt(ClickedButton.getName())[0],transformCoordinatesStringToInt(ClickedButton.getName())[1],(byte)0,(byte)0,(byte)0);
                    shipCounter--;
                    shipPlacementStage++;
                    playerShips.setShipCoordinate(transformCoordinatesStringToInt(ClickedButton.getName()), shipCounter);
                }
            }
            if (shipCounter==0){
                shipIsSelected=false;
                shipPlacementStage=0;
                //if (playerShips.getShipCoordinates().size()==19){
                if (playerShips.getSize()==19){
                    endOfPlacing=1;
                }
            }
        }
    }

    int getFinishShipPlacement(){
        return endOfPlacing;
    }

    private int[] transformCoordinatesStringToInt (String temp){
        int[] arrayOfCoordinates = new int[2];
        StringBuilder bld = new StringBuilder(temp);
        arrayOfCoordinates[0]=Character.getNumericValue(bld.charAt(0));
        arrayOfCoordinates[1]=Character.getNumericValue(bld.charAt(1));
        return arrayOfCoordinates;
    }

    void resetShipCoordinate(){
        playerShips.clearShipCoordinate();
        shipIsSelected = false;
        shipPlacementStage=0;
    }

    void setGameField(GameField gameField) {
        this.gameField = gameField;
        playerShips.setGameField(gameField);
    }
}
