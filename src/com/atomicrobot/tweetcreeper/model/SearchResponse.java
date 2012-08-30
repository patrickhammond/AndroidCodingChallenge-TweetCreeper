package com.atomicrobot.tweetcreeper.model;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResponse {

	private final ArrayList<Tweet> tweets;

	@JsonCreator
	public SearchResponse(@JsonProperty("results") ArrayList<Tweet> tweets) {
		this.tweets = tweets;
	}

	@JsonProperty("results")
	public ArrayList<Tweet> getTweets() {
		return tweets;
	}
}
