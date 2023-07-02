package ship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ShipClient {
    public static int sendMessegeMove(String id, int x0, int y0, int x1, int y1) throws IOException {
        int shipMove;

        Socket s = new Socket("localhost", 2000);

        PrintWriter pr = new PrintWriter(s.getOutputStream());
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);

        pr.println("S;M;" + id + ";" + x0 + ";" + y0 + ";" + x1 + ";" + y1 );
        pr.flush();
        boolean repeat = true;
        String[] messeage = new String[2];
        while (repeat){
            String str = bf.readLine();
            messeage = str.split(";");
            if(messeage[0].equals("S")){
                repeat = false;
            }
        }
        shipMove = Integer.parseInt(messeage[1]);
        s.close();
        return shipMove;

    }
    public static int[][] sendMessegeScan(String id) throws IOException {
        boolean shipExist;


        Socket s = new Socket("localhost", 2000);

        PrintWriter pr = new PrintWriter(s.getOutputStream());
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        int[][] shipsLocation;

        pr.println("S;S;" + id);
        pr.flush();
        String str = bf.readLine();
        System.out.println(str);
        String[] mesege = str.split(";");
        s.close();
        shipExist = Boolean.parseBoolean(mesege[1]);
        if(shipExist){
            int shipNumber = mesege.length/2 - 1;
            shipsLocation = new int[shipNumber+1][2];
            int messagePozition=2;
            for(int i=0;i<shipNumber;i++){
                shipsLocation[i+1][0] = Integer.parseInt(mesege[messagePozition]);
                shipsLocation[i+1][1] = Integer.parseInt(mesege[messagePozition+1]);
                messagePozition+=2;
            }
            shipsLocation[0][0]=1; // statek istnieje
        }else{
            shipsLocation = new int[1][1];
            shipsLocation[0][0]=0; // statek nie istnieje zostal uderzony przez inny statek
        }
        return shipsLocation;
    }
    public static boolean sendMessageNewShip(String id , int x ,int y) throws IOException {
        boolean shipNotCreate;

        Socket s = new Socket("localhost", 2000);

        PrintWriter pr = new PrintWriter(s.getOutputStream());
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);

        pr.println("S;N;" + id + ";" + x + ";" + y );
        pr.flush();
        String str = bf.readLine();
        s.close();
        shipNotCreate = Boolean.parseBoolean(str);
        return shipNotCreate;
    }
}
