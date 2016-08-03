package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MainMenu extends JFrame{

    void initMenu(){
        JFrame mainFrame = new JFrame("Морской Бой v.0.5");
        GridLayout gl = new GridLayout(3,1);
        FlowLayout fl = new FlowLayout();
        JPanel mainMenu = new JPanel();
        JPanel multiSelect = new JPanel();
        mainMenu.setLayout(gl);
        multiSelect.setLayout(fl);

        JButton buttonStartSingle = new JButton("Играть с компьютером");
        buttonStartSingle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainMenu.setVisible(false);
                mainFrame.dispose();
                GameField MainGame = new GameField(0);
            }
        });

        JButton buttonStartMulti = new JButton("Играть по сети");
        buttonStartMulti.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainMenu.setVisible(false);
                JButton buttonServer = new JButton("Начать игру");
                JButton buttonClient = new JButton("Присоедениться к игре");
                multiSelect.add(buttonServer);
                multiSelect.add(buttonClient);
                mainFrame.getContentPane().add(multiSelect);
                mainFrame.pack();
            }
        });

        JButton buttonExit = new JButton("Выход");
        buttonExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainFrame.setVisible(false);
                System.exit(0);
            }
        });

        mainMenu.add(buttonStartSingle);
        mainMenu.add(buttonStartMulti);
        mainMenu.add(buttonExit);

        mainFrame.getContentPane().add(mainMenu);
        //mainFrame.pack();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(300,200);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
}