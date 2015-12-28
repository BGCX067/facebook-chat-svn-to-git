package com.example.chatdemo;

import java.util.Collection;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.util.Log;

public class FacebookHelp implements MessageListener {

	XMPPConnection connection;

	private volatile static FacebookHelp fclient;

	static String username = "xxxxxxxx";

	static String password = "xxxxxxxx";

	private FacebookHelp() {
		
	}

	public static FacebookHelp getInstance() {

		if (fclient == null) {

			synchronized (FacebookHelp.class) {

				if (fclient == null) {

					fclient = new FacebookHelp();

				}

			}

		}

		return fclient;

	}

	public void login(String userName, String password) {
		SASLAuthentication.registerSASLMechanism("DIGEST-MD5",
				MySASLDigestMD5Mechanism.class);

		ConnectionConfiguration config = new ConnectionConfiguration(

		"chat.facebook.com", 5222, "chat.facebook.com");

		config.setCompressionEnabled(true);

		config.setSASLAuthenticationEnabled(true);

		connection = new XMPPConnection(config);
		try {
			connection.connect();
			connection.login(userName, password);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void login(String tocken) {
		ConnectionConfiguration config = new ConnectionConfiguration(
				"chat.facebook.com", 5222);
		config.setSASLAuthenticationEnabled(true);
		connection = new XMPPConnection(config);
		SASLAuthentication.registerSASLMechanism("X-FACEBOOK-PLATFORM",
				SASLXFacebookPlatformMechanism.class);
		SASLAuthentication.supportSASLMechanism("X-FACEBOOK-PLATFORM", 0);
		try {
			connection.connect();
			connection.login("281842721924787", tocken);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void regListenser(ConnectionListener arg0){
		connection.addConnectionListener(arg0);
	}

	public void sendMessage(String message, String to) throws XMPPException {

		Chat chat = connection.getChatManager().createChat(to, this);

		chat.sendMessage(message);

	}

	public void listenTochat(final MessageListener msgListener) {
		connection.getChatManager().addChatListener(new ChatManagerListener() {
			
			@Override
			public void chatCreated(Chat arg0, boolean arg1) {
				// TODO Auto-generated method stub
				arg0.addMessageListener(msgListener);
			}
		});
	}

	public void displayBuddyList() {

		Roster roster = connection.getRoster();

		roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

		Collection<RosterEntry> entries = roster.getEntries();

		Log.d("", "buddy(ies):" + entries.size());

		for (RosterEntry r : entries) {
			Log.d("", "buddy(ies):" + r.getUser());
		}

	}

	public void addRoster(String bot, String email, String input) {

		FacebookHelp c = FacebookHelp.getInstance();

		try {

			addRoster(bot, input);

			c.sendMessage(input, email);

		} catch (XMPPException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

	}

	public void addRoster(String bot, String input) {

		FacebookHelp c = FacebookHelp.getInstance();

		try {

			c.login(username, password);

			Roster roster = connection.getRoster();

			roster.createEntry(input, null, null);

		} catch (XMPPException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

		c.disconnect();

	}

	public void disconnect() {

		connection.disconnect();

	}

	public void processMessage(Chat chat, Message message) {

		if (message.getType() == Message.Type.chat)

			System.out.println(chat.getParticipant() + " says: "

			+ message.getBody());

	}

	public void alert(String bot, String email, String msg) {

		FacebookHelp c = FacebookHelp.getInstance();

		// turn on the enhanced debugger

		XMPPConnection.DEBUG_ENABLED = false;

		// provide your login information here

		try {

			c.login(username, password);

			c.sendMessage(msg, email);

		} catch (XMPPException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

		c.disconnect();

	}

}