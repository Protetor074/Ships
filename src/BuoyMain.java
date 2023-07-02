/**
 * AUTOR:
 * Kamil Gondek
 *
 * SPOSÓB KOMPILACJI:
 * set path="C:\Program Files\Java\jdk-17.0.2\bin\";%path%
 * javac BuoyMain.java
 *
 * SPOSÓB BUDOWANIA:
 * jar --create --file Lab06_pop.jar --main-class main.BuoyMain --module-version 1.0 -C tojar\classes module-info.class -C tojar classes -C tojar src
 *
 * SPOSÓB URUCHOMIENIA:
 * run64.bat
 *
 * */




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BuoyMain {

    static int[] pozytion = new int[2];
    static Socket s;

    static int[][] boyaArea = new int[25][3];
    static int[][] shipPrint = new int[25][3];

    public static void main(String[] args) throws IOException {
        int boya = Integer.parseInt(args[0]);
        startBoya(boya);
        calculateBoyRange(pozytion[1], pozytion[0]);
        activeBoya(boya);


    }

    static void startBoya(int boyaNumber) {//Połączenie ze światem
        pozytion[0] = ((boyaNumber - 1) * 5 + 2) % 40;
        int x = boyaNumber / 8;
        pozytion[1] = (x) * 5 + 2;


    }

    private static void activeBoya(int boyaNumber) throws IOException {
        s = new Socket("localhost", 2000);
        System.out.println("Połączono - Boya numer:" + boyaNumber);


        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);

        String boyaNR = boyaNumber + "";

        while (true) {
            String str = bf.readLine();
            //System.out.println(str);
            String[] message = str.split(";");
            if (message[0].equals("B")) {
                int boyaMessage = (message.length - 1) / 4;
                for (int i = 0; i < boyaMessage; i++)
                    if (message[i * 4 + 1].equals(boyaNR)) {
                        if (message[i * 4 + 4].equals("true")) {
                            System.out.println("Namalować statek na pozycji x = " + message[i * 4 + 3] + " y = " + message[i * 4 + 2]);
                            calculateSeaLevel(Integer.parseInt(message[i * 4 + 3]), Integer.parseInt(message[i * 4 + 2]));
                            changeSeaLevelUp();
                        } else {
                            System.out.println("Usunąć statek z pozycji x = " + message[i * 4 + 3] + " y = " + message[i * 4 + 2]);
                            calculateSeaLevel(Integer.parseInt(message[i * 4 + 3]), Integer.parseInt(message[i * 4 + 2]));
                            changeSeaLevelDown();
                        }
                        //Wyświetlanie poziomu w oku boi
                        for (int i0 = 0; i0 < 5; i0++) {
                            for (int j = 0; j < 5; j++) {
                                System.out.print(boyaArea[i0 * 5 + j][2]);
                            }
                            System.out.println();
                        }
                        System.out.println();
                        //wysłać do centrali
                        sendMesageToCentral();
                    }
            }
        }
    }

    private static void calculateSeaLevel(int shipX, int shipY) {
        //poziom 4
        printPositionAndLevelShip(12, 4, shipX, shipY);
        //poziom 3
        printPositionAndLevelShip(17, 3, shipX, shipY + 1);
        printPositionAndLevelShip(7, 3, shipX, shipY - 1);
        printPositionAndLevelShip(11, 3, shipX - 1, shipY);
        printPositionAndLevelShip(13, 3, shipX + 1, shipY);
        //poziom 2
        printPositionAndLevelShip(2, 2, shipX, shipY - 2);
        printPositionAndLevelShip(6, 2, shipX - 1, shipY - 1);
        printPositionAndLevelShip(10, 2, shipX - 2, shipY);
        printPositionAndLevelShip(16, 2, shipX - 1, shipY + 1);
        printPositionAndLevelShip(22, 2, shipX, shipY + 2);
        printPositionAndLevelShip(18, 2, shipX + 1, shipY + 1);
        printPositionAndLevelShip(14, 2, shipX + 2, shipY);
        printPositionAndLevelShip(8, 2, shipX + 1, shipY - 1);
        //poziom 1
        printPositionAndLevelShip(1, 1, shipX - 1, shipY - 2);
        printPositionAndLevelShip(3, 1, shipX + 1, shipY - 2);
        printPositionAndLevelShip(5, 1, shipX - 2, shipY - 1);
        printPositionAndLevelShip(15, 1, shipX - 2, shipY + 1);
        printPositionAndLevelShip(21, 1, shipX - 1, shipY + 2);
        printPositionAndLevelShip(23, 1, shipX + 1, shipY + 2);
        printPositionAndLevelShip(19, 1, shipX + 2, shipY + 1);
        printPositionAndLevelShip(9, 1, shipX + 2, shipY - 1);
        //poziom 0
        printPositionAndLevelShip(0, 0, shipX - 2, shipY - 2);
        printPositionAndLevelShip(4, 0, shipX + 2, shipY - 2);
        printPositionAndLevelShip(20, 0, shipX - 2, shipY + 2);
        printPositionAndLevelShip(24, 0, shipX + 2, shipY + 2);

        //Wyświetlanie
    }

    private static void printPositionAndLevelShip(int i, int level, int shipX, int shipY) {
        shipPrint[i][0] = shipX;
        shipPrint[i][1] = shipY;
        shipPrint[i][2] = level;
    }

    private static void calculateBoyRange(int xCenterPosition, int yCenterPosition) {


        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                boyaArea[i * 5 + j][0] = xCenterPosition - 2 + j;
                boyaArea[i * 5 + j][1] = yCenterPosition - 2 + i;
            }
        }

        //Wyświetlanie
    }

    private static void changeSeaLevelUp() {
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 25; j++) {
                if (boyaArea[i][0] == shipPrint[j][0] && boyaArea[i][1] == shipPrint[j][1]) {
                    boyaArea[i][2] = boyaArea[i][2] + shipPrint[j][2];
                }
            }
        }
    }

    private static void changeSeaLevelDown() {
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 25; j++) {
                if (boyaArea[i][0] == shipPrint[j][0] && boyaArea[i][1] == shipPrint[j][1]) {
                    boyaArea[i][2] = boyaArea[i][2] - shipPrint[j][2];
                }
            }
        }
    }

    private static void sendMesageToCentral() throws IOException {
        String mesage = "";
        for(int i=0;i<25;i++){
            mesage = mesage + boyaArea[i][0] +";"+ boyaArea[i][1] +";"+ boyaArea[i][2] +";";
        }
        Socket s = new Socket("localhost", 4000);
        PrintWriter pr = new PrintWriter(s.getOutputStream());
        pr.println(mesage);
        pr.flush();
        s.close();
    }


}
