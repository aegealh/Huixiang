package com.pybeta.huixiang.weibo;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.pybeta.huixiang.HuixiangApp;
import com.pybeta.huixiang.R;
import com.pybeta.huixiang.api.ApiContants;
import com.pybeta.huixiang.model.Account;
import com.pybeta.huixiang.utils.ProgressDialogFragment;
import com.pybeta.huixiang.utils.Utility;

public class WeiboActivity extends ActionBarActivity {

	private WebView mWebView;
	private MenuItem mRefreshItem;
	
	private ProgressDialogFragment mProgressDlg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weibo);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		setResult(RESULT_CANCELED);
		
		mWebView = (WebView) findViewById(R.id.oauth_web_view);
		mWebView.setWebViewClient(new WeiboWebViewClient());

		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSaveFormData(false);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}
	
	private void completeRefresh() {
		if (mRefreshItem.getActionView() != null) {
			mRefreshItem.getActionView().clearAnimation();
			mRefreshItem.setActionView(null);
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWebView.clearCache(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			mWebView.stopLoading();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (mWebView.canGoBack()) {
			mWebView.goBack();
		} else {
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		mRefreshItem = menu.findItem(R.id.action_refresh);
		refresh();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_refresh:
			refresh();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void refresh() {
		mWebView.loadUrl("about:blank");

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);

		Animation rotation = AnimationUtils.loadAnimation(this, R.anim.refresh);
		iv.startAnimation(rotation);

		mRefreshItem.setActionView(iv);
		mWebView.loadUrl(getWeiboOAuthUrl());
	}

	private String getWeiboOAuthUrl() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("client_id", ApiContants.APP_KEY);
		parameters.put("response_type", "token");
		parameters.put("redirect_uri", ApiContants.DIRECT_URL);
		parameters.put("display", "mobile");
		return ApiContants.URL_OAUTH2_ACCESS_AUTHORIZE + "?" + Utility.encodeUrl(parameters);
	}

	private void handleRedirectUrl(WebView view, String url) {
		Bundle values = Utility.parseUrl(url);

		String error = values.getString("error");
		String error_code = values.getString("error_code");

		Intent intent = new Intent();
		intent.putExtras(values);

		if (error == null && error_code == null) {
			final String access_token = values.getString("access_token");
			setResult(RESULT_OK, intent);
			
			mProgressDlg = ProgressDialogFragment.newInstance();
			mProgressDlg.setMessage(getString(R.string.auth_progress));
			mProgressDlg.setRequest(HuixiangApp.get().getApi().authuser(access_token, new AuthuserListener(), new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					if (mProgressDlg != null && mProgressDlg.isAdded()) {
						mProgressDlg.dismiss();
					}
					Toast.makeText(getApplicationContext(), R.string.auth_failed, Toast.LENGTH_LONG).show();
				}
			}));
			mProgressDlg.show(getSupportFragmentManager(), "authuser");
		} else {
			finish();
		}
	}
	
	private class AuthuserListener implements Listener<Account> {

		@Override
		public void onResponse(Account response) {
			if (mProgressDlg != null && mProgressDlg.isAdded()) {
				mProgressDlg.dismiss();
			}
			HuixiangApp.get().setAccount(response);
			
			Toast.makeText(getApplicationContext(), R.string.auth_success, Toast.LENGTH_LONG).show();
			
			finish();
		}

	}
	
	private class WeiboWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (url.startsWith(ApiContants.DIRECT_URL)) {
				handleRedirectUrl(view, url);
				view.stopLoading();
				return;
			}
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if (!url.equals("about:blank")) {
				completeRefresh();
			}
		}
	}
}
