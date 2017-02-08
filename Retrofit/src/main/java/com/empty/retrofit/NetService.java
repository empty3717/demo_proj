package com.empty.retrofit;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by empty on 2017/2/7.
 */

public interface NetService {

	//最简单的方式，参数非必填的话可以传null
	@GET( "book/search" )
	Call< BookSearchResponse > getSearchBooks(@Query( "q" ) String name,
	                                          @Query( "tag" ) String tag,
	                                          @Query( "start" ) int start,
	                                          @Query( "count" ) int count);

	//通过map来传递参数
	@GET( "book/search" )
	Call< BookSearchResponse > getSearchBooks(@QueryMap Map< String, String > params);

	//一个key，多个Value
	@GET( "book/search" )
	Call< BookSearchResponse > getSearchBooks(@Query( "q" ) List< String > name);

	@GET( "book/{path}" )
	Call< BookSearchResponse > getSearchBooks(@Path( "path" ) String path,
	                                          @Query( "q" ) String name,
	                                          @Query( "tag" ) String tag,
	                                          @Query( "start" ) int start,
	                                          @Query( "count" ) int count);

}
