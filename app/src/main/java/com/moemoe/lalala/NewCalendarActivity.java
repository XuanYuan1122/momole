package com.moemoe.lalala;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.moemoe.lalala.adapter.CalendarDayAdapter;
import com.moemoe.lalala.adapter.RecyclerItemClickListener;
import com.moemoe.lalala.data.CalendarDayBean;
import com.moemoe.lalala.data.CalendarDbBean;
import com.moemoe.lalala.network.OneParameterCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
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

import java.util.Calendar;

/**
 * Created by yi on 2016/11/10.
 */
@ContentView(R.layout.ac_calendar)
public class NewCalendarActivity extends BaseActivity {

    private static final String TAG = "NewCalendarActivity";
    @ViewInject(R.id.iv_back)
    private ImageView mIvBack;
    @ViewInject(R.id.recyclerview)
    private PullAndLoadView mPullAndLoadView;
    @ViewInject(R.id.suspension_bar)
    private RelativeLayout mSuspensionBar;
    @ViewInject(R.id.iv_background)
    private ImageView mIvBg;
    @ViewInject(R.id.tv_cal_time)
    private TextView mTvTime;
    @ViewInject(R.id.tv_week)
    private TextView mTvWeek;
    private CalendarDayAdapter mAdapter;
    private boolean mIsHasLoadedAll = false;
    private boolean mIsLoading = false;
    private String mLastDay;
    private String mToday;
    private DbManager db;
    private CalendarDbBean dbBean;
    private int mSuspensionHeight;
    private int mMinHeight;
    private CalendarDayBean.Day mCurDay;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Picasso.with(this).cancelTag(TAG);
    }

    @Override
    protected void initView() {
        db = x.getDb(MoemoeApplication.sDaoConfig);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mSuspensionHeight = DensityUtil.dip2px(144);
        mMinHeight = DensityUtil.dip2px(44);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mToday = getFormatDate(String.valueOf(year),String.valueOf(month + 1),String.valueOf(day));
        SwipeRefreshLayout mSwipeRefreshWidget = mPullAndLoadView.getSwipeRefreshLayout();
        mSwipeRefreshWidget.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
        RecyclerView mRv = mPullAndLoadView.getRecyclerView();
        mAdapter = new CalendarDayAdapter(this,TAG);
        mRv.setAdapter(mAdapter);
        mRv.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object o = mAdapter.getItem(position);
                if(o instanceof CalendarDayBean.Items){
                    CalendarDayBean.Items item = (CalendarDayBean.Items) o;
                    if (!TextUtils.isEmpty(item.getSchema())) {
                        Uri uri = Uri.parse(item.getSchema());
                        IntentUtils.toActivityFromUri(NewCalendarActivity.this, uri, view);
                    }
                }

            }
        }));
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Picasso.with(NewCalendarActivity.this).resumeTag(TAG);
                } else {
                    Picasso.with(NewCalendarActivity.this).pauseTag(TAG);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = linearLayoutManager.findFirstVisibleItemPosition();
                View firVisibleView = linearLayoutManager.findViewByPosition(position);
                View secVisibleView = linearLayoutManager.findViewByPosition(position + 1);
                if (position == RecyclerView.NO_POSITION) return;
                if(position == 0){
                    if(firVisibleView.getTop() > -mMinHeight){
                        mSuspensionBar.setVisibility(View.GONE);
                    }else{
                        mSuspensionBar.setVisibility(View.VISIBLE);
                        mSuspensionBar.setY(-mMinHeight);
                        updateSuspensionBar(mAdapter.getDay(position));
                    }
                }
                if(mAdapter.isTop(position + 1)){
                    if(secVisibleView.getTop() <= (mSuspensionHeight-mMinHeight) && secVisibleView.getTop() >= 0){
                        int max = Math.max(-(mSuspensionHeight - secVisibleView.getTop()),-mSuspensionHeight);
                        mSuspensionBar.setY(max);
                    }else if(secVisibleView.getTop() > mSuspensionHeight-mMinHeight){
                        mSuspensionBar.setY(-mMinHeight);
                    }
                }
                if(position != 0 && mAdapter.isTop(position)){
                    if(firVisibleView.getTop() <= 0 && firVisibleView.getTop() > -mMinHeight){
                        int max = Math.max(-(mSuspensionHeight - firVisibleView.getTop()),-(mSuspensionHeight + mMinHeight));
                        mSuspensionBar.setY(max);
                        if(dy < 0){
                            updateSuspensionBar(mAdapter.getLastDay(position));
                        }
                    }else if(firVisibleView.getTop() <= -mMinHeight){
                        mSuspensionBar.setY(-mMinHeight);
                        updateSuspensionBar(mAdapter.getDay(position));
                    }
                }
            }
        });
        mPullAndLoadView.setLayoutManager(linearLayoutManager);
        mPullAndLoadView.setPullCallback(new PullCallback() {
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
        mPullAndLoadView.initLoad();
    }

    private void updateSuspensionBar(CalendarDayBean.Day day) {
        if(mCurDay != day && day != null){
            mTvTime.setText(day.getDay());
            mTvWeek.setText(getResources().getString(R.string.label_cal_top,day.getWeek(),day.getSize(),day.getReadTime()));
            Picasso.with(this)
                    .load(StringUtils.getUrl(this, Otaku.URL_QINIU +  day.getBg().getPath(), DensityUtil.getScreenWidth(), DensityUtil.dip2px(144), false, false))
                    .resize(DensityUtil.getScreenWidth(), DensityUtil.dip2px(144))
                    .placeholder(R.drawable.ic_default_doc_l)
                    .error(R.drawable.ic_default_doc_l)
                    .config(Bitmap.Config.RGB_565)
                    .tag(TAG)
                    .into(mIvBg);
            mCurDay = day;
        }
    }


    public void loadDataFromDb(){
        try {
            dbBean = db.selector(CalendarDbBean.class)
                    .where("uuid", "=", "cache")
                    .findFirst();
            if(dbBean != null){
                if(dbBean.json != null){
                    Gson gson = new Gson();
                    CalendarDayBean datas = gson.fromJson( dbBean.json,CalendarDayBean.class);
                    mAdapter.addData(datas,true);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
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
                requestDocList(mToday);
                mIsHasLoadedAll = false;
            }else {
                requestDocList(mLastDay);
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

    private void requestDocList(final String day){
        if(NetworkUtils.checkNetworkAndShowError(this)){
            if (!TextUtils.isEmpty(mLastDay) || day.equals(mToday)){
                Otaku.getCalendarV2().requestCalDayList(day, new OneParameterCallback<CalendarDayBean>() {
                    @Override
                    public void action(CalendarDayBean calendarDayBean) {
                        mLastDay = calendarDayBean.getDay().getYesterday();
                        if (day.equals(mToday)){
                            mAdapter.addData(calendarDayBean,true);
                            dbBean = new CalendarDbBean();
                            dbBean.id = "cache";
                            Gson gson = new Gson();
                            dbBean.json = gson.toJson(calendarDayBean);
                            try {
                                db.saveOrUpdate(dbBean);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }else {
                            mAdapter.addData(calendarDayBean,false);
                        }
                        if(calendarDayBean.getItems() != null && calendarDayBean.getItems().size() > 0){
                            mPullAndLoadView.isLoadMoreEnabled(true);
                        }else {
                            mPullAndLoadView.isLoadMoreEnabled(false);
                            ToastUtil.showCenterToast(NewCalendarActivity.this,R.string.msg_all_load_down);
                        }
                        mPullAndLoadView.setComplete();
                        mIsLoading = false;
                    }
                }, new OneParameterCallback<Integer>() {
                    @Override
                    public void action(Integer integer) {
                        mPullAndLoadView.setComplete();
                        mIsLoading = false;
                        ErrorCodeUtils.showErrorMsgByCode(NewCalendarActivity.this,integer);
                    }
                });
            }
        }
    }

    private String getFormatDate(String year, String month, String day){
        StringBuilder sBuffer = new StringBuilder();
        if(month.length() < 2){
            month = "0" + month;
        }
        if(day.length() < 2){
            day = "0" + day;
        }
        sBuffer.append(year).append(month).append(day);
        return sBuffer.toString();
    }
}
