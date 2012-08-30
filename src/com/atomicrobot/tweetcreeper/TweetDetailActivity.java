package com.atomicrobot.tweetcreeper;

import com.atomicrobot.tweetcreeper.SearchResponseHandler.SearchResponseCallbacks;
import com.atomicrobot.tweetcreeper.model.SearchResponse;
import com.atomicrobot.tweetcreeper.ws.TwitterSearchService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class TweetDetailActivity extends FragmentActivity implements
		SearchResponseCallbacks, UserTweetsFragment.Callbacks {

	private static final String ARG_SCREEN_NAME = "ScreenName";

	public static Intent buildIntent(Context context, String screenName) {
		Intent i = new Intent(context, TweetDetailActivity.class);
		i.putExtra(ARG_SCREEN_NAME, screenName);
		return i;
	}

	private SearchResponseHandler searchResponseHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_detail);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		String screenName = getIntent().getStringExtra(ARG_SCREEN_NAME);

		if (savedInstanceState == null) {
			UserTweetsFragment fragment = UserTweetsFragment
					.newInstance(screenName);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.tweet_detail_container, fragment, "detail").commit();
		}
		
		searchResponseHandler = (SearchResponseHandler) getLastCustomNonConfigurationInstance();
		if (searchResponseHandler == null) {
			searchResponseHandler = new SearchResponseHandler();
			searchResponseHandler.updateCallbacks(this);
		}
	}
	
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		searchResponseHandler.updateCallbacks(null);
		return searchResponseHandler;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			NavUtils.navigateUpTo(this, new Intent(this,
					TweetListActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void handleSearchError() {
		Toast.makeText(this, "Doh! Couldn't pull some tweets",
				Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void requestTweets(String screenName) {
		setProgressBarIndeterminateVisibility(true);
		Log.v("DEBUG", "Requesting tweets for " + screenName);
		Intent i = TwitterSearchService.buildSearchIntent(this,
				searchResponseHandler, screenName, null);
		startService(i);
	}
	
	@Override
	public void handleSearchResponse(SearchResponse response) {
		setProgressBarIndeterminateVisibility(false);
		Log.v("DEBUG", "Received new tweets to display!");
		UserTweetsFragment fragment = findUserTweetsFragment();
		fragment.updateTweets(response);
	}

	private UserTweetsFragment findUserTweetsFragment() {
		setProgressBarIndeterminateVisibility(false);
		return (UserTweetsFragment) getSupportFragmentManager()
				.findFragmentByTag("detail");
	}
}
