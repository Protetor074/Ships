package central;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class CentralGUI extends JFrame {

    JPanel mainPanel;

    public CentralGUI() throws IOException {
        super("Centrum Dowodzenia");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(null);
        createGUI();
        startServer();
    }

    private void createGUI(){
        setSize(900,920);
        mainPanel = new JPanel();
        setContentPane(mainPanel);
        mainPanel.setLayout(null);
        setResizable(false);

        CentralMap centralMap = new CentralMap();
        mainPanel.add(centralMap);
        centralMap.setBounds(0,0,880,880);
        centralMap.setBorder(BorderFactory.createTitledBorder("Mapa"));

    }

    public void startServer() throws IOException {
        ServerSocket ss = new ServerSocket(4000);
        while (true) {
            //część serwerowa
            Socket s = ss.accept();
            newMessage(s);
        }
    }



    private void newMessage(Socket s) throws IOException {
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);

        String string = bf.readLine();
        System.out.println(string);
        String[] message = string.split(";");
        editSea(message);
    }

    private void editSea(String[] message){
        for(int i=0;i<75;i=i+3){
            int x = Integer.parseInt(message[i]);
            int y = Integer.parseInt(message[i+1]);
            int level = Integer.parseInt(message[i+2]);
            CentralMap.bigMap[y][x].setBackground(new Color(0,0,255-level*20));
        }
    }

}
