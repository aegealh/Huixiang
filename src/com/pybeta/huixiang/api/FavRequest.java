package com.pybeta.huixiang.api;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class FavRequest extends BaseRequest<Object> {

	private long pieceid;

	public FavRequest(Listener<Object> listener, ErrorListener errListener) {
		super(Method.POST, ApiContants.FAV_URL, listener, errListener);
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		Map<String, String> params = new HashMap<String, String>();
		params.put("pieceid", String.valueOf(pieceid));
		return params;
	}

	public long getPieceid() {
		return pieceid;
	}

	public void setPieceid(long pieceid) {
		this.pieceid = pieceid;
	}

	@Override
	protected Object parseNetworkResponseDelegate(String jsonString) {
		return null;
	}
}
