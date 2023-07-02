package world;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class WorldGUI extends JFrame {

    //część serwerowa
    private Socket s;
    private PrintWriter pr;
    private PrintWriter[] prb;
    private final ArrayList<String[]> shipList = new ArrayList<>();

    private final int boyaNumber = 64;//Liczba boi

    //część gui
    JPanel mainPanel;

    public WorldGUI() throws IOException {
        super("Świat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(null);
        createGUI();
        startServer();
    }

    private void createGUI() {
        setSize(900, 920);
        mainPanel = new JPanel();
        setContentPane(mainPanel);
        mainPanel.setLayout(null);
        setResizable(false);

        WorldMap worldMap = new WorldMap();
        mainPanel.add(worldMap);
        worldMap.setBounds(0, 0, 880, 880);
        worldMap.setBorder(BorderFactory.createTitledBorder("Mapa"));
    }


    public void startServer() throws IOException {
        ServerSocket ss = new ServerSocket(2000);
        Socket[] s1 = new Socket[boyaNumber];
        prb = new PrintWriter[boyaNumber];

        for (int i = 0; i < boyaNumber; i++) {
            s1[i] = ss.accept();
            prb[i] = new PrintWriter(s1[i].getOutputStream());
        }


        while (true) {
            s = ss.accept();
            //socketBoya = serverSocketBoya.accept();

            Thread t1 = new Thread(() -> {
                try {
                    newMessage(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t1.start();
        }
    }

    private void newMessage(Socket s) throws IOException {
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        pr = new PrintWriter(s.getOutputStream());
        BufferedReader bf = new BufferedReader(in);

        String string = bf.readLine();
        System.out.println(string);
        String[] message = string.split(";");

        String boyaMesage;

        if (message[0].equals("S")) {//Wiadomość od statku
            int shipNotExist = 0;
            for (String[] strings : shipList) {
                if (strings[0].equals(message[2])) {
                    shipNotExist = 1;
                    break;
                }
            }

            String id;

            if (message[1].equals("S")) {//Skan
                StringBuilder sendMesage;
                id = message[2];
                if (shipNotExist == 1) {
                    sendMesage = new StringBuilder(id + ";" + true);
                    for (String[] strings : shipList) {
                        sendMesage.append(";").append(strings[1]).append(";").append(strings[2]);
                    }
                } else {
                    sendMesage = new StringBuilder(id + ";" + false);

                }
                pr.println(sendMesage);
                pr.flush();

            }
            if (message[1].equals("M")) {//Ruch
                int shipMove;// 0 - nie poruszył się 1 - poruszył się bez kolizji 2 - poruszył się z koizją
                if (shipNotExist == 1) {
                    int[] pozition = new int[4];
                    id = message[2];
                    pozition[0] = Integer.parseInt(message[3]);
                    pozition[1] = Integer.parseInt(message[4]);
                    pozition[2] = Integer.parseInt(message[5]);
                    pozition[3] = Integer.parseInt(message[6]);
                    shipMove = moveShip(id, pozition[0], pozition[1], pozition[2], pozition[3]);
                    if (shipMove == 0) {
                        System.out.println("Statek o id = " + id + " nie poruszył się.");
                    }
                    if (shipMove == 1) {
                        System.out.println("Statek o id = " + id + " poruszył się z pozycji x = " + (pozition[0] + 1) + " y = " + (pozition[1] + 1) + " na pozycje x = " + (pozition[2] + 1) + " y = " + (pozition[3] + 1));
                        boyaMesage = selectBoya(pozition[0], pozition[1], false);
                        for (int i = 0; i < boyaNumber; i++) {
                            prb[i].println(boyaMesage);
                            prb[i].flush();
                        }
                        boyaMesage = selectBoya(pozition[2], pozition[3], true);
                        for (int i = 0; i < boyaNumber; i++) {
                            prb[i].println(boyaMesage);
                            prb[i].flush();
                        }
                    }
                    if (shipMove == 2) {
                        System.out.println("Statek o id = " + id + " na pozycji x = " + (pozition[0] + 1) + " y = " + (pozition[1] + 1) + " zderzył się ze statkiem na pozycje x = " + (pozition[2] + 1) + " y = " + (pozition[3] + 1));
                        boyaMesage = selectBoya(pozition[0], pozition[1], false);
                        for (int i = 0; i < boyaNumber; i++) {
                            prb[i].println(boyaMesage);
                            prb[i].flush();
                        }
                        boyaMesage = selectBoya(pozition[2], pozition[3], false);
                        for (int i = 0; i < boyaNumber; i++) {
                            prb[i].println(boyaMesage);
                            prb[i].flush();
                        }
                    }
                } else {
                    shipMove = 2;
                }
                pr.println("S" + ";" + shipMove);
                pr.flush();
            }
            if (message[1].equals("N")) {//Wprowadza nowy statek
                int[] pozition = new int[2];
                boolean shipNotCreate;
                id = message[2];
                pozition[0] = Integer.parseInt(message[3]);
                pozition[1] = Integer.parseInt(message[4]);
                shipNotCreate = addShip(id, pozition[0], pozition[1]);
                pr.println(shipNotCreate);
                pr.flush();
                if (!shipNotCreate) {
                    System.out.println("Nowy statek na pozycji : x = " + (pozition[0] + 1) + " y = " + (pozition[1] + 1) + " jego id to " + id);
                    boyaMesage = selectBoya(pozition[0] ,pozition[1],true);
                    for (int i = 0; i < boyaNumber; i++) {
                        prb[i].println(boyaMesage);
                        prb[i].flush();
                    }

                }
            }
        }

        //Wiadomość od boji

    }

    private boolean addShip(String id, int x, int y) {
        boolean shipNotCreate = false;
        for (String[] strings : shipList) {
            if (x == Integer.parseInt(strings[1]) && y == Integer.parseInt(strings[2])) {
                shipNotCreate = true;
            }
        }
        if (!shipNotCreate) {
            String[] newShip = new String[3];
            newShip[0] = id;
            newShip[1] = x + "";
            newShip[2] = y + "";
            shipList.add(newShip);
            WorldMap.bigMap[x][y].setBackground(Color.red);
        }
        return shipNotCreate;
    }

    private synchronized int moveShip(String id, int x0, int y0, int x1, int y1) {
        int shipMove;// 0 - nie poruszył się 1 - poruszył się bez kolizji 2 - poruszył się z koizją

        if (x1 < 0 || x1 > 39 || y1 < 0 || y1 > 39) {
            System.out.println("Statek o id = " + id + " próbował wyjść poza mape.");
            shipMove = 0;
        } else {
            shipMove = 1;
            for (String[] strings : shipList) {
                if (x1 == Integer.parseInt(strings[1]) && y1 == Integer.parseInt(strings[2])) {
                    shipMove = 2;
                }
            }
        }

        if (shipMove == 1) {
            //oznaczenie nowej pozycji
            WorldMap.bigMap[x1][y1].setBackground(Color.red);

            //usunięcie statku ze starej z uwzględnieniem lokalizacji boji
            if ((x0 == 2 || x0 == 7 || x0 == 12 || x0 == 17 || x0 == 22 || x0 == 27 || x0 == 32 || x0 == 37) && (y0 == 2 || y0 == 7 || y0 == 12 || y0 == 17 || y0 == 22 || y0 == 27 || y0 == 32 || y0 == 37)) {
                WorldMap.bigMap[x0][y0].setBackground(Color.WHITE);
            } else {
                WorldMap.bigMap[x0][y0].setBackground(new Color(0, 0, 250));
            }
            //zmiana pozycji statku w liście
            for (String[] strings : shipList) {
                if (strings[0].equals(id)) {
                    strings[1] = x1 + "";
                    strings[2] = y1 + "";
                    break;
                }
            }
        }

        if (shipMove == 2) {//kolizja
            //usuwnie statku z pozycji x0 y0
            if ((x0 == 2 || x0 == 7 || x0 == 12 || x0 == 17 || x0 == 22 || x0 == 27 || x0 == 32 || x0 == 37) && (y0 == 2 || y0 == 7 || y0 == 12 || y0 == 17 || y0 == 22 || y0 == 27 || y0 == 32 || y0 == 37)) {
                WorldMap.bigMap[x0][y0].setBackground(Color.WHITE);
            } else {
                WorldMap.bigMap[x0][y0].setBackground(new Color(0, 0, 250));
            }
            //usuwanie statku z pozycji x1 y0
            if ((x1 == 2 || x1 == 7 || x1 == 12 || x1 == 17 || x1 == 22 || x1 == 27 || x1 == 32 || x1 == 37) && (y1 == 2 || y1 == 7 || y1 == 12 || y1 == 17 || y1 == 22 || y1 == 27 || y1 == 32 || y1 == 37)) {
                WorldMap.bigMap[x1][y1].setBackground(Color.WHITE);
            } else {
                WorldMap.bigMap[x1][y1].setBackground(new Color(0, 0, 250));
            }
            //usutanie statku powodującego kolizje
            for (int i = 0; i < shipList.size(); i++) {
                if (shipList.get(i)[0].equals(id)) {
                    shipList.remove(i);
                    break;
                }
            }
            //usuwanie statku uderzonego
            for (int i = 0; i < shipList.size(); i++) {
                if (x1 == Integer.parseInt(shipList.get(i)[1]) && y1 == Integer.parseInt(shipList.get(i)[2])) {
                    shipList.remove(i);
                    break;
                }
            }

        }
        return shipMove;
    }

    private String selectBoya(int xShipPozytion, int yShipPozytion, boolean shipExist) throws IOException {//shipExist true - statek znajduje się na danym polu, false - statek zniknoł z danego pola
        int[] pozytion = new int[2];
        String boyaMesage;
        boyaMesage = "B";
        pr = new PrintWriter(s.getOutputStream());

        for (int i = 1; i <= 64; i++) {
            pozytion[0] = ((i - 1) * 5 + 2) % 40;
            int x = i / 8;
            pozytion[1] = (x) * 5 + 2;
            if (xShipPozytion <= pozytion[0] + 4 && xShipPozytion >= pozytion[0] - 4 && yShipPozytion <= pozytion[1] + 4 && yShipPozytion >= pozytion[1] - 4) {
                System.out.println(i);
                boyaMesage = boyaMesage + ";" + i + ";" + xShipPozytion + ";" + yShipPozytion + ";" + shipExist;

            }
        }
        return boyaMesage;
    }

}
