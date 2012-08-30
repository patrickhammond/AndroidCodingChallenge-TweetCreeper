package com.atomicrobot.tweetcreeper.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet {

	private final String text;
	private final String timestamp;

	@JsonCreator
	public Tweet(@JsonProperty("text") String text,
			@JsonProperty("created_at") String timestamp) {
		this.text = text;
		this.timestamp = timestamp;
	}

	@JsonProperty("text")
	public String getText() {
		return text;
	}

	@JsonProperty("created_at")
	public String getTimestamp() {
		return timestamp;
	}
}
