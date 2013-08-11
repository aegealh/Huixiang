package com.pybeta.huixiang;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.devspark.progressfragment.ProgressFragment;
import com.pybeta.huixiang.model.Piece;
import com.pybeta.huixiang.utils.AnimationUtil;
import com.pybeta.huixiang.utils.ProgressDialogFragment;
import com.pybeta.huixiang.utils.Utility;
import com.pybeta.huixiang.weibo.WeiboActivity;

public class HomeFragment extends ProgressFragment implements Listener<List<Piece>>, ErrorListener, OnClickListener, OnTouchListener {

	private static final int REQUEST_CODE_FAV = 1;
	private static final int REQUEST_CODE_POST_WEIBO = 2;
	
	private View mContentView;
	private View mShareView;
	private TextView mPieceView;

	private List<Piece> mPieces;
	private int mCurrentIndex = 0;
	private Request<?> mInFlightRequest;

	private final int mTouchSlop;
	private boolean mIsBeingDragged, mIsHandlingTouchEvent;
	private float mInitialMotionY, mLastMotionY;

	private MotionEvent downEvent;
	
	private ProgressDialogFragment mProgressDlg;

	public static HomeFragment newInstance() {
		HomeFragment fragment = new HomeFragment();
		return fragment;
	}

	public HomeFragment() {
		mTouchSlop = ViewConfiguration.get(HuixiangApp.get().getApplicationContext()).getScaledTouchSlop();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setContentView(mContentView);
		setEmptyText(R.string.empty);

		if (mPieces == null || mPieces.size() == 0) {
			setContentShown(false);
			mInFlightRequest = HuixiangApp.get().getApi().getPieces(this, this);
		} else {
			setContentShown(true);
			updatePiece();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.home_view_content, null);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mPieceView = (TextView) mContentView.findViewById(R.id.huixiang_piece);
		mShareView = mContentView.findViewById(R.id.share_widget_view);
		mContentView.setOnTouchListener(this);

		mContentView.findViewById(R.id.share_widget_fav).setOnClickListener(this);
		mContentView.findViewById(R.id.share_widget_weibo).setOnClickListener(this);
		mContentView.findViewById(R.id.share_widget_weixin).setOnClickListener(this);
	}

	@Override
	public void onResponse(List<Piece> response) {
		setContentShown(true);

		if (mPieces == null) {
			mPieces = response;
		} else {
			synchronized (mPieces) {
				mPieces.addAll(response);
			}
		}
		updatePiece();

		mInFlightRequest = null;
	}

	private void updatePiece() {
		Piece piece = null;
		synchronized (mPieces) {
			if (mCurrentIndex > mPieces.size() - 10 && mInFlightRequest == null) {
				mInFlightRequest = HuixiangApp.get().getApi().getPieces(this, this);
			}

			if (mCurrentIndex < mPieces.size()) {
				piece = mPieces.get(mCurrentIndex);
				mPieceView.setText(piece.getContent());

				AnimationUtil.fadeInItem(mPieceView, 1000, 0, View.VISIBLE, null);
			}
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		setContentShown(true);
		setContentEmpty(true);
		mInFlightRequest = null;
	}

	private void showNextPiece() {
		mCurrentIndex++;
		updatePiece();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.share_widget_fav: {
			addFav();
			break;
		}
		case R.id.share_widget_weibo: {
			postWeibo();
			break;
		}
		case R.id.share_widget_weixin: {
			break;
		}
		default:
			break;
		}
		
		toggleShareWidget();
	}

	private void postWeibo() {
		if (Utility.isLogin()) {
			postWeiboReal();
		} else {
			Intent intent = new Intent(getActivity(), WeiboActivity.class);
			startActivityForResult(intent, REQUEST_CODE_POST_WEIBO);
		}
	}

	private void postWeiboReal() {
		Piece piece = null;
		if (mPieces != null && mCurrentIndex < mPieces.size()) {
			piece = mPieces.get(mCurrentIndex);
		}

		if (piece != null) {
			mProgressDlg = ProgressDialogFragment.newInstance();
			mProgressDlg.setMessage(getString(R.string.post_weibo_progress));
			mProgressDlg.setRequest(HuixiangApp.get().getApi().postWeibo(piece.getId(), piece.getContent(), new Listener<Object>() {
				@Override
				public void onResponse(Object response) {
					if (mProgressDlg != null && mProgressDlg.isAdded()) {
						mProgressDlg.dismiss();
					}
					Toast.makeText(getActivity(), R.string.post_weibo_success, Toast.LENGTH_LONG).show();
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					if (mProgressDlg != null && mProgressDlg.isAdded()) {
						mProgressDlg.dismiss();
					}
					Toast.makeText(getActivity(), R.string.post_weibo_failed, Toast.LENGTH_LONG).show();
				}
			}));
			mProgressDlg.show(getFragmentManager(), "post_weibo");
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_FAV && resultCode == Activity.RESULT_OK) {
			addFav();
		} else if (requestCode == REQUEST_CODE_POST_WEIBO && resultCode == Activity.RESULT_OK) {
			postWeibo();
		}
	}
	
	private void addFav() {
		if (Utility.isLogin()) {
			addFavReal();
		} else {
			Intent intent = new Intent(getActivity(), WeiboActivity.class);
			startActivityForResult(intent, REQUEST_CODE_FAV);
		}
	}

	private void addFavReal() {
		Piece piece = null;
		if (mPieces != null && mCurrentIndex < mPieces.size()) {
			piece = mPieces.get(mCurrentIndex);
		}

		if (piece != null) {
			mProgressDlg = ProgressDialogFragment.newInstance();
			mProgressDlg.setMessage(getString(R.string.fav_progress));
			mProgressDlg.setRequest(HuixiangApp.get().getApi().fav(piece.getId(), new AddFavListener(), new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					if (mProgressDlg != null && mProgressDlg.isAdded()) {
						mProgressDlg.dismiss();
					}
					Toast.makeText(getActivity(), R.string.fav_failed, Toast.LENGTH_LONG).show();
				}
			}));
			mProgressDlg.show(getFragmentManager(), "fav");
		}
	}

	private class AddFavListener implements Listener<Object> {
		@Override
		public void onResponse(Object response) {
			if (mProgressDlg != null && mProgressDlg.isAdded()) {
				mProgressDlg.dismiss();
			}
			Toast.makeText(getActivity(), R.string.fav_success, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {

		if (!mIsHandlingTouchEvent && onInterceptTouchEvent(view, event)) {
			mIsHandlingTouchEvent = true;
		}

		if (mIsHandlingTouchEvent) {
			onTouchEvent(view, event);
		}

		return true;
	}

	private boolean onInterceptTouchEvent(View view, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE: {
			if (!mIsBeingDragged && mInitialMotionY > 0f) {
				final float y = event.getY();
				final float yDiff = y - mInitialMotionY;

				if (yDiff > mTouchSlop) {
					mIsBeingDragged = true;
				} else if (yDiff < -mTouchSlop) {
					resetTouch();
				}
			}
			break;
		}
		case MotionEvent.ACTION_DOWN: {
			downEvent = MotionEvent.obtain(event);
			mInitialMotionY = event.getY();
			break;
		}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: {
			if (downEvent != null) {
				if (event.getEventTime() - downEvent.getEventTime() < 1000 && Math.abs(event.getX() - downEvent.getX()) <= 10
						&& Math.abs(event.getY() - downEvent.getY()) <= 10) {
					toggleShareWidget();
				}
			}
			resetTouch();
			break;
		}
		}
		return mIsBeingDragged;
	}

	private void toggleShareWidget() {
		if (mShareView.getVisibility() == View.VISIBLE) {
			AnimationUtil.fadeItem(mShareView, 500, 0, View.GONE, null);
		} else {
			AnimationUtil.fadeInItem(mShareView, 800, 0, View.VISIBLE, null);
		}
	}

	private boolean onTouchEvent(View view, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE: {
			final float y = event.getY();
			if (mIsBeingDragged) {
				final float yDx = y - mLastMotionY;
				if (yDx >= -mTouchSlop) {
					if (yDx > 0f) {
						mLastMotionY = y;
					}
				} else {
					onPullEnded();
					resetTouch();
				}
			}
			break;
		}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: {
			if (mIsBeingDragged) {
				onPullEnded();
			}
			resetTouch();
			break;
		}
		}
		return true;
	}

	private void onPullEnded() {
		showNextPiece();
	}

	private void resetTouch() {
		mIsBeingDragged = false;
		mIsHandlingTouchEvent = false;
		mInitialMotionY = mLastMotionY = -1f;
	}

}
