package com.atomicrobot.tweetcreeper.ws;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.atomicrobot.tweetcreeper.model.SearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TwitterSearchService extends IntentService {

	private static final String ARG_MESSENGER = "Handler";
	private static final String ARG_SCREEN_NAME = "ScreenName";
	private static final String ARG_TWEET_ID = "TweetId";

	public static Intent buildSearchIntent(Context context, Handler handler,
			String screenName, String tweetId) {
		Intent i = new Intent(context, TwitterSearchService.class);
		i.putExtra(ARG_MESSENGER, new Messenger(handler));
		i.putExtra(ARG_SCREEN_NAME, screenName);
		i.putExtra(ARG_TWEET_ID, tweetId);
		return i;
	}

	public TwitterSearchService() {
		super("TwitterSearchService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Messenger messenger = (Messenger) intent
				.getParcelableExtra(ARG_MESSENGER);
		String screenName = intent.getStringExtra(ARG_SCREEN_NAME);
		String tweetId = intent.getStringExtra(ARG_TWEET_ID);

		Log.v("DEBUG", "Starting a search for " + screenName);

		String urlString = buildSearchUrl(screenName, tweetId);
		try {
			URL url = new URL(urlString);
			Object response = url.getContent();
			if (response == null) {
				handleError(messenger);
			} else {
				InputStream is = (InputStream) response;
				ObjectMapper om = new ObjectMapper();
				SearchResponse searchResponse = om.readValue(is,
						SearchResponse.class);

				Log.v("DEBUG", "Done searching...");

				String asString = om.writeValueAsString(searchResponse);

				Bundle b = new Bundle();
				b.putString("response", asString);
				b.putBoolean("success", true);
				Message m = Message.obtain();
				m.setData(b);
				messenger.send(m);

				Log.v("DEBUG", "Done sending message back to the handler...");
			}
		} catch (IOException ex) {
			handleError(messenger);
		} catch (RemoteException ex) {
			handleError(messenger);
		}

		Log.v("DEBUG", "Done with this service request...");
	}

	private void handleError(Messenger messenger) {
		Bundle b = new Bundle();
		b.putBoolean("success", false);
		Message m = Message.obtain();
		m.setData(b);
		try {
			messenger.send(m);
		} catch (RemoteException ex) {
			// Damn it, I give up!
		}
	}

	private String buildSearchUrl(String screenName, String tweetId) {
		String url = "http://search.twitter.com/search.json?q=" + screenName;
		if (tweetId != null) {
			url += "&max_id=" + tweetId;
		}
		return url;

	}
}
