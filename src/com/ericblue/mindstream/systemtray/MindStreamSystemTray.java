package com.ericblue.mindstream.systemtray;

import org.apache.log4j.Logger;


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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
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
 * <p>Title:        MindStreamSystemTray</p><br>
 * <p>Description:  Description: System tray app for streaming data from the Neurosky MindSet/MindWave</p><br>
 * @author          <a href="http://eric-blue.com">Eric Blue</a><br>
 *
 * $Date: 2012-07-08 03:31:27 $ 
 * $Author: ericblue76 $
 * $Revision: 1.8 $
 *
 */


public class MindStreamSystemTray {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(MindStreamSystemTray.class);
    
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
            logger.debug("initializePreferences() - Setting default ThinkGear Host");
            prefs.put("thinkgearHost", ThinkGearSocketClient.DEFAULT_HOST);
        }

        if (prefs.getInt("thinkgearPort", 0) == 0) {
            logger.debug("initializePreferences() - Setting default ThinkGear Host");
            prefs.putInt("thinkgearPort", ThinkGearSocketClient.DEFAULT_PORT);
        }

        if (prefs.get("fileLocation", null) == null) {
            logger.debug("initializePreferences() - Setting default CSV file location");
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
            logger.debug("initializeGUI() - SystemTray is not supported");
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
        // Not implemented yet
        // MenuItem broadcastSocketItem = new MenuItem("Broadcast (Socket)");
        // MenuItem broadcastHttpItem = new MenuItem("Broadcast (HTTP)");
        MenuItem saveFileItem = new MenuItem("Save (File)");

        MenuItem exitItem = new MenuItem("Exit");

        // Add components to popup menu
        popup.add(aboutItem);
        popup.add(preferencesItem);
        popup.addSeparator();

        popup.add(viewDebug);
        popup.add(cbConnect);
        popup.add(streamMenu);
        // streamMenu.add(broadcastSocketItem);
        // streamMenu.add(broadcastHttpItem);
        streamMenu.add(saveFileItem);
        popup.addSeparator();

        popup.add(exitItem);

        final DebugWindow debugWindow = new DebugWindow();
        final PreferencesWindow preferencesWindow = new PreferencesWindow();

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            logger.debug("initializeGUI() - TrayIcon could not be added.");
            return;
        }

        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Mindstream - https://github.com/ericblue/MindStream");
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
                logger.debug("$ActionListener.actionPerformed(ActionEvent) - " + item.getLabel());
                if ("Broadcast (Socket)".equals(item.getLabel())) {
                    String message = "Broadcasting socket on port (xyz). Uncheck Connect to ThinkGear Socket to stop.";
                    trayIcon.displayMessage("INFO", message, TrayIcon.MessageType.INFO);

                } else if ("Broadcast (HTTP)".equals(item.getLabel())) {
                    String message = "Broadcasting HTTP to url (xyz). Uncheck Connect to ThinkGear Socket to stop.";
                    trayIcon.displayMessage("INFO", message, TrayIcon.MessageType.INFO);

                } else if ("Save (File)".equals(item.getLabel())) {

                    final String csvFile = PreferenceManager.loadPreferences().get("fileLocation", "");
                    String message = "Saving file " + csvFile + ". Uncheck Connect to ThinkGear Socket to stop.";
                    trayIcon.displayMessage("INFO", message, TrayIcon.MessageType.INFO);

                    SwingWorker worker = new SwingWorker<Void, Void>() {
                        public Void doInBackground() {

                            if (csvFile == null) {
                                trayIcon.displayMessage("ERROR", "File location must be set in Preferences!",
                                        TrayIcon.MessageType.ERROR);
                            }

                            FileWriter writer = null;
                            String newLine = System.getProperty("line.separator");

                            try {
                                writer = new FileWriter(csvFile);
                            } catch (IOException e1) {
                                trayIcon.displayMessage("ERROR", "Error opening file for writing!",
                                        TrayIcon.MessageType.ERROR);
                                logger.error("$SwingWorker<Void,Void>.doInBackground()", e1);
                            }

                            // HEADER
                            try {
                                writer.append("TIMESTAMP,POOR_SIGNAL_LEVEL,ATTENTION,MEDITATION,");
                                writer.append("DELTA,THETA,LOW_ALPHA,HIGH_ALPHA,LOW_BETA,HIGH_BETA,");
                                writer.append("LOW_GAMMA,HIGH_GAMA");
                                writer.append(newLine);
             
                            } catch (IOException e2) {
                                trayIcon.displayMessage("Write Error", e2.getMessage(), TrayIcon.MessageType.ERROR);
                            }

                            SimpleDateFormat fmt = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

                            while (client.isDataAvailable()) {
                                logger.debug("$SwingWorker<Void,Void>.doInBackground() - Writing...");

                                logger.debug("$SwingWorker<Void,Void>.doInBackground() - " + client.getData());
                                try {
                                    String clientData = client.getData();
                                    logger.debug("$SwingWorker<Void,Void>.doInBackground() - " + clientData);
                                    JSONObject json = new JSONObject(clientData);

                                    /*
                                     * JH: check just in case it's not there due
                                     * to poorSignallevel
                                     */
                                    if (!json.isNull("eegPower")) {

                                        String timeStamp = fmt.format(new Date());
                                        writer.append(timeStamp + ',');
                                        /*
                                         * JH: check for existence of
                                         * poorSignalLevel. If not available,
                                         * assume 0 *
                                         */
                                        if (!json.isNull("poorSignalLevel")) {
                                            writer.append(Integer.toString(json.getInt("poorSignalLevel")) + ',');
                                        } else {
                                            writer.append("0,");
                                        }

                                        /*
                                         * JH: check for existence of eSense. I
                                         * noticed it's possible to get eegPower
                                         * without eSense when poorSignallevel
                                         * >0
                                         */
                                        if (!json.isNull("eSense")) {

                                            JSONObject esense = json.getJSONObject("eSense");

                                            /*
                                             * JH: Don't know if it's possible
                                             * for these attributes to not exist
                                             * even when the JSON Object exists
                                             */
                                            writer.append(Integer.toString(esense.getInt("attention")) + ',');
                                            writer.append(Integer.toString(esense.getInt("meditation")) + ',');

                                        } else {
                                            logger.debug("$SwingWorker<Void,Void>.doInBackground() - eSense is null!");
                                        }

                                        JSONObject eegPower = json.getJSONObject("eegPower");

                                        writer.append(Integer.toString(eegPower.getInt("delta")) + ',');
                                        writer.append(Integer.toString(eegPower.getInt("theta")) + ',');
                                        writer.append(Integer.toString(eegPower.getInt("lowAlpha")) + ',');
                                        writer.append(Integer.toString(eegPower.getInt("highAlpha")) + ',');
                                        writer.append(Integer.toString(eegPower.getInt("lowBeta")) + ',');
                                        writer.append(Integer.toString(eegPower.getInt("highBeta")) + ',');
                                        writer.append(Integer.toString(eegPower.getInt("lowGamma")) + ',');
                                        writer.append(Integer.toString(eegPower.getInt("highGamma")));
                                        writer.append(newLine);

                                    } else {
                                        logger.debug("$SwingWorker<Void,Void>.doInBackground() - eegPower is null!");
                                    }

                                    writer.flush();

                                } catch (JSONException e1) {
                                    trayIcon.displayMessage("JSON Error", e1.getMessage(), TrayIcon.MessageType.ERROR);
                                } catch (IOException e2) {
                                    trayIcon.displayMessage("Write Error", e2.getMessage(), TrayIcon.MessageType.ERROR);
                                }

                            }

                            try {
                                logger.debug("$SwingWorker<Void,Void>.doInBackground() - Closing file...");
                                writer.close();
                            } catch (IOException e) {
                                trayIcon.displayMessage("Write Error", e.getMessage(), TrayIcon.MessageType.ERROR);
                            }

                            return null;

                        }
                    };

                    worker.execute();

                }
            }
        };

        // broadcastSocketItem.addActionListener(listener);
        // broadcastHttpItem.addActionListener(listener);
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
            logger.error("createImage(String, String) - Resource not found: " + path, null);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}