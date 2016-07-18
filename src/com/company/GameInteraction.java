package com.company;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
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

    /*void setPlayerAI(PlayerAI temp) {
        this.playerAI = temp;
    }*/
    void setGameField(GameField gameField){
        this.gameField = gameField;
    }

    public void actionPerformed(ActionEvent e) {
        JButton ClickedButton;

        ClickedButton = (JButton) e.getSource();

        System.out.println("хожу я!");
        System.out.println("");
        System.out.print("First player coords: ");
        for (int[] arr : playerShips.getShipCoordinates()) {
            System.out.print(Arrays.toString(arr));
        }
        System.out.println("");
        System.out.println("");
        System.out.print("Secont player coords: ");
        for (int[] arr : enemyShips.getShipCoordinates()) {
            System.out.print(Arrays.toString(arr));
        }
        System.out.println("");



        if (checkIfEnemyShipIsHit(transformCoordinatesStringToInt(ClickedButton.getName()))==1){
            gameField.setButton(transformCoordinatesStringToInt(ClickedButton.getName())[0],transformCoordinatesStringToInt(ClickedButton.getName())[1],(byte)1,(byte)1,(byte)1);
            playerShips.deleteOneShipBlock(transformCoordinatesStringToInt(ClickedButton.getName()));
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
        ArrayList <int[]> enemyCords;
        enemyCords=enemyShips.getShipCoordinates();
        int hit=0;
        for (int i = 0; i < enemyCords.size(); i++) {
            if (enemyCords.get(i)[0]==temp[0] && enemyCords.get(i)[1]==temp[1]) {
                hit=1;
            }
        }
        return hit;
    }
}