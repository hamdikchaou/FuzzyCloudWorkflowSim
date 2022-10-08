//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Clavier {
    public Clavier() {
    }

    public static String lireString() {
        String ligne_lue = null;

        try {
            InputStreamReader lecteur = new InputStreamReader(System.in);
            BufferedReader entree = new BufferedReader(lecteur);
            ligne_lue = entree.readLine();
        } catch (IOException var3) {
            System.exit(0);
        }

        return ligne_lue;
    }

    public static float lireFloat() {
        float x = 0.0F;

        try {
            String ligne_lue = lireString();
            x = Float.parseFloat(ligne_lue);
        } catch (NumberFormatException var2) {
            System.out.println("***Erreur de donn�e***");
            System.exit(0);
        }

        return x;
    }

    public static double lireDouble() {
        double x = 0.0;

        try {
            String ligne_lue = lireString();
            x = Double.parseDouble(ligne_lue);
        } catch (NumberFormatException var3) {
            System.out.println("***Erreur de donn�e ***");
            System.exit(0);
        }

        return x;
    }

    public static int lireInt() {
        int n = 0;

        try {
            String ligne_lue = lireString();
            n = Integer.parseInt(ligne_lue);
        } catch (NumberFormatException var2) {
            System.out.println("***Erreur de donn�e ***");
            System.exit(0);
        }

        return n;
    }
}
