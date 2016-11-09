package com.moemoe.lalala.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.util.DensityUtil;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.CalendarDayUiType;
import com.moemoe.lalala.data.DepartmentBean;
import com.moemoe.lalala.data.DocItemBean;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.DocLabelView;
import com.moemoe.lalala.view.MyRoundedImageView;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.recycler.PullAndLoadView;
import com.moemoe.lalala.view.recycler.PullCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by yi on 2016/9/23.
 */
@ContentView(R.layout.ac_one_pulltorefresh_list)
public class PgcFragment extends BaseFragment {
    public static final String TAG = "PgcFragment";

    @FindView(R.id.list)
    private PullAndLoadView mListPost;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private DocListAdapter mDocAdapter;
    private boolean mIsHasLoadedAll = false;
    private boolean mIsLoading = false;
    private ArrayList<DepartmentBean.DepartmentDoc> mDocData = new ArrayList<>();

    private String uuid;
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Picasso.with(getActivity())
                .cancelTag(TAG);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uuid = getArguments().getString("uuid");
        mSwipeRefreshLayout = mListPost.getSwipeRefreshLayout();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
        mRecyclerView = mListPost.getRecyclerView();
        mDocAdapter = new DocListAdapter();
        mRecyclerView.setAdapter(mDocAdapter);
        mDocAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object object = mDocAdapter.getItem(position);
                if (object != null && object instanceof DepartmentBean.DepartmentDoc) {
                    DepartmentBean.DepartmentDoc bean = (DepartmentBean.DepartmentDoc) object;
                    if (!TextUtils.isEmpty(bean.schema)) {
                        Uri uri = Uri.parse(bean.schema);
                        IntentUtils.toActivityFromUri(getActivity(), uri, view);
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Picasso.with(getActivity()).resumeTag(TAG);
                } else {
                    Picasso.with(getActivity()).pauseTag(TAG);
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mListPost.setLayoutManager(linearLayoutManager);
        mListPost.isLoadMoreEnabled(true);
        mListPost.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                new UpdateTask(false).execute();
            }

            @Override
            public void onRefresh() {
                new UpdateTask(true).execute();
                mListPost.isLoadMoreEnabled(true);
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
        mListPost.initLoad();
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
                requestDocList(0);
                mIsHasLoadedAll = false;
            }else {
                requestDocList(mDocAdapter.getItemCount());
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

    private void requestDocList(final int index){
        if(!NetworkUtils.checkNetworkAndShowError(getActivity())){
            return;
        }
        Otaku.getDocV2().requestFavoriteDocListPgc(PreferenceManager.getInstance(getActivity()).getToken(),uuid, index, Otaku.LENGTH).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
//                DepartmentBean bean = new DepartmentBean();
//                bean.readFromJsonContent(s);
                ArrayList<DepartmentBean.DepartmentDoc> beans = DepartmentBean.readFromJsonList(getActivity(), s);
                if (index == 0) {
                    mDocData.clear();
                    mDocAdapter.setData(beans);
                }else {
                    mDocAdapter.addData(beans);
                }
                mDocData.addAll(beans);
                if (index != 0) {
                    if (beans.size() >= Otaku.LENGTH) {
                        mListPost.isLoadMoreEnabled(true);
                    } else {
                        mListPost.isLoadMoreEnabled(false);
                        mIsHasLoadedAll = true;
                    }
                }
                mListPost.setComplete();
                mIsLoading = false;
            }

            @Override
            public void failure(String e) {
                mListPost.setComplete();
                mIsLoading = false;
                //NetworkUtils.checkNetworkAndShowError(MyPostActivity.this);
                ToastUtil.showToast(getActivity(), R.string.msg_refresh_fail);
            }
        }));
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public class DocListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private LayoutInflater mLayoutInflater;
        private OnItemClickListener mOnItemClickListener;
        private ArrayList<DepartmentBean.DepartmentDoc> docData = new ArrayList<>();

        public DocListAdapter(){
            mLayoutInflater = LayoutInflater.from(getActivity());
        }

        public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
        }

        public void setData(ArrayList<DepartmentBean.DepartmentDoc> data){
            int bfSize = docData.size();
            docData.clear();
            docData.addAll(data);
            int afSize = docData.size();
            if(bfSize == 0){
                notifyItemRangeInserted(1,afSize);
            }else {
                notifyItemRangeChanged(1, afSize);
                if(bfSize - afSize > 0){
                    notifyItemRangeRemoved(afSize + 1,bfSize - afSize);
                }
            }
            notifyDataSetChanged();
        }

        public void addData(ArrayList<DepartmentBean.DepartmentDoc> data){
            int bp = docData.size();
            docData.addAll(data);
            notifyItemRangeInserted(bp + 1, data.size());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LinearVViewHolder(mLayoutInflater.inflate(R.layout.item_calender_type1_item,parent,false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
            if(holder instanceof LinearVViewHolder){
                final DepartmentBean.DepartmentDoc doc = (DepartmentBean.DepartmentDoc) getItem(position);
                final LinearVViewHolder linearVViewHolder = (LinearVViewHolder) holder;
                if(doc.mark != null && !doc.mark.equals("")){
                    linearVViewHolder.mTvTag.setVisibility(View.VISIBLE);
                    linearVViewHolder.mTvTag.setText(doc.mark);
                }else{
                    linearVViewHolder.mTvTag.setVisibility(View.GONE);
                }
                Picasso.with(getActivity())
                        .load(StringUtils.getUrl(getActivity(), doc.icon.path, DensityUtil.dip2px(90),DensityUtil.dip2px(90),false,true))
                        .resize(DensityUtil.dip2px(90), DensityUtil.dip2px(90))
                        .centerCrop()
                        .tag(TAG)
                        .config(Bitmap.Config.RGB_565)
                        .placeholder(R.drawable.ic_default_avatar_l)
                        .error(R.drawable.ic_default_avatar_l)
                        .into(linearVViewHolder.mIvTitle);
                TextPaint tp = linearVViewHolder.mTvTitle.getPaint();
                int style = CalendarDayUiType.getType(doc.ui);
                linearVViewHolder.mRlDocLikePack.setVisibility(View.VISIBLE);
                linearVViewHolder.mRlDocCommentPack.setVisibility(View.VISIBLE);
                linearVViewHolder.mTvLikeNum.setText(StringUtils.getNumberInLengthLimit(doc.likes, 2));
                linearVViewHolder.mTvCommentNum.setText(StringUtils.getNumberInLengthLimit(doc.comments, 2));
                if(style == CalendarDayUiType.valueOf(CalendarDayUiType.NEWS)){
                    tp.setFakeBoldText(true);
                    linearVViewHolder.mSubtitle.setVisibility(View.VISIBLE);
                    if(linearVViewHolder.mDlView != null) {
                        linearVViewHolder.mDlView.setVisibility(View.GONE);
                    }
                    linearVViewHolder.mSubtitle.setText(doc.content);
                }else{
                    tp.setFakeBoldText(false);
                    linearVViewHolder.mSubtitle.setVisibility(View.GONE);
                    if(linearVViewHolder.mDlView != null) {
                        linearVViewHolder.mDlView.setVisibility(View.GONE);
                    }
                }
                if(linearVViewHolder.mIvVideo != null){
                    linearVViewHolder.mIvVideo.setVisibility(View.GONE);
                }
                linearVViewHolder.mTvTitle.setText(doc.title);
                linearVViewHolder.mTvTime.setText(StringUtils.timeFormate(doc.updateTime));
                if (linearVViewHolder.mTvName != null){
                    linearVViewHolder.mTvName.setText(doc.userName);
                }
                linearVViewHolder.itemView.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        int pos = linearVViewHolder.getLayoutPosition();
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(linearVViewHolder.itemView, pos);
                        }
                    }
                });
                linearVViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = linearVViewHolder.getLayoutPosition();
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemLongClick(linearVViewHolder.itemView, pos);
                        }
                        return false;
                    }
                });
            }
        }

        public Object getItem(int position){
            return docData.get(position);
        }

        @Override
        public int getItemCount() {
            return docData.size();
        }

        public class LinearVViewHolder extends RecyclerView.ViewHolder{

            TextView mTvTag;
            MyRoundedImageView mIvTitle;
            ImageView mIvVideo;
            TextView mTvTitle;
            TextView mTvName;
            TextView mTvTime;
            TextView mSubtitle;
            View mRlDocLikePack;
            View mRlDocCommentPack;
            DocLabelView mDlView;
            TextView mTvLikeNum;
            TextView mTvCommentNum;

            public LinearVViewHolder(View itemView) {
                super(itemView);
                mTvTag = (TextView) itemView.findViewById(R.id.tv_tag);
                mIvTitle = (MyRoundedImageView) itemView.findViewById(R.id.iv_item_image);
                mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
                mTvName = (TextView) itemView.findViewById(R.id.tv_creator_name);
                mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
                mSubtitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
                mRlDocLikePack = itemView.findViewById(R.id.rl_doc_like_pack);
                mRlDocCommentPack = itemView.findViewById(R.id.rl_doc_comment_pack);
                mDlView = (DocLabelView) itemView.findViewById(R.id.dv_label_root);
                mIvVideo = (ImageView) itemView.findViewById(R.id.iv_video);
                mTvLikeNum = (TextView) itemView.findViewById(R.id.tv_post_pants_num);
                mTvCommentNum = (TextView) itemView.findViewById(R.id.tv_post_comment_num);
            }
        }
    }
}
