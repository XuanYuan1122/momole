package com.moemoe.lalala.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.CreateNormalDocActivity;
import com.moemoe.lalala.FriendsMainActivity;
import com.moemoe.lalala.ImageBigSelectActivity;
import com.moemoe.lalala.MoemoeApplication;
import com.moemoe.lalala.R;
import com.moemoe.lalala.adapter.ClassRecyclerViewAdapter;
import com.moemoe.lalala.adapter.NewDocLabelAdapter;
import com.moemoe.lalala.callback.MoeMoeCallback;
import com.moemoe.lalala.data.BannerBean;
import com.moemoe.lalala.data.DocListBean;
import com.moemoe.lalala.data.FeaturedBean;
import com.moemoe.lalala.network.OneParameterCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.DocLabelView;
import com.moemoe.lalala.view.MyRoundedImageView;
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
    @ViewInject(R.id.list)
    private PullAndLoadView mListDocs;
    private ClassDocListAdapter mClassAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<DocListBean> mClassData = new ArrayList<>();
    private ArrayList<BannerBean> mBannerBeans = new ArrayList<>();
    private ArrayList<FeaturedBean> mFeaturedBeans = new ArrayList<>();
    private ClassDocListAdapter.BannerViewHolder mBannerViewHolder;
    private int mCurrentItem = 0;
    @ViewInject(R.id.iv_send_post)
    private View mSendPost;
    @ViewInject(R.id.iv_send_music_post)
    private View mSendMusicPost;

    private ScheduledExecutorService scheduledExecutorService;
    private DbManager db;
    private String mRoomId;
    private MoeMoeCallback allFinishCallback;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mBannerViewHolder.viewPager != null) {
                mBannerViewHolder.viewPager.setCurrentItem(mCurrentItem, true);
            }
        }
    };
    private boolean mIsHasLoadedAll = false;
    private boolean mIsLoading = false;
    private boolean mIsPullDown;

    /**
     * 开始循环banner
     */
    private void startBanner() {
        if(scheduledExecutorService != null) scheduledExecutorService.shutdown();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new BannerRunnable(), 3, 3, TimeUnit.SECONDS);
    }

    private class BannerRunnable implements Runnable {

        @Override
        public void run() {
            if(mBannerViewHolder.viewPager != null){
                synchronized (ClassFragment.class) {
                    mCurrentItem = (mBannerViewHolder.viewPager.getCurrentItem() + 1) % mBannerBeans.size();
                    handler.obtainMessage().sendToTarget();
                }
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
        db = x.getDb(MoemoeApplication.sDaoConfig);
        mRoomId = "CLASSROOM";
        SwipeRefreshLayout mSwipeRefreshLayout = mListDocs.getSwipeRefreshLayout();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
        mRecyclerView = mListDocs.getRecyclerView();
        mClassAdapter = new ClassDocListAdapter();
        mRecyclerView.setAdapter(mClassAdapter);
        mClassAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object object = mClassAdapter.getItem(position);
                if (object != null) {

                    if (object instanceof DocListBean) {
                        DocListBean bean = (DocListBean) object;
                        if (!TextUtils.isEmpty(bean.getDesc().getSchema())) {
                            Uri uri = Uri.parse(bean.getDesc().getSchema());
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
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int curY = 0;
            boolean isChange = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
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

    private void loadDataFromDb(){
        try {
            BannerBean topAdBean = db.selector(BannerBean.class)
                    .where("uuid","=","cache" + mRoomId)
                    .findFirst();
            FeaturedBean featuredBean = db.selector(FeaturedBean.class)
                    .where("uuid","=","cache" + mRoomId)
                    .findFirst();
            DocListBean classDataBean = db.selector(DocListBean.class)
                    .where("id", "=", "cache" + mRoomId)
                    .findFirst();
            if(topAdBean != null && classDataBean != null && topAdBean.json != null && classDataBean.json != null && featuredBean != null && featuredBean.json != null){
                Gson gson = new Gson();
                mBannerBeans = gson.fromJson(topAdBean.json,new TypeToken<ArrayList<BannerBean>>(){}.getType());
                mFeaturedBeans = gson.fromJson(featuredBean.json,new TypeToken<ArrayList<FeaturedBean>>(){}.getType());
                ArrayList<DocListBean> beans = gson.fromJson(classDataBean.json,new TypeToken<ArrayList<DocListBean>>(){}.getType());
                mClassData.clear();
                mClassData.addAll(beans);
                mClassAdapter.notifyDataSetChanged();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void requestFeatured(){
        Otaku.getDocV2().requestFeatured(mRoomId, new OneParameterCallback<ArrayList<FeaturedBean>>() {
            @Override
            public void action(ArrayList<FeaturedBean> featuredBeen) {
                mFeaturedBeans.clear();
                mFeaturedBeans = featuredBeen;
                FeaturedBean adBean = new FeaturedBean();
                adBean.uuid = "cache" + mRoomId;
                Gson gson = new Gson();
                adBean.json = gson.toJson(featuredBeen,new TypeToken<ArrayList<FeaturedBean>>(){}.getType());
                try {
                    db.saveOrUpdate(adBean);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                mClassAdapter.notifyItemChanged(1);
                allFinishCallback.onSuccess();
            }
        }, new OneParameterCallback<Integer>() {
            @Override
            public void action(Integer integer) {
                allFinishCallback.onSuccess();
            }
        });
    }

    /**
     * 请求banner数据
     *
     */
    private void requestBannerData() {
        if(!NetworkUtils.checkNetworkAndShowError(getActivity())){
            return;
        }
        Otaku.getDocV2().requestNewBanner(mRoomId, new OneParameterCallback<ArrayList<BannerBean>>() {
            @Override
            public void action(ArrayList<BannerBean> bannerBeen) {
                mBannerBeans.clear();
                mBannerBeans = bannerBeen;
                BannerBean adBean = new BannerBean();
                adBean.uuid = "cache" + mRoomId;
                Gson gson = new Gson();
                adBean.json = gson.toJson(bannerBeen,new TypeToken<ArrayList<BannerBean>>(){}.getType());
                try {
                    db.saveOrUpdate(adBean);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                mClassAdapter.notifyItemChanged(0);
                allFinishCallback.onSuccess();
            }
        }, new OneParameterCallback<Integer>() {
            @Override
            public void action(Integer integer) {
                allFinishCallback.onSuccess();
            }
        });
    }

    /**
     * 去重
     */
    public void addNewDataToList(ArrayList<DocListBean> oriData, ArrayList<DocListBean> newData) {
        if (newData != null && oriData != null && newData.size() > 0) {
            for (DocListBean news : newData) {
                boolean has = false;
                for (int i = 0; i < oriData.size(); i++) {
                    if (oriData.get(i).getDesc().getId().equals(news.getDesc().getId())) {
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
     */
    private void requestDocAndClubList(final int index, final boolean isPullDown) {
        Otaku.getDocV2().requestTagDocList(index, Otaku.LENGTH, "", new OneParameterCallback<ArrayList<DocListBean>>() {
            @Override
            public void action(ArrayList<DocListBean> docListBeen) {
                if (isPullDown) {
                    DocListBean classDataBean = new DocListBean();
                    classDataBean.id = "cache" + mRoomId;
                    Gson gson = new Gson();
                    classDataBean.json = gson.toJson(docListBeen,new TypeToken<ArrayList<DocListBean>>(){}.getType());
                    try {
                        db.saveOrUpdate(classDataBean);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    int bfSize = mClassData.size();
                    mClassData.clear();
                    mClassData.addAll(docListBeen);
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
                    addNewDataToList(mClassData, docListBeen);
                    int afSize = mClassData.size();
                    mClassAdapter.notifyItemRangeInserted(bfSize + 2,afSize - bfSize);
                }

                if(index != 0){
                    if (docListBeen.size() != 0) {
                        mListDocs.isLoadMoreEnabled(true);
                    } else {
                        ToastUtil.showCenterToast(getActivity(),R.string.msg_all_load_down);
                    }
                }else {
                    mListDocs.isLoadMoreEnabled(true);
                }
                allFinishCallback.onSuccess();
            }
        }, new OneParameterCallback<Integer>() {
            @Override
            public void action(Integer integer) {
                allFinishCallback.onSuccess();
            }
        });
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

        ClassDocListAdapter(){
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
                    ViewGroup.LayoutParams params =  linearHViewHolder.itemView.getLayoutParams();
                    params.height = 0;
                    linearHViewHolder.itemView.setLayoutParams(params);
                    return;
                }else {
                    ViewGroup.LayoutParams params =  linearHViewHolder.itemView.getLayoutParams();
                    params.height = DensityUtil.dip2px(110);
                    linearHViewHolder.itemView.setLayoutParams(params);
                }
                linearHViewHolder.mRvList.setBackgroundColor(Color.WHITE);
                linearHViewHolder.recyclerViewAdapter.setData(mFeaturedBeans);
                linearHViewHolder.recyclerViewAdapter.setOnItemClickListener(new ClassRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        FeaturedBean docBean = mFeaturedBeans.get(position);
                        if(!TextUtils.isEmpty(docBean.getSchema())){
                            Uri uri = Uri.parse(docBean.getSchema());
                            IntentUtils.toActivityFromUri(getActivity(),uri,view);
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                    }
                });
            }else if(viewHolder instanceof ClassDocListAdapter.BannerViewHolder){
                mBannerViewHolder = (ClassDocListAdapter.BannerViewHolder) viewHolder;
                if(mBannerBeans.size() == 0){
                    ViewGroup.LayoutParams params =  mBannerViewHolder.itemView.getLayoutParams();
                    params.height = 0;
                    mBannerViewHolder.itemView.setLayoutParams(params);
                    return;
                }else {
                    ViewGroup.LayoutParams params =  mBannerViewHolder.itemView.getLayoutParams();
                    params.height = DensityUtil.dip2px(144);
                    mBannerViewHolder.itemView.setLayoutParams(params);
                    startBanner();
                }
                ArrayList<ImageView> imageViews = new ArrayList<>();
                mBannerViewHolder.llPointGroup.removeAllViews();
                for(int i=0;i<mBannerBeans.size();i++) {
                    final BannerBean bean = mBannerBeans.get(i);
                    ImageView image = new ImageView(getActivity());
                    Picasso.with(getActivity())
                            .load(StringUtils.getUrl(getActivity(),Otaku.URL_QINIU + bean.getBg().getPath(), DensityUtil.getScreenWidth(), DensityUtil.dip2px(150), false, false))
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
                            if(!TextUtils.isEmpty(bean.getSchema())){
                                Uri uri = Uri.parse(bean.getSchema());
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
                if(o instanceof DocListBean){
                    final DocListBean post = (DocListBean) o;
                    final ClassDocListAdapter.DocViewHolder holder = (ClassDocListAdapter.DocViewHolder) viewHolder;

                    if(holder.ivClubCreatorFlag != null){
                        holder.ivClubCreatorFlag.setVisibility(View.GONE);
                        holder.tvCreatorName.setSelected(false);
                    }
                    if(holder.docLabel != null && post.getTags() != null){
                        holder.docLabel.setDocLabelAdapter(holder.docLabelAdapter);
                        holder.docLabelAdapter.setData(post.getTags(),false);
                        if(post.getTags().size()>0) {
                            holder.docLabel.setVisibility(View.VISIBLE);
                            holder.vDocSep.setVisibility(View.VISIBLE);
                        }else{
                            holder.docLabel.setVisibility(View.GONE);
                            holder.vDocSep.setVisibility(View.GONE);
                        }
                    }else {
                        if(holder.docLabel != null) holder.docLabel.setVisibility(View.GONE);
                        holder.vDocSep.setVisibility(View.GONE);
                    }

                    holder.ivLevelColor.setBackgroundColor(StringUtils.readColorStr(post.getUserLevelColor(), ContextCompat.getColor(getContext(),R.color.main_title_cyan)));
                    holder.tvLevel.setText(String.valueOf(post.getUserLevel()));
                    holder.tvLevel.setTextColor(StringUtils.readColorStr(post.getUserLevelColor(),ContextCompat.getColor(getContext(),R.color.main_title_cyan)));
                    holder.tvCreatorName.setText(post.getUserName());
                    if(holder.ivCreatorAvatar != null){
                        Picasso.with(getActivity())
                                .load(StringUtils.getUrl(getActivity(),Otaku.URL_QINIU + post.getUserIcon().getPath(), DensityUtil.dip2px(44), DensityUtil.dip2px(44), false, false))
                                .resize(DensityUtil.dip2px(44), DensityUtil.dip2px(44))
                                .placeholder(R.drawable.ic_default_avatar_m)
                                .error(R.drawable.ic_default_avatar_m)
                                .centerCrop()
                                .tag(TAG)
                                .config(Bitmap.Config.RGB_565)
                                .into(holder.ivCreatorAvatar);
                        holder.ivCreatorAvatar.setTag(R.id.id_creator_uuid, post.getUserId());
                    }
                    holder.ivClubCreatorFlag.setVisibility(View.GONE);
                    holder.tvCreatorName.setSelected(false);
                    holder.ivClubCreatorFlag.setVisibility(View.GONE);
                    holder.tvCreatorName.setSelected(false);
                    if(TextUtils.isEmpty(post.getDesc().getTitle())){
                        holder.tvPostTitle.setVisibility(View.GONE);
                    }else{
                        holder.tvPostTitle.setVisibility(View.VISIBLE);
                        holder. tvPostTitle.setText(post.getDesc().getTitle());
                    }
                    // 点赞/评论
                    holder.tvCommentNum.setText(StringUtils.getNumberInLengthLimit(post.getDesc().getComments(), 3));
                    holder.tvPantsNum.setText(StringUtils.getNumberInLengthLimit(post.getDesc().getLikes(), 3));
                    // 时间,内容
                    holder.tvPostDate.setText(StringUtils.timeFormate(post.getUpdateTime()));
                    holder.tvPostBrief.setText(post.getDesc().getContent());
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
                    if (post.getDesc().getMusic() != null){

                        holder.rlMusicRoot.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity())
                                .load(StringUtils.getUrl(getActivity(),Otaku.URL_QINIU + post.getDesc().getMusic().getCover().getPath(), DensityUtil.dip2px(90), DensityUtil.dip2px(90), false, true))
                                .resize(DensityUtil.dip2px(90), DensityUtil.dip2px(90))
                                .placeholder(R.drawable.ic_default_avatar_l)
                                .error(R.drawable.ic_default_avatar_l)
                                .centerCrop()
                                .tag(TAG)
                                .config(Bitmap.Config.RGB_565)
                                .into(holder.musicImg);
                        holder.musicTitle.setText(post.getDesc().getMusic().getName());
                    }else {
                        holder.rlMusicRoot.setVisibility(View.GONE);
                        if (post.getDesc().getImages() != null && post.getDesc().getImages().size() > 0) {
                            holder.llImagePack.setVisibility(View.VISIBLE);
                            holder.rlIcon1.setVisibility(View.VISIBLE);
                            holder.rlIcon1.setVisibility(View.VISIBLE);
                            if (FileUtil.isGif(post.getDesc().getImages().get(0).getPath())) {
                                holder.ivGifIcon1.setVisibility(View.VISIBLE);
                            } else {
                                holder.ivGifIcon1.setVisibility(View.GONE);
                            }
                            Picasso.with(getActivity())
                                    .load(StringUtils.getUrl(getActivity(),Otaku.URL_QINIU + post.getDesc().getImages().get(0).getPath(), (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, false, true))
                                    .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                                    .placeholder(R.drawable.ic_default_club_l)
                                    .error(R.drawable.ic_default_club_l)
                                    .centerCrop()
                                    .tag(TAG)
                                    .config(Bitmap.Config.RGB_565)
                                    .into(holder.ivIcon1);
                            if(post.getDesc().getImages().size() > 1){
                                holder.rlIcon2.setVisibility(View.VISIBLE);
                                if (FileUtil.isGif(post.getDesc().getImages().get(1).getPath())) {
                                    holder.ivGifIcon2.setVisibility(View.VISIBLE);
                                } else {
                                    holder.ivGifIcon2.setVisibility(View.GONE);
                                }
                                Picasso.with(getActivity())
                                        .load(StringUtils.getUrl(getActivity(),Otaku.URL_QINIU + post.getDesc().getImages().get(1).getPath(), (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, false, true))
                                        .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                                        .placeholder(R.drawable.ic_default_club_l)
                                        .error(R.drawable.ic_default_club_l)
                                        .centerCrop()
                                        .tag(TAG)
                                        .config(Bitmap.Config.RGB_565)
                                        .into(holder.ivIcon2);
                            }
                            // 是否显示第三张图
                            if(holder.rlSpecialTypePack.getVisibility() != View.VISIBLE && post.getDesc().getImages().size() > 2){
                                holder.rlIcon3.setVisibility(View.VISIBLE);
                                if (FileUtil.isGif(post.getDesc().getImages().get(2).getPath())) {
                                    holder.ivGifIcon3.setVisibility(View.VISIBLE);
                                } else {
                                    holder.ivGifIcon3.setVisibility(View.GONE);
                                }
                                Picasso.with(getActivity())
                                        .load(StringUtils.getUrl(getActivity(),Otaku.URL_QINIU + post.getDesc().getImages().get(2).getPath(), (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3,false,true))
                                        .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                                        .placeholder(R.drawable.ic_default_club_l)
                                        .error(R.drawable.ic_default_club_l)
                                        .centerCrop()
                                        .tag(TAG)
                                        .config(Bitmap.Config.RGB_565)
                                        .into(holder.ivIcon3);
                            }
                            // 是否显示  “共xx张图”
                            if(post.getDesc().getImages().size() > 3 || (holder.rlSpecialTypePack.getVisibility() == View.VISIBLE && post.getDesc().getImages().size() > 2)){
                                holder.tvIconNum.setVisibility(View.VISIBLE);
                                holder.tvIconNum.setText(getActivity().getString(R.string.label_post_icon_num, post.getDesc().getImages().size()));
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
            return mClassData.size() + 2;
        }

        class BannerViewHolder extends RecyclerView.ViewHolder{

            ViewPager viewPager;
            LinearLayout llPointGroup;
            View llPointContainer;
            View pressedPoint;
            View root;
            int width;

            BannerViewHolder(View itemView) {
                super(itemView);
                viewPager = (ViewPager) itemView.findViewById(R.id.pager_class_banner);
                llPointGroup = (LinearLayout) itemView.findViewById(R.id.ll_point_group);
                pressedPoint = itemView.findViewById(R.id.view_pressed_point);
                llPointContainer = itemView.findViewById(R.id.ll_point_container);
                root = itemView.findViewById(R.id.rl_root);
                if(scheduledExecutorService != null){
                    scheduledExecutorService.shutdown();
                }
            }
        }

        class BannerAdapter extends PagerAdapter {

            private ArrayList<ImageView> images;

            BannerAdapter(ArrayList<ImageView> images){
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

        class LinearHViewHolder extends RecyclerView.ViewHolder{

            View root;
            RecyclerView mRvList;
            ClassRecyclerViewAdapter recyclerViewAdapter;

            LinearHViewHolder(View itemView) {
                super(itemView);
                mRvList = (RecyclerView) itemView.findViewById(R.id.rv_class_featured);
                root = itemView.findViewById(R.id.ll_root);
                recyclerViewAdapter = new ClassRecyclerViewAdapter(itemView.getContext());
                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                mRvList.setLayoutManager(layoutManager);
                mRvList.setAdapter(recyclerViewAdapter);
            }
        }

        class DocViewHolder extends RecyclerView.ViewHolder{
            ImageView ivCreatorAvatar;
            @ViewInject(R.id.tv_post_creator_name)
            TextView tvCreatorName;
            View ivClubCreatorFlag;
            @ViewInject(R.id.tv_post_update_time)
            TextView tvPostDate;
            @ViewInject(R.id.tv_post_title)
            TextView tvPostTitle;
            @ViewInject(R.id.tv_post_brief)
            TextView tvPostBrief;
            @ViewInject(R.id.ll_image_3)
            View llImagePack;
            @ViewInject(R.id.rl_post_special_flag)
            View rlSpecialTypePack;
            @ViewInject(R.id.rl_post_image_1)
            View rlIcon1;
            @ViewInject(R.id.rl_post_image_2)
            View rlIcon2;
            @ViewInject(R.id.rl_post_image_3)
            View rlIcon3;
            @ViewInject(R.id.iv_post_image_1_gif_flag)
            View ivGifIcon1;
            @ViewInject(R.id.iv_post_image_2_gif_flag)
            View ivGifIcon2;
            @ViewInject(R.id.iv_post_image_3_gif_flag)
            View ivGifIcon3;
            @ViewInject(R.id.iv_post_image_1)
            ImageView ivIcon1;
            @ViewInject(R.id.iv_post_image_2)
            ImageView ivIcon2;
            @ViewInject(R.id.iv_post_image_3)
            ImageView ivIcon3;
            @ViewInject(R.id.tv_post_img_num)
            TextView tvIconNum;
            @ViewInject(R.id.tv_post_comment_num)
            TextView tvCommentNum;
            @ViewInject(R.id.tv_post_pants_num)
            TextView tvPantsNum;
            @ViewInject(R.id.rl_music_root)
            View rlMusicRoot;
            @ViewInject(R.id.iv_item_image)
            MyRoundedImageView musicImg;
            @ViewInject(R.id.tv_music_title)
            TextView musicTitle;
            View ivLevelColor;
            TextView tvLevel;
            ImageView ivIconClassOffical;
            //标签
            View vDocSep;
            DocLabelView docLabel;
            NewDocLabelAdapter docLabelAdapter;

            DocViewHolder(View itemView) {
                super(itemView);
                ivClubCreatorFlag = itemView.findViewById(R.id.iv_post_owner_flag);
                ivLevelColor = itemView.findViewById(R.id.iv_level_bg);
                tvLevel = (TextView)itemView.findViewById(R.id.tv_level);
                vDocSep = itemView.findViewById(R.id.view_doc_sep);
                docLabel = (DocLabelView) itemView.findViewById(R.id.dv_doc_label_root);
                ivIconClassOffical = (ImageView) itemView.findViewById(R.id.iv_class_post_img);
                x.view().inject(this, itemView);
                ivCreatorAvatar = (ImageView) itemView.findViewById(R.id.iv_post_creator);
                if (ivCreatorAvatar != null) {
                    ivCreatorAvatar.setOnClickListener(mAvatarListener);
                }
                docLabelAdapter = new NewDocLabelAdapter(getActivity(),true);
                ivIcon1.setOnClickListener(mIconListener);
                ivIcon2.setOnClickListener(mIconListener);
                ivIcon3.setOnClickListener(mIconListener);
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
                    final DocListBean docBean = (DocListBean) v.getTag(R.id.id_filebean);
                    Intent intent = new Intent(context, ImageBigSelectActivity.class);
                    intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, docBean.getDesc().getImages());
                    intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX, index);
                    // 以后可选择 有返回数据
                    context.startActivity(intent);

                }
            };

        }

    }
}
