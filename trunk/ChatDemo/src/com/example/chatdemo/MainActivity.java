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

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements MessageListener {
	XMPPConnection connection;
	private String AppIdString = "281842721924787";
	private String access_token = "AAAEAVZA9SprMBAGZC699qGevcDeVoLjRvVSFuRbeUZCFd4YuQvGwWtyLTc17OESF1rHAe7awmtygsZBZA3RqVjFKZCE3T03OLl99S2ehbRZBAZDZD";
	private ProgressDialog mProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mProgressDialog = ProgressDialog.show(this, "ÌáÊ¾", "µÇÂ½ÖÐ...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				login(access_token);

			}
		}).start();
		initView();
	}
	TextView textView;
	private void initView() {
		final EditText editText = (EditText) findViewById(R.id.editText1);
		Button button = (Button) findViewById(R.id.button1);
		 textView = (TextView) findViewById(R.id.ShowTextView);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String string = editText.getText().toString();
				editText.getText().clear();
				try {
					sendMessage(string, "-100004270664076@chat.facebook.com");
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
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
			displayBuddyList();
			mProgressDialog.dismiss();
		} catch (XMPPException e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connection.getChatManager().addChatListener(new ChatManagerListener() {
			
			@Override
			public void chatCreated(Chat arg0, boolean arg1) {
				// TODO Auto-generated method stub
				arg0.addMessageListener(MainActivity.this);
			}
		});
	}

	public void sendMessage(String message, String to) throws XMPPException {

		Chat chat = connection.getChatManager().createChat(to, this);

		chat.sendMessage(message);

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

	public void regListenser(ConnectionListener arg0) {
		connection.addConnectionListener(arg0);
	}


	@Override
	public void processMessage(Chat arg0, Message arg1) {
		// TODO Auto-generated method stub
		textView.setText(arg1.getBody());
	}
}
