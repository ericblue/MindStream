package com.ericblue.mindstream.window;

import org.apache.log4j.Logger;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import com.ericblue.mindstream.preferences.PreferenceManager;

/**
 * <p>Title:		PreferencesWindow</p><br>
 * <p>Description:	Preferences Window</p><br>
 * @author		    <a href="http://eric-blue.com">Eric Blue</a><br>
 *
 * $Date: 2012-07-08 03:31:28 $ 
 * $Author: ericblue76 $
 * $Revision: 1.6 $
 *
 */


public class PreferencesWindow extends JFrame {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(PreferencesWindow.class);

	private JTabbedPane contentPane;
	private JTextField thinkgearHost;
	private JTextField broadcastPort;
	private JTextField thinkgearPort;
	private JTextField broadcastUrl;
	private Preferences prefs;
	private JTextField fileLocation;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PreferencesWindow frame = new PreferencesWindow();
					frame.setVisible(true);
				} catch (Exception e) {
                    logger.error("$Runnable.run()", e);
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PreferencesWindow() {

        logger.debug("PreferencesWindow() - loading prefs...");
		prefs = PreferenceManager.loadPreferences();

		setTitle("Preferences");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 610, 410);
		contentPane = new JTabbedPane();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		JComponent panel1 = makePanel1("Panel #1");
		contentPane.addTab("ThinkGear", null, panel1, "Does nothing");

		JComponent panel2 = makePanel2("Panel #2");
		contentPane.addTab("Broadcast", null, panel2, "Does nothing");

		setContentPane(contentPane);

	}

	protected JComponent makePanel1(String text) {
		JPanel panel = new JPanel(false);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);

		thinkgearHost = new JTextField();
		sl_panel.putConstraint(SpringLayout.SOUTH, thinkgearHost, 51, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, thinkgearHost, -302, SpringLayout.EAST, panel);
		panel.add(thinkgearHost);
		thinkgearHost.setColumns(10);

		JLabel lblThinkgearHost = new JLabel("ThinkGear Host");

		thinkgearHost.setText(prefs.get("thinkgearHost", ""));
		sl_panel.putConstraint(SpringLayout.WEST, thinkgearHost, 19, SpringLayout.EAST, lblThinkgearHost);
		sl_panel.putConstraint(SpringLayout.NORTH, lblThinkgearHost, 34, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.NORTH, thinkgearHost, -2, SpringLayout.NORTH, lblThinkgearHost);
		sl_panel.putConstraint(SpringLayout.WEST, lblThinkgearHost, 10, SpringLayout.WEST, panel);
		panel.add(lblThinkgearHost);

		thinkgearPort = new JTextField();
		thinkgearPort.setText(prefs.get("thinkgearPort", ""));
		sl_panel.putConstraint(SpringLayout.WEST, thinkgearPort, 0, SpringLayout.WEST, thinkgearHost);
		sl_panel.putConstraint(SpringLayout.EAST, thinkgearPort, -381, SpringLayout.EAST, panel);
		thinkgearPort.setColumns(10);
		panel.add(thinkgearPort);

		JLabel lblThinkgearPort = new JLabel("ThinkGear Port");
		sl_panel.putConstraint(SpringLayout.NORTH, thinkgearPort, -2, SpringLayout.NORTH, lblThinkgearPort);
		sl_panel.putConstraint(SpringLayout.NORTH, lblThinkgearPort, 27, SpringLayout.SOUTH, lblThinkgearHost);
		sl_panel.putConstraint(SpringLayout.WEST, lblThinkgearPort, 0, SpringLayout.WEST, lblThinkgearHost);
		panel.add(lblThinkgearPort);

		JButton btnCancel = new JButton("Cancel");

		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				dispose();
			}
		});
		sl_panel.putConstraint(SpringLayout.SOUTH, btnCancel, -10, SpringLayout.SOUTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, btnCancel, -104, SpringLayout.EAST, panel);

		panel.add(btnCancel);

		JButton btnSave = new JButton("Save");
		sl_panel.putConstraint(SpringLayout.SOUTH, btnSave, -10, SpringLayout.SOUTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, btnSave, -38, SpringLayout.EAST, panel);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (thinkgearHost.getText().length() == 0) {
					JOptionPane.showMessageDialog(null, "Host must be supplied!");
					return;
				}
				prefs.put("thinkgearHost", thinkgearHost.getText());

				String portErrMsg = "Port must be supplied! Range = [1 - 65535]";

				int port = 0;
				try {
					port = Integer.parseInt(thinkgearPort.getText());
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, portErrMsg);
					return;
				}

				if ((port < 1) || (port > 65535)) {
					JOptionPane.showMessageDialog(null, portErrMsg);
					return;
				}
				prefs.putInt("thinkgearPort", port);

				dispose();

			}
		});
		sl_panel.putConstraint(SpringLayout.NORTH, btnSave, 0, SpringLayout.NORTH, btnCancel);
		sl_panel.putConstraint(SpringLayout.WEST, btnSave, 4, SpringLayout.EAST, btnCancel);
		panel.add(btnSave);

		return panel;
	}

	protected JComponent makePanel2(String text) {
		final JPanel panel = new JPanel(false);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);

		broadcastPort = new JTextField();
		sl_panel.putConstraint(SpringLayout.SOUTH, broadcastPort, 51, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, broadcastPort, -381, SpringLayout.EAST, panel);
		panel.add(broadcastPort);
		broadcastPort.setColumns(10);

		JLabel lblBroadcastPort = new JLabel("Port (Socket)");

		broadcastPort.setText(prefs.get("broadcastPort", ""));
		sl_panel.putConstraint(SpringLayout.WEST, broadcastPort, 19, SpringLayout.EAST, lblBroadcastPort);
		sl_panel.putConstraint(SpringLayout.NORTH, lblBroadcastPort, 34, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.NORTH, broadcastPort, -2, SpringLayout.NORTH, lblBroadcastPort);
		sl_panel.putConstraint(SpringLayout.WEST, lblBroadcastPort, 10, SpringLayout.WEST, panel);
		panel.add(lblBroadcastPort);

		broadcastUrl = new JTextField();
		sl_panel.putConstraint(SpringLayout.WEST, broadcastUrl, 0, SpringLayout.WEST, broadcastPort);
		sl_panel.putConstraint(SpringLayout.EAST, broadcastUrl, -130, SpringLayout.EAST, panel);
		broadcastUrl.setText(prefs.get("broadcastUrl", ""));
		broadcastUrl.setColumns(10);
		panel.add(broadcastUrl);

		JLabel lblBroadcastUrl = new JLabel("URL (HTTP)");
		sl_panel.putConstraint(SpringLayout.NORTH, broadcastUrl, -2, SpringLayout.NORTH, lblBroadcastUrl);
		sl_panel.putConstraint(SpringLayout.NORTH, lblBroadcastUrl, 27, SpringLayout.SOUTH, lblBroadcastPort);
		sl_panel.putConstraint(SpringLayout.WEST, lblBroadcastUrl, 0, SpringLayout.WEST, lblBroadcastPort);
		panel.add(lblBroadcastUrl);

		JLabel lblCsvFilesave = new JLabel("CSV File (Save)");
		sl_panel.putConstraint(SpringLayout.WEST, lblCsvFilesave, 0, SpringLayout.WEST, lblBroadcastPort);
		panel.add(lblCsvFilesave);

		fileLocation = new JTextField();
		sl_panel.putConstraint(SpringLayout.NORTH, fileLocation, 27, SpringLayout.SOUTH, broadcastUrl);
		sl_panel.putConstraint(SpringLayout.NORTH, lblCsvFilesave, 2, SpringLayout.NORTH, fileLocation);
		sl_panel.putConstraint(SpringLayout.WEST, fileLocation, 0, SpringLayout.WEST, broadcastPort);
		sl_panel.putConstraint(SpringLayout.EAST, fileLocation, 0, SpringLayout.EAST, broadcastUrl);
		fileLocation.setText(prefs.get("fileLocation", ""));
		fileLocation.setColumns(10);
		panel.add(fileLocation);

		JButton btnFileSelect = new JButton("Choose");
		sl_panel.putConstraint(SpringLayout.NORTH, btnFileSelect, -2, SpringLayout.NORTH, lblCsvFilesave);
		sl_panel.putConstraint(SpringLayout.WEST, btnFileSelect, 6, SpringLayout.EAST, fileLocation);
		sl_panel.putConstraint(SpringLayout.SOUTH, btnFileSelect, 139, SpringLayout.NORTH, panel);

		panel.add(btnFileSelect);

		btnFileSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser fc = new JFileChooser();
				fc.showOpenDialog(panel);
				File selFile = fc.getSelectedFile();
				if (selFile != null) {
					fileLocation.setText((selFile.getAbsolutePath()));
				}

			}
		});

		JButton btnCancel = new JButton("Cancel");

		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO reload prefs and re-initialize properly
				dispose();
			}
		});
		sl_panel.putConstraint(SpringLayout.SOUTH, btnCancel, -10, SpringLayout.SOUTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, btnCancel, -104, SpringLayout.EAST, panel);

		panel.add(btnCancel);

		JButton btnSave = new JButton("Save");
		sl_panel.putConstraint(SpringLayout.SOUTH, btnSave, -10, SpringLayout.SOUTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, btnSave, -38, SpringLayout.EAST, panel);
		sl_panel.putConstraint(SpringLayout.EAST, btnFileSelect, 9, SpringLayout.EAST, btnSave);

		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String portErrMsg = "Port must be supplied! Range = [1 - 65535]";

				int port = 0;
				try {
					port = Integer.parseInt(broadcastPort.getText());
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, portErrMsg);
					return;
				}

				if ((port < 1) || (port > 65535)) {
					JOptionPane.showMessageDialog(null, portErrMsg);
					return;
				}
				prefs.putInt("broadcastPort", port);

				prefs.put("broadcastUrl", broadcastUrl.getText());

				prefs.put("fileLocation", fileLocation.getText());

				dispose();

			}
		});
		sl_panel.putConstraint(SpringLayout.NORTH, btnSave, 0, SpringLayout.NORTH, btnCancel);
		sl_panel.putConstraint(SpringLayout.WEST, btnSave, 4, SpringLayout.EAST, btnCancel);
		panel.add(btnSave);

		return panel;
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = PreferencesWindow.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
            logger.error("createImageIcon(String) - Couldn't find file: " + path, null);
			return null;
		}
	}
}
