package com.pybeta.huixiang.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;

import com.pybeta.huixiang.HuixiangApp;
import com.pybeta.huixiang.R;
import com.pybeta.huixiang.model.Account;

public class Utility {

	private Utility() {
	}

	public static boolean isLogin() {
		Account account = HuixiangApp.get().getAccount();
		if (account != null && !TextUtils.isEmpty(account.getClient_hash())) {
			return true;
		} else {
			return false;
		}
	}

	public static void share(Context ctx, String content) {
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, content);
		ctx.startActivity(Intent.createChooser(intent, ctx.getString(R.string.share_intent_title)));
	}

	public static String encodeUrl(Map<String, String> param) {
		if (param == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		Set<String> keys = param.keySet();
		boolean first = true;

		for (String key : keys) {
			String value = param.get(key);
			if (!TextUtils.isEmpty(value)) {
				if (first)
					first = false;
				else
					sb.append("&");
				try {
					sb.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(param.get(key), "UTF-8"));
				} catch (UnsupportedEncodingException e) {

				}
			}

		}

		return sb.toString();
	}

	public static Bundle decodeUrl(String s) {
		Bundle params = new Bundle();
		if (s != null) {
			String array[] = s.split("&");
			for (String parameter : array) {
				String v[] = parameter.split("=");
				try {
					params.putString(URLDecoder.decode(v[0], "UTF-8"), URLDecoder.decode(v[1], "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();

				}
			}
		}
		return params;
	}

	public static void closeSilently(Closeable closeable) {
		if (closeable != null)
			try {
				closeable.close();
			} catch (IOException ignored) {

			}
	}

	/**
	 * Parse a URL query and fragment parameters into a key-value bundle.
	 */
	public static Bundle parseUrl(String url) {
		url = url.replace("weiboconnect", "http");
		try {
			URL u = new URL(url);
			Bundle b = decodeUrl(u.getQuery());
			b.putAll(decodeUrl(u.getRef()));
			return b;
		} catch (MalformedURLException e) {
			return new Bundle();
		}
	}

	public static int length(String paramString) {
		int i = 0;
		for (int j = 0; j < paramString.length(); j++) {
			if (paramString.substring(j, j + 1).matches("[Α-￥]"))
				i += 2;
			else
				i++;
		}

		if (i % 2 > 0) {
			i = 1 + i / 2;
		} else {
			i = i / 2;
		}

		return i;
	}

	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();

		return networkInfo != null && networkInfo.isConnected();
	}

	public static boolean isWifi(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				return true;
			}
		}
		return false;
	}

	public static int getNetType(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return networkInfo.getType();
		}
		return -1;
	}
}
