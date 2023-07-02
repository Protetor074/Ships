package central;

import javax.swing.*;
import java.awt.*;

public class CentralMap extends JPanel {

    public static JPanel bigMap[][];

    public CentralMap() {
        setLayout(new GridLayout(40, 40));
        addPanel();
    }

    public void addPanel() {
        bigMap = new JPanel[40][40];
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 40; j++) {
                JPanel panel = new JPanel();
                panel.setBackground(new Color(0, 0, 255));
                panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                add(panel);
                bigMap[i][j] = panel;
            }
        }

    }


}
