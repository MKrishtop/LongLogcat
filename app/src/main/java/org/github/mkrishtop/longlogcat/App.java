package org.github.mkrishtop.longlogcat;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class App extends JFrame {

	public static void main(String[] args) {
                EventQueue.invokeLater(new Runnable() {
                        public void run() {
                                try {
                                        App app = new App();
                                        app.init();
                                        app.setVisible(true);
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                });
    }

    void init() {
        setBounds(100, 100, 200, 200);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

}