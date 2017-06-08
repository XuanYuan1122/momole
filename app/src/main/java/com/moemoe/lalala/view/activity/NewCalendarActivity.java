package com.moemoe.lalala.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerCalendarComponent;
import com.moemoe.lalala.di.modules.CalendarModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.CalendarDayEntity;
import com.moemoe.lalala.presenter.CalendarContract;
import com.moemoe.lalala.presenter.CalendarPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.adapter.CalendarDayAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/1.
 */

public class NewCalendarActivity extends BaseAppCompatActivity implements CalendarContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.recyclerview)
    PullAndLoadView mPullAndLoadView;
    @BindView(R.id.suspension_bar)
    RelativeLayout mSuspensionBar;
    @BindView(R.id.iv_background)
    ImageView mIvBg;
    @BindView(R.id.tv_cal_time)
    TextView mTvTime;
    @BindView(R.id.tv_week)
    TextView mTvWeek;
    private CalendarDayAdapter mAdapter;
    private String mToday;
    private String mLastDay;
    private int mSuspensionHeight;
    private int mMinHeight;
    @Inject
    CalendarPresenter mPresenter;
    private CalendarDayEntity.Day mCurDay;
    private boolean isLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_calendar;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerCalendarComponent.builder()
                .calendarModule(new CalendarModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mSuspensionHeight = DensityUtil.dip2px(this,144);
        mMinHeight = DensityUtil.dip2px(this,44);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mToday = getFormatDate(String.valueOf(year),String.valueOf(month + 1),String.valueOf(day));
        mPullAndLoadView.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mPullAndLoadView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CalendarDayAdapter(this);
        mPullAndLoadView.getRecyclerView().setAdapter(mAdapter);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object o = mAdapter.getItem(position);
                if(o instanceof CalendarDayEntity.Items){
                    CalendarDayEntity.Items item = (CalendarDayEntity.Items) o;
                    if (!TextUtils.isEmpty(item.getSchema())) {
                        String mSchema = item.getSchema();
                        if(mSchema.contains(getString(R.string.label_doc_path)) && !mSchema.contains("uuid")){
                            String begin = mSchema.substring(0,mSchema.indexOf("?") + 1);
                            String uuid = mSchema.substring(mSchema.indexOf("?") + 1);
                            mSchema = begin + "uuid=" + uuid + "&from_name=板报";
                        }
                        Uri uri = Uri.parse(mSchema);
                        IntentUtils.toActivityFromUri(NewCalendarActivity.this, uri, view);
                    }
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mPullAndLoadView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    if(!isFinishing())Glide.with(NewCalendarActivity.this).resumeRequests();
//                } else {
//                    if(!isFinishing())Glide.with(NewCalendarActivity.this).pauseRequests();
//                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = ((LinearLayoutManager)mPullAndLoadView.getRecyclerView().getLayoutManager()).findFirstVisibleItemPosition();
                View firVisibleView = mPullAndLoadView.getRecyclerView().getLayoutManager().findViewByPosition(position);
                View secVisibleView = mPullAndLoadView.getRecyclerView().getLayoutManager().findViewByPosition(position + 1);
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
        mPullAndLoadView.setLoadMoreEnabled(false);
        mPullAndLoadView.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                if(!TextUtils.isEmpty(mLastDay)) {
                    isLoading = true;
                    mPresenter.doRequest(mLastDay,false);
                }
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.doRequest(mToday,true);
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        });
        mPresenter.doRequest(mToday,true);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        mPresenter.release();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateSuspensionBar(CalendarDayEntity.Day day) {
        if(mCurDay != day && day != null){
            mTvTime.setText(day.getDay());
            mTvWeek.setText(getResources().getString(R.string.label_cal_top,day.getWeek(),day.getSize(),day.getReadTime()));
            Glide.with(this)
                    .load(StringUtils.getUrl(this, ApiService.URL_QINIU +  day.getBg().getPath(), DensityUtil.getScreenWidth(this), DensityUtil.dip2px(this,144), false, false))
                    .override(DensityUtil.getScreenWidth(this), DensityUtil.dip2px(this,144))
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .into(mIvBg);
            mCurDay = day;
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

    @Override
    public void onSuccess(CalendarDayEntity entities, boolean pull) {
        isLoading = false;
        mPullAndLoadView.setLoadMoreEnabled(true);
        mPullAndLoadView.setComplete();
        mLastDay = entities.getDay().getYesterday();
        if(pull){
            mAdapter.addData(entities,true);
        }else {
            mAdapter.addData(entities,false);
        }
    }

    @Override
    public void onFailure(int code,String msg) {
        isLoading = false;
        mPullAndLoadView.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(NewCalendarActivity.this,code,msg);
    }
}
