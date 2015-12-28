package com.example.chatdemo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.harmony.javax.security.sasl.Sasl;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.Base64;

/**
 * @see org.jivesoftware.smack.sasl.SASLMechanism
 */
public class SASLXFacebookPlatformMechanism extends SASLMechanism {
	// private String sessionKey = "";
	// private String sessionSecret = "";
	private String apiKey = "281842721924787";
	private String access_token = "AAAEAVZA9SprMBAGZC699qGevcDeVoLjRvVSFuRbeUZCFd4YuQvGwWtyLTc17OESF1rHAe7awmtygsZBZA3RqVjFKZCE3T03OLl99S2ehbRZBAZDZD";

	/**
	 * Constructor that create a SASLAuthentication Mechanism
	 * 
	 * @param saslAuthentication
	 *            represent the Authentication type of the server connection.
	 */
	public SASLXFacebookPlatformMechanism(SASLAuthentication saslAuthentication) {
		super(saslAuthentication);
	}

	@Override
	protected void authenticate() throws IOException, XMPPException {
		getSASLAuthentication().send(
				new SASLMechanism.AuthMechanism(getName(), ""));
	}

	/**
	 * Method to be overridden in order to make the connection to the xmpp
	 * server facebook.
	 * 
	 * @param apiKeyAndSessionKey
	 *            concatenation between api_key and session_key separeted by |.
	 * @param host
	 *            the host to which connect.
	 * @param sessionSecret
	 *            the secret of the current session.
	 * @throws IOException
	 * @throws XMPPException
	 */
	// @Override
	// public void authenticate(String apiKeyAndSessionKey, String host,
	// String sessionSecret) throws IOException, XMPPException {
	// if (apiKeyAndSessionKey == null || sessionSecret == null)
	// throw new IllegalStateException("Invalid parameters!");
	//
	// String[] keyArray = apiKeyAndSessionKey.split("\\|");
	//
	// if (keyArray == null || keyArray.length != 2)
	// throw new IllegalStateException(
	// "Api key or session key is not present!");
	//
	// this.apiKey = keyArray[0];
	// this.sessionKey = keyArray[1];
	// this.sessionSecret = sessionSecret;
	//
	// this.authenticationId = sessionKey;
	// this.password = sessionSecret;
	// this.hostname = host;
	//
	// String[] mechanisms = { "DIGEST-MD5" };
	// Map<String, String> props = new HashMap<String, String>();
	// sc = Sasl.createSaslClient(mechanisms, null, "xmpp", host, props, this);
	// authenticate();
	// }
	@Override
	public void authenticate(String apiKey, String host, String acces_token)
			throws IOException, XMPPException {
		if (apiKey == null || acces_token == null) {
			throw new IllegalArgumentException("Invalid parameters");
		}

		this.access_token = acces_token;
		this.apiKey = apiKey;
		this.hostname = host;

		String[] mechanisms = { getName() };
		Map<String, String> props = new HashMap<String, String>();
		this.sc = Sasl.createSaslClient(mechanisms, null, "xmpp", host, props,
				this);
		authenticate();
	}

	/**
	 * Returns the common name of the SASL mechanism. E.g.: PLAIN, DIGEST-MD5 or
	 * GSSAPI
	 * 
	 * @return the name of the connection mechanism.
	 */
	protected String getName() {
		return "X-FACEBOOK-PLATFORM";
	}

	/**
	 * The server is challenging the SASL mechanism for the stanza he just sent.
	 * 
	 * @see org.jivesoftware.smack.sasl.SASLMechanism
	 * 
	 * @param challenge
	 *            a base64 encoded string representing the challenge.
	 * @throws java.io.IOException
	 *             if an exception sending the response occurs.
	 */
	// @Override
	// public void challengeReceived(String challenge) throws IOException {
	// // Build the challenge response stanza encoding the response text
	// StringBuilder stanza = new StringBuilder();
	//
	// byte response[] = null;
	// if (challenge != null) {
	// String decodedResponse = new String(Base64.decode(challenge));
	// Map<String, String> parameters = getQueryMap(decodedResponse);
	//
	// String version = "1.0";
	// String nonce = parameters.get("nonce");
	// String method = parameters.get("method");
	//
	// Long callId = new GregorianCalendar().getTimeInMillis() / 1000;
	//
	// String sig = "api_key=" + apiKey + "call_id=" + callId + "method="
	// + method + "nonce=" + nonce + "session_key=" + sessionKey
	// + "v=" + version + sessionSecret;
	//
	// try {
	// sig = MD5(sig);
	// } catch (NoSuchAlgorithmException e) {
	// throw new IllegalStateException(e);
	// }
	// String composedResponse = "api_key=" + apiKey + "&" + "call_id="
	// + callId + "&" + "method=" + method + "&" + "nonce="
	// + nonce + "&" + "session_key=" + sessionKey + "&" + "v="
	// + version + "&" + "sig=" + sig;
	//
	// response = composedResponse.getBytes();
	// }
	//
	// String authenticationText = "";
	//
	// if (response != null) {
	// authenticationText = Base64.encodeBytes(response,
	// Base64.DONT_BREAK_LINES);
	// }
	//
	// stanza.append("<response xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">");
	// stanza.append(authenticationText);
	// stanza.append("</response>");
	//
	// // Send the authentication to the server
	// // getSASLAuthentication().send(stanza.toString());
	// getSASLAuthentication().send(new Response(authenticationText));
	// }
	@Override
	public void challengeReceived(String challenge) throws IOException {
		byte[] response = null;

		if (challenge != null) {
			String decodedChallenge = new String(Base64.decode(challenge));
			Map<String, String> parameters = getQueryMap(decodedChallenge);

			String version = "1.0";
			String nonce = parameters.get("nonce");
			String method = parameters.get("method");

			long callId = new GregorianCalendar().getTimeInMillis();

			String composedResponse = "api_key="
					+ URLEncoder.encode(apiKey, "utf-8") + "&call_id=" + callId
					+ "&method=" + URLEncoder.encode(method, "utf-8")
					+ "&nonce=" + URLEncoder.encode(nonce, "utf-8")
					+ "&access_token="
					+ URLEncoder.encode(access_token, "utf-8") + "&v="
					+ URLEncoder.encode(version, "utf-8");

			response = composedResponse.getBytes("utf-8");
		}

		String authenticationText = "";

		if (response != null) {
			authenticationText = Base64.encodeBytes(response,
					Base64.DONT_BREAK_LINES);
		}

		// Send the authentication to the server
		getSASLAuthentication().send(new Response(authenticationText));
	}

	/**
	 * 
	 * @see org.jivesoftware.smack.sasl.SASLMechanism
	 */
	private Map<String, String> getQueryMap(String query) {
		String[] params = query.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String param : params) {
			String name = param.split("=")[0];
			String value = param.split("=")[1];
			map.put(name, value);
		}
		return map;
	}

	/**
	 * 
	 * @see org.jivesoftware.smack.sasl.SASLMechanism
	 */
	private String convertToHex(byte[] data) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	/**
	 * 
	 * @see org.jivesoftware.smack.sasl.SASLMechanism
	 */
	private String MD5(String text) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		byte[] md5hash = new byte[32];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		md5hash = md.digest();
		return convertToHex(md5hash);
	}
}
