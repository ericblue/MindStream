package com.ericblue.mindstream.client;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Scanner;

/**
 * <p>Title:		ThinkGearSocketClient</p><br>
 * <p>Description:	NeuroSky ThinkGear socket client - supports JSON output</p><br>
 * @author		    <a href="http://eric-blue.com">Eric Blue</a><br>
 *
 * $Date: 2012-07-08 03:31:28 $ 
 * $Author: ericblue76 $
 * $Revision: 1.4 $
 *
 */


public class ThinkGearSocketClient {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ThinkGearSocketClient.class);

	public static final String DEFAULT_HOST = "127.0.0.1";
	public static final int DEFAULT_PORT = 13854;

	private String host;
	private int port;
	private boolean rawOutput;
	private boolean connected;
	SocketChannel channel;
	Scanner in;

	/**
	 * Default constructor using Thinkgear default host/port
	 */
	public ThinkGearSocketClient() {

		this.host = DEFAULT_HOST;
		this.port = DEFAULT_PORT;
		this.connected = false;

	}

	/**
	 *  Constructor
	 * 
	 * @param host
	 * @param port
	 */
	public ThinkGearSocketClient(String host, int port, boolean rawOutput) {

		this.host = host;
		this.port = port;
		this.connected = false;
		this.rawOutput = rawOutput;

	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isConnected() {
		return this.connected;
	}

	public void connect() throws IOException {

		if (!this.connected) {
            logger.debug("connect() - Starting new connection...");
			this.channel = SocketChannel.open(new InetSocketAddress(this.host, this.port));

			CharsetEncoder enc = Charset.forName("US-ASCII").newEncoder();
			String jsonCommand = "{\"enableRawOutput\": " + rawOutput +", \"format\": \"Json\"}\n";
			this.channel.write(enc.encode(CharBuffer.wrap(jsonCommand)));

			this.in = new Scanner(channel);
			this.connected = true;
		} else {
            logger.debug("connect() - Already connected...");
		}

	}

	public boolean isDataAvailable() {
		if (this.connected) {
			return this.in.hasNextLine();
		} else {
			return false;
		}
	}

	public String getData() {
		return this.in.nextLine();
	}

	public void close() throws IOException {

		if (this.connected) {
            logger.debug("close() - Closing connection...");
			this.in.close();
			this.channel.close();
			this.connected = false;
		}
	}

}
