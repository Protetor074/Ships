/**
 * AUTOR:
 * Kamil Gondek
 *
 * SPOSÓB KOMPILACJI:
 * set path="C:\Program Files\Java\jdk-17.0.2\bin\";%path%
 * javac WordMain.java
 *
 * SPOSÓB BUDOWANIA:
 * jar --create --file Lab06_pop.jar --main-class main.WordMain --module-version 1.0 -C tojar\classes module-info.class -C tojar classes -C tojar src
 *
 * SPOSÓB URUCHOMIENIA:
 * java -p lab06_pop.jar -m main.AainSimulation
 *
 * */

package main;

import world.WorldGUI;

import java.io.IOException;

public class WordMain {
    public static void main(String[] args) throws IOException {
        new WorldGUI();

    }

}
