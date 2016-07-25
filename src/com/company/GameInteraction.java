package com.company;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

class GameInteraction implements ActionListener{
    private GameField gameField;
    private Fleet playerShips,enemyShips;
    private int side;
    private ArrayList<int[]> damagedShipCoordinates = new ArrayList<>();

    GameInteraction(int temp){
        side = temp;
    }

    void linkToShipCoords(Fleet tempPlayer, Fleet tempEnemy){
        playerShips=tempPlayer;
        enemyShips=tempEnemy;
    }

    void setGameField(GameField gameField){
        this.gameField = gameField;
    }

    public void actionPerformed(ActionEvent e) {
        if (!gameField.getGameEnded()){
            JButton ClickedButton;
            ClickedButton = (JButton) e.getSource();
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
            if (enemyShips.getSize()<1){
                gameField.setGameEnded();
                JOptionPane.showMessageDialog(null,
                        "Победил игрок 1", "Конец игры",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    private int[] transformCoordinatesStringToInt (String temp){
        int[] arrayOfClickedButtonCoords = new int[2];
        StringBuilder bld = new StringBuilder(temp);
        arrayOfClickedButtonCoords[0]=Character.getNumericValue(bld.charAt(0));
        arrayOfClickedButtonCoords[1]=Character.getNumericValue(bld.charAt(1));
        return arrayOfClickedButtonCoords;
    }

    private int checkIfEnemyShipIsHit(int[] temp) {
        return enemyShips.checkThisBlockisEmpty(temp[0],temp[1]);
    }
}