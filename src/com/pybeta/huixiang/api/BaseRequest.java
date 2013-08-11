package com.pybeta.huixiang.api;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.pybeta.huixiang.BuildConfig;
import com.pybeta.huixiang.HuixiangApp;
import com.pybeta.huixiang.model.Account;

public abstract class BaseRequest<T> extends Request<T> {

	private final Listener<T> mListener;

	public BaseRequest(int method, String url, Listener<T> listener, ErrorListener errListener) {
		super(method, url, errListener);
		if (BuildConfig.DEBUG) System.out.println("url: " + url);
		mListener = listener;
	}
	
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Account account = HuixiangApp.get().getAccount();
		if (account != null && !TextUtils.isEmpty(account.getClient_hash())) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("Cookie", "cu=" + account.getClient_hash());
			return params;
		} else {
			return super.getHeaders();
		}
	}
	
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			if (BuildConfig.DEBUG) System.out.println("response: " + jsonString);	
			T result = parseNetworkResponseDelegate(jsonString);
			return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}
	
	protected abstract T parseNetworkResponseDelegate(String jsonString);

	@Override
	protected void deliverResponse(T response) {
		if (mListener != null) {
			mListener.onResponse(response);
		}
	}

}