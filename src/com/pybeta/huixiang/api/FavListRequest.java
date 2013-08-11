package com.pybeta.huixiang.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pybeta.huixiang.model.Piece;

public class FavListRequest extends BaseRequest<List<Piece>> {

	private long page;
	private long per = 10;

	public FavListRequest(long page, Listener<List<Piece>> listener, ErrorListener errListener) {
		super(Method.GET, String.format("%s?per=10&page=%d", ApiContants.FAV_LIST_URL, page), listener, errListener);
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		Map<String, String> params = new HashMap<String, String>();
		params.put("page", String.valueOf(page));
		params.put("per", String.valueOf(per));
		return params;
	}

	public long getPage() {
		return page;
	}

	public void setPage(long page) {
		this.page = page;
	}

	public long getPer() {
		return per;
	}

	public void setPer(long per) {
		this.per = per;
	}

	@Override
	protected List<Piece> parseNetworkResponseDelegate(String jsonString) {
		return new Gson().fromJson(jsonString, new TypeToken<ArrayList<Piece>>(){}.getType());
	}

}
