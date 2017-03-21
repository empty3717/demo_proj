package com.empty.refreshlib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.empty.refreshlib.library.PullToRefreshBase;
import com.empty.refreshlib.library.PullToRefreshListView;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		PullToRefreshListView listView = ( PullToRefreshListView ) findViewById( R.id.pull_listView );
		listView.setOnRefreshListener( new PullToRefreshBase.OnRefreshListener2< ListView >() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase< ListView > refreshView) {

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase< ListView > refreshView) {

			}
		} );
	}
}
