package ship;

import javax.swing.*;
import java.awt.*;

public class ShipMap extends JPanel {

    public static JPanel[][] bigMap;

    public ShipMap() {
        setLayout(new GridLayout(40, 40));
        addPanel();
    }

    public void addPanel() {
        bigMap = new JPanel[40][40];
        int x=2;
        for (int row = 0; row < 40; row++) {
            int y=2;
            for (int column = 0; column < 40; column++) {
                JPanel panel = new JPanel();
                if(column==y&&row==x){
                    panel.setBackground(Color.WHITE);
                    y=y+5;
                }else{
                    panel.setBackground(new Color(0,0,250));
                }
                panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                add(panel);
                bigMap[row][column] = panel;
            }
            if(row==x){
                x=x+5;
            }
        }
    }
}
