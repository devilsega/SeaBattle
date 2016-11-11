package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MainMenu extends JFrame{
    private final String VERSION = "Морской Бой v.1.0";

    void initMenu(){
        JFrame mainFrame = new JFrame(VERSION);
        GridLayout gl = new GridLayout(3,1);
        JPanel mainMenu = new JPanel();
        mainMenu.setLayout(gl);
        final int PORT=7788;
        final String LOCALADDRESS="127.0.0.1";

        JButton buttonStartSingle = new JButton("Играть с компьютером");
        buttonStartSingle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Извините, с компьютером пока не поиграть.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton buttonStartMulti = new JButton("Играть по сети");
        buttonStartMulti.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainMenu.setVisible(false);

                JButton buttonServer = new JButton("Начать игру");
                JButton buttonClient = new JButton("Присоедениться к игре");
                JLabel serverAddressLabel = new JLabel("Адрес сервера:");
                JTextField netAddress = new  JTextField();
                JPanel buttonsPanel = new JPanel();
                JPanel serverAddressPanel = new JPanel();
                JPanel multiPlayer = new JPanel();

                multiPlayer.setLayout(new BoxLayout(multiPlayer, BoxLayout.Y_AXIS));
                serverAddressPanel.setLayout(new BoxLayout(serverAddressPanel, BoxLayout.Y_AXIS));
                buttonsPanel.add(buttonServer);
                buttonsPanel.add(buttonClient);
                serverAddressPanel.add(serverAddressLabel);
                serverAddressPanel.add(netAddress);
                multiPlayer.add(buttonsPanel);
                multiPlayer.add(serverAddressPanel);
                buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                serverAddressPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                mainFrame.getContentPane().add(multiPlayer);
                mainFrame.pack();
                buttonServer.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mainMenu.setVisible(false);
                        mainFrame.dispose();
                        Server serverSP = new Server(0,PORT);
                        Thread serverThread = new Thread(serverSP);
                        serverThread.start();
                        Client_listener player = new Client_listener(1,LOCALADDRESS,PORT,VERSION);
                    }
                });
                buttonClient.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!netAddress.getText().equals("")){
                            mainMenu.setVisible(false);
                            mainFrame.dispose();
                            Client_listener player = new Client_listener(1,netAddress.getText(),PORT,VERSION);
                        }
                    }
                });
            }
        });

        JButton buttonExit = new JButton("Выход");
        buttonExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.setVisible(false);
                System.exit(0);
            }
        });

        mainMenu.add(buttonStartSingle);
        mainMenu.add(buttonStartMulti);
        mainMenu.add(buttonExit);

        mainFrame.getContentPane().add(mainMenu);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(300,200);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
}