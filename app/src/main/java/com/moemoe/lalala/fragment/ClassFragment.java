package com.moemoe.lalala.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
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
import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.CreateNormalDocActivity;
import com.moemoe.lalala.FriendsMainActivity;
import com.moemoe.lalala.ImageBigSelectActivity;
import com.moemoe.lalala.MoemoeApplication;
import com.moemoe.lalala.R;
import com.moemoe.lalala.adapter.ClassRecyclerViewAdapter;
import com.moemoe.lalala.adapter.NewDocLabelAdapter;
import com.moemoe.lalala.callback.MoeMoeCallback;
import com.moemoe.lalala.data.BannerBen;
import com.moemoe.lalala.data.DocItemBean;
import com.moemoe.lalala.data.DocTag;
import com.moemoe.lalala.data.FeaturedBean;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.FileUtil;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yi on 2016/9/23.
 */
@ContentView(R.layout.ac_one_pulltorefresh_list)
public class ClassFragment extends BaseFragment {
    public static final String TAG = "ClassFragment";
    public static final int REQUEST_CODE_CREATE_DOC = 2333;
    public static final String EXTRA_ROOM_ID = "room_id";
    public static final String EXTRA_TAGNAME = "tagName";
    @FindView(R.id.list)
    private PullAndLoadView mListDocs;
    private ClassDocListAdapter mClassAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<DocItemBean> mClassData = new ArrayList<>();
    private ArrayList<BannerBen> mBannerBeans = new ArrayList<>();
    private ArrayList<FeaturedBean> mFeaturedBeans = new ArrayList<>();
    private ClassDocListAdapter.BannerViewHolder mBannerViewHolder;
    private int mCurrentItem = 0;
    private String mLastUuid;
    private String mLastPullUuid;
    private int mSpilitPosition = 0;
    private boolean mShouldChange = true;
    private boolean mIsFling = false;
    private int mFirstVisibleItem;
    private int mLastVisibleItem;
//    @FindView(R.id.view_shadow)
//    private View mViewShadow;
//    @FindView(R.id.iv_class_bg)
//    private ImageView mIvBg;
    @FindView(R.id.iv_send_post)
    private View mSendPost;
    @FindView(R.id.iv_send_music_post)
    private View mSendMusicPost;

    private ScheduledExecutorService scheduledExecutorService;
    private DbManager db;
    private String mRoomId;
    private MoeMoeCallback allFinishCallback;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mBannerViewHolder.viewPager != null && mShouldChange) {
                mBannerViewHolder.viewPager.setCurrentItem(mCurrentItem, true);
            }
        }
    };
    private boolean mIsHasLoadedAll = false;
    private boolean mIsLoading = false;
    private int mScrollHeight;
    private Drawable mActionBarBackground;
    private boolean mIsPullDown;

    /**
     * 开始循环banner
     *
     * @author Haru
     */
    private void startBanner() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new BannerRunnable(), 3, 3, TimeUnit.SECONDS);
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
    public void onDestroyView() {
        super.onDestroyView();
        Picasso.with(getActivity()).cancelTag(TAG);
        if(scheduledExecutorService != null) scheduledExecutorService.shutdown();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = Utils.getDb(MoemoeApplication.sDaoConfig);
        mLastUuid = PreferenceManager.getInstance(getActivity()).getCurrentRead();
        mRoomId = "CLASSROOM";
       // mIvBg.setBackgroundColor(getActivity().getResources().getColor(R.color.bg_activity));
        mSwipeRefreshLayout = mListDocs.getSwipeRefreshLayout();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
        mRecyclerView = mListDocs.getRecyclerView();
        mClassAdapter = new ClassDocListAdapter();
        mRecyclerView.setAdapter(mClassAdapter);
        mClassAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object object = mClassAdapter.getItem(position);
                if (object != null) {

                    if (object instanceof DocItemBean) {
                        DocItemBean bean = (DocItemBean) object;
                        if (!TextUtils.isEmpty(bean.doc.schema)) {
                            Uri uri = Uri.parse(bean.doc.schema);
                            IntentUtils.toActivityFromUri(getActivity(), uri,view);
                        }
                    }
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
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int curY = 0;
            boolean isChange = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    mIsFling = true;
                } else {
                    mIsFling = false;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Picasso.with(getActivity()).resumeTag(TAG);
                } else {
                    Picasso.with(getActivity()).pauseTag(TAG);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                curY += dy;
                if (isChange) {
                    if (dy > 10) {
                        sendBtnOut();
                        isChange = false;
                    }
                } else {
                    if (dy < -10) {
                        sendBtnIn();
                        isChange = true;
                    }
                }
            }
        });
        loadDataFromDb();
        mListDocs.initLoad();
        mSendPost.setVisibility(View.VISIBLE);
        mSendPost.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                go2CreateDoc(CreateNormalDocActivity.TYPE_IMG_DOC);
            }
        });
        mSendMusicPost.setVisibility(View.VISIBLE);
        mSendMusicPost.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                go2CreateDoc(CreateNormalDocActivity.TYPE_MUSIC_DOC);
            }
        });
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

    private void sendBtnIn(){
        ObjectAnimator sendPostIn = ObjectAnimator.ofFloat(mSendPost,"translationY",mSendPost.getHeight()+ DensityUtil.dip2px(10),0).setDuration(300);
        sendPostIn.setInterpolator(new OvershootInterpolator());
        ObjectAnimator sendMusicIn = ObjectAnimator.ofFloat(mSendMusicPost,"translationY",mSendMusicPost.getHeight()+DensityUtil.dip2px(10),0).setDuration(300);
        sendMusicIn.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(sendPostIn).with(sendMusicIn);
        set.start();
    }

    private void sendBtnOut(){
        ObjectAnimator sendPostOut = ObjectAnimator.ofFloat(mSendPost,"translationY",0,mSendPost.getHeight()+DensityUtil.dip2px(10)).setDuration(300);
        sendPostOut.setInterpolator(new OvershootInterpolator());
        ObjectAnimator sendMusicOut = ObjectAnimator.ofFloat(mSendMusicPost,"translationY",0,mSendMusicPost.getHeight()+DensityUtil.dip2px(10)).setDuration(300);
        sendMusicOut.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(sendPostOut).with(sendMusicOut);
        set.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == CreateNormalDocActivity.RESPONSE_CODE){
            mRecyclerView.scrollToPosition(0);
            requestDocAndClubList(0, true);
        }
    }

    /**
     * 前往创建帖子界面
     */
    private void go2CreateDoc(int type){
        // 检查是否登录，是否关注，然后前面创建帖子界面
        if (DialogUtils.checkLoginAndShowDlg(getActivity())){
            Intent intent = new Intent(getActivity(), CreateNormalDocActivity.class);
            intent.putExtra(CreateNormalDocActivity.TYPE_CREATE,type);
            startActivityForResult(intent, REQUEST_CODE_CREATE_DOC);
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
                requestDocAndClubList(mClassData.size(), mIsPullDown);
            }
            return null;
        }
    }

    public void setRefreshing(){
        if(mListDocs != null){
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private void loadDataFromDb(){
        try {
            BannerBen topAdBean = db.selector(BannerBen.class)
                    .where("uuid","=","cache" + mRoomId)
                    .findFirst();
            FeaturedBean featuredBean = db.selector(FeaturedBean.class)
                    .where("uuid","=","cache" + mRoomId)
                    .findFirst();
            DocItemBean classDataBean = db.selector(DocItemBean.class)
                    .where("id", "=", "cache" + mRoomId)
                    .findFirst();
            if(topAdBean != null && classDataBean != null && topAdBean.json != null && classDataBean.json != null && featuredBean != null && featuredBean.json != null){
                mBannerBeans = BannerBen.readFromJsonList(getActivity(), topAdBean.json);
                mFeaturedBeans = FeaturedBean.readFromJsonList(getActivity(), featuredBean.json);
                ArrayList<DocItemBean> beans = DocItemBean.readFromJsonList(getActivity(), classDataBean.json);
                if (mClassData != null && mClassData.size() > 0) {
                    mSpilitPosition = 0;
                    mLastPullUuid = mClassData.get(0).doc.id;
                }
                mClassData.clear();
                mClassData.addAll(beans);
                mClassAdapter.notifyDataSetChanged();
            }
        } catch (DbException e) {

        }
    }

    private void requestFeatured(){
        Otaku.getDocV2().requestFeatured(PreferenceManager.getInstance(getActivity()).getToken(), mRoomId).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                mFeaturedBeans.clear();
                mFeaturedBeans = FeaturedBean.readFromJsonList(getActivity(), s);
                FeaturedBean adBean = new FeaturedBean();
                adBean.uuid = "cache" + mRoomId;
                adBean.json = s;
                try {
                    db.saveOrUpdate(adBean);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                mClassAdapter.notifyItemChanged(1);
                allFinishCallback.onSuccess();
            }

            @Override
            public void failure(String e) {

                allFinishCallback.onSuccess();
            }
        }));
    }

    /**
     * 请求banner数据
     *
     * @author Haru
     */
    private void requestBannerData() {
        if(!NetworkUtils.checkNetworkAndShowError(getActivity())){
            return;
        }
        Otaku.getDocV2().requestNewBanner(PreferenceManager.getInstance(getActivity()).getToken(),mRoomId).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                mBannerBeans.clear();
                mBannerBeans = BannerBen.readFromJsonList(getActivity(), s);
                BannerBen adBean = new BannerBen();
                adBean.uuid = "cache" + mRoomId;
                adBean.json = s;
                try {
                    db.saveOrUpdate(adBean);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                mClassAdapter.notifyItemChanged(0);
                allFinishCallback.onSuccess();
            }

            @Override
            public void failure(String e) {

                allFinishCallback.onSuccess();
            }
        }));
    }

    /**
     * 去重
     * @param oriData
     * @param newData
     * @author Haru
     */
    public void addNewDataToList(ArrayList<DocItemBean> oriData, ArrayList<DocItemBean> newData) {
        if (newData != null && oriData != null && newData.size() > 0) {
            for (DocItemBean news : newData) {
                boolean has = false;
                for (int i = 0; i < oriData.size(); i++) {
                    if (oriData.get(i).doc.id.equals(news.doc.id)) {
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    oriData.add(news);
                }
            }
        }
    }

    /**
     * 请求class主页除banner外数据
     * @param index
     * @param isPullDown
     * @author Haru
     */
    private void requestDocAndClubList(final int index, final boolean isPullDown) {
        Otaku.getDocV2().requestClassList(PreferenceManager.getInstance(getActivity()).getToken(),index, Otaku.LENGTH,mRoomId).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                ArrayList<DocItemBean> beans = DocItemBean.readFromJsonList(getActivity(), s);
                if (isPullDown) {
                    DocItemBean classDataBean = new DocItemBean();
                    classDataBean.id = "cache" + mRoomId;
                    classDataBean.json = s;
                    try {
                        db.saveOrUpdate(classDataBean);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    if (mClassData != null && mClassData.size() > 0) {
                        mSpilitPosition = 0;
                        mLastPullUuid = mClassData.get(0).doc.id;
                    }
                    int bfSize = mClassData.size();
                    mClassData.clear();
                    mClassData.addAll(beans);
                    //mClassAdapter.notifyDataSetChanged();
                    int afSize = mClassData.size();
                    if(bfSize == 0){
                        mClassAdapter.notifyItemRangeInserted(2,mClassData.size());
                    }else {
                        mClassAdapter.notifyItemRangeChanged(2,mClassData.size());
                        if(bfSize - afSize > 0){
                            mClassAdapter.notifyItemRangeRemoved(afSize + 2,bfSize - afSize);
                        }
                    }

                } else {
                    int bfSize = mClassData.size();
                    addNewDataToList(mClassData, beans);
                    int afSize = mClassData.size();
                    //mClassAdapter.notifyDataSetChanged();
                    mClassAdapter.notifyItemRangeInserted(bfSize + 2,afSize - bfSize);
                }

                if(index != 0){
                    if (beans.size() != 0) {
                        mListDocs.isLoadMoreEnabled(true);
                    } else {
//                    mListDocs.isLoadMoreEnabled(false);
//                    mIsHasLoadedAll = true;
                        ToastUtil.showCenterToast(getActivity(),R.string.msg_all_load_down);
                    }
                }
                allFinishCallback.onSuccess();
            }

            @Override
            public void failure(String e) {
                allFinishCallback.onSuccess();
            }
        }));
    }

    public void blurBackGroundByAnim(){
        // mViewShadow.setVisibility(View.VISIBLE);
        listAnim(true,true);
//        mIvBg.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                mIvBg.getViewTreeObserver().removeOnPreDrawListener(this);
//                mIvBg.buildDrawingCache();
//
//                Bitmap bmp = mIvBg.getDrawingCache();
//                BitmapBlur.blur(getActivity(), bmp, mIvBg);
//                return true;
//            }
//        });
    }

    public void blurBackGroundNoAnim(){
        //mViewShadow.setVisibility(View.VISIBLE);
        listAnim(true,false);
//        mIvBg.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                mIvBg.getViewTreeObserver().removeOnPreDrawListener(this);
//                mIvBg.buildDrawingCache();
//
//                Bitmap bmp = mIvBg.getDrawingCache();
//                BitmapBlur.blur(getActivity(),bmp,mIvBg);
//                return true;
//            }
//        });
    }

    public boolean normalBackGroundNoAnim(){
        if(!mIsFling){
            // mViewShadow.setVisibility(View.GONE);
            listAnim(false, false);
            // mIvBg.setImageResource(R.drawable.bg_main_clas);
        }
        return mIsFling;
    }

    public boolean normalBackGroundByAnim(){
        if(!mIsFling){
            //mViewShadow.setVisibility(View.GONE);
            listAnim(false, true);
            //mIvBg.setImageDrawable(mBGDrawable);
            // mIvBg.setImageResource(R.drawable.bg_main_clas);
        }
        return mIsFling;
    }

    public void listAnim(final boolean isIn,boolean shouldAnim){
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mFirstVisibleItem = manager.findFirstVisibleItemPosition();
        mLastVisibleItem = manager.findLastVisibleItemPosition();
        if(isIn){
            mShouldChange = true;
        }else{
            mShouldChange = false;
        }
        if(shouldAnim){
            for(int i = 0;i <= mLastVisibleItem - mFirstVisibleItem;i++){
                final View item = mRecyclerView.getChildAt(i);
                Animation animation;
                if(isIn){
                    animation = AnimationUtils.loadAnimation(getActivity(), R.anim.list_anim_in);
                }else{
                    animation = AnimationUtils.loadAnimation(getActivity(), R.anim.list_anim_out);
                }
                animation.setFillAfter(true);
                animation.setStartOffset(100 * i);
                if(item != null){
                    item.startAnimation(animation);
                }
            }
            mRecyclerView.setVisibility(View.VISIBLE);
        }else{
            if(isIn){
                mRecyclerView.setVisibility(View.VISIBLE);
            }else{
                mRecyclerView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    private class ClassDocListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private static final int VIEW_TYPE_BANNER = 0;
        private static final int VIEW_TYPE_DOC= 1;
        private static final int VIEW_TYPE_FEATURED= 2;
        private LayoutInflater mInflater;
        private OnItemClickListener mOnItemClickListener;
        private int[] mTagId = {R.drawable.btn_class_from_orange,R.drawable.btn_class_from_blue,
                R.drawable.btn_class_from_green,R.drawable.btn_class_from_pink,R.drawable.btn_class_from_yellow};

        public ClassDocListAdapter(){
            mInflater = LayoutInflater.from(getActivity());
        }

        public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == VIEW_TYPE_BANNER){
                return new ClassDocListAdapter.BannerViewHolder(mInflater.inflate(R.layout.item_banner_class,parent,false));
            } else if(viewType == VIEW_TYPE_DOC){
                return new ClassDocListAdapter.DocViewHolder(mInflater.inflate(R.layout.item_doc_club_class,parent,false));
            } else if(viewType == VIEW_TYPE_FEATURED){
                return new ClassDocListAdapter.LinearHViewHolder(mInflater.inflate(R.layout.item_class_featured,parent,false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
            if(viewHolder instanceof ClassDocListAdapter.LinearHViewHolder){
                ClassDocListAdapter.LinearHViewHolder linearHViewHolder = (ClassDocListAdapter.LinearHViewHolder) viewHolder;
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
                            IntentUtils.toActivityFromUri(getActivity(),uri,view);
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                    }
                });
            }else if(viewHolder instanceof ClassDocListAdapter.BannerViewHolder){
                if(mBannerBeans.size() == 0){
                    viewHolder.itemView.setVisibility(View.GONE);
                    return;
                }else {
                    viewHolder.itemView.setVisibility(View.VISIBLE);
                }
                mBannerViewHolder = (ClassDocListAdapter.BannerViewHolder) viewHolder;
                ArrayList<ImageView> imageViews = new ArrayList<>();
                mBannerViewHolder.llPointGroup.removeAllViews();
                for(int i=0;i<mBannerBeans.size();i++) {
                    final BannerBen bean = mBannerBeans.get(i);
                    ImageView image = new ImageView(getActivity());
                    Picasso.with(getActivity())
                            .load(StringUtils.getUrl(getActivity(), bean.bg.path, DensityUtil.getScreenWidth(), DensityUtil.dip2px(150), false, false))
                            .fit()
                            .placeholder(R.drawable.ic_default_banner)
                            .error(R.drawable.ic_default_banner)
                            .tag(TAG)
                            .config(Bitmap.Config.RGB_565)
                            .into(image);
                    imageViews.add(image);
                    if(mBannerBeans.size() > 1){
                        View view = new View(getActivity());
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
                                IntentUtils.toActivityFromUri(getActivity(), uri,v);
                            }
                        }
                    });
                }
                mBannerViewHolder.viewPager.setAdapter(new ClassDocListAdapter.BannerAdapter(imageViews));
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
            }else if(viewHolder instanceof ClassDocListAdapter.DocViewHolder){
                Object o = getItem(position);
                if(o instanceof DocItemBean){
                    final DocItemBean post = (DocItemBean) o;
                    final ClassDocListAdapter.DocViewHolder holder = (ClassDocListAdapter.DocViewHolder) viewHolder;

                    if(holder.ivClubCreatorFlag != null){
                        holder.ivClubCreatorFlag.setVisibility(View.GONE);
                        holder.tvCreatorName.setSelected(false);
                    }
                    if(holder.docLabel != null && post.tags != null){
                        holder.docLabel.setDocLabelAdapter(holder.docLabelAdapter);
                        holder.docLabelAdapter.setData(post.tags,false);
//                        holder.docLabel.setItemClickListener(new DocLabelView.LabelItemClickListener() {
//
//                            @Override
//                            public void itemClick(int position) {
//                                if (position < post.tags.size()) {
//                                    holder.plusLabel(post, position);
//                                }
//                            }
//                        });
                        if(post.tags.size()>0) {
                            holder.docLabel.setVisibility(View.VISIBLE);
                            holder.vDocSep.setVisibility(View.VISIBLE);
                        }else{
                            holder.docLabel.setVisibility(View.GONE);
                            holder.vDocSep.setVisibility(View.GONE);
                        }
                    }else {
                        holder.docLabel.setVisibility(View.GONE);
                        holder.vDocSep.setVisibility(View.GONE);
                    }

                    holder.ivLevelColor.setBackgroundColor(post.user.level_color);
                    holder.tvLevel.setText(post.user.level + "");
                    holder.tvLevel.setTextColor(post.user.level_color);
                    holder.tvCreatorName.setText(post.user.nickname);
                    if(holder.ivCreatorAvatar != null){
                        Picasso.with(getActivity())
                                .load(StringUtils.getUrl(getActivity(), post.user.icon.path, DensityUtil.dip2px(44), DensityUtil.dip2px(44), false, false))
                                .resize(DensityUtil.dip2px(44), DensityUtil.dip2px(44))
                                .placeholder(R.drawable.ic_default_avatar_m)
                                .error(R.drawable.ic_default_avatar_m)
                                .centerCrop()
                                .tag(TAG)
                                .config(Bitmap.Config.RGB_565)
                                .into(holder.ivCreatorAvatar);
                        holder.ivCreatorAvatar.setTag(R.id.id_creator_uuid, post.user.id);
                    }
                    holder.ivClubCreatorFlag.setVisibility(View.GONE);
                    holder.tvCreatorName.setSelected(false);
                    holder.ivClubCreatorFlag.setVisibility(View.GONE);
                    holder.tvCreatorName.setSelected(false);
                    if(TextUtils.isEmpty(post.doc.title)){
                        holder.tvPostTitle.setVisibility(View.GONE);
                    }else{
                        holder.tvPostTitle.setVisibility(View.VISIBLE);
                        holder. tvPostTitle.setText(post.doc.title);
                    }
                    // 点赞/评论
                    holder.tvCommentNum.setText(StringUtils.getNumberInLengthLimit(post.doc.comments, 3));
                    holder.tvPantsNum.setText(StringUtils.getNumberInLengthLimit(post.doc.likes, 3));
                    // 时间,内容
                    holder.tvPostDate.setText(StringUtils.timeFormate(post.doc.updateTime));
                    holder.tvPostBrief.setText(post.doc.content);
                    // 加载特殊帖子样式：投票，视频
                    holder.rlSpecialTypePack.setVisibility(View.GONE);
                    // 加载图片
                    holder.ivIcon1.setImageBitmap(null);
                    holder.ivIcon2.setImageBitmap(null);
                    holder.ivIcon3.setImageBitmap(null);
                    holder.ivIcon1.setTag(R.id.id_filebean, post);
                    holder.ivIcon2.setTag(R.id.id_filebean, post);
                    holder. ivIcon3.setTag(R.id.id_filebean, post);
                    holder.rlIcon1.setVisibility(View.INVISIBLE);
                    holder. rlIcon2.setVisibility(View.INVISIBLE);
                    if (holder.rlSpecialTypePack.getVisibility() == View.VISIBLE) {
                        holder. rlIcon3.setVisibility(View.GONE);
                    } else {
                        holder.rlIcon3.setVisibility(View.INVISIBLE);
                    }
                    holder.tvIconNum.setVisibility(View.GONE);
                    holder.tvPostFromName.setTag(R.id.id_filebean, post);
                    holder.ivDocHot.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(post.doc.music.url)){
                        holder.rlMusicRoot.setVisibility(View.VISIBLE);
                        holder.llImagePack.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity())
                                .load(StringUtils.getUrl(getActivity(), post.doc.music.cover.path, DensityUtil.dip2px(90), DensityUtil.dip2px(90), false, true))
                                .resize(DensityUtil.dip2px(90), DensityUtil.dip2px(90))
                                .placeholder(R.drawable.ic_default_avatar_l)
                                .error(R.drawable.ic_default_avatar_l)
                                .centerCrop()
                                .tag(TAG)
                                .config(Bitmap.Config.RGB_565)
                                .into(holder.musicImg);
                        holder.musicTitle.setText(post.doc.music.name);
                    }else {
                        holder.rlMusicRoot.setVisibility(View.GONE);
                        if (post.doc.images != null && post.doc.images.size() > 0) {
                            holder.llImagePack.setVisibility(View.VISIBLE);
                            holder.rlIcon1.setVisibility(View.VISIBLE);
                            holder.rlIcon1.setVisibility(View.VISIBLE);
                            if (FileUtil.isGif(post.doc.images.get(0).path)) {
                                holder.ivGifIcon1.setVisibility(View.VISIBLE);
                            } else {
                                holder.ivGifIcon1.setVisibility(View.GONE);
                            }
                            Picasso.with(getActivity())
                                    .load(StringUtils.getUrl(getActivity(), post.doc.images.get(0).path, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, false, true))
                                    .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                                    .placeholder(R.drawable.ic_default_club_l)
                                    .error(R.drawable.ic_default_club_l)
                                    .centerCrop()
                                    .tag(TAG)
                                    .config(Bitmap.Config.RGB_565)
                                    .into(holder.ivIcon1);
                            if(post.doc.images.size() > 1){
                                holder.rlIcon2.setVisibility(View.VISIBLE);
                                if (FileUtil.isGif(post.doc.images.get(1).path)) {
                                    holder.ivGifIcon2.setVisibility(View.VISIBLE);
                                } else {
                                    holder.ivGifIcon2.setVisibility(View.GONE);
                                }
                                Picasso.with(getActivity())
                                        .load(StringUtils.getUrl(getActivity(), post.doc.images.get(1).path, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, false, true))
                                        .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                                        .placeholder(R.drawable.ic_default_club_l)
                                        .error(R.drawable.ic_default_club_l)
                                        .centerCrop()
                                        .tag(TAG)
                                        .config(Bitmap.Config.RGB_565)
                                        .into(holder.ivIcon2);
                            }
                            // 是否显示第三张图
                            if(holder.rlSpecialTypePack.getVisibility() != View.VISIBLE && post.doc.images.size() > 2){
                                holder.rlIcon3.setVisibility(View.VISIBLE);
                                if (FileUtil.isGif(post.doc.images.get(2).path)) {
                                    holder.ivGifIcon3.setVisibility(View.VISIBLE);
                                } else {
                                    holder.ivGifIcon3.setVisibility(View.GONE);
                                }
                                Picasso.with(getActivity())
                                        .load(StringUtils.getUrl(getActivity(), post.doc.images.get(2).path, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3,false,true))
                                        .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                                        .placeholder(R.drawable.ic_default_club_l)
                                        .error(R.drawable.ic_default_club_l)
                                        .centerCrop()
                                        .tag(TAG)
                                        .config(Bitmap.Config.RGB_565)
                                        .into(holder.ivIcon3);
                            }
                            // 是否显示  “共xx张图”
                            if(post.doc.images.size() > 3 || (holder.rlSpecialTypePack.getVisibility() == View.VISIBLE && post.doc.images.size() > 2)){
                                holder.tvIconNum.setVisibility(View.VISIBLE);
                                holder.tvIconNum.setText(getActivity().getString(R.string.label_post_icon_num, post.doc.images.size()));
                            }
                            //  }
                        }else if (holder.rlSpecialTypePack.getVisibility() == View.VISIBLE){
                            // 没有图片，只有投票或者视频
                            holder.llImagePack.setVisibility(View.VISIBLE);
                        } else {
                            holder.llImagePack.setVisibility(View.GONE);
                        }
                    }
                    holder.itemView.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            int pos = holder.getLayoutPosition();
                            if (mOnItemClickListener != null) {
                                mOnItemClickListener.onItemClick(holder.itemView, pos);
                            }
                        }
                    });
                }
            }
        }

        public Object getItem(int position){
            if(position == 0){
                return mBannerBeans.get(mBannerViewHolder.viewPager.getCurrentItem());
            }else if(position == 1){
                return "";
            }else {
                return mClassData.get(position - 2);
            }
        }

        @Override
        public int getItemViewType(int position) {
            int type ;
            if (position == 0) {
                type = VIEW_TYPE_BANNER;
            } else if(position == 1){
                type = VIEW_TYPE_FEATURED;
            } else{
                type = VIEW_TYPE_DOC;
            }
            return type;
        }

        @Override
        public int getItemCount() {
//            if(mBannerBeans != null && mBannerBeans.size() > 0){
//                if(mSpilitPosition != 0){
//                    return mClassData.size() + 3;
//                }else {
//                    return mClassData.size() + 2;
//                }
//            }
            return mClassData.size() + 2;
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

        private View.OnClickListener mAvatarListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String uuid = (String) v.getTag(R.id.id_creator_uuid);
                if (!TextUtils.isEmpty(uuid)) {
                    Intent intent = new Intent(getActivity(), FriendsMainActivity.class);
                    intent.putExtra(BaseActivity.EXTRA_KEY_UUID, uuid);
                    startActivity(intent);
                }
            }
        };

        private View.OnClickListener mFromNameListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Context context = v.getContext();
//                final DocItemBean docBean = (DocItemBean) v.getTag(R.id.id_filebean);
//                Intent intent = new Intent(context, ClubPostListActivity.class);
//                intent.putExtra(BaseActivity.EXTRA_KEY_UUID, docBean.doc.id);
//                context.startActivity(intent);
            }
        };

        class LinearHViewHolder extends RecyclerView.ViewHolder{

            RecyclerView mRvList;
            ClassRecyclerViewAdapter recyclerViewAdapter;

            public LinearHViewHolder(View itemView) {
                super(itemView);
                mRvList = (RecyclerView) itemView.findViewById(R.id.rv_class_featured);
                recyclerViewAdapter = new ClassRecyclerViewAdapter(itemView.getContext());
                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                mRvList.setLayoutManager(layoutManager);
                mRvList.setAdapter(recyclerViewAdapter);
            }
        }

        class DocViewHolder extends RecyclerView.ViewHolder{
            public ImageView ivDocHot;
            public ImageView ivCreatorAvatar;
            @FindView(R.id.tv_post_creator_name)
            public TextView tvCreatorName;
            public View ivClubCreatorFlag;
            @FindView(R.id.tv_post_update_time)
            public TextView tvPostDate;
            @FindView(R.id.tv_post_title)
            public TextView tvPostTitle;
            @FindView(R.id.tv_post_brief)
            public TextView tvPostBrief;
            @FindView(R.id.ll_image_3)
            public View llImagePack;
            @FindView(R.id.rl_post_special_flag)
            public View rlSpecialTypePack;
            @FindView(R.id.iv_post_flag_icon)
            public ImageView ivSpecialTypeIcon;
            @FindView(R.id.tv_post_vote_brief)
            public TextView tvVoteBrief;
            @FindView(R.id.rl_post_image_1)
            public View rlIcon1;
            @FindView(R.id.rl_post_image_2)
            public View rlIcon2;
            @FindView(R.id.rl_post_image_3)
            public View rlIcon3;
            @FindView(R.id.iv_post_image_1_gif_flag)
            public View ivGifIcon1;
            @FindView(R.id.iv_post_image_2_gif_flag)
            public View ivGifIcon2;
            @FindView(R.id.iv_post_image_3_gif_flag)
            public View ivGifIcon3;
            @FindView(R.id.iv_post_image_1)
            public ImageView ivIcon1;
            @FindView(R.id.iv_post_image_2)
            public ImageView ivIcon2;
            @FindView(R.id.iv_post_image_3)
            public ImageView ivIcon3;
            @FindView(R.id.tv_post_img_num)
            public TextView tvIconNum;
            @FindView(R.id.iv_pants)
            public ImageView ivPants;
            @FindView(R.id.tv_post_comment_num)
            public TextView tvCommentNum;
            @FindView(R.id.tv_post_pants_num)
            public TextView tvPantsNum;
            @FindView(R.id.view_flag_recommend)
            public View ivFlagRecommand;
            @FindView(R.id.rl_music_root)
            public View rlMusicRoot;
            @FindView(R.id.iv_item_image)
            public MyRoundedImageView musicImg;
            @FindView(R.id.tv_music_title)
            public TextView musicTitle;
            public View ivLevelColor;
            public TextView tvLevel;
            public TextView tvPostFromName;
            public ImageView ivIconClassOffical;
            //标签
            public View vDocSep;
            public DocLabelView docLabel;
            public NewDocLabelAdapter docLabelAdapter;

            public DocViewHolder(View itemView) {
                super(itemView);
                ivClubCreatorFlag = itemView.findViewById(R.id.iv_post_owner_flag);
                ivLevelColor = itemView.findViewById(R.id.iv_level_bg);
                tvLevel = (TextView)itemView.findViewById(R.id.tv_level);
                vDocSep = itemView.findViewById(R.id.view_doc_sep);
                docLabel = (DocLabelView) itemView.findViewById(R.id.dv_doc_label_root);
                tvPostFromName = (TextView) itemView.findViewById(R.id.tv_post_bottom_from_name);
                ivDocHot = (ImageView) itemView.findViewById(R.id.iv_class_doc_hot);
                ivIconClassOffical = (ImageView) itemView.findViewById(R.id.iv_class_post_img);
                Utils.view().inject(this, itemView);
                ivCreatorAvatar = (ImageView) itemView.findViewById(R.id.iv_post_creator);
                if (ivCreatorAvatar != null) {
                    ivCreatorAvatar.setOnClickListener(mAvatarListener);
                }
                docLabelAdapter = new NewDocLabelAdapter(getActivity(),true);
                ivIcon1.setOnClickListener(mIconListener);
                ivIcon2.setOnClickListener(mIconListener);
                ivIcon3.setOnClickListener(mIconListener);
                tvPostFromName.setOnClickListener(mFromNameListener);
                tvPostFromName.setVisibility(View.GONE);
            }

            private View.OnClickListener mIconListener = new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int index = 0;
                    if (v.equals(ivIcon1)){
                        index = 0;
                    } else if (v.equals(ivIcon2)) {
                        index = 1;
                    } else if (v.equals(ivIcon3)) {
                        index = 2;
                    }else if(v.equals(ivIconClassOffical)){
                        index = 0;
                    }

                    Context context = v.getContext();
                    final DocItemBean docBean = (DocItemBean) v.getTag(R.id.id_filebean);
                    Intent intent = new Intent(context, ImageBigSelectActivity.class);
                    intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, docBean.doc.images);
                    intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX, index);
                    // 以后可选择 有返回数据
                    context.startActivity(intent);

                }
            };

            private void plusLabel(final DocItemBean post, final int position){
                if (!NetworkUtils.checkNetworkAndShowError(getActivity())) {
                    return;
                }
                if (post != null) {
                    if (DialogUtils.checkLoginAndShowDlg(getActivity())) {
                        final DocTag tagBean = post.tags.get(position);
                        if(tagBean.liked){
                            ((BaseActivity)getActivity()).createDialog();
                            Otaku.getDocV2().dislikeNewTag(PreferenceManager.getInstance(getActivity()).getToken(), tagBean.id, post.doc.id).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                                @Override
                                public void success(String token, String s) {
                                    ((BaseActivity)getActivity()).finalizeDialog();
                                    post.tags.remove(position);
                                    tagBean.liked = false;
                                    tagBean.likes--;
                                    if (tagBean.likes > 0) {
                                        post.tags.add(position, tagBean);
                                    }
                                    if (post.tags.size() > 0) {
                                        vDocSep.setVisibility(View.VISIBLE);
                                    } else {
                                        vDocSep.setVisibility(View.GONE);
                                    }
                                    docLabelAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void failure(String e) {
                                    ((BaseActivity)getActivity()).finalizeDialog();
                                }
                            }));
                        }else {
                            ((BaseActivity)getActivity()).createDialog();
                            Otaku.getDocV2().likeNewTag(PreferenceManager.getInstance(getActivity()).getToken(), tagBean.id, post.doc.id).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                                @Override
                                public void success(String token, String s) {
                                    ((BaseActivity)getActivity()).finalizeDialog();
                                    post.tags.remove(position);
                                    tagBean.liked = true;
                                    tagBean.likes++;
                                    post.tags.add(position, tagBean);
                                    docLabelAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void failure(String e) {
                                    ((BaseActivity)getActivity()).finalizeDialog();
                                }
                            }));
                        }
                    }
                }
            }
        }

        class SplitViewHolder extends RecyclerView.ViewHolder{

            private TextView tv;
            public SplitViewHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.tv_time_table);
            }
        }
    }
}
