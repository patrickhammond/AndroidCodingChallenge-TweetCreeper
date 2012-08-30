package com.atomicrobot.tweetcreeper;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.atomicrobot.tweetcreeper.SearchResponseHandler.SearchResponseCallbacks;
import com.atomicrobot.tweetcreeper.model.SearchResponse;
import com.atomicrobot.tweetcreeper.ws.TwitterSearchService;

public class TweetListActivity extends FragmentActivity implements
		TweetListFragment.Callbacks, UserTweetsFragment.Callbacks,
		SearchResponseCallbacks {

	private boolean mTwoPane;

	private SearchResponseHandler searchResponseHandler;

	private ArrayList<String> screenNames = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		screenNames.add("patrickhammond");

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_list);
		setProgressBarIndeterminateVisibility(false);

		if (findViewById(R.id.tweet_detail_container) != null) {
			mTwoPane = true;
			((TweetListFragment) getSupportFragmentManager().findFragmentById(
					R.id.tweet_list)).setActivateOnItemClick(true);
		}

		searchResponseHandler = (SearchResponseHandler) getLastCustomNonConfigurationInstance();
		if (searchResponseHandler == null) {
			searchResponseHandler = new SearchResponseHandler();
		}
		searchResponseHandler.updateCallbacks(this);
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		searchResponseHandler.updateCallbacks(null);
		return searchResponseHandler;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		final SearchView searchView = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				addAndDisplayScreenName(query);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			UserTweetsFragment fragment = findUserTweetsFragment();
			if (fragment != null) {
				fragment.refresh();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void addAndDisplayScreenName(String screenName) {
		if (!screenNames.contains(screenName)) {
			screenNames.add(screenName);
		}

		onScreenNameSelected(screenName);
	}

	@Override
	public ArrayList<String> getScreenNames() {
		return screenNames;
	}

	@Override
	public void onScreenNameSelected(String screenName) {
		if (mTwoPane) {
			UserTweetsFragment fragment = UserTweetsFragment
					.newInstance(screenName);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.tweet_detail_container, fragment, "detail")
					.commit();
		} else {
			Intent intent = TweetDetailActivity.buildIntent(this, screenName);
			startActivity(intent);
		}
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

	@Override
	public void handleSearchError() {
		Toast.makeText(this, "Doh! Couldn't pull some tweets",
				Toast.LENGTH_LONG).show();
	}
}
