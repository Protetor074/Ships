/**
 * AUTOR:
 * Kamil Gondek
 *
 * SPOSÓB KOMPILACJI:
 * set path="C:\Program Files\Java\jdk-17.0.2\bin\";%path%
 * javac CentralaMain.java
 *
 * SPOSÓB BUDOWANIA:
 * jar --create --file Lab05_pop.jar --main-class main.CentralaMain --module-version 1.0 -C tojar\classes module-info.class -C tojar classes -C tojar src
 *
 * SPOSÓB URUCHOMIENIA:
 * java -p lab06_pop.jar -m main.AainSimulation
 *
 * */

package main;

import central.CentralGUI;

import java.io.IOException;

public class CentralaMain {
    public static void main(String[] args) throws IOException {
        new CentralGUI();
    }
}
