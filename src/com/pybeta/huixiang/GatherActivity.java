package com.pybeta.huixiang;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.pybeta.huixiang.utils.ProgressDialogFragment;
import com.pybeta.huixiang.utils.Utility;
import com.pybeta.huixiang.weibo.WeiboActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class GatherActivity extends ActionBarActivity implements OnClickListener {

	private static final int REQUEST_CODE_GATHER = 1;
	
	private EditText mGatherText;
	private Button mGatherAction;
	private ImageView mShareWeibo;
	private ImageView mShareDouban;
	
	private boolean mIsShareWeibo;

	private ProgressDialogFragment mProgressDlg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gather);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mGatherText = (EditText) findViewById(R.id.gather_piece);
		mGatherAction = (Button) findViewById(R.id.gather_action);
		mShareWeibo = (ImageView) findViewById(R.id.share_widget_weibo);
		mShareDouban = (ImageView) findViewById(R.id.share_widget_douban);

		mIsShareWeibo = false;
		
		mGatherAction.setOnClickListener(this);
		mShareDouban.setOnClickListener(this);
		mShareWeibo.setOnClickListener(this);

		Intent intent = getIntent();
		if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND)) {
			String content = intent.getStringExtra(Intent.EXTRA_TEXT);
			if (!TextUtils.isEmpty(content)) {
				mGatherText.setText(content);
				mGatherText.setSelection(content.length());
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gather_action: {
			gather();
			break;
		}
		case R.id.share_widget_douban: {
			break;
		}
		case R.id.share_widget_weibo: {
			toggleWeibo();
			break;
		}
		default:
			break;
		}
	}

	private void toggleWeibo() {
		if (mIsShareWeibo) {
			mShareWeibo.setImageResource(R.drawable.weibo);
			mIsShareWeibo = false;
		} else {
			mShareWeibo.setImageResource(R.drawable.weibo_nor);
			mIsShareWeibo = true;
		}
	}

	private void gather() {
		if (Utility.isLogin()) {
			String share = mIsShareWeibo ? "weibo" : null;
			String content = mGatherText.getText().toString().trim();
			if (!TextUtils.isEmpty(content)) {
				mProgressDlg = ProgressDialogFragment.newInstance();
				mProgressDlg.setMessage(getString(R.string.gather_progress));
				mProgressDlg.setRequest(HuixiangApp.get().getApi().add(content, null, share, new GatherListener(), new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (mProgressDlg != null && mProgressDlg.isAdded()) {
							mProgressDlg.dismiss();
						}
						Toast.makeText(GatherActivity.this, R.string.gather_failed, Toast.LENGTH_LONG).show();
					}
				}));
				mProgressDlg.show(getSupportFragmentManager(), "gather");
			}
		} else {
			Intent intent = new Intent(this, WeiboActivity.class);
			startActivityForResult(intent, REQUEST_CODE_GATHER);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_GATHER && resultCode == Activity.RESULT_OK) {
			gather();
		}
	}
	
	class GatherListener implements Listener<Object> {
		@Override
		public void onResponse(Object response) {
			if (mProgressDlg != null && mProgressDlg.isAdded()) {
				mProgressDlg.dismiss();
			}
			Toast.makeText(GatherActivity.this, R.string.gather_success, Toast.LENGTH_LONG).show();
			
			finish();
		}
	}
}
