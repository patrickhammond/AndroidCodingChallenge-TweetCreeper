package com.atomicrobot.tweetcreeper;

import java.util.ArrayList;

import android.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TweetListFragment extends ListFragment {

	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	private int mActivatedPosition = ListView.INVALID_POSITION;

	public interface Callbacks {

		public ArrayList<String> getScreenNames();

		public void onScreenNameSelected(String screenName);
	}

	private Callbacks callbacks;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callbacks = (Callbacks) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				R.layout.simple_list_item_activated_1, R.id.text1,
				callbacks.getScreenNames()));
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		String screenName = callbacks.getScreenNames().get(position);
		Log.v("DEBUG", "click on screenname " + screenName);
		callbacks.onScreenNameSelected(screenName);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	public void setActivateOnItemClick(boolean activateOnItemClick) {
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	public void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
}
