package com.moemoe.lalala;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.util.DensityUtil;
import com.app.ex.DbException;
import com.app.view.DbManager;
import com.moemoe.lalala.adapter.CalendarOneDayRecyclerViewAdapter;
import com.moemoe.lalala.data.CalendarDayItem;
import com.moemoe.lalala.data.CalendarDayType;
import com.moemoe.lalala.data.CalendarEvent;
import com.moemoe.lalala.data.CalendarOneDayBean;
import com.moemoe.lalala.netamusic.data.model.Song;
import com.moemoe.lalala.netamusic.player.IPlayBack;
import com.moemoe.lalala.netamusic.player.Player;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.IConstants;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.SpacesItemDecoration;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.calendar.CalendarLayout;
import com.moemoe.lalala.view.calendar.CalendarView;
import com.moemoe.lalala.view.calendar.adapter.TopViewPagerAdapter;
import com.moemoe.lalala.view.calendar.util.DateBean;
import com.moemoe.lalala.view.calendar.util.OtherUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Haru on 2016/7/21 0021.
 */
@ContentView(R.layout.frag_calendar)
public class CalendarActivity extends BaseActivity implements IConstants,IPlayBack.Callback{

    @FindView(R.id.container)
    private CalendarLayout mContainer;
    @FindView(R.id.tv_current_month)
    private TextView mTvToday;
    @FindView(R.id.vp_calendar)
    private ViewPager mViewPager;
    @FindView(R.id.view_content)
    private RecyclerView mRvList;
    @FindView(R.id.tv_music_name)
    private TextView mTopMusicName;
    @FindView(R.id.iv_back)
    private ImageView mIvBack;

    private List<View> calenderViews = new ArrayList<>();
    private CalendarOneDayBean mOneDayData;
    private CalendarOneDayRecyclerViewAdapter mOneDayAdapter;
    private  ArrayList<CalendarDayItem> mOneDayList;

    private int mCurSelectMonth = -1;
    private int mCurSelectDay = -1;
    private boolean mIsFirstEnter = true;
    //private MusicPlayBroadCast mReceiver;
    private String mCurDay ;
    private DbManager db;
    private Player mPlayer;

    /**
     * 日历向左或向右可翻动的天数
     */
    private int INIT_PAGER_INDEX = 30;

    @Override
    protected void initView() {
        db = com.app.Utils.getDb(MoemoeApplication.sDaoConfig);
//        MapActivity.sMusicServiceManager.connectService();
//        MapActivity.sMusicServiceManager.setOnServiceConnectComplete(new IOnServiceConnectComplete() {
//            @Override
//            public void onServiceConnectComplete(IMediaService service) {
//                LogUtil.i("连接完毕");
//            }
//        });
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mPlayer = Player.getInstance();
        mPlayer.registerCallback(this);
       // mReceiver = new MusicPlayBroadCast();
        IntentFilter filter = new IntentFilter(BROADCAST_NAME);
        filter.addAction(BROADCAST_NAME);
       // registerReceiver(mReceiver, filter);
        mOneDayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        mTvToday.setText(OtherUtils.formatDate(calendar.getTime()));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < 3; i++) {
            CalendarView calendarView = new CalendarView(this, i, year, month);
            calendarView.setOnCalendarClickListener(new OnMyCalendarClickerListener());
            if (i == 0) {
                mContainer.setRowNum(calendarView.getColorDataPosition() / 7);
            }
            calenderViews.add(calendarView);
        }
        final TopViewPagerAdapter adapter = new TopViewPagerAdapter(this, calenderViews, INIT_PAGER_INDEX, calendar);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new OnMyViewPageChangeListener());
        mViewPager.setCurrentItem(INIT_PAGER_INDEX);
        mOneDayAdapter = new CalendarOneDayRecyclerViewAdapter(this);
        mRvList.setAdapter(mOneDayAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position != -1) {
                    int type = CalendarDayType.getType(mOneDayList.get(position).type);
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
        mRvList.setLayoutManager(layoutManager);

        mRvList .addItemDecoration(new SpacesItemDecoration(DensityUtil.dip2px(9)));
        mOneDayAdapter.setOnItemClickListener(new CalendarOneDayRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CalendarDayItem.CalendarData bean = mOneDayAdapter.getItem(position);
                String id = null;
                if(bean instanceof CalendarDayItem.CalendarDoc){
                    CalendarDayItem.CalendarDoc doc = (CalendarDayItem.CalendarDoc) bean;
                    id = doc.id;
                }
                if(!TextUtils.isEmpty(id)){
                    Uri uri = Uri.parse(id);
                    IntentUtils.toActivityFromUri(CalendarActivity.this, uri,view);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        String toDay = getFormatDate(String.valueOf(year),String.valueOf(month + 1),String.valueOf(day));
        mCurSelectDay = day;
        mCurSelectMonth = month;
        //MusicInfo info = MapActivity.sMusicServiceManager.getCurMusic();
        Song info = mPlayer.getPlayingSong();
        if(info != null && !TextUtils.isEmpty(info.getDisplayName())){
            mTopMusicName.setText(info.getDisplayName());
            mTopMusicName.setVisibility(View.VISIBLE);
        }else {
            mTopMusicName.setText("");
            mTopMusicName.setVisibility(View.GONE);
        }
        mTopMusicName.setOnClickListener(toMusicDetail);
        mCurDay = toDay;
        loadDataFromDb(toDay);
        requestOneDayData(toDay);

        mRvList.setItemAnimator(new DefaultItemAnimator());
    }

    View.OnClickListener toMusicDetail = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(CalendarActivity.this, MusicDetailActivity.class);
            startActivity(intent);
        }
    };

    private void loadDataFromDb(String day){
        try {
            mOneDayData = new CalendarOneDayBean();
            mOneDayData = db.selector(CalendarOneDayBean.class)
                    .where("day","=",day)
                    .findFirst();
            if(mOneDayData != null && mOneDayData.dbJson != null){
                mOneDayList = new ArrayList<>();
                mOneDayAdapter.setData(mOneDayList);
                mOneDayData.readFromJsonList(this,mOneDayData.dbJson);
                mOneDayList.addAll(mOneDayData.items);
                mOneDayAdapter.setData(mOneDayList);
                mOneDayAdapter.setSelectDay(day);
            }else {
                mOneDayList.clear();
                mOneDayAdapter.setData(mOneDayList);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void requestEventDays(String time,final CalendarView calendarView){
        Otaku.getCalendarV2().requestFeatured(mPreferMng.getToken(), time).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                HashMap<String,CalendarEvent> event = CalendarEvent.readFromJsonArray(CalendarActivity.this,s);
                calendarView.setEventDays(event);
            }

            @Override
            public void failure(String e) {

            }
        }));
    }

    /**
     * 日期滚动时回调
     */
    class OnMyViewPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            CalendarView calendarView = (CalendarView) calenderViews.get(position % 3);
            //事件获取
            DateBean firstData = (DateBean) calendarView.getCurAdapter().getItem(0);
            DateBean lastData = (DateBean) calendarView.getCurAdapter().getItem(calendarView.getCurAdapter().getCount() - 1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(firstData.getDate());
            int bgYear = calendar.get(Calendar.YEAR);
            int bgMon = calendar.get(Calendar.MONTH) + 1;
            int bgDay = calendar.get(Calendar.DAY_OF_MONTH);
            String bg = getFormatDate(String.valueOf(bgYear), String.valueOf(bgMon), String.valueOf(bgDay));

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(lastData.getDate());
            int edYear = calendar1.get(Calendar.YEAR);
            int edMon = calendar1.get(Calendar.MONTH) + 1;
            int edDay = calendar1.get(Calendar.DAY_OF_MONTH);
            String ed = getFormatDate(String.valueOf(edYear), String.valueOf(edMon), String.valueOf(edDay));

            requestEventDays(bg+"-"+ed,calendarView);
            if(mIsFirstEnter){
                mIsFirstEnter = false;
                return;
            }
            mTvToday.setText(calendarView.getCurrentDay());
            int i = ((TopViewPagerAdapter)mViewPager.getAdapter()).getDay();
            int n = ((TopViewPagerAdapter)mViewPager.getAdapter()).getSelectMonth();
            if( n == calendarView.getCurrentMonth()){
                mContainer.setRowNum(i/7);
                calendarView.initFirstDayPosition(i);
                //设置含有事件的日期 1-9号
                //initEventDays(calendarView);
            }else {
                mContainer.setRowNum(0);
                calendarView.initFirstDayPosition(-1);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private String getFormatDate(String year, String month, String day){
        StringBuffer sBuffer = new StringBuffer();
        if(month.length() < 2){
            month = "0" + month;
        }
        if(day.length() < 2){
            day = "0" + day;
        }
        sBuffer.append(year).append(month).append(day);
        return sBuffer.toString();
    }


    /**
     * 点击某个日期回调
     */
    class OnMyCalendarClickerListener implements CalendarView.OnCalendarClickListener {
        @Override
        public void onCalendarClick(int position, DateBean dateBean) {
            mTvToday.setText(OtherUtils.formatDate(dateBean.getDate()));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateBean.getDate());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String selectDay = getFormatDate(String.valueOf(year),String.valueOf(month + 1),String.valueOf(day));
            mCurDay = selectDay;
            loadDataFromDb(selectDay);
            requestOneDayData(selectDay);
            mCurSelectMonth = month;
            mCurSelectDay = day;
            if(!NetworkUtils.isNetworkAvailable(CalendarActivity.this)){
                ToastUtil.showCenterToast(CalendarActivity.this, R.string.a_server_msg_connection);
            }
        }
    }

    private void requestOneDayData(final String day) {
        mOneDayAdapter.resetPreMusicPosition();
        Otaku.getCalendarV2().requestCalendarOneDay(mPreferMng.getToken(),day).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                mOneDayData = new CalendarOneDayBean();
                mOneDayData.readFromJsonList(CalendarActivity.this, s);
                mOneDayData.day.replaceAll("-", "");
                if(!mCurDay.equals(mOneDayData.day)){
                    return;
                }
                mOneDayData.dbJson = s;
                try {
                    db.saveOrUpdate(mOneDayData);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                mOneDayList = new ArrayList<>();
                mOneDayAdapter.setData(mOneDayList);
                mOneDayList.addAll(mOneDayData.items);
                mOneDayAdapter.setData(mOneDayList);
                mOneDayAdapter.setSelectDay(day);
            }

            @Override
            public void failure(String e) {

            }
        }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.unregisterCallback(this);
        mOneDayAdapter.unregisterCallback();
        //unregisterReceiver(mReceiver);
    }


    @Override
    public void onSwitchLast(@Nullable Song last) {

    }

    @Override
    public void onSwitchNext(@Nullable Song next) {

    }

    @Override
    public void onComplete(@Nullable Song next) {
        mTopMusicName.setText("");
        mTopMusicName.setVisibility(View.GONE);
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        if(isPlaying){
            Song info = mPlayer.getPlayingSong();
            if(info != null && !TextUtils.isEmpty(info.getDisplayName())){
                mTopMusicName.setText(info.getDisplayName());
                mTopMusicName.setVisibility(View.VISIBLE);
            }else {
                mTopMusicName.setText("");
                mTopMusicName.setVisibility(View.GONE);
            }
        }
    }

//    class MusicPlayBroadCast extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getAction().equals(BROADCAST_NAME)) {
//                MusicInfo musicInfo = new MusicInfo();
//                int curPlayIndex = intent.getIntExtra(PLAY_MUSIC_INDEX, -1);
//                int playState = intent.getIntExtra(PLAY_STATE_NAME, MPS_NOFILE);
//                int prePlayPosition = intent.getIntExtra(PLAY_PRE_MUSIC_POSITION,-1);
//                Bundle bundle = intent.getBundleExtra(MusicInfo.KEY_MUSIC);
//                if (bundle != null) {
//                    musicInfo = bundle.getParcelable(MusicInfo.KEY_MUSIC);
//                }
//                if(prePlayPosition != -1){
//                    mOneDayAdapter.notifyItemChanged(prePlayPosition);
//                }
//                //mOneDayAdapter.notifyItemChanged(musicInfo.position);
//                switch (playState) {
//                    case MPS_INVALID:
//                        mTopMusicName.setText("");
//                        mTopMusicName.setVisibility(View.GONE);
//                        mOneDayAdapter.notifyItemChanged(musicInfo.position);
//                        break;
//                    case MPS_RESET:
//                        mTopMusicName.setText("");
//                        mTopMusicName.setVisibility(View.GONE);
//                        break;
//                    case MPS_PAUSE:
//                        mOneDayAdapter.notifyItemChanged(musicInfo.position);
//                        break;
//                    case MPS_PLAYING:
//                        mOneDayAdapter.notifyItemChanged(musicInfo.position);
//                        break;
//                    case MPS_PREPARE:
//                        mTopMusicName.setVisibility(View.VISIBLE);
//                        mTopMusicName.setText(musicInfo.musicName);
//                        mTopMusicName.setSelected(true);
//                        mOneDayAdapter.notifyItemChanged(musicInfo.position);
//                        break;
//                    case MPS_NOFILE:
//                        mTopMusicName.setText("");
//                        mTopMusicName.setVisibility(View.GONE);
//                        mOneDayAdapter.notifyItemChanged(musicInfo.position);
//                        break;
//                }
//            }
//        }
//    }
}
