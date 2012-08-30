package com.atomicrobot.tweetcreeper;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.atomicrobot.tweetcreeper.model.SearchResponse;
import com.atomicrobot.tweetcreeper.model.Tweet;

public class UserTweetsFragment extends ListFragment {

	public interface Callbacks {
		public void requestTweets(String screenName);
	}

	private static final String ARG_SCREEN_NAME = "ScreenName";

	public static UserTweetsFragment newInstance(String screenName) {
		UserTweetsFragment f = new UserTweetsFragment();
		Bundle args = new Bundle();
		args.putString(ARG_SCREEN_NAME, screenName);
		f.setArguments(args);
		return f;
	}

	private Callbacks callbacks;
	private String screenName;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callbacks = (Callbacks) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		screenName = getArguments().getString(ARG_SCREEN_NAME);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		getListView().setOnScrollListener(new OnScrollListener() {
//
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
//				if (loadMore) {
//					refresh();
//				}
//			}
//
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//			}
//		});
	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	public void refresh() {
		if (callbacks != null) {
			callbacks.requestTweets(screenName);
		}
	}

	public void updateTweets(SearchResponse response) {
		ArrayList<Tweet> tweets = response.getTweets();
		ArrayAdapter<Tweet> adapter = new ArrayAdapter<Tweet>(getActivity(),
				R.layout.tweet, R.id.tweet_text, tweets) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = LayoutInflater.from(getActivity()).inflate(
							R.layout.tweet, null);
					convertView.setTag(new Tag(convertView));
				}

				Tag tag = (Tag) convertView.getTag();

				Tweet tweet = getItem(position);
				tag.text.setText(tweet.getText());
				tag.timestamp.setText(tweet.getTimestamp());

				return convertView;
			}
		};
		setListAdapter(adapter);
	}

	private static class Tag {
		final TextView text;
		final TextView timestamp;

		Tag(View v) {
			text = (TextView) v.findViewById(R.id.tweet_text);
			timestamp = (TextView) v.findViewById(R.id.tweet_timestamp);
		}
	}
}
