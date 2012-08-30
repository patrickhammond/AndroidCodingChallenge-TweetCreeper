package com.atomicrobot.tweetcreeper;

import java.io.IOException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.atomicrobot.tweetcreeper.model.SearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

class SearchResponseHandler extends Handler {

	public interface SearchResponseCallbacks {
		public void handleSearchResponse(SearchResponse response);

		public void handleSearchError();
	}

	private SearchResponseCallbacks callbacks;

	public void updateCallbacks(SearchResponseCallbacks callbacks) {
		this.callbacks = callbacks;
	}

	@Override
	public void handleMessage(Message msg) {
		Bundle data = msg.getData();
		if (data.getBoolean("success")) {
			String response = data.getString("response");

			try {
				ObjectMapper om = new ObjectMapper();
				SearchResponse searchResponse = om.readValue(response,
						SearchResponse.class);
				if (callbacks != null) {
					callbacks.handleSearchResponse(searchResponse);
				}
			} catch (IOException e) {
				if (callbacks != null) {
					callbacks.handleSearchError();
				}
			}
		} else {
			if (callbacks != null) {
				callbacks.handleSearchError();
			}
		}
	}
}