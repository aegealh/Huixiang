package com.pybeta.huixiang.api;

import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.pybeta.huixiang.model.Account;
import com.pybeta.huixiang.model.Piece;

public class HuixiangApi {

	private final RequestQueue mQueue;

	public HuixiangApi(RequestQueue queue) {
		mQueue = queue;
	}
	
	public void cancelAllRequest() {
		mQueue.cancelAll(new RequestFilter() {
			@Override
			public boolean apply(Request<?> request) {
				return true;
			}
		});
	}
	
	public Request<?> getPieces(Listener<List<Piece>> listener, ErrorListener errorListener) {
		return mQueue.add(new PiecesRequest(listener, errorListener));
	}
	
	public Request<?> authuser(String access_token, Listener<Account> listener, ErrorListener errorListener) {
		return mQueue.add(new AuthuserRequest(access_token, listener, errorListener));
	}

	public Request<?> add(String content, String link, String share, Listener<Object> listener, ErrorListener errorListener) {
		AddRequest request = new AddRequest(listener, errorListener);
		request.setContent(content);
		request.setLink(link);
		request.setShare(share);
		return mQueue.add(request);
	}

	public Request<?> fav(long id, Listener<Object> listener, ErrorListener errorListener) {
		FavRequest request = new FavRequest(listener, errorListener);
		request.setPieceid(id);
		return mQueue.add(request);
	}
	
	public Request<?> unfav(long id, Listener<Object> listener, ErrorListener errorListener) {
		UnfavRequest request = new UnfavRequest(listener, errorListener);
		request.setPieceid(id);
		return mQueue.add(request);
	}
	
	public Request<?> favList(long page, Listener<List<Piece>> listener, ErrorListener errorListener) {
		FavListRequest request = new FavListRequest(page, listener, errorListener);
		request.setPage(page);
		return mQueue.add(request);
	}
	
	public Request<?> postWeibo(long id, String status, Listener<Object> listener, ErrorListener errorListener) {
		WeiboPostRequest request = new WeiboPostRequest(listener, errorListener);
		request.setStatus(status);
		request.setPieceid(id);
		return mQueue.add(request);
	}
}
