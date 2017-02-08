package com.empty.retrofit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitDemoActivity extends AppCompatActivity {

	private TextView mTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_retrofit_demo );

		initView();

		requestData();

	}

	/**
	 * 请求数据
	 */
	private void requestData() {
		Retrofit retrofit = new Retrofit.Builder()
			.baseUrl( "https://api.douban.com/v2/" )
			.addConverterFactory( GsonConverterFactory.create() )
			.build();

		NetService                 netService = retrofit.create( NetService.class );
		Call< BookSearchResponse > call       = netService.getSearchBooks( "search", "小王子", "", 0, 3 );
		call.enqueue( new Callback< BookSearchResponse >() {
			@Override
			public void onResponse(Call< BookSearchResponse > call, Response< BookSearchResponse > response) {
				mTv.setText( response.body().toString() );
			}

			@Override
			public void onFailure(Call< BookSearchResponse > call, Throwable t) {

			}
		} );
	}

	/**
	 * 初始化View
	 */
	private void initView() {
		mTv = ( TextView ) findViewById( R.id.tv );
	}
}
