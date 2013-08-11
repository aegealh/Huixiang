package com.pybeta.huixiang;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.pybeta.huixiang.api.HuixiangApi;
import com.pybeta.huixiang.model.Account;
import com.pybeta.huixiang.utils.Contants;
import com.pybeta.huixiang.utils.FileUtils;

public class HuixiangApp extends Application {
	private static HuixiangApp sInstance;

	public static HuixiangApp get() {
		return sInstance;
	}

	private Account mAccount;
	private HuixiangApi mApi;

	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;

		loadAccountInfo();
		
		RequestQueue queue = Volley.newRequestQueue(this);
		mApi = new HuixiangApi(queue);
	}

	public HuixiangApi getApi() {
		return mApi;
	}
	
	public Account getAccount() {
		if (mAccount == null) {
			loadAccountInfo();
		}
		return mAccount;
	}

	public void setAccount(Account Account) {
		this.mAccount = Account;
		if (mAccount != null) {
			FileUtils.saveInternalFile(getApplicationContext(), new Gson().toJson(mAccount), Contants.ACCOUNT_FILE_NAME);
		} else {
			FileUtils.saveInternalFile(getApplicationContext(), "", Contants.ACCOUNT_FILE_NAME);
		}
	}
	
	private void loadAccountInfo() {
		String jsonString = FileUtils.loadInternalFile(getApplicationContext(), Contants.ACCOUNT_FILE_NAME);
		if (!TextUtils.isEmpty(jsonString)) {
			mAccount = new Gson().fromJson(jsonString, Account.class);
		}
	}
}
