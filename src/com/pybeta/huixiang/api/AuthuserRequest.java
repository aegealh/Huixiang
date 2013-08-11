package com.pybeta.huixiang.api;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.pybeta.huixiang.model.Account;

public class AuthuserRequest extends BaseRequest<Account>{
	
	private String access_token;
	
	public AuthuserRequest(String access_token, Listener<Account> listener, ErrorListener errListener) {
		super(Method.POST, ApiContants.AUTHUSER_URL, listener, errListener);
		this.access_token = access_token;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", access_token);
		params.put("name", "weibo");
		return params;
	}
	
	@Override
	protected Account parseNetworkResponseDelegate(String jsonString) {
		return new Gson().fromJson(jsonString, Account.class);
	}
	
}
