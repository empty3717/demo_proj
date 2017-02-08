package com.empty.retrofit;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by empty on 2017/2/7.
 */

public class MyInterceptor implements Interceptor {

	@Override
	public Response intercept(Chain chain) throws IOException {

		Request request = chain.request();
		HttpUrl httpUrl = request.url().newBuilder().addQueryParameter( "aa", "bb" ).build();
		request = request.newBuilder().url( httpUrl ).build();

		return chain.proceed( request );
	}
}
