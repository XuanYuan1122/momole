package com.moemoe.lalala;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.util.DensityUtil;
import com.app.ex.DbException;
import com.app.view.DbManager;
import com.moemoe.lalala.adapter.CalendarRecyclerViewAdapter;
import com.moemoe.lalala.adapter.ClassRecyclerViewAdapter;
import com.moemoe.lalala.callback.MoeMoeCallback;
import com.moemoe.lalala.data.BannerBen;
import com.moemoe.lalala.data.CalendarDayType;
import com.moemoe.lalala.data.CalendarDayUiType;
import com.moemoe.lalala.data.DepartmentBean;
import com.moemoe.lalala.data.FeaturedBean;
import com.moemoe.lalala.netamusic.data.model.PlayList;
import com.moemoe.lalala.netamusic.data.model.Song;
import com.moemoe.lalala.netamusic.player.IPlayBack;
import com.moemoe.lalala.netamusic.player.Player;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.SpacesItemDecoration;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.DocLabelView;
import com.moemoe.lalala.view.MyRoundedImageView;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.recycler.PullAndLoadView;
import com.moemoe.lalala.view.recycler.PullCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

;

/**
 * Created by Haru on 2016/8/9 0009.
 */
@ContentView(R.layout.ac_one_pulltorefresh_list)
public class DepartmentActivity extends BaseActivity implements IPlayBack.Callback{
    public static final String TAG = "DepartmentActivity";
    public static final String EXTRA_ROOM_ID = "room_id";
    public static final String EXTRA_NAME = "name";

//    @FindView(R.id.iv_class_bg)
//    private ImageView mIvBg;
    @FindView(R.id.rl_bar)
    private View mRlRoot;
    @FindView(R.id.tv_title)
    private TextView mTitle;
    @FindView(R.id.iv_back)
    private ImageView mIvBack;
    @FindView(R.id.list)
    private PullAndLoadView mListDocs;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<BannerBen> mBannerBeans = new ArrayList<>();
    private ArrayList<FeaturedBean> mFeaturedBeans = new ArrayList<>();
    private ArrayList<DepartmentBean.DepartmentDoc> mDepartmentList = new ArrayList<>();
    private String mBefore;
    private ScheduledExecutorService scheduledExecutorService;
    private DbManager db;
    private String mRoomId;
    private boolean mIsHasLoadedAll = false;
    private boolean mIsLoading = false;
    private DepartmentListAdapter mListAdapter;
    private DepartmentListAdapter.BannerViewHolder mBannerViewHolder;
    private boolean mShouldChange = true;
    private int mCurrentItem = 0;
    private boolean mIsPullDown;
    private MoeMoeCallback allFinishCallback;
    private Player mPlayer;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mBannerViewHolder.viewPager != null && mShouldChange) {
                mBannerViewHolder.viewPager.setCurrentItem(mCurrentItem, true);
            }
        }
    };

    /**
     * 开始循环banner
     *
     * @author Haru
     */
    private void startBanner() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new BannerRunnable(), 3, 3, TimeUnit.SECONDS);
    }

    @Override
    public void onSwitchLast(@Nullable Song last) {

    }

    @Override
    public void onSwitchNext(@Nullable Song next) {

    }

    @Override
    public void onComplete(@Nullable Song next) {

    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {

    }

    private class BannerRunnable implements Runnable {

        @Override
        public void run() {
            synchronized (mBannerViewHolder.viewPager) {
                mCurrentItem = (mBannerViewHolder.viewPager.getCurrentItem() + 1) % mBannerBeans.size();
                handler.obtainMessage().sendToTarget();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Picasso.with(DepartmentActivity.this).cancelTag(TAG);
        mPlayer.unregisterCallback(this);
        if(scheduledExecutorService != null) scheduledExecutorService.shutdown();
    }

    @Override
    protected void initView() {
        db = Utils.getDb(MoemoeApplication.sDaoConfig);
        mPlayer = Player.getInstance();
        mPlayer.registerCallback(this);
        mBefore = "";
        mRoomId = "";
        if(mIntent != null){
            String roomId = mIntent.getStringExtra(EXTRA_KEY_UUID);
            if(!TextUtils.isEmpty(roomId)){
                mRoomId = roomId;
            }
            String title = mIntent.getStringExtra(EXTRA_NAME);
            if(!TextUtils.isEmpty(title)){
                mTitle.setText(title);
                mTitle.setVisibility(View.VISIBLE);
            }else {
                mTitle.setVisibility(View.GONE);
            }
        }
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mRlRoot.setVisibility(View.VISIBLE);
        mRlRoot.getBackground().mutate().setAlpha(0);

//        mIvBg.setBackgroundColor(getResources().getColor(R.color.bg_activity));
        mSwipeRefreshLayout = mListDocs.getSwipeRefreshLayout();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
        mRecyclerView = mListDocs.getRecyclerView();
        mListAdapter = new DepartmentListAdapter();
        mRecyclerView.setAdapter(mListAdapter);
        mListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object object = mListAdapter.getItem(position);
                if (object != null) {
                    if (object instanceof DepartmentBean.DepartmentDoc) {
                        DepartmentBean.DepartmentDoc bean = (DepartmentBean.DepartmentDoc) object;
                        if (!TextUtils.isEmpty(bean.schema)) {
                            Uri uri = Uri.parse(bean.schema);
                            IntentUtils.toActivityFromUri(DepartmentActivity.this, uri,view);
                        }
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position > 1) {
                    String[] uis = mDepartmentList.get(position - 2).ui.split("#");
                    int type = CalendarDayType.getType(uis[0]);
                    if (type == CalendarDayType.valueOf(CalendarDayType.DOC_G_2)) {
                        return 1;
                    } else {
                        return 2;
                    }
                } else {
                    return 2;
                }
            }
        });
        mListDocs.setLayoutManager(layoutManager);
        mListDocs.isLoadMoreEnabled(true);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                new UpdateTask(false).execute();
            }

            @Override
            public void onRefresh() {
                new UpdateTask(true).execute();
                mListDocs.isLoadMoreEnabled(true);
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
        mRecyclerView .addItemDecoration(new SpacesItemDecoration(DensityUtil.dip2px(9)));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int curY = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Picasso.with(DepartmentActivity.this).resumeTag(TAG);
                } else {
                    Picasso.with(DepartmentActivity.this).pauseTag(TAG);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                curY += dy;
                toolBarAlpha(curY);
            }
        });
        loadDataFromDb();
        mListDocs.initLoad();
        allFinishCallback = new MoeMoeCallback() {
            AtomicInteger count = new AtomicInteger(0);
            @Override
            public void onSuccess() {
                int curSize = count.incrementAndGet();
                if(curSize == 3 && mIsPullDown){
                    mListDocs.setComplete();
                    mIsLoading = false;
                    count.set(0);
                }
                if(!mIsPullDown){
                    mListDocs.setComplete();
                    mIsLoading = false;
                    count.set(0);
                }
            }

            @Override
            public void onFailure() {

            }
        };
    }


    private void loadDataFromDb(){
        try {
            BannerBen topAdBean = db.selector(BannerBen.class)
                    .where("uuid","=","cache" + mRoomId)
                    .findFirst();
            FeaturedBean featuredBean = db.selector(FeaturedBean.class)
                    .where("uuid","=","cache" + mRoomId)
                    .findFirst();
            DepartmentBean classDataBean = db.selector(DepartmentBean.class)
                    .where("id", "=", "cache" + mRoomId)
                    .findFirst();
            if(topAdBean != null && classDataBean != null && topAdBean.json != null && classDataBean.json != null && featuredBean != null && featuredBean.json != null){
                mBannerBeans = BannerBen.readFromJsonList(DepartmentActivity.this, topAdBean.json);
                mFeaturedBeans = FeaturedBean.readFromJsonList(DepartmentActivity.this, featuredBean.json);
                DepartmentBean bean = new DepartmentBean();
                bean.readFromJsonContent(classDataBean.json);
                ArrayList<DepartmentBean.DepartmentDoc> beans = DepartmentBean.readFromJsonList(DepartmentActivity.this, bean.list);
                mDepartmentList.clear();
                mDepartmentList.addAll(beans);
                mListAdapter.notifyDataSetChanged();
            }
        } catch (DbException e) {

        }
    }

    public void toolBarAlpha(int curY) {
        int startOffset = 0;
        int endOffset = mRlRoot.getHeight();
        if (Math.abs(curY) <= startOffset) {
            mRlRoot.getBackground().mutate().setAlpha(0);
            mTitle.setTextColor(Color.argb(0, 255, 255, 255));
        } else if (Math.abs(curY) > startOffset && Math.abs(curY) < endOffset) {
            float precent = (float) (Math.abs(curY) - startOffset) / endOffset;
            int alpha = Math.round(precent * 255);
            mRlRoot.getBackground().mutate().setAlpha(alpha);
            mTitle.setTextColor(Color.argb(alpha, 255, 255, 255));
        } else if (Math.abs(curY) >= endOffset) {
            mRlRoot.getBackground().mutate().setAlpha(255);
            mTitle.setTextColor(Color.argb(255, 255, 255, 255));
        }
    }

    private class UpdateTask extends AsyncTask<Void,Void,Void> {



        public UpdateTask(boolean IsPullDown){
            mIsPullDown = IsPullDown;
        }


        @Override
        protected Void doInBackground(Void... params) {
            mIsLoading = true;
            if (mIsPullDown) {
                requestBannerData();
                requestFeatured();
                requestDocAndClubList(0, true);
            }else {
                requestDocAndClubList(mDepartmentList.size(), mIsPullDown);
            }
            return null;
        }
    }

    /**
     * 请求banner数据
     *
     * @author Haru
     */
    private void requestBannerData() {
        if(!NetworkUtils.checkNetworkAndShowError(this)){
            return;
        }
        Otaku.getDocV2().requestNewBanner(mPreferMng.getToken(), mRoomId).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                mBannerBeans = BannerBen.readFromJsonList(DepartmentActivity.this, s);
                BannerBen adBean = new BannerBen();
                adBean.uuid = "cache" + mRoomId;
                adBean.json = s;
                try {
                    db.saveOrUpdate(adBean);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                mListAdapter.notifyItemChanged(0);
                allFinishCallback.onSuccess();
            }

            @Override
            public void failure(String e) {
                allFinishCallback.onSuccess();
            }
        }));
    }

    private void requestFeatured(){
        Otaku.getDocV2().requestFeatured(mPreferMng.getToken(), mRoomId).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                ArrayList<FeaturedBean> featuredBeans = FeaturedBean.readFromJsonList(DepartmentActivity.this, s);
                mFeaturedBeans.clear();
                mFeaturedBeans.addAll(featuredBeans);
                FeaturedBean adBean = new FeaturedBean();
                adBean.uuid = "cache" + mRoomId;
                adBean.json = s;
                try {
                    db.saveOrUpdate(adBean);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                mListAdapter.notifyItemChanged(1);
                allFinishCallback.onSuccess();
            }

            @Override
            public void failure(String e) {
                allFinishCallback.onSuccess();
            }
        }));
    }

    /**
     * 请求class主页除banner外数据
     * @param index
     * @param isPullDown
     * @author Haru
     */
    private void requestDocAndClubList(final int index, final boolean isPullDown) {
        if(index == 0){
            mBefore = null;
        }
        Otaku.getDocV2().requestDepartmentDoc(mPreferMng.getToken(), index, Otaku.LENGTH, mRoomId,mBefore).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                DepartmentBean bean = new DepartmentBean();
                bean.readFromJsonContent(s);
                ArrayList<DepartmentBean.DepartmentDoc> beans = DepartmentBean.readFromJsonList(DepartmentActivity.this, bean.list);
                if (isPullDown) {
                    bean.id = "cache" + mRoomId;
                    bean.json = s;
                    try {
                        db.saveOrUpdate(bean);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    int bfSize = mDepartmentList.size();
                    mDepartmentList.clear();
                    mBefore = bean.before;
                    mDepartmentList.addAll(beans);
                    int afSize = mDepartmentList.size();
                    if(bfSize == 0){
                        mListAdapter.notifyItemRangeInserted(2,mDepartmentList.size());
                    }else {
                        mListAdapter.notifyItemRangeChanged(2,mDepartmentList.size());
                        if(bfSize - afSize > 0){
                            mListAdapter.notifyItemRangeRemoved(afSize + 2,bfSize - afSize);
                        }
                    }
                } else {
                    int bfSize = mDepartmentList.size();
                    mDepartmentList.addAll(beans);
                    int afSize = mDepartmentList.size();
                    //mListAdapter.notifyDataSetChanged();
                    mListAdapter.notifyItemRangeInserted(bfSize + 2,afSize - bfSize);
                }

                if (beans.size() != 0) {
                    mListDocs.isLoadMoreEnabled(true);
                } else {
                    mListDocs.isLoadMoreEnabled(false);
                    mIsHasLoadedAll = true;
                }
                allFinishCallback.onSuccess();
            }

            @Override
            public void failure(String e) {
                allFinishCallback.onSuccess();
               // NetworkUtils.checkNetworkAndShowError(DepartmentActivity.this);
                ToastUtil.showToast(DepartmentActivity.this, R.string.msg_refresh_fail);
            }
        }));
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    private class DepartmentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private static final int VIEW_TYPE_BANNER = 0;
        private static final int VIEW_TYPE_FEATURED = 1;

        private int mPreMusicPosition = -1;
        private LayoutInflater mInflater;
        private OnItemClickListener mOnItemClickListener;
        private int[] mTagId = {R.drawable.btn_class_from_orange,R.drawable.btn_class_from_blue,
                R.drawable.btn_class_from_green,R.drawable.btn_class_from_pink,R.drawable.btn_class_from_yellow};

        public DepartmentListAdapter(){
            mInflater = LayoutInflater.from(DepartmentActivity.this);
        }

        public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == VIEW_TYPE_BANNER){
                return new BannerViewHolder(mInflater.inflate(R.layout.item_banner_class,parent,false));
            }else if(viewType == VIEW_TYPE_FEATURED){
                return new FeaturedLinearHViewHolder(mInflater.inflate(R.layout.item_class_featured,parent,false));
            }else if(viewType == CalendarDayType.valueOf(CalendarDayType.DOC_V_1)){
                return new LinearVViewHolder(mInflater.inflate(R.layout.item_calender_type1_item,parent,false));
            }else if(viewType== CalendarDayType.valueOf(CalendarDayType.DOC_G_2)){
                return new LinearVViewHolder(mInflater.inflate(R.layout.item_calender_type2_item,parent,false));
            }else if(viewType == CalendarDayType.valueOf(CalendarDayType.DOC_H_1)){
                return new LinearVViewHolder(mInflater.inflate(R.layout.item_calender_type1_item,parent,false));
            }else if(viewType == CalendarDayType.valueOf(CalendarDayType.DOC_V_2)){
                return new NewsViewHolder(mInflater.inflate(R.layout.item_calender_type4_item,parent,false));
            }else if(viewType == CalendarDayType.valueOf(CalendarDayType.DOC_V_3)){
                return new LinearVViewHolder(mInflater.inflate(R.layout.item_calender_type2_item,parent,false));
            }
            return new LinearVViewHolder(mInflater.inflate(R.layout.item_calender_type1_item,parent,false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            if(viewHolder instanceof FeaturedLinearHViewHolder){
                FeaturedLinearHViewHolder linearHViewHolder = (FeaturedLinearHViewHolder) viewHolder;
                if(mFeaturedBeans.size() == 0){
                    linearHViewHolder.itemView.setVisibility(View.GONE);
                    return;
                }else {
                    linearHViewHolder.itemView.setVisibility(View.VISIBLE);
                }
                linearHViewHolder.mRvList.setBackgroundColor(Color.WHITE);
                linearHViewHolder.recyclerViewAdapter.setData(mFeaturedBeans);
                linearHViewHolder.recyclerViewAdapter.setOnItemClickListener(new ClassRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        FeaturedBean docBean = mFeaturedBeans.get(position);
                        if(!TextUtils.isEmpty(docBean.schema)){
                            Uri uri = Uri.parse(docBean.schema);
                            IntentUtils.toActivityFromUri(DepartmentActivity.this, uri,view);
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                    }
                });
            }else if(viewHolder instanceof BannerViewHolder){
                mBannerViewHolder = (BannerViewHolder) viewHolder;
                if(mBannerBeans.size() == 0){
                    viewHolder.itemView.setVisibility(View.GONE);
                    return;
                }else {
                    viewHolder.itemView.setVisibility(View.VISIBLE);
                }
                ArrayList<ImageView> imageViews = new ArrayList<>();
                mBannerViewHolder.llPointGroup.removeAllViews();
                for(int i=0;i<mBannerBeans.size();i++) {
                    final BannerBen bean = mBannerBeans.get(i);
                    ImageView image = new ImageView(DepartmentActivity.this);
                    Picasso.with(DepartmentActivity.this)
                            .load(StringUtils.getUrl(DepartmentActivity.this, bean.bg.path, DensityUtil.getScreenWidth(), DensityUtil.dip2px(150), false, false))
                            .fit()
                            .placeholder(R.drawable.ic_default_banner)
                            .error(R.drawable.ic_default_banner)
                            .centerCrop()
                            .config(Bitmap.Config.RGB_565)
                            .tag(TAG)
                            .into(image);
                    imageViews.add(image);
                    if(mBannerBeans.size() > 1){
                        View view = new View(DepartmentActivity.this);
                        view.setBackgroundResource(R.drawable.icon_class_banner_switch_white);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(10), DensityUtil.dip2px(10));
                        if (i > 0) {
                            params.leftMargin = DensityUtil.dip2px(4);
                        }
                        view.setLayoutParams(params);
                        mBannerViewHolder.llPointGroup.addView(view);
                    }
                    image.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            if(!TextUtils.isEmpty(bean.schema)){
                                Uri uri = Uri.parse(bean.schema);
                                IntentUtils.toActivityFromUri(DepartmentActivity.this, uri,v);
                            }
                        }
                    });
                }
                mBannerViewHolder.viewPager.setAdapter(new BannerAdapter(imageViews));
                if(mBannerBeans.size() > 1){
                    mBannerViewHolder.llPointContainer.setVisibility(View.VISIBLE);
                    mBannerViewHolder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            int len = (int) (mBannerViewHolder.width * positionOffset) + position * mBannerViewHolder.width;
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBannerViewHolder.pressedPoint.getLayoutParams();
                            params.leftMargin = len;
                            mBannerViewHolder.pressedPoint.setLayoutParams(params);
                        }

                        @Override
                        public void onPageSelected(int position) {

                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                    mBannerViewHolder.llPointGroup.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mBannerViewHolder.llPointGroup.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            mBannerViewHolder.width = mBannerViewHolder.llPointGroup.getChildAt(1).getLeft()
                                    - mBannerViewHolder.llPointGroup.getChildAt(0).getLeft();
                        }
                    });
                }else {
                    mBannerViewHolder.llPointContainer.setVisibility(View.GONE);
                }
            }else if(viewHolder instanceof LinearVViewHolder){
                Object beanTmp = getItem(position);
                final DepartmentBean.DepartmentDoc docBean = (DepartmentBean.DepartmentDoc) beanTmp;
                final LinearVViewHolder linearVViewHolder = (LinearVViewHolder) viewHolder;
                if(docBean.mark != null && !docBean.mark.equals("")){
                    linearVViewHolder.mTvTag.setVisibility(View.VISIBLE);
                    linearVViewHolder.mTvTag.setText(docBean.mark);
                }else{
                    linearVViewHolder.mTvTag.setVisibility(View.GONE);
                }
                if(getItemViewType(position) == CalendarDayType.valueOf(CalendarDayType.DOC_V_1)){
                    Picasso.with(DepartmentActivity.this)
                            .load(StringUtils.getUrl(DepartmentActivity.this, docBean.icon.path, DensityUtil.dip2px(90), DensityUtil.dip2px(90), false, true))
                            .resize(DensityUtil.dip2px(90), DensityUtil.dip2px(90))
                            .placeholder(R.drawable.ic_default_avatar_l)
                            .error(R.drawable.ic_default_avatar_l)
                            .centerCrop()
                            .config(Bitmap.Config.RGB_565)
                            .tag(TAG)
                            .into(linearVViewHolder.mIvTitle);
                }else {
                    Picasso.with(DepartmentActivity.this)
                            .load(StringUtils.getUrl(DepartmentActivity.this, docBean.icon.path, DensityUtil.dip2px(173), DensityUtil.dip2px(110), false, true))
                            .resize(DensityUtil.dip2px(173), DensityUtil.dip2px(110))
                            .placeholder(R.drawable.ic_default_video)
                            .error(R.drawable.ic_default_video)
                            .centerCrop()
                            .config(Bitmap.Config.RGB_565)
                            .tag(TAG)
                            .into(linearVViewHolder.mIvTitle);
                }

                TextPaint tp = linearVViewHolder.mTvTitle.getPaint();
                String uiTmp = docBean.ui;
                String[] strs = uiTmp.split("#");
                String ui = strs[1];
                int style = CalendarDayUiType.getType(ui);
                if(!TextUtils.isEmpty(docBean.uiTitle)){
                    linearVViewHolder.mTvFromName.setVisibility(View.VISIBLE);
                    linearVViewHolder.mTvFromName.setText(docBean.uiTitle);
                    linearVViewHolder.mTvFromName.setBackgroundResource(mTagId[StringUtils.getHashOfString(docBean.uiTitle, mTagId.length)]);
                }else {
                    linearVViewHolder.mTvFromName.setVisibility(View.GONE);
                }
                linearVViewHolder.mRlDocLikePack.setVisibility(View.VISIBLE);
                linearVViewHolder.mRlDocCommentPack.setVisibility(View.VISIBLE);
                linearVViewHolder.mTvLikeNum.setText(StringUtils.getNumberInLengthLimit(docBean.likes, 2));
                linearVViewHolder.mTvCommentNum.setText(StringUtils.getNumberInLengthLimit(docBean.comments, 2));
                if(style == CalendarDayUiType.valueOf(CalendarDayUiType.NEWS)){
                    tp.setFakeBoldText(true);
                    linearVViewHolder.mSubtitle.setVisibility(View.VISIBLE);
                    if(linearVViewHolder.mDlView != null) {
                        linearVViewHolder.mDlView.setVisibility(View.GONE);
                    }
                    linearVViewHolder.mSubtitle.setText(docBean.content);
                }else{
                    tp.setFakeBoldText(false);
                    linearVViewHolder.mSubtitle.setVisibility(View.GONE);
                    if(linearVViewHolder.mDlView != null){
                        linearVViewHolder.mDlView.setVisibility(View.GONE);
                    }
                }
                if(style == CalendarDayUiType.valueOf(CalendarDayUiType.MUSIC)){
                    linearVViewHolder.mSubtitle.setVisibility(View.VISIBLE);
                    linearVViewHolder.mSubtitle.setText(docBean.content);
//                    MusicInfo musicInfo = MapActivity.sMusicServiceManager.findMusicInfoByUrl(docBean.musicUrl);
//                    if(musicInfo == null){
//                        musicInfo = new MusicInfo();
//                        musicInfo.musicName = docBean.musicName;
//                        musicInfo.position = position;
//                        musicInfo.url = docBean.musicUrl;
//                        musicInfo.img = docBean.icon.path;
//                        MapActivity.sMusicServiceManager.addMusicInfo(musicInfo);
//                    }else {
//                        musicInfo.position = position;
//                    }
//                    final MusicInfo cur = musicInfo;
                    linearVViewHolder.mIvVideo.setVisibility(View.VISIBLE);
                    if(mPlayer.isPlaying()){
                        Song musicInfo = mPlayer.getPlayingSong();
                        if(musicInfo.getPath().equals(docBean.musicUrl)){
                            linearVViewHolder.mIvVideo.setImageResource(R.drawable.icon_video_stop);
                        }else {
                            linearVViewHolder.mIvVideo.setImageResource(R.drawable.icon_video_play);
                        }
                    }else {
                        linearVViewHolder.mIvVideo.setImageResource(R.drawable.icon_video_play);
                    }
//                    if(cur.playState == IConstants.MPS_PLAYING || cur.playState == IConstants.MPS_PREPARE){
//                        linearVViewHolder.mIvVideo.setImageResource(R.drawable.icon_video_stop);
//                    }else{
//                        linearVViewHolder.mIvVideo.setImageResource(R.drawable.icon_video_play);
//                    }
                    linearVViewHolder.mIvVideo.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
//                            if(cur.playState == IConstants.MPS_PLAYING || cur.playState == IConstants.MPS_PREPARE){
//                                MapActivity.sMusicServiceManager.pause();
//                                linearVViewHolder.mIvVideo.setImageResource(R.drawable.icon_video_play);
//                            }else{
//                                MapActivity.sMusicServiceManager.playByUrl(docBean.musicUrl);
//                                linearVViewHolder.mIvVideo.setImageResource(R.drawable.icon_video_stop);
//                            }
//                            if(position != mPreMusicPosition){
//                                if(mPreMusicPosition != -1){
//                                    DepartmentBean.DepartmentDoc doc1 = (DepartmentBean.DepartmentDoc) getItem(mPreMusicPosition);
//                                    MusicInfo musicInfo = MapActivity.sMusicServiceManager.findMusicInfoByUrl(doc1.musicUrl);
//                                    musicInfo.playState = IConstants.MPS_NOFILE;
//                                    notifyItemChanged(mPreMusicPosition);
//                                }
//                                mPreMusicPosition = position;
//                            }
                            Song musicInfo = mPlayer.getPlayingSong();
                            if(mPlayer.isPlaying() && musicInfo.getPath().equals(docBean.musicUrl)){
                                mPlayer.pause();
                                linearVViewHolder.mIvVideo.setImageResource(R.drawable.icon_video_play);
                            }else if(musicInfo != null && musicInfo.getPath().equals(docBean.musicUrl)){
                                mPlayer.play();
                                linearVViewHolder.mIvVideo.setImageResource(R.drawable.icon_video_stop);
                            }else {
                                Song song = new Song();
                                song.setCoverPath(docBean.icon.path);
                                song.setDisplayName(docBean.musicName);
                                song.setPath(docBean.musicUrl);
                                PlayList playList = new PlayList(song);
                                mPlayer.play(playList,0);
                                linearVViewHolder.mIvVideo.setImageResource(R.drawable.icon_video_stop);
                            }
                            if(position != mPreMusicPosition){
                                if(mPreMusicPosition != -1){
                                    notifyItemChanged(mPreMusicPosition);
                                }
                                mPreMusicPosition = position;
                            }
                        }
                    });
                }else{
                    if(linearVViewHolder.mIvVideo != null){
                        linearVViewHolder.mIvVideo.setVisibility(View.GONE);
                    }
                }
                linearVViewHolder.mTvTitle.setText(docBean.title);
                linearVViewHolder.mTvTime.setText(StringUtils.timeFormate(docBean.updateTime));
                if (linearVViewHolder.mTvName != null){
                    linearVViewHolder.mTvName.setText(docBean.userName);
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
            }else if(viewHolder instanceof NewsViewHolder){
                Object beanTmp = getItem(position);
                DepartmentBean.DepartmentDoc doc = (DepartmentBean.DepartmentDoc) beanTmp;
                final NewsViewHolder newsViewHolder = (NewsViewHolder) viewHolder;
                newsViewHolder.mTvTitle.setText(doc.title);
                newsViewHolder.mTvName.setText(doc.userName);
                newsViewHolder.mTvTime.setText(StringUtils.timeFormate(doc.updateTime));
                newsViewHolder.mTvSubtitle.setText(doc.content);
                newsViewHolder.mTvIconNum.setVisibility(View.GONE);
                newsViewHolder.mIvIcon1.setTag(R.id.id_filebean, doc);
                newsViewHolder.mIvIcon2.setTag(R.id.id_filebean, doc);
                newsViewHolder. mIvIcon3.setTag(R.id.id_filebean, doc);
                newsViewHolder.mRlDocLikePack.setVisibility(View.VISIBLE);
                newsViewHolder.mRlDocCommentPack.setVisibility(View.VISIBLE);
                newsViewHolder.mTvLikeNum.setText(StringUtils.getNumberInLengthLimit(doc.likes, 2));
                newsViewHolder.mTvCommentNum.setText(StringUtils.getNumberInLengthLimit(doc.comments, 2));
                if(!TextUtils.isEmpty(doc.uiTitle)){
                    newsViewHolder.mTvFromName.setVisibility(View.VISIBLE);
                    newsViewHolder.mTvFromName.setText(doc.uiTitle);
                    newsViewHolder.mTvFromName.setBackgroundResource(mTagId[StringUtils.getHashOfString(doc.uiTitle, mTagId.length)]);
                }else {
                    newsViewHolder.mTvFromName.setVisibility(View.GONE);
                }
                if(doc.images.size() > 0){
                    newsViewHolder.mLlImagePack.setVisibility(View.VISIBLE);
                    newsViewHolder.mRlIcon2.setVisibility(View.INVISIBLE);
                    newsViewHolder.mRlIcon3.setVisibility(View.INVISIBLE);
                    if(FileUtil.isGif(doc.images.get(0).path)){
                        newsViewHolder.mIvGifIcon1.setVisibility(View.VISIBLE);
                    }else {
                        newsViewHolder.mIvGifIcon1.setVisibility(View.GONE);
                    }
                    Picasso.with(DepartmentActivity.this)
                            .load(StringUtils.getUrl(DepartmentActivity.this, doc.images.get(0).path, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, false, true))
                            .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                            .placeholder(R.drawable.ic_default_club_l)
                            .error(R.drawable.ic_default_club_l)
                            .centerCrop()
                            .config(Bitmap.Config.RGB_565)
                            .tag(TAG)
                            .into(newsViewHolder.mIvIcon1);
                    newsViewHolder.mIvGifIcon1.setVisibility(View.GONE);
                    if(doc.images.size() > 1){
                        newsViewHolder.mRlIcon2.setVisibility(View.VISIBLE);
                        if(FileUtil.isGif(doc.images.get(1).path)){
                            newsViewHolder.mIvGifIcon2.setVisibility(View.VISIBLE);
                        }else {
                            newsViewHolder.mIvGifIcon2.setVisibility(View.GONE);
                        }
                        Picasso.with(DepartmentActivity.this)
                                .load(StringUtils.getUrl(DepartmentActivity.this, doc.images.get(1).path, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, false, true))
                                .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                                .placeholder(R.drawable.ic_default_club_l)
                                .error(R.drawable.ic_default_club_l)
                                .centerCrop()
                                .config(Bitmap.Config.RGB_565)
                                .tag(TAG)
                                .into(newsViewHolder.mIvIcon2);
                    }
                    if(doc.images.size() > 2){
                        newsViewHolder.mRlIcon3.setVisibility(View.VISIBLE);
                        if(FileUtil.isGif(doc.images.get(2).path)){
                            newsViewHolder.mIvGifIcon3.setVisibility(View.VISIBLE);
                        }else {
                            newsViewHolder.mIvGifIcon3.setVisibility(View.GONE);
                        }
                        Picasso.with(DepartmentActivity.this)
                                .load(StringUtils.getUrl(DepartmentActivity.this,doc.images.get(2).path,(DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3,(DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3,false,true))
                                .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                                .placeholder(R.drawable.ic_default_club_l)
                                .error(R.drawable.ic_default_club_l)
                                .centerCrop()
                                .config(Bitmap.Config.RGB_565)
                                .tag(TAG)
                                .into(newsViewHolder.mIvIcon3);
                    }
                    if(doc.images.size() > 3){
                        newsViewHolder.mTvIconNum.setVisibility(View.VISIBLE);
                        newsViewHolder.mTvIconNum.setText(DepartmentActivity.this.getString(R.string.label_post_icon_num, doc.images.size()));
                    }
                }else{
                    newsViewHolder.mLlImagePack.setVisibility(View.GONE);
                }
                newsViewHolder.itemView.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        int pos = newsViewHolder.getLayoutPosition();
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(newsViewHolder.itemView, pos);
                        }
                    }
                });
            }


        }

        public Object getItem(int position){
            if(position == 0){
                return mBannerBeans.get(mBannerViewHolder.viewPager.getCurrentItem());
            }else if(position == 1){
                return "";
            }else {
                return mDepartmentList.get(position - 2);
            }
        }

        @Override
        public int getItemViewType(int position) {
            int type ;
            if (position == 0 ) {
                type = VIEW_TYPE_BANNER;
            } else if(position == 1){
                type = VIEW_TYPE_FEATURED;
            }else {
                String uiTmp = ((DepartmentBean.DepartmentDoc)getItem(position)).ui;
                String[] strs = uiTmp.split("#");
                String ui = strs[0];
                type = CalendarDayType.getType(ui);
            }
            return type;
        }

        @Override
        public int getItemCount() {
        //    if(mBannerBeans != null && mBannerBeans.size() > 0){
            return mDepartmentList.size() + 2;
      //      }
           // return 0;
        }

        class BannerViewHolder extends RecyclerView.ViewHolder{

            ViewPager viewPager;
            LinearLayout llPointGroup;
            View llPointContainer;
            View pressedPoint;
            int width;

            public BannerViewHolder(View itemView) {
                super(itemView);
                viewPager = (ViewPager) itemView.findViewById(R.id.pager_class_banner);
                llPointGroup = (LinearLayout) itemView.findViewById(R.id.ll_point_group);
                pressedPoint = itemView.findViewById(R.id.view_pressed_point);
                llPointContainer = itemView.findViewById(R.id.ll_point_container);
                if(scheduledExecutorService != null){
                    scheduledExecutorService.shutdown();
                }
                startBanner();
            }
        }

        class BannerAdapter extends PagerAdapter {

            private ArrayList<ImageView> images;

            public BannerAdapter(ArrayList<ImageView> images){
                this.images = images;
            }

            @Override
            public int getCount() {
                return images.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }


            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(images.get(position));
                return images.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        }

        class FeaturedLinearHViewHolder extends RecyclerView.ViewHolder{

            RecyclerView mRvList;
            ClassRecyclerViewAdapter recyclerViewAdapter;

            public FeaturedLinearHViewHolder(View itemView) {
                super(itemView);
                mRvList = (RecyclerView) itemView.findViewById(R.id.rv_class_featured);
                recyclerViewAdapter = new ClassRecyclerViewAdapter(itemView.getContext());
                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                mRvList.setLayoutManager(layoutManager);
                mRvList.setAdapter(recyclerViewAdapter);
            }
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
            TextView mTvFromName;

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
                mTvFromName = (TextView) itemView.findViewById(R.id.tv_post_from_name);
            }
        }

        public class LinearHViewHolder extends RecyclerView.ViewHolder{

            RecyclerView mRvList;
            CalendarRecyclerViewAdapter recyclerViewAdapter;

            public LinearHViewHolder(View itemView) {
                super(itemView);
                mRvList = (RecyclerView) itemView.findViewById(R.id.rv_calender_type3);
                recyclerViewAdapter = new CalendarRecyclerViewAdapter(itemView.getContext());
                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                mRvList.setLayoutManager(layoutManager);
                mRvList.setAdapter(recyclerViewAdapter);
            }
        }

        public class NewsViewHolder extends RecyclerView.ViewHolder{

            TextView mTvTitle;
            TextView mTvName;
            TextView mTvTime;
            TextView mTvSubtitle;
            View mLlImagePack;
            View mRlSpecialTypePack;
            ImageView mIvSpecialTypeIcon;
            TextView mTvVoteBrief;
            View mRlIcon1;
            View mRlIcon2;
            View mRlIcon3;
            View mIvGifIcon1;
            View mIvGifIcon2;
            View mIvGifIcon3;
            ImageView mIvIcon1;
            ImageView mIvIcon2;
            ImageView mIvIcon3;
            TextView mTvIconNum;
            View mRlDocLikePack;
            View mRlDocCommentPack;
            TextView mTvLikeNum;
            TextView mTvCommentNum;
            TextView mTvFromName;

            public NewsViewHolder(View itemView) {
                super(itemView);
                mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
                mTvName = (TextView) itemView.findViewById(R.id.tv_name);
                mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
                mTvSubtitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
                mLlImagePack = itemView.findViewById(R.id.ll_image_3);
                mRlSpecialTypePack = itemView.findViewById(R.id.rl_post_special_flag);
                mIvSpecialTypeIcon = (ImageView) itemView.findViewById(R.id.iv_post_flag_icon);
                mTvVoteBrief = (TextView) itemView.findViewById(R.id.tv_post_vote_brief);
                mRlDocLikePack = itemView.findViewById(R.id.rl_doc_like_pack);
                mRlDocCommentPack = itemView.findViewById(R.id.rl_doc_comment_pack);
                mTvLikeNum = (TextView) itemView.findViewById(R.id.tv_post_pants_num);
                mTvCommentNum = (TextView) itemView.findViewById(R.id.tv_post_comment_num);
                mRlIcon1 = itemView.findViewById(R.id.rl_post_image_1);
                mRlIcon2 = itemView.findViewById(R.id.rl_post_image_2);
                mRlIcon3 = itemView.findViewById(R.id.rl_post_image_3);

                mIvGifIcon1 = itemView.findViewById(R.id.iv_post_image_1_gif_flag);
                mIvGifIcon2 = itemView.findViewById(R.id.iv_post_image_2_gif_flag);
                mIvGifIcon3 = itemView.findViewById(R.id.iv_post_image_3_gif_flag);

                mIvIcon1 = (ImageView)itemView.findViewById(R.id.iv_post_image_1);
                mIvIcon2 = (ImageView)itemView.findViewById(R.id.iv_post_image_2);
                mIvIcon3 = (ImageView)itemView.findViewById(R.id.iv_post_image_3);
                mTvIconNum = (TextView)itemView.findViewById(R.id.tv_post_img_num);
                mTvFromName = (TextView) itemView.findViewById(R.id.tv_post_from_name);
                mIvIcon1.setOnClickListener(mIconListener);
                mIvIcon2.setOnClickListener(mIconListener);
                mIvIcon3.setOnClickListener(mIconListener);
            }


            View.OnClickListener mIconListener = new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int index = 0;
                    if (v.equals(mIvIcon1)){
                        index = 0;
                    } else if (v.equals(mIvIcon2)) {
                        index = 1;
                    } else if (v.equals(mIvIcon3)) {
                        index = 2;
                    }

                    Context context = v.getContext();
                    final DepartmentBean.DepartmentDoc docBean = (DepartmentBean.DepartmentDoc) v.getTag(R.id.id_filebean);
                    Intent intent = new Intent(context, ImageBigSelectActivity.class);
                    intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, docBean.images);
                    intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX, index);
                    // 以后可选择 有返回数据
                    context.startActivity(intent);

                }
            };
        }
    }
}
