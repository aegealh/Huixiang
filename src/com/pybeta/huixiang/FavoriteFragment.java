package com.pybeta.huixiang;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.devspark.progressfragment.ProgressFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.pybeta.huixiang.model.Piece;
import com.pybeta.huixiang.utils.ProgressDialogFragment;
import com.pybeta.huixiang.utils.Utility;

public class FavoriteFragment extends ProgressFragment implements Listener<List<Piece>>, ErrorListener, OnRefreshListener2<ListView>, OnItemClickListener {

	private View mContentView;

	private FavPieceAdapter mFavPieceAdapter;
	private PullToRefreshListView mPullRefreshListView;

	private long mCurrentPage = 1;

	private Piece mSelectedPiece;
	private List<Piece> mPieces;

	private ProgressDialogFragment mProgressDlg;
	
	public static FavoriteFragment newInstance() {
		FavoriteFragment fragment = new FavoriteFragment();
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setContentView(mContentView);
		setEmptyText(R.string.fav_empty);

		if (mPieces == null || mPieces.size() == 0) {
			setContentShown(false);
			HuixiangApp.get().getApi().favList(mCurrentPage, this, this);
		} else {
			setContentShown(true);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fav_view_content, null);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mPullRefreshListView = (PullToRefreshListView) mContentView.findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setMode(Mode.BOTH);
		mPullRefreshListView.setOnRefreshListener(this);

		mFavPieceAdapter = new FavPieceAdapter(getActivity(), mPieces);
		mPullRefreshListView.setAdapter(mFavPieceAdapter);

		mPullRefreshListView.getRefreshableView().setSelector(android.R.color.transparent);
		mPullRefreshListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final Piece piece = mFavPieceAdapter.getItem(position - 1);
		
		if (piece != null) {
			mSelectedPiece = piece;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.app_name);
			builder.setItems(R.array.fav_action_list, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0: {
						unfav(piece);
						break;
					}
					case 1: {
						postWeibo(piece);
						break;
					}
					case 2: {
						Utility.share(getActivity(), piece.getContent());
						break;
					}
					default:
						break;
					}
				}
			});
			builder.create().show();
		}
	}

	protected void postWeibo(Piece piece) {
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

	protected void unfav(Piece piece) {
		if (piece != null) {
			mProgressDlg = ProgressDialogFragment.newInstance();
			mProgressDlg.setMessage(getString(R.string.unfav_progress));
			mProgressDlg.setRequest(HuixiangApp.get().getApi().unfav(piece.getId(), new UnFavListener(), new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					if (mProgressDlg != null && mProgressDlg.isAdded()) {
						mProgressDlg.dismiss();
					}
					Toast.makeText(getActivity(), R.string.unfav_failed, Toast.LENGTH_LONG).show();
				}
			}));
			mProgressDlg.show(getFragmentManager(), "unfav");
		}
	}
	
	class UnFavListener implements Listener<Object> {
		@Override
		public void onResponse(Object response) {
			if (mProgressDlg != null && mProgressDlg.isAdded()) {
				mProgressDlg.dismiss();
			}
			Toast.makeText(getActivity(), R.string.unfav_success, Toast.LENGTH_LONG).show();
			mFavPieceAdapter.removePiece(mSelectedPiece);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		mCurrentPage = 1;
		HuixiangApp.get().getApi().favList(mCurrentPage, this, this);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		mCurrentPage = mCurrentPage + 1;
		HuixiangApp.get().getApi().favList(mCurrentPage, this, this);
	}

	@Override
	public void onErrorResponse(VolleyError e) {
		setContentShown(true);
		
		if (mCurrentPage == 1) {
			setContentEmpty(true);
		}

		mPullRefreshListView.onRefreshComplete();
	}

	@Override
	public void onResponse(List<Piece> response) {
		setContentShown(true);

		mPullRefreshListView.onRefreshComplete();

		if (response != null && response.size() < 10) {
			mPullRefreshListView.setMode(Mode.PULL_FROM_START);
		} else {
			mPullRefreshListView.setMode(Mode.BOTH);
		}

		if (mCurrentPage == 1) {
			mPieces = response;

			if (response == null || response.size() == 0) {
				setContentEmpty(true);
			} else {
				setContentEmpty(false);
			}
		} else {
			mPieces.addAll(response);
		}
		mFavPieceAdapter.setPieces(mPieces);
	}

	public class FavPieceAdapter extends BaseAdapter {

		private LayoutInflater mLayoutInflater;
		private List<Piece> mPieces;

		public FavPieceAdapter(Context context, List<Piece> pieces) {
			mLayoutInflater = LayoutInflater.from(context);
			mPieces = pieces;
		}

		public void removePiece(Piece piece) {
			if (mPieces != null && piece != null) {
				for (Piece p : mPieces) {
					if (p.getId() == piece.getId()) {
						mPieces.remove(p);
						notifyDataSetChanged();
						return;
					}
				}
			}
		}

		public void setPieces(List<Piece> pieces) {
			mPieces = pieces;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mPieces == null ? 0 : mPieces.size();
		}

		@Override
		public Piece getItem(int position) {
			return mPieces == null ? null : mPieces.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return createViewFromResource(position, convertView, parent, R.layout.fav_piece_item);
		}

		private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
			View view;
			ViewHolder viewHolder = null;
			if (convertView == null) {
				view = mLayoutInflater.inflate(resource, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.piece = (TextView) view.findViewById(R.id.huixiang_piece);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}
			bindView(position, viewHolder);
			return view;
		}

		private void bindView(int position, ViewHolder viewHolder) {
			Piece piece = getItem(position);
			viewHolder.piece.setText(piece.getContent());
		}
	}

	public class ViewHolder {
		TextView piece;
	}

}
