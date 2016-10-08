package com.moemoe.lalala.fragment;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.Callback;
import com.app.common.util.DensityUtil;
import com.app.ex.DbException;
import com.app.http.request.UriRequest;
import com.app.image.ImageOptions;
import com.app.view.DbManager;
import com.squareup.picasso.Picasso;
import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.MoemoeApplication;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.ReplyBean;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.recycler.PullAndLoadView;
import com.moemoe.lalala.view.recycler.PullCallback;
;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Haru
 * @version 创建时间：2015年10月29日 上午11:51:51
 */
@SuppressLint("ValidFragment")
@ContentView(R.layout.ac_one_pulltorefresh_list)
public class MyCommentFragment extends BaseFragment {


	@FindView(R.id.list)
	private PullAndLoadView mListDocs;

	private RecyclerView mRecyclerView;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	private CommentListAdapter mDocAdapter;
	private ArrayList<ReplyBean> mReplyBean = new ArrayList<>();
	private boolean mIsHasLoadedAll;
	private boolean mIsLoading;
	private DbManager db;

	public MyCommentFragment() {
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		db = Utils.getDb(MoemoeApplication.sDaoConfig);
		mSwipeRefreshLayout = mListDocs.getSwipeRefreshLayout();
		mSwipeRefreshLayout.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
//		mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
//				.getDisplayMetrics()));
		mRecyclerView = mListDocs.getRecyclerView();
		mDocAdapter = new CommentListAdapter();
		mRecyclerView.setAdapter(mDocAdapter);
		mDocAdapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				ReplyBean bean = mDocAdapter.getItem(position);
				if (bean != null) {
					Uri uri = Uri.parse(bean.schema);
					IntentUtils.toActivityFromUri(getActivity(), uri,view);
				}
			}

			@Override
			public void onItemLongClick(View view, int position) {

			}
		});

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
		mListDocs.setLayoutManager(linearLayoutManager);
		mListDocs.isLoadMoreEnabled(true);
		mListDocs.setPullCallback(new PullCallback() {
			@Override
			public void onLoadMore() {
				new UpdateTask(false).execute();
			}

			@Override
			public void onRefresh() {
				new UpdateTask(true).execute();
				mIsHasLoadedAll = false;
			}

			@Override
			public boolean isLoading() {
				return mIsLoading;
			}

			@Override
			public boolean hasLoadedAllItems() {
				return mIsHasLoadedAll;
			}
		});
		loadDataFromDb();
		mListDocs.initLoad();
	}

	private void loadDataFromDb(){
		try {
			ReplyBean docBean = db.selector(ReplyBean.class)
					.where("uuid","=","cache")
					.findFirst();
			if(docBean != null && docBean.json != null){
				ArrayList<ReplyBean> datas = ReplyBean.readFromJsonList(getActivity(),docBean.json);
				mReplyBean.addAll(datas);
				mDocAdapter.notifyDataSetChanged();
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	private void requestCommentList(final int index){
		Otaku.getAccountV2().requestCommentFromOther(PreferenceManager.getInstance(getActivity()).getToken(), index).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
			@Override
			public void success(String token, String s) {
				// TODO 没有去重
				ArrayList<ReplyBean> datas = ReplyBean.readFromJsonList(getActivity(),s);
				ReplyBean docBean = new ReplyBean();
				docBean.uuid = "cache";
				docBean.json = s;
				try {
					db.saveOrUpdate(docBean);
				} catch (DbException e) {
					e.printStackTrace();
				}

				if (index == 0) {
					mReplyBean.clear();
					mReplyBean.addAll(datas);
					mDocAdapter.notifyDataSetChanged();
				} else {
					int size = mReplyBean.size();
					mReplyBean.addAll(datas);
					int bfSize = mReplyBean.size();
					mDocAdapter.notifyItemRangeInserted(size, bfSize - size);
				}
				if (index != 0) {
					if (datas.size() >= Otaku.LENGTH) {
						mListDocs.isLoadMoreEnabled(true);
					} else {
						mListDocs.isLoadMoreEnabled(false);
						mIsHasLoadedAll = true;
					}
				} else {
					mListDocs.isLoadMoreEnabled(true);
					mIsHasLoadedAll = false;
				}
			}

			@Override
			public void failure(String e) {

			}
		}));
//		Otaku.getAccount().requestCommentFromOther(PreferenceManager.getInstance(getActivity()).getToken(), index, new Callback.InterceptCallback<String>() {
//			@Override
//			public void beforeRequest(UriRequest request) throws Throwable {
//
//			}
//
//			@Override
//			public void afterRequest(UriRequest request) throws Throwable {
//
//			}
//
//			@Override
//			public void onSuccess(String result) {
//				try {
//					JSONObject json = new JSONObject(result);
//					if (json.optInt("ok") == Otaku.SERVER_OK) {
//						// TODO 没有去重
//						ArrayList<ReplyBean> datas = ReplyBean.readFromJsonList(getActivity(),json.optString("data"));
//						ReplyBean docBean = new ReplyBean();
//						docBean.uuid = "cache";
//						docBean.json = json.optString("data");
//						db.saveOrUpdate(docBean);
//
//						if (index == 0) {
//							mReplyBean.clear();
//							mReplyBean.addAll(datas);
//							mDocAdapter.notifyDataSetChanged();
//						} else {
//							int size = mReplyBean.size();
//							mReplyBean.addAll(datas);
//							int bfSize = mReplyBean.size();
//							mDocAdapter.notifyItemRangeInserted(size, bfSize - size);
//						}
//						if (index != 0) {
//							if (datas.size() >= Otaku.LENGTH) {
//								mListDocs.isLoadMoreEnabled(true);
//							} else {
//								mListDocs.isLoadMoreEnabled(false);
//								mIsHasLoadedAll = true;
//							}
//						} else {
//							mListDocs.isLoadMoreEnabled(true);
//							mIsHasLoadedAll = false;
//						}
//					} else {
//						String err = json.optString("error_code");
//						if(TextUtils.isEmpty(err)){
//							err = json.optString("data");
//						}
//						if(!TextUtils.isEmpty(err) && err.contains("TOKEN")){
//							String uuid = PreferenceManager.getInstance(getActivity()).getUUid();
//							if(!TextUtils.isEmpty(uuid)){
//								((BaseActivity) getActivity()).tryLoginFirst(null);
//							}
//						}
//						//ToastUtil.showCenterToast(getActivity(), R.string.msg_server_connection);
//					}
//				} catch (Exception e) {
//
//				}
//			}
//
//			@Override
//			public void onError(Throwable ex, boolean isOnCallback) {
//				//ToastUtil.showCenterToast(getActivity(), R.string.msg_server_connection);
//			}
//
//			@Override
//			public void onCancelled(CancelledException cex) {
//
//			}
//
//			@Override
//			public void onFinished() {
//
//			}
//		},getActivity());
	}

	private class UpdateTask extends AsyncTask<Void,Void,Void> {

		private boolean mIsPullDown;

		public UpdateTask(boolean IsPullDown){
			this.mIsPullDown = IsPullDown;
		}


		@Override
		protected Void doInBackground(Void... params) {
			mIsLoading = true;
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			if (mIsPullDown) {
				requestCommentList(0);
				mListDocs.isLoadMoreEnabled(true);
			}else {
				requestCommentList(mDocAdapter.getItemCount());
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			mListDocs.setComplete();
			mIsLoading = false;
		}
	}

	public interface OnItemClickListener {
		void onItemClick(View view, int position);
		void onItemLongClick(View view, int position);
	}

	private class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.CommentViewHolder> {

		private LayoutInflater mInflater;
		private OnItemClickListener mOnItemClickListener;

		public CommentListAdapter() {
			mInflater = LayoutInflater.from(getActivity());
		}

		public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
			this.mOnItemClickListener = mOnItemClickListener;
		}

		@Override
		public CommentListAdapter.CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new CommentViewHolder(mInflater.inflate(R.layout.item_message_new, parent, false));
		}

		@Override
		public void onBindViewHolder(final CommentListAdapter.CommentViewHolder viewHolder, int position) {
			ReplyBean bean = getItem(position);
//			Utils.image().bind(viewHolder.ivAvatar,StringUtils.getUrl(getActivity(), bean.fromIcon.path,DensityUtil.dip2px(44), DensityUtil.dip2px(44),false,false), new ImageOptions.Builder()
//					.setSize(DensityUtil.dip2px(44), DensityUtil.dip2px(44))
//					.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//					.setLoadingDrawableId(R.drawable.ic_default_avatar_m)
//					.setFailureDrawableId(R.drawable.ic_default_avatar_m)
//					.build());
			Picasso.with(getActivity())
					.load(StringUtils.getUrl(getActivity(), bean.fromIcon.path,DensityUtil.dip2px(44), DensityUtil.dip2px(44),false,false))
					.resize(DensityUtil.dip2px(44), DensityUtil.dip2px(44))
					.placeholder(R.drawable.ic_default_avatar_m)
					.error(R.drawable.ic_default_avatar_m)
					.into(viewHolder.ivAvatar);
			viewHolder.tvName.setText(bean.fromName);
			viewHolder.tvDate.setText(bean.date);
			viewHolder.tvContent.setText(bean.content);
			viewHolder.itemView.setOnClickListener(new NoDoubleClickListener() {
				@Override
				public void onNoDoubleClick(View v) {
					int pos = viewHolder.getLayoutPosition();
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onItemClick(viewHolder.itemView, pos);
					}
				}
			});
//				tvDocContent.setVisibility(View.GONE);
			//viewHolder.tvClubName.setText(bean.club_name);
			//viewHolder.tvDocContent.setText("");
		}

		public ReplyBean getItem(int position) {
			return mReplyBean.get(position);
		}

		@Override
		public int getItemCount() {
			return mReplyBean.size();
		}

		public class CommentViewHolder extends RecyclerView.ViewHolder{
			public ImageView ivAvatar;
			public TextView tvName;
			public TextView tvDate;
			public TextView tvContent;
			public TextView tvDocContent;
			public TextView tvClubName;
			public CommentViewHolder(View itemView) {
				super(itemView);
				ivAvatar = (ImageView) itemView.findViewById(R.id.iv_creator);
				tvName = (TextView) itemView.findViewById(R.id.tv_name);
				tvDate = (TextView) itemView.findViewById(R.id.tv_date);
				tvContent = (TextView) itemView.findViewById(R.id.tv_content);
				tvDocContent = (TextView) itemView.findViewById(R.id.tv_doc_content);
				tvClubName = (TextView) itemView.findViewById(R.id.tv_club_name);
			}
		}
	}
}
