package com.pybeta.huixiang.api;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.pybeta.huixiang.HuixiangApp;
import com.pybeta.huixiang.model.Account;

public class WeiboPostRequest extends BaseRequest<Object> {

	private String status;
	private long pieceid;

	public WeiboPostRequest(Listener<Object> listener, ErrorListener errListener) {
		super(Method.POST, ApiContants.URL_POST_WEIBO, listener, errListener);
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		Map<String, String> params = new HashMap<String, String>();
		params.put("status", status + "\t" + String.format(ApiContants.PIECE_URL_PATTERN, pieceid));
		Account account = HuixiangApp.get().getAccount();
		if (account != null && !TextUtils.isEmpty(account.getWeibo_access_token())) {
			params.put("access_token", account.getWeibo_access_token());
		}
		return params;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
