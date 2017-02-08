package com.moemoe.lalala.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moemoe.lalala.MoemoeApplication;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.ReplyBean;
import com.moemoe.lalala.network.OneParameterCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.recycler.PullAndLoadView;
import com.moemoe.lalala.view.recycler.PullCallback;
import com.squareup.picasso.Picasso;

import org.xutils.DbManager;
import org.xutils.common.util.DensityUtil;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

;

/**
 * @author Haru
 * @version 创建时间：2015年10月29日 上午11:51:51
 */
@SuppressLint("ValidFragment")
@ContentView(R.layout.ac_one_pulltorefresh_list)
public class MyCommentFragment extends BaseFragment {
	private static final String TAG = "MyCommentFragment";

	@ViewInject(R.id.list)
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
	public void onDestroyView() {
		super.onDestroyView();
		Picasso.with(getActivity())
				.cancelTag(TAG);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		db = x.getDb(MoemoeApplication.sDaoConfig);
		mSwipeRefreshLayout = mListDocs.getSwipeRefreshLayout();
		mSwipeRefreshLayout.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
		mRecyclerView = mListDocs.getRecyclerView();
		mDocAdapter = new CommentListAdapter();
		mRecyclerView.setAdapter(mDocAdapter);
		mRecyclerView.setHasFixedSize(true);
		mDocAdapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				ReplyBean bean = mDocAdapter.getItem(position);
				if (bean != null) {
					Uri uri = Uri.parse(bean.getSchema());
					IntentUtils.toActivityFromUri(getActivity(), uri,view);
				}
			}

			@Override
			public void onItemLongClick(View view, int position) {

			}
		});

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
		mListDocs.setLayoutManager(linearLayoutManager);
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
					.where("uuid","=",PreferenceManager.getInstance(getContext()).getUUid())
					.findFirst();
			if(docBean != null && docBean.json != null){
				Gson gson = new Gson();
				ArrayList<ReplyBean> datas = gson.fromJson(docBean.json,new TypeToken<ArrayList<ReplyBean>>(){}.getType());
				mReplyBean.addAll(datas);
				mDocAdapter.notifyDataSetChanged();
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	private void requestCommentList(final int index){
		Otaku.getAccountV2().requestCommentFromOther(index, new OneParameterCallback<ArrayList<ReplyBean>>() {
			@Override
			public void action(ArrayList<ReplyBean> replyBeen) {
				// TODO 没有去重
				ArrayList<ReplyBean> datas = replyBeen;
				ReplyBean docBean = new ReplyBean();
				docBean.uuid = PreferenceManager.getInstance(getContext()).getUUid();
				Gson gson = new Gson();
				docBean.json = gson.toJson(replyBeen, new TypeToken<ArrayList<ReplyBean>>() {
				}.getType());
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
				}
				mListDocs.setComplete();
				mIsLoading = false;
			}
		}, new OneParameterCallback<Integer>() {
			@Override
			public void action(Integer integer) {
				mListDocs.setComplete();
				mIsLoading = false;
				ErrorCodeUtils.showErrorMsgByCode(getContext(),integer);
			}
		});
	}

	private class UpdateTask extends AsyncTask<Void,Void,Void> {

		private boolean mIsPullDown;

		public UpdateTask(boolean IsPullDown){
			this.mIsPullDown = IsPullDown;
		}


		@Override
		protected Void doInBackground(Void... params) {
			mIsLoading = true;
			if (mIsPullDown) {
				requestCommentList(0);
			}else {
				requestCommentList(mDocAdapter.getItemCount());
			}
			return null;
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(Void result) {

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
			Picasso.with(getActivity())
					.load(StringUtils.getUrl(getActivity(),Otaku.URL_QINIU + bean.getFromIcon().getPath(),DensityUtil.dip2px(44), DensityUtil.dip2px(44),false,false))
					.resize(DensityUtil.dip2px(44), DensityUtil.dip2px(44))
					.placeholder(R.drawable.ic_default_avatar_m)
					.error(R.drawable.ic_default_avatar_m)
					.config(Bitmap.Config.RGB_565)
					.tag(TAG)
					.into(viewHolder.ivAvatar);
			viewHolder.tvName.setText(bean.getFromName());
			viewHolder.tvDate.setText(bean.getCreateTime());
			viewHolder.tvContent.setText(bean.getContent());
			viewHolder.itemView.setOnClickListener(new NoDoubleClickListener() {
				@Override
				public void onNoDoubleClick(View v) {
					int pos = viewHolder.getLayoutPosition();
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onItemClick(viewHolder.itemView, pos);
					}
				}
			});
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
