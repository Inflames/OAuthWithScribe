package de.c2g.oauth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.hft.il.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {
	/** Called when the activity is first created. */

	public final static String C2G_BASE = "http://www.car2go.com/api/v2.1/";
	public final static String C2G_ACCOUNTS = "accounts";
	public final static String C2G_BOOKINGS = "bookings";
	public final static String C2G_BOOKING = "booking";
	private final static String C2G_CONSUMERKEY = "AdrianMarsch";
	private final static String C2G_SHAREDSECRET = "f9WOr3fkr8BREt6l";

	private static OAuthService service;
	private static Token requestToken;
	private static Token accessToken;
	private static boolean accountStatus = false;
	private static Calendar expirationDate;

	TextView text;
	Button loadA;
	String url = null;
	WebView web;
	EditText pinEdit;
	Toast toast;
	CheckBox check;
	Button bPin;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.anmeldung);

		pinEdit = (EditText) findViewById(R.id.pin);
		check = (CheckBox) findViewById(R.id.check);
		check.setClickable(false);
		check.setChecked(accountHandler());
		//loadA = (Button) findViewById(R.id.loader);
		// loadA.setClickable(accountHandler());
		//loadA.setEnabled((accountHandler()));
	

		try {
			loadScreen();
		} catch (IllegalArgumentException e) {
			toast = Toast
					.makeText(
							Login.this,
							"Fehler beim Verbindungsaufbau ! Überprüfen sie ihre Internetverbindung",
							Toast.LENGTH_SHORT);
			toast.show();
			e.printStackTrace();
			Log.d(url, "Connection is failed" + e.getMessage());
		}

	}

	//
	public void action(final View view) {

		switch (view.getId()) {
		case R.id.PinButton:
			try {
				validate();
				//finish();
			} catch (IllegalArgumentException e) {
				toast = Toast
						.makeText(
								Login.this,
								"Der eingebene Code ist falsch ! Bitte versuchen sie es erneut",
								Toast.LENGTH_SHORT);
				toast.show();
				pinEdit.setText("");
				e.printStackTrace();
				Log.d(url, "Validate is failed" + e.getMessage());
				try {
					loadScreen();
				} catch (IllegalArgumentException ex) {
					Log.d(url, "No relead" + ex.getMessage());
					e.printStackTrace();
				}
			}

			break;

		
		}
	}

	// sceen for login
	// servicebuilder
	public void loadScreen() throws IllegalArgumentException {
		
		

		service = new ServiceBuilder().provider(Car2GoApi.class)
				.apiKey(C2G_CONSUMERKEY).apiSecret(C2G_SHAREDSECRET)
				.scope(C2G_BASE).build();

		requestToken = service.getRequestToken();
		url = service.getAuthorizationUrl(requestToken);

		Log.d(url, "da......");

		web = (WebView) findViewById(R.id.login);
		web.getSettings().setJavaScriptEnabled(true);
		web.setWebViewClient(new WebViewClient());
		web.requestFocus(View.FOCUS_DOWN);
		web.loadUrl(url);
		web.getUrl();
	}

	// check code input
	private void validate() throws IllegalArgumentException {

		String pin2;

		pin2 = pinEdit.getText().toString();

		accessToken = service.getAccessToken(requestToken, new Verifier(pin2));

// create a timestamp and add 31 days
		
		
		expirationDate = Calendar.getInstance();
		expirationDate.setTime(new Date());

		expirationDate.add(Calendar.DAY_OF_MONTH, 31);
		Log.d(expirationDate.getTime().toString(),"date");

		toast = Toast.makeText(Login.this, "Anmeldung erfolgreich: "
				+ accessToken.toString(), Toast.LENGTH_SHORT);
		toast.show();

		try {
			dbWriter(accessToken);
		} catch (IOException e) {
			toast = Toast.makeText(Login.this,
					"Speichern des Accounts fehlgeschlagen", Toast.LENGTH_LONG);
			toast.show();
			Log.d("saveFile", "failed");
			e.printStackTrace();
		}

		Log.d(accessToken.toString(), "da............");

		// implement startIntent --->

	}

	// call for querys
	public static String privateService(String requestUrl) {

		OAuthRequest request = new OAuthRequest(Verb.GET, requestUrl);

		service.signRequest(accessToken, request);
		Response response = request.send();

		Util.d("C2G rc=" + response.getCode());
		return response.getBody();

	}

	// not in used --> test for saving
	public byte[] getBytes(Object obj) throws java.io.IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		oos.flush();
		oos.close();
		baos.close();
		byte[] ba = baos.toByteArray();
		return ba;
	}

	// convert object to ByteArray
	private byte[] toByteArray(Object paramObject) throws IOException {
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		new ObjectOutputStream(localByteArrayOutputStream)
				.writeObject(paramObject);
		localByteArrayOutputStream.close();
		return localByteArrayOutputStream.toByteArray();
	}

	public boolean ableToLoadAccessToken() {
		Date date = new Date();
		Calendar currentDate = new GregorianCalendar();
		currentDate.setTime(date);
		//
		if (currentDate.before(expirationDate)) {
			return true;
		} else
			return false;
	}

	// create saveFile
	public void dbWriter(Token AccessToken) throws IOException {

		byte[] arrayOfByte = toByteArray(AccessToken);
		ByteStream.write(arrayOfByte);
        
	}

	// load saveFile
	public void dbReader() {
		accessToken = (Token) ByteStream.read();
		
		Log.d(accessToken.toString(), "loadAccount");
	}

	// checking account
	public boolean accountHandler() {
        
		bPin = (Button) findViewById(R.id.PinButton);;
		String s = "/sdcard/data/ByteStream1.txt";

		File f = new File(s);
		if (f.exists() ) {

			accountStatus = true;
			dbReader();
			bPin.setText("Neuer Account");
			
			 
			
			toast = Toast.makeText(Login.this,
					"Es wurde ein Account gefunden ! ",
					Toast.LENGTH_LONG);
			toast.show();
		}
		else
			check.setBackgroundColor(Color.RED);
			

		return accountStatus;

	}
	
	
	
	

	public void accoutSwitcher() {

		// Coming soon...
	}
	
	
	

}