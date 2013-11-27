package com.github.mkrishtop.longlogcat;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class App extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1190251470735175599L;

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
		setBounds(100, 100, 900, 600);
		CellConstraints cc = new CellConstraints();

		FormLayout layout = new FormLayout("pref", "pref");
		setLayout(layout);
		JTextField text = new JTextField();
		try {
			System.out.println("logcat en: " + Runtime.getRuntime().exec("adb logcat").getInputStream().read());
		} catch (IOException e) {
			e.printStackTrace();
		}
		add(new JButton("abc"), cc.xy(1, 1));
	}

}