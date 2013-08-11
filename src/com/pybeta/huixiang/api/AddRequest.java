package com.pybeta.huixiang.api;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class AddRequest extends BaseRequest<Object> {

	public static final String SHARE_WEIBO = "weibo";
	public static final String SHARE_DOUBAN = "douban";
	
	private String content;
	private long id;
	private String link;
	private String share;
	
	public AddRequest(Listener<Object> listener, ErrorListener errListener) {
		super(Method.POST, ApiContants.ADD_URL, listener, errListener);
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		Map<String, String> params = new HashMap<String, String>();

		if (!TextUtils.isEmpty(content)) {
			params.put("content", content);
		}
		if (!TextUtils.isEmpty(link)) {
			params.put("link", link);
		}
		if (!TextUtils.isEmpty(share)) {
			params.put("share", share);
		}
		
		return params;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getShare() {
		return share;
	}

	public void setShare(String share) {
		this.share = share;
	}

	@Override
	protected Object parseNetworkResponseDelegate(String jsonString) {
		return null;
	}

}
