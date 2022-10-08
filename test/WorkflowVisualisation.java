//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package test;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class WorkflowVisualisation {
    private JFrame frame;

    public static void lunch() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    WorkflowVisualisation window = new WorkflowVisualisation();
                    window.frame.setVisible(true);
                } catch (Exception var2) {
                    var2.printStackTrace();
                }

            }
        });
    }

    public WorkflowVisualisation() {
        this.initialize();
    }

    private void initialize() {
        this.frame = new JFrame();
        this.frame.setBounds(100, 100, 450, 300);
        this.frame.setDefaultCloseOperation(3);
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", 1, 14));
        textArea.setTabSize(12);
        textArea.setBackground(Color.WHITE);
        this.frame.getContentPane().add(textArea, "Center");
        FileReader flux = null;
        BufferedReader input = null;

        try {
            flux = new FileReader("E://hamdi/these/workflow.work");
            input = new BufferedReader(flux);

            String str;
            while((str = input.readLine()) != null) {
                textArea.append(str);
                textArea.append("\n");
            }
        } catch (IOException var6) {
            System.out.println("Impossible d'ouvrir le fichier : " + var6.toString());
        }

    }
}
