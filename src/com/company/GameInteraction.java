package com.company;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Arrays;

class GameInteraction implements ActionListener{
    //private PlayerAI playerAI;
    private GameField gameField;
    private Fleet playerShips,enemyShips;
    private int side;

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
        JButton ClickedButton;
        ClickedButton = (JButton) e.getSource();

        System.out.println("хожу я!");
        if (checkIfEnemyShipIsHit(transformCoordinatesStringToInt(ClickedButton.getName()))==1){
            System.out.println("HIT!");
            gameField.setButton(transformCoordinatesStringToInt(ClickedButton.getName())[0],transformCoordinatesStringToInt(ClickedButton.getName())[1],(byte)1,(byte)1,(byte)1);
            enemyShips.deleteOneShipBlock(transformCoordinatesStringToInt(ClickedButton.getName()));
            gameField.runTheSinglePlayerGame();
        }
        else{
            gameField.setButton(transformCoordinatesStringToInt(ClickedButton.getName())[0],transformCoordinatesStringToInt(ClickedButton.getName())[1],(byte)1,(byte)1,(byte)0);
            gameField.runTheSinglePlayerGame();
        }
        /*if (playerShips.getShipCoordinates().size()==0){
            JOptionPane.showConfirmDialog(null,
                    "Победил игрок 1", "Конец игры",
                    JOptionPane.PLAIN_MESSAGE);
        }*/
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