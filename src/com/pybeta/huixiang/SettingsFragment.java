package com.pybeta.huixiang;

import com.pybeta.huixiang.model.Account;
import com.pybeta.huixiang.utils.Utility;
import com.pybeta.huixiang.weibo.WeiboActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingsFragment extends Fragment implements OnClickListener {

	private static final int REQUEST_CODE_OAUTH = 1;

	private TextView mAccount;

	public static SettingsFragment newInstance() {
		SettingsFragment fragment = new SettingsFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_settings, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mAccount = (TextView) view.findViewById(R.id.settings_name);
		updateAccount();

		view.findViewById(R.id.settings_account).setOnClickListener(this);
		view.findViewById(R.id.settings_feedback).setOnClickListener(this);
		view.findViewById(R.id.settings_rate).setOnClickListener(this);
		view.findViewById(R.id.settings_about).setOnClickListener(this);
	}

	private void updateAccount() {
		Account account = HuixiangApp.get().getAccount();
		if (Utility.isLogin() && account != null && !TextUtils.isEmpty(account.getName())) {
			mAccount.setText(account.getName());
		} else {
			mAccount.setText(R.string.settings_oauth);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_OAUTH && resultCode == Activity.RESULT_OK) {
			updateAccount();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.settings_account: {
			if (!Utility.isLogin()) {
				Intent intent = new Intent(getActivity(), WeiboActivity.class);
				startActivityForResult(intent, REQUEST_CODE_OAUTH);
			} else {
				HuixiangApp.get().setAccount(null);
			}
			updateAccount();
			break;
		}
		case R.id.settings_feedback: {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL, new String[] { "marvinlix@gmail.com" });
			i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.about_huixiang));
			startActivity(Intent.createChooser(i, getString(R.string.feedback_intent_title)));
			break;
		}
		case R.id.settings_rate: {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id=com.pybeta.huixiang"));
			startActivity(intent);
			break;
		}
		case R.id.settings_about: {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("http://huixiang.im/"));
			startActivity(intent);
			break;
		}
		default:
			break;
		}
	}

}
