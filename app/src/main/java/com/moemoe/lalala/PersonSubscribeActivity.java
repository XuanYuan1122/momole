package com.moemoe.lalala;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.Callback;
import com.app.ex.DbException;
import com.app.http.request.UriRequest;
import com.app.view.DbManager;
import com.moemoe.lalala.data.CalendarDayItem;
import com.moemoe.lalala.data.RssDbBean;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.NoDoubleClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/5/6 0006.
 */
@ContentView(R.layout.ac_one_list)
public class PersonSubscribeActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.tv_toolbar_title)
    private TextView mTvTitle;
    @FindView(R.id.list)
    private RecyclerView mLvList;
    @FindView(R.id.sl_refresh)
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PersonAdapter mAdapter;
    private ArrayList<CalendarDayItem.RssInstance> mRssInstances;
    private String mDay;
    private int mTotal;
    private DbManager db;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 透明状态栏
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            // 透明导航栏
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            // 设置状态栏的颜色
//            tintManager.setStatusBarTintResource(R.color.main_title_cyan);
//            getWindow().getDecorView().setFitsSystemWindows(true);
//        }
//    }

    @Override
    protected void initView() {
        db = Utils.getDb(MoemoeApplication.sDaoConfig);
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mRssInstances = new ArrayList<>();
        Intent i = getIntent();
        mDay = i.getStringExtra("day");
        mTotal = i.getIntExtra("total", -1);
        mAdapter = new PersonAdapter();
        mLvList.setAdapter(mAdapter);
        loadDataFromDb();
        requestRss();
        mTvTitle.setText("个人订阅");
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_light_cyan, R.color.main_title_cyan);
//        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
//                .getDisplayMetrics()));

//        mLvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                CalendarDayItem.RssInstance item = mAdapter.getItem(position);
//                if(!TextUtils.isEmpty(item.target)){
//                    Uri uri = Uri.parse(item.target);
//                    IntentUtils.toActivityFromUri(PersonSubscribeActivity.this, uri);
//                }
//            }
//        });
    }

    public static void startActivity(Context context,String day,int total){
        context.startActivity(intentOf(context, day,total));
    }

    public static Intent intentOf(Context context,String day,int total){
        return new Intent(context,PersonSubscribeActivity.class).putExtra("day",day).putExtra("total",total);
    }

    private void loadDataFromDb(){
        try {
            RssDbBean bean = db.selector(RssDbBean.class)
                    .where("day","=",mDay)
                    .findFirst();
            if(bean != null && bean.json != null){
                JSONArray array = new JSONArray(bean.json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.optJSONObject(i);
                    CalendarDayItem.RssInstance rssInstance = new CalendarDayItem.RssInstance();
                    rssInstance.readFromJsonContent(PersonSubscribeActivity.this, object.toString());
                    mRssInstances.add(rssInstance);
                }
                mAdapter.setDate(mRssInstances);
            }
        } catch (DbException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestRss(){
        if(!NetworkUtils.isNetworkAvailable(this)){
            ToastUtil.showCenterToast(this,R.string.a_server_msg_connection);
            return;
        }
        Otaku.getCalendarV2().requestRss(mPreferMng.getToken(), mDay,0, mTotal).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                JSONArray array = null;
                try {
                    array = new JSONArray(s);
                    RssDbBean bean = new RssDbBean();
                    bean.day = mDay;
                    bean.json = s;
                    db.saveOrUpdate(bean);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.optJSONObject(i);
                        CalendarDayItem.RssInstance rssInstance = new CalendarDayItem.RssInstance();
                        rssInstance.readFromJsonContent(PersonSubscribeActivity.this, object.toString());
                        mRssInstances.add(rssInstance);
                    }
                    mAdapter.setDate(mRssInstances);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(String e) {

            }
        }));
//        Otaku.getCalendar().requestRss(mPreferMng.getToken(), mDay,0, mTotal, new Callback.InterceptCallback<String>() {
//            @Override
//            public void beforeRequest(UriRequest request) throws Throwable {
//
//            }
//
//            @Override
//            public void afterRequest(UriRequest request) throws Throwable {
//
//            }
//
//            @Override
//            public void onSuccess(String result) {
//                try{
//                    JSONObject json = new JSONObject(result);
//                    if(json.optInt("ok") == Otaku.SERVER_OK){
//                        JSONArray array = new JSONArray(json.optString("data"));
//                        RssDbBean bean = new RssDbBean();
//                        bean.day = mDay;
//                        bean.json = json.optString("data");
//                        db.saveOrUpdate(bean);
//                        for (int i = 0; i < array.length(); i++) {
//                            JSONObject object = array.optJSONObject(i);
//                            CalendarDayItem.RssInstance rssInstance = new CalendarDayItem.RssInstance();
//                            rssInstance.readFromJsonContent(PersonSubscribeActivity.this, object.toString());
//                            mRssInstances.add(rssInstance);
//                        }
//                        mAdapter.setDate(mRssInstances);
//                    }else {
//                        String err = json.optString("error_code");
//                        if(TextUtils.isEmpty(err)){
//                            err = json.optString("data");
//                        }
//                        if(!TextUtils.isEmpty(err) && err.contains("TOKEN")){
//                            String uuid = mPreferMng.getUUid();
//                            if(!TextUtils.isEmpty(uuid)){
//                                tryLoginFirst(null);
//                            }
//                        }
//                        ToastUtil.showCenterToast(PersonSubscribeActivity.this, R.string.msg_server_connection);
//                    }
//                }catch (Exception e){
//
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                ToastUtil.showCenterToast(PersonSubscribeActivity.this, R.string.msg_server_connection);
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        },this);
    }

    @Override
    public void onRefresh() {
        requestRss();
    }

//    public static class PersonAdapter extends BaseAdapter {
//
//        private Context context;
//        ArrayList<CalendarDayItem.RssInstance> mRssInstances;
//
//        public PersonAdapter(){
//            mRssInstances = new ArrayList<>();
//        }
//
//        public void setDate(ArrayList<CalendarDayItem.RssInstance> rssInstances,Context context){
//            mRssInstances = rssInstances;
//            this.context = context;
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public int getCount() {
//            return mRssInstances.size();
//        }
//
//        @Override
//        public CalendarDayItem.RssInstance getItem(int position) {
//            return mRssInstances.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            NormalHolder holder = null;
//            if(convertView == null){
//                convertView = LayoutInflater.from(context).inflate(R.layout.item_calender_type5_item,
//                        null);
//                holder = new NormalHolder();
//                holder.ivState = (ImageView) convertView.findViewById(R.id.iv_state);
//                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
//                holder.tvEventStatus = (TextView) convertView.findViewById(R.id.tv_event_status);
//                holder.tvChapter = (TextView) convertView.findViewById(R.id.tv_event_chapter);
//                convertView.setTag(holder);
//            }
//            holder = (NormalHolder) convertView.getTag();
//            CalendarDayItem.RssInstance bean = getItem(position);
//            if(!bean.unread){
//                holder.ivState.setImageResource(R.drawable.icon_timetable_subscibe_hook);
//            }else {
//                holder.ivState.setImageResource(R.drawable.icon_timetable_subscibe_circle);
//            }
//            holder.tvTitle.setText(bean.title);
//            if(bean.tip != null && !bean.tip.equals("")){
//                holder.tvEventStatus.setVisibility(View.VISIBLE);
//                holder.tvChapter.setVisibility(View.VISIBLE);
//                holder.tvChapter.setText(bean.tip);
//            }else {
//                holder.tvEventStatus.setVisibility(View.GONE);
//                holder.tvChapter.setVisibility(View.GONE);
//            }
//            return convertView;
//        }
//
//        class NormalHolder {
//            ImageView ivState;
//            TextView tvTitle;
//            TextView tvEventStatus;
//            TextView tvChapter;
//        }
//    }

    public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.NormalHolder>{


        @Override
        public NormalHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NormalHolder(LayoutInflater.from(PersonSubscribeActivity.this).inflate(R.layout.item_calender_type5_item,
                        null));
        }

        @Override
        public void onBindViewHolder(NormalHolder holder,final int position) {
            CalendarDayItem.RssInstance bean = getItem(position);
            if(!bean.unread){
                holder.ivState.setImageResource(R.drawable.icon_timetable_subscibe_hook);
            }else {
                holder.ivState.setImageResource(R.drawable.icon_timetable_subscibe_circle);
            }
            holder.tvTitle.setText(bean.title);
            if(bean.tip != null && !bean.tip.equals("")){
                holder.tvEventStatus.setVisibility(View.VISIBLE);
                holder.tvChapter.setVisibility(View.VISIBLE);
                holder.tvChapter.setText(bean.tip);
            }else {
                holder.tvEventStatus.setVisibility(View.GONE);
                holder.tvChapter.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    CalendarDayItem.RssInstance item = mAdapter.getItem(position);
                    if(!TextUtils.isEmpty(item.target)){
                        Uri uri = Uri.parse(item.target);
                        IntentUtils.toActivityFromUri(PersonSubscribeActivity.this, uri,v);
                    }
                }
            });
        }

        public CalendarDayItem.RssInstance getItem(int position) {
            return mRssInstances.get(position);
        }

        public void setDate(ArrayList<CalendarDayItem.RssInstance> rssInstances){
            mRssInstances = rssInstances;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mRssInstances.size();
        }

        class NormalHolder extends RecyclerView.ViewHolder{
            ImageView ivState;
            TextView tvTitle;
            TextView tvEventStatus;
            TextView tvChapter;

            public NormalHolder(View itemView) {
                super(itemView);
                ivState = (ImageView) itemView.findViewById(R.id.iv_state);
                tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
                tvEventStatus = (TextView) itemView.findViewById(R.id.tv_event_status);
                tvChapter = (TextView) itemView.findViewById(R.id.tv_event_chapter);
            }


        }
    }
}
