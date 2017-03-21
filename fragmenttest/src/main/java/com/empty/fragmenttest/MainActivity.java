package com.empty.fragmenttest;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	private ViewPager        vp;
	private List< Fragment > mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		vp = ( ViewPager ) findViewById( R.id.vp );
		vp.setOffscreenPageLimit( 0 );
		mList = new ArrayList<>();
		mList.add( new Fragment1() );
		mList.add( new Fragment2() );
		mList.add( new Fragment3() );

		MyAdapter myAdapter = new MyAdapter( getSupportFragmentManager() );
		vp.setAdapter( myAdapter );
	}

	class MyAdapter extends FragmentStatePagerAdapter {

		public MyAdapter(FragmentManager fm) {
			super( fm );
		}

		@Override
		public Fragment getItem(int position) {
			return mList.get( position );
		}

		@Override
		public int getCount() {
			if ( mList != null ) {
				return mList.size();
			}
			return 0;
		}
	}
}
