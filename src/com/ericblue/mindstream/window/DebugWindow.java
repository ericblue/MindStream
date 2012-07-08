package com.ericblue.mindstream.window;

import org.apache.log4j.Logger;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

/**
 * <p>Title:		DebugWindow</p><br>
 * <p>Description:	Displays real-time JSON output from ThinkGear socket</p><br>
 * @author		    <a href="http://eric-blue.com">Eric Blue</a><br>
 *
 * $Date: 2012-07-08 03:31:28 $ 
 * $Author: ericblue76 $
 * $Revision: 1.4 $
 *
 */


public class DebugWindow extends JFrame {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(DebugWindow.class);

	private JPanel contentPane;
	private JTextArea textArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DebugWindow frame = new DebugWindow();
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
	public DebugWindow() {
		setTitle("Debug Output");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 828, 562);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JButton btnClose = new JButton("Close");
		btnClose.setBounds(new Rectangle(20, 20, 20, 20));
		btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);
		SpringLayout sl_contentPane = new SpringLayout();
		sl_contentPane.putConstraint(SpringLayout.WEST, btnClose, 363, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnClose, -10, SpringLayout.SOUTH, contentPane);
		contentPane.setLayout(sl_contentPane);
		contentPane.add(btnClose);

		this.textArea = new JTextArea();
		textArea.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(812, 465));
		contentPane.add(scrollPane);

		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

                logger.debug("$ActionListener.actionPerformed(ActionEvent) - " + e.getActionCommand());

				if ("Close".equals(e.getActionCommand())) {

					DebugWindow.getFrames()[0].setVisible(false);

				}

			}
		};

		btnClose.addActionListener(listener);

	}

	public JTextArea getTextArea() {
		return textArea;
	}
}
