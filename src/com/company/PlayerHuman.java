package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PlayerHuman implements ActionListener{
    private GameField gameField;
    private Fleet playerShips,enemyShips;
    private int side, enemySide;
    private ArrayList<int[]> damagedShipCoordinates = new ArrayList<>();
    private boolean shipIsSelected;
    private int shipCounter, endOfPlacing;
    private int shipPlacementStage = 0;

    PlayerHuman(int temp){
        side = temp;
        playerShips = new Fleet(side);
        if (side==0)enemySide=1;
        else enemySide=0;
    }

    public void actionPerformed(ActionEvent e) {
        JButton ClickedButton;
        ClickedButton = (JButton) e.getSource();
        if (gameField.getGamestage()==0)gameSetup(ClickedButton);
        if (gameField.getGamestage()==1)gamePlay(ClickedButton);
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

    void linkToShipCoords(Fleet tempPlayer, Fleet tempEnemy){
        playerShips=tempPlayer;
        enemyShips=tempEnemy;
    }

    private int checkIfEnemyShipIsHit(int[] temp) {
        return enemyShips.checkThisBlockisEmpty(temp[0],temp[1]);
    }

    private void gameSetup(JButton ClickedButton) {
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
                if (playerShips.getSize()==19){
                    endOfPlacing=1;
                }
            }
        }
    }

    private void gamePlay(JButton ClickedButton) {
        boolean enemyPlayerTurnIsGoing=gameField.getTurnState(enemySide);
        if (!gameField.getGameEnded() && !enemyPlayerTurnIsGoing){
            boolean playerTurnIsGoing = true;
            gameField.setTurnState(playerTurnIsGoing,side);
            if (checkIfEnemyShipIsHit(transformCoordinatesStringToInt(ClickedButton.getName()))==1){                                //hit
                gameField.setButton(transformCoordinatesStringToInt(ClickedButton.getName())[0],transformCoordinatesStringToInt(ClickedButton.getName())[1],(byte)1,(byte)1,(byte)1);
                int[]temp={transformCoordinatesStringToInt(ClickedButton.getName())[0],transformCoordinatesStringToInt(ClickedButton.getName())[1]};
                damagedShipCoordinates.add(temp);
                int shipIsDestroyed = enemyShips.deleteOneShipBlock(transformCoordinatesStringToInt(ClickedButton.getName()));
                if (shipIsDestroyed==1){
                    for (int[] value:damagedShipCoordinates){
                        gameField.setButton(value[0],value[1],1,2,1);
                    }
                    damagedShipCoordinates.clear();
                }
                gameField.runTheSinglePlayerGame();
            }
            else{
                gameField.setButton(transformCoordinatesStringToInt(ClickedButton.getName())[0],transformCoordinatesStringToInt(ClickedButton.getName())[1],(byte)1,(byte)1,(byte)0);       //miss
                gameField.runTheSinglePlayerGame();
            }
            playerTurnIsGoing = false;
            gameField.setTurnState(playerTurnIsGoing,side);
            if (enemyShips.getSize()<1){
                gameField.setGameEnded();
                JOptionPane.showMessageDialog(null,
                        "Победил игрок 1", "Конец игры",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }
}
