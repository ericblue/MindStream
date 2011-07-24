
package com.ericblue.mindstream.systemtray;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.ericblue.mindstream.client.ThinkGearSocketClient;
import com.ericblue.mindstream.preferences.PreferenceManager;
import com.ericblue.mindstream.window.DebugWindow;
import com.ericblue.mindstream.window.PreferencesWindow;

/**
 * <p>Title:		MindStreamSystemTray</p><br>
 * <p>Description:	System tray app for streaming data from the Neurosky MindStream/MindSet</p><br>
 * @author		    <a href="http://eric-blue.com">Eric Blue</a><br>
 *
 * $Date: 2011-07-24 17:54:27 $ 
 * $Author: ericblue76 $
 * $Revision: 1.5 $
 *
 */


public class MindStreamSystemTray {
	
	/**
     * System tray launcher 
     * 
     * @param args
     * @return void
     */
	
	public static void main(String[] args) {
		// TODO Set look and feel
		// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

		/* Turn off metal's use of bold fonts */
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initializeGUI();
			}
		});
	}
	

	/**
     * Initializes preferences on first time launch 
     * 
     * @param none
     * @return void
     */
	private static void initializePreferences() {
		
		Preferences prefs = PreferenceManager.loadPreferences();
		
		if (prefs.get("thinkgearHost", null) == null) {
			System.out.println("Setting default ThinkGear Host");
			prefs.put("thinkgearHost", ThinkGearSocketClient.DEFAULT_HOST);
		}
		
		if (prefs.getInt("thinkgearPort", 0) == 0) {
			System.out.println("Setting default ThinkGear Host");
			prefs.putInt("thinkgearPort", ThinkGearSocketClient.DEFAULT_PORT);
		}
		
		if (prefs.get("fileLocation", null) == null) {
			System.out.println("Setting default CSV file location");
			String file = System.getProperty("user.home") + System.getProperty("file.separator");
			file += "mindstream.csv";
			prefs.put("fileLocation", file);
		}
		
	}
	
	/**
     * Initialize GUI 
     * 
     * @param none
     * @return void
     */
	
	private static void initializeGUI() {
		
		// TODO Cleanup all System.out/.err with log4j calls
		
		// Check the SystemTray support
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
			
		initializePreferences();

		// TODO Load default preferences if they haven't been initialized
		String host = PreferenceManager.loadPreferences().get("thinkgearHost", "");
		int port = PreferenceManager.loadPreferences().getInt("thinkgearPort", 0);

		final ThinkGearSocketClient client = new ThinkGearSocketClient(host, port);

		final PopupMenu popup = new PopupMenu();
		final TrayIcon trayIcon = new TrayIcon(createImage("images/logo.jpg", "tray icon"));
		trayIcon.setImageAutoSize(true);
		trayIcon.setToolTip("MindStream");
		final SystemTray tray = SystemTray.getSystemTray();

		// Create a popup menu components
		MenuItem aboutItem = new MenuItem("About");
		MenuItem preferencesItem = new MenuItem("Preferences");
		final MenuItem viewDebug = new MenuItem("View EEG data (JSON)");
		viewDebug.setEnabled(false);
		final CheckboxMenuItem cbConnect = new CheckboxMenuItem("Connect to ThinkGear Socket");
		final Menu streamMenu = new Menu("MindStream");
		streamMenu.setEnabled(false);
		MenuItem broadcastSocketItem = new MenuItem("Broadcast (Socket)");
		MenuItem broadcastHttpItem = new MenuItem("Broadcast (HTTP)");
		MenuItem saveFileItem = new MenuItem("Save (File)");

		MenuItem exitItem = new MenuItem("Exit");

		// Add components to popup menu
		popup.add(aboutItem);
		popup.add(preferencesItem);
		popup.addSeparator();

		popup.add(viewDebug);
		popup.add(cbConnect);
		popup.add(streamMenu);
		streamMenu.add(broadcastSocketItem);
		streamMenu.add(broadcastHttpItem);
		streamMenu.add(saveFileItem);
		popup.addSeparator();

		popup.add(exitItem);

		final DebugWindow debugWindow = new DebugWindow();
		final PreferencesWindow preferencesWindow = new PreferencesWindow();

		trayIcon.setPopupMenu(popup);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
			return;
		}

		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "This dialog box is run from the About menu item");
			}
		});

		preferencesItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				preferencesWindow.setVisible(true);
				preferencesWindow.getContentPane().requestFocus();

			}
		});

		viewDebug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				debugWindow.setVisible(true);
				SwingWorker worker = new SwingWorker<Void, Void>() {
					public Void doInBackground() {

						while (client.isDataAvailable()) {

							debugWindow.getTextArea().append(client.getData() + '\n');
							debugWindow.getTextArea().setCaretPosition(debugWindow.getTextArea().getText().length());

						}

						return null;

					}
				};

				worker.execute();

			}
		});

		cbConnect.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				int cbState = e.getStateChange();
				if (cbState == ItemEvent.SELECTED) {

					String host = PreferenceManager.loadPreferences().get("thinkgearHost", "");
					int port = PreferenceManager.loadPreferences().getInt("thinkgearPort", 0);

					if (!client.isConnected()) {
						try {
							client.setHost(host);
							client.setPort(port);

							client.connect();
							viewDebug.setEnabled(true);
							streamMenu.setEnabled(true);
						} catch (IOException e1) {
							cbConnect.setState(false);
							viewDebug.setEnabled(false);
							streamMenu.setEnabled(false);
							trayIcon.displayMessage("Connection Error", e1.getMessage(), TrayIcon.MessageType.ERROR);
						}

					}

				} else {
					try {
						client.close();
						viewDebug.setEnabled(false);
						streamMenu.setEnabled(false);
					} catch (IOException e1) {
						trayIcon.displayMessage("Close Error", e1.getMessage(), TrayIcon.MessageType.ERROR);
					}
				}
			}
		});

		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MenuItem item = (MenuItem) e.getSource();
				// TrayIcon.MessageType type = null;
				System.out.println(item.getLabel());
				if ("Broadcast (Socket)".equals(item.getLabel())) {
					String message = "Broadcasting socket on port (xyz). Uncheck Connect to ThinkGear Socket to stop.";
					trayIcon.displayMessage("INFO", message, TrayIcon.MessageType.INFO);

				} else if ("Broadcast (HTTP)".equals(item.getLabel())) {
					String message = "Broadcasting HTTP to url (xyz). Uncheck Connect to ThinkGear Socket to stop.";
					trayIcon.displayMessage("INFO", message, TrayIcon.MessageType.INFO);

				} else if ("Save (File)".equals(item.getLabel())) {

					String message = "Saving file (xyz). Uncheck Connect to ThinkGear Socket to stop.";
					trayIcon.displayMessage("INFO", message, TrayIcon.MessageType.INFO);

					SwingWorker worker = new SwingWorker<Void, Void>() {
						public Void doInBackground() {

							String csvFile = PreferenceManager.loadPreferences().get("fileLocation", "");
							if (csvFile == null) {
								trayIcon.displayMessage("ERROR", "File location must be set in Preferences!",
										TrayIcon.MessageType.ERROR);
							}

							FileWriter writer = null;

							try {
								writer = new FileWriter(csvFile);
							} catch (IOException e1) {
								trayIcon.displayMessage("ERROR", "Error opening file for writing!",
										TrayIcon.MessageType.ERROR);
								e1.printStackTrace();
							}

							// HEADER
							try {
								writer.append("TIMESTAMP,POOR_SIGNAL_LEVEL,ATTENTION,MEDITATION");
								writer.append("DELTA,THETA,LOW_ALPHA,HIGH_ALPHA,LOW_BETA,HIGH_BETA");
								writer.append("LOW_GAMMA,HIGH_GAMA\n");
							} catch (IOException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}

							SimpleDateFormat fmt = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

							while (client.isDataAvailable()) {
								System.out.println("Writing...");

								System.out.println(client.getData() + '\n');
								try {

									JSONObject json = new JSONObject(client.getData());

									String timeStamp = fmt.format(new Date());
									writer.append(timeStamp + ',');
									writer.append(json.getString("poorSignalLevel") + ',');
									JSONObject esense = json.getJSONObject("eSense");
									if (esense != null) {
										writer.append(esense.getString("attention") + ',');
										writer.append(esense.getString("meditation") + ',');
									}

									JSONObject eegPower = json.getJSONObject("eegPower");
									if (eegPower != null) {
										writer.append(eegPower.getString("delta") + ',');
										writer.append(eegPower.getString("theta") + ',');
										writer.append(eegPower.getString("lowAlpha") + ',');
										writer.append(eegPower.getString("highAlpha") + ',');
										writer.append(eegPower.getString("lowBeta") + ',');
										writer.append(eegPower.getString("highBeta") + ',');
										writer.append(eegPower.getString("lowGamma") + ',');
										writer.append(eegPower.getString("highGamma") + '\n');
									}

									writer.flush();

								} catch (JSONException e1) {
									e1.printStackTrace();
								} catch (IOException e2) {
									// TODO Auto-generated catch block
									e2.printStackTrace();
								}

							}

							try {
								System.out.println("Closing file...");
								writer.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							return null;

						}
					};

					worker.execute();

				}
			}
		};

		broadcastSocketItem.addActionListener(listener);
		broadcastHttpItem.addActionListener(listener);
		saveFileItem.addActionListener(listener);

		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tray.remove(trayIcon);
				System.exit(0);
			}
		});
	}

	// Obtain the image URL
	protected static Image createImage(String path, String description) {
		URL imageURL = MindStreamSystemTray.class.getResource(path);

		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}
}
