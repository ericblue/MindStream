package com.ericblue.mindstream.client;

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
 * $Date: 2011-07-24 17:54:27 $ 
 * $Author: ericblue76 $
 * $Revision: 1.3 $
 *
 */


public class ThinkGearSocketClient {

	public static final String DEFAULT_HOST = "127.0.0.1";
	public static final int DEFAULT_PORT = 13854;

	private String host;
	private int port;
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
	public ThinkGearSocketClient(String host, int port) {

		this.host = host;
		this.port = port;
		this.connected = false;

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
			System.out.println("Starting new connection...");
			this.channel = SocketChannel.open(new InetSocketAddress(this.host, this.port));

			CharsetEncoder enc = Charset.forName("US-ASCII").newEncoder();
			String jsonCommand = "{\"enableRawOutput\": false, \"format\": \"Json\"}\n";
			this.channel.write(enc.encode(CharBuffer.wrap(jsonCommand)));

			this.in = new Scanner(channel);
			this.connected = true;
		} else {
			System.out.println("Already connected...");
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
			System.out.println("Closing connection...");
			this.channel.close();
			this.connected = false;
		}
	}

}
