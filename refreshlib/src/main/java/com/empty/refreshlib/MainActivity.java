package com.empty.refreshlib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.empty.refreshlib.library.PullToRefreshBase;
import com.empty.refreshlib.library.PullToRefreshListView;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		final PullToRefreshListView listView = ( PullToRefreshListView ) findViewById( R.id.pull_listView );
		listView.setMode( PullToRefreshListView.Mode.BOTH );
		listView.setScrollingWhileRefreshingEnabled( false );
		listView.setOnRefreshListener( new PullToRefreshBase.OnRefreshListener2< ListView >() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase< ListView > refreshView) {
				Log.e("ddd" , "下拉");
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase< ListView > refreshView) {
				Log.e("ddd" , "上拉");

			}
		} );
	}
}
