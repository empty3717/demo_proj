package com.empty.fragmenttest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by empty on 2017/2/24.
 */

public class Fragment3 extends Fragment {
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.d( "onCreateView", this.getClass().getSimpleName().toString() );
		View     root     = inflater.inflate( R.layout.fragment, null );
		TextView textView = ( TextView ) root.findViewById( R.id.tv );
		textView.setText( "3333333333333333" );
		return root;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d( "onDestroyView", this.getClass().getSimpleName().toString() );
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
		Log.d( "onCreate", this.getClass().getSimpleName().toString() );
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d( "onDestroy", this.getClass().getSimpleName().toString() );
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d( "onStart", this.getClass().getSimpleName().toString() );
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d( "onStop", this.getClass().getSimpleName().toString() );
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d( "onResume", this.getClass().getSimpleName().toString() );
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d( "onPause", this.getClass().getSimpleName().toString() );
	}
}
