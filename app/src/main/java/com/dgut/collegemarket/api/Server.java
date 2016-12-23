package com.dgut.collegemarket.api;

import java.io.Serializable;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Server {
	static OkHttpClient client;
	
	static {
		CookieManager cookieManager = new CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		
		client = new OkHttpClient.Builder()
				.cookieJar(new JavaNetCookieJar(cookieManager))
				.build();
	}
	
	public static OkHttpClient getSharedClient(){
		return client;
	}
	

	public static String serverAddress_wuzeen = "http://172.27.0.23:8080/CollegeMarket/";
//	public static String serverAddress_zhongweiyuan = "http://172.27.0.22:8080/CollegeMarket/";
	public static Request.Builder requestBuilderWithApi(String api){
		String url=serverAddress_wuzeen+"api/"+api;
		System.out.println("访问了："+url);
		return new Request.Builder()
		.url(url);
	}
}
