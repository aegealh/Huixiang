package com.pybeta.huixiang.api;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pybeta.huixiang.model.Piece;

public class PiecesRequest extends BaseRequest<List<Piece>> {

	public PiecesRequest(Listener<List<Piece>> listener, ErrorListener errListener) {
		super(Method.GET, ApiContants.PIECES_URL, listener, errListener);
	}

	@Override
	protected List<Piece> parseNetworkResponseDelegate(String jsonString) {
		return new Gson().fromJson(jsonString, new TypeToken<ArrayList<Piece>>(){}.getType());
	}

}
