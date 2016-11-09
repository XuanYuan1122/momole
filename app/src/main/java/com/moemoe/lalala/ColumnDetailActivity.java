package com.moemoe.lalala;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.util.DensityUtil;
import com.app.ex.DbException;
import com.app.view.DbManager;
import com.squareup.picasso.Picasso;
import com.moemoe.lalala.data.CalendarDayItem;
import com.moemoe.lalala.data.CalendarDayUiType;
import com.moemoe.lalala.data.ColumnDbbean;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.DocLabelView;
import com.moemoe.lalala.view.MyRoundedImageView;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.recycler.PullAndLoadView;
import com.moemoe.lalala.view.recycler.PullCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Haru on 2016/5/9 0009.
 */
@ContentView(R.layout.ac_column_detail)
public class ColumnDetailActivity extends BaseActivity{

    public static final String EXTRA_UI = "ui";
    public static final String EXTRA_TITLE = "name";

    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.tv_toolbar_title)
    private TextView mTvTitle;
    @FindView(R.id.rv_future)
    private PullAndLoadView mFuturePv;
    @FindView(R.id.rv_past)
    private PullAndLoadView mPastPv;
    @FindView(R.id.tv_time)
    private TextView mTvTime;
    @FindView(R.id.ll_time_root)
    private View mLlTimeRoot;

    private RecyclerView mFutureRv;
    private DocListAdapter mFutureAdapter;
    private RecyclerView mPastRv;
    private DocListAdapter mPastAdapter;
    private String mUi;
    private String mBarId;
    private String mTitle;
    private boolean mIsPast = true;

    private HashMap<String,ArrayList<CalendarDayItem.CalendarDoc>> mPastMap;
    private HashMap<String,ArrayList<CalendarDayItem.CalendarDoc>> mFutureMap;
    private ArrayList<Object> mPastList;
    private ArrayList<Object> mFuturelist;
    private boolean mIsLoading;
    private int mCurIndex = 0;
    //private MusicServiceManager mServiceManager;
    private DbManager db;
    private  ColumnDbbean columnDbbean = new ColumnDbbean();
    private boolean mIsHasLoadedAll = false;

    @Override
    protected void initView() {
        db = Utils.getDb(MoemoeApplication.sDaoConfig);
        mBarId = mIntent.getStringExtra(EXTRA_KEY_UUID);
        mUi = mIntent.getStringExtra(EXTRA_UI);
        mTitle = mIntent.getStringExtra(EXTRA_TITLE);
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTvTitle.setText(mTitle);
      //  mServiceManager = MapActivity.sMusicServiceManager;
        SwipeRefreshLayout swipeRefreshLayout = mFuturePv.getSwipeRefreshLayout();
        swipeRefreshLayout.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
        mFuturePv.isLoadMoreEnabled(false);
        mFutureRv = mFuturePv.getRecyclerView();
        mFutureAdapter = new DocListAdapter();
        mFutureAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object bean = mPastAdapter.getItem(position);
                String id = null;
                if (bean instanceof CalendarDayItem.CalendarDoc) {
                    CalendarDayItem.CalendarDoc doc = (CalendarDayItem.CalendarDoc) bean;
                    id = doc.id;
                }
                if (!TextUtils.isEmpty(id)) {
                    Uri uri = Uri.parse(id);
                    IntentUtils.toActivityFromUri(ColumnDetailActivity.this, uri,view);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mFutureRv.setAdapter(mFutureAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mFuturePv.setLayoutManager(linearLayoutManager);
        mFutureMap = new HashMap<>();
        mFuturelist = new ArrayList<>();
        mFuturePv.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {

            }

            @Override
            public void onRefresh() {
                new UpdateTask(true).execute();
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        });


        SwipeRefreshLayout swipeRefreshLayout1 = mPastPv.getSwipeRefreshLayout();
        swipeRefreshLayout1.setEnabled(false);
        mPastPv.isLoadMoreEnabled(true);
        mPastRv = mPastPv.getRecyclerView();
        mPastAdapter = new DocListAdapter();
        mPastAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object bean = mPastAdapter.getItem(position);
                String id = null;
                if (bean instanceof CalendarDayItem.CalendarDoc) {
                    CalendarDayItem.CalendarDoc doc = (CalendarDayItem.CalendarDoc) bean;
                    id = doc.id;
                }
                if (!TextUtils.isEmpty(id)) {
                    Uri uri = Uri.parse(id);
                    IntentUtils.toActivityFromUri(ColumnDetailActivity.this, uri,view);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mPastRv.setAdapter(mPastAdapter);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        mPastPv.setLayoutManager(linearLayoutManager1);
        mPastMap = new HashMap<>();
        mPastList = new ArrayList<>();
        mPastPv.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                new UpdateTask(false).execute();
            }

            @Override
            public void onRefresh() {

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

        mTvTime.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mIsPast = !mIsPast;
                if(mIsPast){
                    mFutureAdapter.resetPreMusicPosition();
                    mPastPv.setVisibility(View.VISIBLE);
                    mFuturePv.setVisibility(View.GONE);
                    mTvTime.setText(R.string.label_coming_soon);
                }else {
                    mPastAdapter.resetPreMusicPosition();
                    mPastPv.setVisibility(View.GONE);
                    mFuturePv.setVisibility(View.VISIBLE);
                    mTvTime.setText(R.string.label_review_past);
                    if(mFuturelist.size() == 0) requestFresh(-1);
                }
            }
        });
        requestFresh(0);
        mFuturePv.setVisibility(View.GONE);
        mPastPv.setVisibility(View.VISIBLE);
        mTvTime.setText(R.string.label_coming_soon);
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
                requestFresh(-mCurIndex-1);
            }else {
                requestFresh(mCurIndex);
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            if(mIsPullDown){
                mFuturePv.setComplete();
            }else {
                mPastPv.setComplete();
            }
            mIsLoading = false;
        }
    }

    /**
     * 计算list的总数
     * @param map
     * @return
     */
    private int getRequestListSize(HashMap<String, ArrayList<CalendarDayItem.CalendarDoc>> map){
        int res = 0;
        for(ArrayList<CalendarDayItem.CalendarDoc> list : map.values()){
            res += list.size();
        }
        return res;
    }

    /**
     * 计算list的总数
     * @param map
     * @return
     */
    private int getAdapterListSize(HashMap<String, ArrayList<CalendarDayItem.CalendarDoc>> map){
        int res = 0;
        for(ArrayList<CalendarDayItem.CalendarDoc> list : map.values()){
            res += list.size() + 1;
        }
        return res;
    }

    private boolean hasDay(ArrayList<Object> list,String day){
        for(Object o : list){
            if(o instanceof String){
                String tmpDay = (String) o;
                if(tmpDay.equals(day)){
                    return true;
                }
            }
        }
        return false;
    }

    private void loadDataFromDb(){
        try {
            ColumnDbbean columnDbbean = db.selector(ColumnDbbean.class)
                    .where("id","=",mBarId)
                    .findFirst();
            if(columnDbbean != null ){
                JSONArray array = null;
                if(mIsPast && columnDbbean.pastJson != null){
                    array = new JSONArray(columnDbbean.pastJson);
                }else if(!mIsPast && columnDbbean.futureJson != null){
                    array = new JSONArray(columnDbbean.futureJson);
                }
                if(array != null){
                    int bfSize = 0;
                    if(mIsPast){
                        bfSize = mPastList.size();
                    }else {
                        bfSize = mFuturelist.size();
                    }
                    for(int i = 0;i < array.length();i++){
                        JSONObject jsonObject = array.optJSONObject(i);
                        ArrayList<CalendarDayItem.CalendarDoc> docList = new ArrayList<>();
                        JSONArray docArray = jsonObject.optJSONArray("docs");
                        if(docArray != null){
                            for(int n = 0;n < docArray.length();n++){
                                JSONObject jsonDoc = docArray.optJSONObject(n);
                                CalendarDayItem.CalendarDoc doc = new CalendarDayItem.CalendarDoc();
                                doc.readFromJson(this,jsonDoc.toString());
                                docList.add(doc);
                            }
                        }
                        String day = jsonObject.optString("day");
                        mCurIndex += docList.size();
                        if (mIsPast){
                            deleteRepeat(docList,mPastList);
                            if(!hasDay(mPastList,day)){
                                mPastList.add(day);
                                mPastList.addAll(docList);
                            }else {
                                mPastList.addAll(docList);
                            }
                        }else {
                            deleteRepeat(docList,mFuturelist);
                            if(!hasDay(mFuturelist,day)){
                                mFuturelist.addAll(0,docList);
                                mFuturelist.add(0,day);
                            }else {
                                String tempDay = (String)mFuturelist.get(0);
                                if(tempDay.equals(day)){
                                    mFuturelist.add(1,docList);
                                }
                            }
                        }
                    }
                    int afSize = 0;
                    if(mIsPast){
                        afSize = mPastList.size();
                        mPastAdapter.notifyItemRangeInserted(bfSize,afSize - bfSize);
                    }else {
                        afSize = mFuturelist.size();
                        mFutureAdapter.notifyItemRangeInserted(0,afSize - bfSize);
                        mFutureAdapter.setPreMusicPosition(mFutureAdapter.getPreMusicPosition() + afSize - bfSize);
                        mFutureRv.scrollToPosition(mFuturelist.size() - 1);
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteRepeat(ArrayList<CalendarDayItem.CalendarDoc> list,ArrayList<Object> docArrayList){

        for(CalendarDayItem.CalendarDoc doc : list){
            for(Object o : docArrayList){
                if(o instanceof CalendarDayItem.CalendarDoc){
                    CalendarDayItem.CalendarDoc doc1 = (CalendarDayItem.CalendarDoc) o;
                    if(doc1.refId.equals(doc.refId)){
                        list.remove(doc);
                        break;
                    }
                }
            }
        }
    }

    private void requestFresh(final int index){
        Otaku.getDocV2().requestNewDocList(mPreferMng.getToken(), mBarId, index).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                JSONArray array = null;
                try {
                    array = new JSONArray(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                columnDbbean.id = mBarId;
                if(array != null){
                    int bfSize = 0;
                    if(mIsPast){
                        columnDbbean.pastJson = array.toString();
                        bfSize = mPastList.size();
                    }else {
                        columnDbbean.futureJson = array.toString();
                        bfSize = mFuturelist.size();
                    }
                    try {
                        db.saveOrUpdate(columnDbbean);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    for(int i = 0;i < array.length();i++){
                        JSONObject jsonObject = array.optJSONObject(i);
                        ArrayList<CalendarDayItem.CalendarDoc> docList = new ArrayList<>();
                        JSONArray docArray = jsonObject.optJSONArray("docs");
                        if(docArray != null){
                            for(int n = 0;n < docArray.length();n++){
                                JSONObject jsonDoc = docArray.optJSONObject(n);
                                CalendarDayItem.CalendarDoc doc = new CalendarDayItem.CalendarDoc();
                                doc.readFromJson(ColumnDetailActivity.this,jsonDoc.toString());
                                docList.add(doc);
                            }
                        }
                        String day = jsonObject.optString("day");
                        mCurIndex += docList.size();
                        if (mIsPast){
                            if(docList.size() < Otaku.LENGTH && index != 0){
                                mIsHasLoadedAll = true;
                                mPastPv.isLoadMoreEnabled(false);
                            }
                            deleteRepeat(docList,mPastList);
                            if(!hasDay(mPastList,day)){
                                mPastList.add(day);
                                mPastList.addAll(docList);
                            }else {
                                mPastList.addAll(docList);
                            }
                        }else {
                            deleteRepeat(docList,mFuturelist);
                            if(!hasDay(mFuturelist,day)){
                                mFuturelist.addAll(0,docList);
                                mFuturelist.add(0,day);
                            }else {
                                String tempDay = (String)mFuturelist.get(0);
                                if(tempDay.equals(day)){
                                    mFuturelist.add(1,docList);
                                }
                            }
                        }
                    }
                    int afSize = 0;
                    if(mIsPast){
                        afSize = mPastList.size();
                        mPastAdapter.notifyItemRangeInserted(bfSize,afSize - bfSize);
                    }else {
                        afSize = mFuturelist.size();
                        mFutureAdapter.notifyItemRangeInserted(0,afSize - bfSize);
                        mFutureAdapter.setPreMusicPosition(mFutureAdapter.getPreMusicPosition() + afSize - bfSize);
                        mFutureRv.scrollToPosition(mFuturelist.size() - 1);
                    }
                }
            }

            @Override
            public void failure(String e) {
                ToastUtil.showCenterToast(ColumnDetailActivity.this, R.string.msg_server_connection);
            }
        }));
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public class DocListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        public int TYPE_SPLIT = 0 ;
        public int TYPE_DOC = 1;
        private LayoutInflater mLayoutInflater;
        private OnItemClickListener mOnItemClickListener;
        private int mPreMusicPosition = -1;

        public DocListAdapter(){
            mLayoutInflater = LayoutInflater.from(ColumnDetailActivity.this);
        }

        public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
        }

        public void resetPreMusicPosition(){
            mPreMusicPosition = -1;
        }

        public int getPreMusicPosition(){
            return mPreMusicPosition;
        }

        public void setPreMusicPosition(int position){
            if(mPreMusicPosition != -1){
                mPreMusicPosition = position;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == TYPE_SPLIT){
                return new SplitViewHolder(mLayoutInflater.inflate(R.layout.item_split,parent,false));
            }else if(viewType == TYPE_DOC){
                return new LinearVViewHolder(mLayoutInflater.inflate(R.layout.item_calender_type1_item,parent,false));
            }
            return new LinearVViewHolder(mLayoutInflater.inflate(R.layout.item_calender_type1_item,parent,false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
            if(holder instanceof SplitViewHolder){
                SplitViewHolder viewHolder = (SplitViewHolder) holder;
                viewHolder.mTvSplit.setText((String) getItem(position));
            }else if(holder instanceof LinearVViewHolder){
                final CalendarDayItem.CalendarDoc doc = (CalendarDayItem.CalendarDoc) getItem(position);
                final LinearVViewHolder linearVViewHolder = (LinearVViewHolder) holder;
                if(doc.mark != null && !doc.mark.equals("")){
                    linearVViewHolder.mTvTag.setVisibility(View.VISIBLE);
                    linearVViewHolder.mTvTag.setText(doc.mark);
                }else{
                    linearVViewHolder.mTvTag.setVisibility(View.GONE);
                }
                Picasso.with(ColumnDetailActivity.this)
                        .load(StringUtils.getUrl(ColumnDetailActivity.this, doc.icon.path,DensityUtil.dip2px(90),DensityUtil.dip2px(90),false,true))
                        .resize(DensityUtil.dip2px(90), DensityUtil.dip2px(90))
                        .centerCrop()
                        .placeholder(R.drawable.ic_default_avatar_l)
                        .error(R.drawable.ic_default_avatar_l)
                        .config(Bitmap.Config.RGB_565)
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
            return mIsPast? mPastList.get(position) : mFuturelist.get(position);
        }


        @Override
        public int getItemViewType(int position) {
            return mIsPast? mPastList.get(position) instanceof String ? TYPE_SPLIT : TYPE_DOC : mFuturelist.get(position) instanceof String ? TYPE_SPLIT : TYPE_DOC;
        }

        @Override
        public int getItemCount() {
            return mIsPast?mPastList.size() : mFuturelist.size();
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

        public class SplitViewHolder extends RecyclerView.ViewHolder{
            TextView mTvSplit;

            public SplitViewHolder(View itemView) {
                super(itemView);
                mTvSplit = (TextView) itemView.findViewById(R.id.tv_time_table);
            }
        }
    }
}
