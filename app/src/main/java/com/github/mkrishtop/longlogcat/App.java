package com.github.mkrishtop.longlogcat;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.json.JSONObject;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class App extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1190251470735175599L;

	JTextArea textArea;
	JList list;
	DefaultListModel listModel;
	
	Map<String, String> logs = new HashMap<String, String>();
	
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
		setBounds(100, 100, 970, 470);
		CellConstraints cc = new CellConstraints();

		FormLayout layout = new FormLayout("150dlu, 300dlu", "fill:250dlu, fill:15dlu");
		setLayout(layout);

		listModel = new DefaultListModel();
		list = new JList(listModel);
		list.setLayoutOrientation(JList.VERTICAL);
		JScrollPane scrollPane1 = new JScrollPane(list);
		list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
				int selectedPosition = list.getSelectedIndex();
				if (selectedPosition >= 0) {
					textArea.setText(logs.get(listModel.getElementAt(selectedPosition).toString()));
				}
            }
        });
		
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		JScrollPane scrollPane2 = new JScrollPane(textArea);
		
		JPanel buttons = new JPanel(new FormLayout("50dlu, 50dlu", "fill:15dlu"));
		
		JButton clearButton = new JButton("Clear");
		JButton copyButton = new JButton("Copy");
		
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				listModel.clear();
				logs.clear();
				textArea.setText("");
			}
		});
		
		copyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				StringSelection selection = new StringSelection(textArea.getText());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			    clipboard.setContents(selection, selection);
			}
		});
		
		buttons.add(clearButton, cc.xy(1, 1));
		buttons.add(copyButton, cc.xy(2, 1));
		
		add(scrollPane1, cc.xy(1, 1));
		add(scrollPane2, cc.xy(2, 1));
		add(buttons, cc.xyw(1, 2, 2));
		
		async();
	}

	private void async() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InputStream is = Runtime.getRuntime().exec("adb logcat").getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader buff = new BufferedReader(isr);

					SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
					
					String line;
					while ((line = buff.readLine()) != null) {

						if (line.startsWith("V/LongLogger")) {
							int index = line.indexOf("): ");
							if (index < 0) continue;
							line = line.substring(index + "): ".length());
							JSONObject jObject = new JSONObject(line);
							String tag = jObject.get("tag").toString();
							try {
							tag = sdf.format(new Date(Long.parseLong((tag.split(" ")[1])))) + " -> " + tag;
							} catch (Exception e) {}
							String prev = logs.get(tag);
							if (prev != null) {
								logs.put(tag, prev + jObject.get("message"));
							} else {
								logs.put(tag, jObject.get("message").toString());
							}
							
							if (jObject.has("end") && Boolean.parseBoolean(jObject.get("end").toString()))
								listModel.addElement(tag);
							list.invalidate();
						}
						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

}