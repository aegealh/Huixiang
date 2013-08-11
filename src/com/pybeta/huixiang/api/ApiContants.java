package com.pybeta.huixiang.api;

public interface ApiContants {

	// huixiang
	String BASE_URL = "http://huixiang.im/api/";
	String PIECE_URL_PATTERN = "http://huixiang.im/piece/%d";
	
	String PIECES_URL = BASE_URL + "pieces";
	String AUTHUSER_URL = BASE_URL + "authuser";
	String ADD_URL = BASE_URL + "add";
	String FAV_URL = BASE_URL + "fav";
	String UNFAV_URL = BASE_URL + "unfav";
	String FAV_LIST_URL = BASE_URL + "mine/favs";
	
	// weibo
	String APP_KEY = "2630274144";
	String DIRECT_URL = "http://huixiang.im/auth/weibo";
	String URL_OAUTH2_ACCESS_AUTHORIZE = "https://api.weibo.com/oauth2/authorize";
	String URL_POST_WEIBO = "https://api.weibo.com/2/statuses/update.json";

}
