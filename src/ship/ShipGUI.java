package ship;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

public class ShipGUI extends JFrame {

    JPanel mainPanel;

    JButton scan;

    JButton[] move;

    private JLabel[] announcement;
    private int announcementNumber;
    private final int maxAnnouncement = 10;

    private static int xpozition;
    private static int ypozition;
    private static String id;

    public ShipGUI() throws IOException {
        super("Statek");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(null);
        createGUI();
        createShip();
        setButton();
    }

    private void createGUI() {
        setSize(1500, 920);
        mainPanel = new JPanel();
        setContentPane(mainPanel);
        mainPanel.setLayout(null);
        setResizable(false);

        ShipMap shipMap = new ShipMap();
        mainPanel.add(shipMap);
        shipMap.setBounds(600, 0, 880, 880);
        shipMap.setBorder(BorderFactory.createTitledBorder("Mapa"));

        JPanel configPanel = new JPanel();
        mainPanel.add(configPanel);
        configPanel.setBorder(BorderFactory.createTitledBorder("Sterowanie"));
        configPanel.setBounds(0, 0, 580, 880);
        configPanel.setLayout(null);

        JPanel message = new JPanel();
        configPanel.add(message);
        message.setBorder(BorderFactory.createTitledBorder("Komunikaty"));
        message.setBounds(20, 20, 540, configPanel.getHeight() - configPanel.getHeight() * 6 / 8 - 40);
        message.setLayout(null);

        announcement = new JLabel[maxAnnouncement];

        for(int i=0;i<maxAnnouncement;i++){
            announcement[i] = new JLabel();
            announcement[i].setText("");
            //announcement[i].setSize(10,10);
            announcement[i].setBounds(10,15+15*i,500,15);
            message.add(announcement[i]);
        }

        scan = new JButton("SCAN");
        Font f = scan.getFont();
        scan.setFont(new Font(f.getFontName(), f.getStyle(), 64));

        scan.setBounds(configPanel.getWidth() / 2 - configPanel.getWidth() / 4, configPanel.getHeight() - configPanel.getHeight() * 2 / 8, configPanel.getWidth() / 2, configPanel.getHeight() / 8);
        configPanel.add(scan);

        char upC = '\u2BC5';
        String up = upC + "";
        char downC = '\u2BC6';
        String down = downC + "";
        char leftC = '\u2BC7';
        String left = leftC + "";
        char rightC = '\u2BC8';
        String right = rightC + "";

        move = new JButton[]{
                new JButton(up),
                new JButton(down),
                new JButton(left),
                new JButton(right)
        };

        move[0].setBounds(configPanel.getWidth() / 2 - configPanel.getWidth() / 16, configPanel.getHeight() - configPanel.getHeight() * 6 / 8, configPanel.getWidth() / 8, configPanel.getWidth() / 8);
        move[1].setBounds(configPanel.getWidth() / 2 - configPanel.getWidth() / 16, configPanel.getHeight() - configPanel.getHeight() * 4 / 8, configPanel.getWidth() / 8, configPanel.getWidth() / 8);
        move[2].setBounds(configPanel.getWidth() / 2 - configPanel.getWidth() / 16 - (configPanel.getHeight() * 6 / 8 - configPanel.getHeight() * 4 / 8) / 2, configPanel.getHeight() - configPanel.getHeight() * 5 / 8, configPanel.getWidth() / 8, configPanel.getWidth() / 8);
        move[3].setBounds(configPanel.getWidth() / 2 - configPanel.getWidth() / 16 + (configPanel.getHeight() * 6 / 8 - configPanel.getHeight() * 4 / 8) / 2, configPanel.getHeight() - configPanel.getHeight() * 5 / 8, configPanel.getWidth() / 8, configPanel.getWidth() / 8);

        Font fm = move[0].getFont();
        for (JButton jButton : move) {
            jButton.setFont(new Font(fm.getFontName(), fm.getStyle(), 32));
            configPanel.add(jButton);
        }

    }

    private void setButton() {
        //W górę
        move[0].addActionListener(e -> {
            try {
                setMove(xpozition - 1, ypozition);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        //W duł
        move[1].addActionListener(e -> {
            try {
                setMove(xpozition + 1, ypozition);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        //W lewo
        move[2].addActionListener(e -> {
            try {
                setMove(xpozition, ypozition - 1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        //W prawo
        move[3].addActionListener(e -> {
            try {
                setMove(xpozition, ypozition + 1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        scan.addActionListener(e -> {
            try {
                scan();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });

    }


    public void createShip() throws IOException {
        String uniqueID = UUID.randomUUID().toString();
        Random generator = new Random();
        boolean shipNotCreate = true;
        int x;
        int y;

        while (shipNotCreate) {
            x = generator.nextInt() % 40;
            if (x < 0) {
                x = -x;
            }
            y = generator.nextInt() % 40;
            if (y < 0) {
                y = -y;
            }
            shipNotCreate = ShipClient.sendMessageNewShip(uniqueID, x, y);
            if (!shipNotCreate) {
                System.out.println("Statek stworzony na współżędnych x = " + (x + 1) + " y = " + (y + 1));
                newAnnouncement("Statek stworzony na współżędnych x = " + (x + 1) + " y = " + (y + 1));
                ShipMap.bigMap[x][y].setBackground(Color.red);
                xpozition = x;
                ypozition = y;
                id = uniqueID;
            }
        }

    }

    public void scan() throws IOException {
        int[][] shipLocation;
        shipLocation = ShipClient.sendMessegeScan(id);
        newAnnouncement("Skan");
        if (shipLocation[0][0] == 0) {
            JOptionPane.showMessageDialog(null, "Statek został uderzony przez inny statek przezco zatoną!");
            System.out.println("Statek został uderzony przez inny statek przezco zatoną!");
            newAnnouncement("Statek został uderzony przez inny statek przezco zatoną!");
            ShipMap.bigMap[xpozition][ypozition].setBackground(Color.MAGENTA);
            turnOffButton();
        } else {
            resetMap();
            if (shipLocation.length != 2) {
                for (int i = 1; i < shipLocation.length; i++) {
                    if (xpozition != shipLocation[i][0] && ypozition != shipLocation[i][1]) {
                        ShipMap.bigMap[shipLocation[i][0]][shipLocation[i][1]].setBackground(Color.black);
                    }
                }
            }
            ShipMap.bigMap[xpozition][ypozition].setBackground(Color.red);
        }
    }

    public void setMove(int newX, int newY) throws IOException {
        int shipMove;
        shipMove = ShipClient.sendMessegeMove(id, xpozition, ypozition, newX, newY);
        if (shipMove == 0) {//nie poruszył się
            System.out.println("Błąd ruchu próba opuszczenia mapy.");
            JOptionPane.showMessageDialog(null, "Błąd ruchu próba opuszczenia mapy.");
            newAnnouncement("Błąd ruchu próba opuszczenia mapy.");
        }
        if (shipMove == 1) {
            System.out.println("Wykonano ruch na pole x = " + newX + " y = " + newY);
            newAnnouncement("Wykonano ruch na pole x = " + newX + " y = " + newY);

            //oznaczenie nowej pozycji statku
            ShipMap.bigMap[newX][newY].setBackground(Color.red);
            if ((xpozition == 2 || xpozition == 7 || xpozition == 12 || xpozition == 17 || xpozition == 22 || xpozition == 27 || xpozition == 32 || xpozition == 37) && (ypozition == 2 || ypozition == 7 || ypozition == 12 || ypozition == 17 || ypozition == 22 || ypozition == 27 || ypozition == 32 || ypozition == 37)) {
                ShipMap.bigMap[xpozition][ypozition].setBackground(Color.WHITE);
            } else {
                ShipMap.bigMap[xpozition][ypozition].setBackground(new Color(0, 0, 250));
            }
            xpozition = newX;
            ypozition = newY;
        }
        if (shipMove == 2) {
            System.out.println("Kolizja");
            newAnnouncement("Kolizja!!! Statek zatonoł.");
            JOptionPane.showMessageDialog(null, "Kolizja!!! Statek zatonoł.");
            ShipMap.bigMap[xpozition][ypozition].setBackground(Color.MAGENTA);
            turnOffButton();
        }
    }

    private void turnOffButton() {
        move[0].setEnabled(false);
        move[1].setEnabled(false);
        move[2].setEnabled(false);
        move[3].setEnabled(false);
        scan.setEnabled(false);
    }

    private void resetMap() {
        int x = 2;
        for (int row = 0; row < 40; row++) {
            int y = 2;
            for (int column = 0; column < 40; column++) {
                if (column == y && row == x) {
                    ShipMap.bigMap[row][column].setBackground(Color.WHITE);
                    y = y + 5;
                } else {
                    ShipMap.bigMap[row][column].setBackground(new Color(0, 0, 250));
                }
            }
            if (row == x) {
                x = x + 5;
            }
        }
    }

    private void newAnnouncement(String text){
        if(announcementNumber<maxAnnouncement){
            announcement[announcementNumber].setText(text);
            announcementNumber++;
        }else{
            for(int i=0;i<maxAnnouncement-1;i++){
                announcement[i].setText(announcement[i+1].getText());
            }
            announcement[maxAnnouncement-1].setText(text);
        }
    }

}
