package com.pybeta.huixiang;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.pybeta.huixiang.utils.Utility;
import com.pybeta.huixiang.weibo.WeiboActivity;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab tab = actionBar.newTab();
		tab.setText(R.string.title_section1);
		tab.setTabListener(new TabListener<HomeFragment>(this, getString(R.string.title_section1), HomeFragment.class));
		actionBar.addTab(tab);

		tab = actionBar.newTab();
		tab.setText(R.string.title_section2);
		tab.setTabListener(new TabListener<FavoriteFragment>(this, getString(R.string.title_section2), FavoriteFragment.class));
		actionBar.addTab(tab);

		tab = actionBar.newTab();
		tab.setText(R.string.title_section3);
		tab.setTabListener(new TabListener<SettingsFragment>(this, getString(R.string.title_section3), SettingsFragment.class));
		actionBar.addTab(tab);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		HuixiangApp.get().getApi().cancelAllRequest();
	}
	
	public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		public TabListener(Activity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (mFragment == null) {
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				ft.attach(mFragment);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				ft.detach(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void gather() {
		if (Utility.isLogin()) {
			Intent intent = new Intent(this, GatherActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, WeiboActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_gather:
			gather();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
