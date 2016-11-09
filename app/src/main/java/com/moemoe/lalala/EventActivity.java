package com.moemoe.lalala;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.Callback;
import com.app.http.RequestParams;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.data.EventDataBean;
import com.moemoe.lalala.network.Otaku;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 云 on 2016/9/30.
 */
@ContentView(R.layout.ac_event)
public class EventActivity extends BaseActivity implements View.OnClickListener{

    @FindView(R.id.rl_root)
    private View mRoot;
    @FindView(R.id.iv_background)
    private ImageView mIvBackground;
    @FindView(R.id.rl_pwd_root)
    private View mPwdRoot;
//    @FindView(R.id.view_pwd)
//    private View mPwdClick;
    @FindView(R.id.ll_pwd_root)
    private LinearLayout mPwdAddRoot;
    @FindView(R.id.tv_sec)
    private TextView mTvSec;
    @FindView(R.id.sv_talk)
    private View mTalkRoot;
    @FindView(R.id.sv_talk_1)
    private View mTalkRoot1;
    @FindView(R.id.tv_a_1)
    private TextView mTvA1;
    @FindView(R.id.tv_a_2_1)
    private TextView mTvA2_1;
    @FindView(R.id.tv_a_2)
    private TextView mTvA2;
    @FindView(R.id.tv_b_1)
    private TextView mTvB1;
    @FindView(R.id.tv_b_2)
    private TextView mTvB2;
    @FindView(R.id.tv_b_1_1)
    private TextView mTvB1_1;
    @FindView(R.id.tv_b_2_1)
    private TextView mTvB2_1;
    @FindView(R.id.tv_main)
    private TextView mTvMain;
    @FindView(R.id.ll_end_root)
    private View mEndRoot;
    @FindView(R.id.tv_pwd_error)
    private TextView mTvError;
    @FindView(R.id.tv_kill_count)
    private TextView mTvKillCount;
    @FindView(R.id.view_1)
    private View mView1;
    @FindView(R.id.view_2)
    private View mView2;
    @FindView(R.id.view_3)
    private View mView3;
    @FindView(R.id.view_4)
    private View mView4;
    @FindView(R.id.view_5)
    private View mView5;
    @FindView(R.id.view_6)
    private View mView6;
    @FindView(R.id.view_7)
    private View mView7;
    @FindView(R.id.view_8)
    private View mView8;
    @FindView(R.id.view_9)
    private View mView9;
    @FindView(R.id.view_0)
    private View mView0;

    private int mErrorCount;
    private GestureDetector mGestureDetector;
    private boolean mIsFinish = false;
    private ArrayList<EventDataBean> list;
    private int mCurPosition;
    private int mKillCount;
    private String mPwd="";
    private boolean mFinish = false;

    @Override
    protected void initView() {
        requestCount();
        initEvent();
        mTvError.setVisibility(View.GONE);
        mCurPosition = 0;
        mErrorCount = 3;
        mKillCount = 1;
        eventAction();
        //mPwdClick.setOnClickListener(this);
        mView0.setOnClickListener(this);
        mView1.setOnClickListener(this);
        mView2.setOnClickListener(this);
        mView3.setOnClickListener(this);
        mView4.setOnClickListener(this);
        mView5.setOnClickListener(this);
        mView6.setOnClickListener(this);
        mView7.setOnClickListener(this);
        mView8.setOnClickListener(this);
        mView9.setOnClickListener(this);
        mGestureDetector = new GestureDetector(this, new GestureListener());
    }

    private void initEvent(){
        list = new ArrayList<>();
        list.add(new EventDataBean(0,"虽说是半夜，但校园里未免也太过冷清了\n" +
                "与其说是“冷清”\n" +
                "实际上是一个人也没有\n" +
                "除了我以外，一个人也没有",getResources().getColor(R.color.white),null,null));
        list.add(new EventDataBean(0,"再怎么说，这也太奇怪了\n" +
                "明明是一所热闹到出格的学校\n" +
                "被意外卷入这里于是见到了各式各样奇怪的学生\n" +
                "以及各式各样奇怪的活动……",getResources().getColor(R.color.white),null,null));
        list.add(new EventDataBean(0,"用牌子代替言语\n" +
                "套着白色鸭子布偶装的中年大叔\n" +
                "只招募外星人\n" +
                "未来人和超能力者的兔女郎社团\n" +
                "明明此前还通宵达旦的喧闹着呢\n" +
                "现在却……消失了？",getResources().getColor(R.color.white),null,null));
        list.add(new EventDataBean(0,"开学季的活动结束了吗？\n" +
                "不…此刻的感觉…\n" +
                "就像他们忽然蒸发了，或者说…\n" +
                "一切都是假象\n" +
                "他们从来没有出现过…",getResources().getColor(R.color.white),null,null));
        list.add(new EventDataBean(0,"“什么嘛，这种感觉…”",getResources().getColor(R.color.main_title_cyan),null,null));
        list.add(new EventDataBean(0,"即使在异世界，我也经常会被大家晾在一边啊…”",getResources().getColor(R.color.main_title_cyan),null,null));
        list.add(new EventDataBean(0,"我这样吐槽自己，安静到诡异的氛围也没有得到驱散。\n" +
                "半夜在这个原理不明的地方闲逛\n" +
                "果然不是正确的事…\n" +
                "回去睡觉吧",getResources().getColor(R.color.white),null,null));
        list.add(new EventDataBean(0,"“咦？那里是……”" +
                "回去睡觉吧",getResources().getColor(R.color.main_title_cyan),null,null));
        list.add(new EventDataBean(0,"从主楼高处的校长办公室里\n" +
                "亮着整个学园中唯一的灯",getResources().getColor(R.color.white),null,null));
        list.add(new EventDataBean(0,"这么晚了还在工作吗？\n" +
                "这里的校长是个严厉又神秘的大叔\n" +
                "总感觉他的笑容后隐藏了些什么秘密",getResources().getColor(R.color.white),null,null));
        list.add(new EventDataBean(0,"“找他问问应该能明白的吧？”",getResources().getColor(R.color.main_title_cyan),null,null));
        list.add(new EventDataBean(0,"“今晚到底怎么了？”",getResources().getColor(R.color.main_title_cyan),null,null));
        list.add(new EventDataBean(0,"“以及，这个世界…和这所学校…”",getResources().getColor(R.color.main_title_cyan),null,null));
        list.add(new EventDataBean(getResources().getColor(R.color.event_background),"校长室里似乎有人在争执。\n" +
                "从声音上来判断，是校长和一名女生。\n" +
                "她的声音……很耳熟……",getResources().getColor(R.color.white),null,null));
        ArrayList<String> a = new ArrayList<>();
        a.add("“…观测…中止…”");
        a.add("“…140813…反叛…排除。”");
        ArrayList<String> b = new ArrayList<>();
        b.add("“为什么？！你们到底想得到什么结果！！”");
        b.add("“请……别……啊！！！”");
        list.add(new EventDataBean(getResources().getColor(R.color.event_background),null,0,a,b));
        list.add(new EventDataBean(getResources().getColor(R.color.event_background),"隐约听到这样的对话。\n" +
                "怎么想，这也不应该是能在学校里听到的语句吧？\n" +
                "在好奇心的趋势下\n" +
                "我透过锁孔往校长室里望去。",getResources().getColor(R.color.white),null,null));
        list.add(new EventDataBean(R.drawable.bg_killschoolmaster_1,"“那是…？”",getResources().getColor(R.color.main_title_cyan),null,null));
        list.add(new EventDataBean(R.drawable.bg_killschoolmaster_1,"是校长和……莲？\n" +
                "两人的表情都十分严肃\n" +
                "应该是产生了什么纠纷吧？\n" +
                "说起“严肃”\n" +
                "莲似乎也很少露出除此以外的表情。",getResources().getColor(R.color.white),null,null));
        list.add(new EventDataBean(R.drawable.bg_killschoolmaster_1,"不爱说话并且经常翘课\n" +
                "虽说是同桌，但我并不了解她。\n" +
                "可是怎么想\n" +
                "她也不像是会和谁产生纠纷的性格\n" +
                "尤其还是…\n" +
                "这里的校长",getResources().getColor(R.color.white),null,null));
        list.add(new EventDataBean(R.drawable.bg_killschoolmaster_2,"“啊咧，那是…………”",getResources().getColor(R.color.main_title_cyan),null,null));
        list.add(new EventDataBean(R.drawable.bg_killschoolmaster_2,"“刀、刀？？开什么玩笑啊？？”",getResources().getColor(R.color.main_title_cyan),null,null));
        list.add(new EventDataBean(R.drawable.bg_killschoolmaster_2,"“喂！莲！你在做什么啊！喂！！校长！！”",getResources().getColor(R.color.main_title_cyan),null,null));
        list.add(new EventDataBean(R.drawable.bg_killschoolmaster_2,"开什么玩笑！！！\n" +
                "这是要出人命的啊！！！\n" +
                "猛烈的转动把手，校长室的门却纹丝不动\n" +
                "这是…密码锁？",getResources().getColor(R.color.white),null,null));
        list.add(new EventDataBean(R.drawable.bg_killschoolmaster_password_wall,null,0,null,null));
        list.add(new EventDataBean(R.drawable.bg_killschoolmaster_3,null,0,null,null));
        list.add(new EventDataBean(R.drawable.bg_killschoolmaster_3,"“莲那家伙把校长杀掉了？”",getResources().getColor(R.color.main_title_cyan),null,null));
        list.add(new EventDataBean(R.drawable.bg_killschoolmaster_3,"到底是怎么回事啊\n" +
                "还是先离开这个地方\n" +
                "恐惧驱使我往后小心翼翼的挪动",getResources().getColor(R.color.white),null,null));
        list.add(new EventDataBean(R.drawable.bg_killschoolmaster_3,"“敌性判定完毕。解除该对象的资讯连结。”",getResources().getColor(R.color.pink_tag_normal),null,null));
        list.add(new EventDataBean(R.drawable.bg_killschoolmaster_3,"仿佛听到莲还在说些什么\n" +
                "然而身体逐渐变冷，感觉从指尖开始消失\n" +
                "意识慢慢陷入黑暗之中",getResources().getColor(R.color.white),null,null));
        list.add(new EventDataBean(R.drawable.bg_killschoolmaster_4,null,0,null,null));
        list.add(new EventDataBean(getResources().getColor(R.color.event_background),null,0,null,null));
    }

    private void initPwdRight(){
        //list.clear();
        list.add(new EventDataBean(getResources().getColor(R.color.event_background),"为什么我会知道密码？\n" +
                "明明没有任何人告诉我。\n" +
                "不，现在还是救校长比较重要",getResources().getColor(R.color.white),null,null));
        ArrayList<String> b = new ArrayList<>();
        b.add("“住手啊！莲！”");
        b.add("“喂！等等，什.....”");
        ArrayList<String> a = new ArrayList<>();
        a.add("”你为什么会知道密码，现在的你\n是不可能观测到的”");
        list.add(new EventDataBean(getResources().getColor(R.color.event_background),null,0,a,b));
        list.add(new EventDataBean(0,"咕咚滚落的声音传入耳膜。\n" +
                "地面好近，咕咚咕咚地滚个不停。\n" +
                "啊啊，脖子被切断了啊",getResources().getColor(R.color.white),null,null));
        list.add(new EventDataBean(getResources().getColor(R.color.event_background),"“下次过来的话带来变化吧，\n好好记住啊”",0,null,null));
        list.add(new EventDataBean(0,"。。。啊啊啊，意识中断。\n" +
                "错误的行动就是这样。\n" +
                "毫无结果，传给下一棒",getResources().getColor(R.color.white),null,null));
    }

    @Override
    public void onBackPressed() {
        if(mIsFinish){
            super.onBackPressed();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void eventAction(){
        if(mFinish){
            AppSetting.isEnterEventToday = true;
            mPreferMng.setLashEventTime(System.currentTimeMillis());
            finish();
            return;
        }
        EventDataBean bean = list.get(mCurPosition);
        if(mCurPosition < 14 || mCurPosition == 15 || mCurPosition == 31 || mCurPosition == 33 || mCurPosition == 35){
            mTalkRoot1.setVisibility(View.GONE);
            mPwdRoot.setVisibility(View.GONE);
            mTalkRoot.setVisibility(View.GONE);
            mEndRoot.setVisibility(View.GONE);
            mTvSec.setVisibility(View.GONE);
            mIvBackground.setVisibility(View.GONE);
            mTvMain.setVisibility(View.VISIBLE);
            mTvMain.setText(bean.content);
            mTvMain.setTextColor(bean.content_color);
            if(mCurPosition == 13 || mCurPosition == 31){
                mRoot.setBackgroundColor(bean.background);
            }
            if(mCurPosition == 35){
                mTvMain.setBackgroundDrawable(null);
                mFinish = true;
            }
            mCurPosition++;
        }else if(mCurPosition == 14 && mTvA1.getVisibility() == View.GONE){
            mPwdRoot.setVisibility(View.GONE);
            mEndRoot.setVisibility(View.GONE);
            mTvSec.setVisibility(View.GONE);
            mIvBackground.setVisibility(View.GONE);
            mTvMain.setVisibility(View.GONE);
            mTalkRoot.setVisibility(View.VISIBLE);
            mTvA1.setText(bean.content_a.get(0));
            mTvA2.setText(bean.content_a.get(1));
            mTvB1.setText(bean.content_b.get(0));
            mTvB2.setText(bean.content_b.get(1));
            mTvA1.setVisibility(View.VISIBLE);
        }else if(mCurPosition == 14 && mTvB1.getVisibility() == View.GONE){
            mTvB1.setVisibility(View.VISIBLE);
        }else if(mCurPosition == 14 && mTvA2.getVisibility() == View.GONE){
            mTvA2.setVisibility(View.VISIBLE);
        }else if(mCurPosition == 14 && mTvB2.getVisibility() == View.GONE){
            mTvB2.setVisibility(View.VISIBLE);
            mCurPosition++;
        }else if((mCurPosition > 15 && mCurPosition < 23)||(mCurPosition > 24 && mCurPosition < 29)){
            mPwdRoot.setVisibility(View.GONE);
            mTalkRoot.setVisibility(View.GONE);
            mEndRoot.setVisibility(View.GONE);
            mTvSec.setVisibility(View.VISIBLE);
            mIvBackground.setVisibility(View.VISIBLE);
            mTvMain.setVisibility(View.GONE);
            mIvBackground.setImageResource(bean.background);
            mTvSec.setText(bean.content);
            mTvSec.setTextColor(bean.content_color);
            mCurPosition++;
        }else if(mCurPosition == 23){
            mPwdRoot.setVisibility(View.VISIBLE);
            mTalkRoot.setVisibility(View.GONE);
            mEndRoot.setVisibility(View.GONE);
            mTvSec.setVisibility(View.GONE);
            mIvBackground.setVisibility(View.VISIBLE);
            mTvMain.setVisibility(View.GONE);
            mIvBackground.setImageResource(bean.background);
        }else if(mCurPosition == 24 || mCurPosition == 29){
            mPwdRoot.setVisibility(View.GONE);
            mTalkRoot.setVisibility(View.GONE);
            mEndRoot.setVisibility(View.GONE);
            mTvSec.setVisibility(View.GONE);
            mIvBackground.setVisibility(View.VISIBLE);
            mTvMain.setVisibility(View.GONE);
            mIvBackground.setImageResource(bean.background);
            mCurPosition++;
        }else if(mCurPosition == 30){
            mPwdRoot.setVisibility(View.GONE);
            mTalkRoot.setVisibility(View.GONE);
            mEndRoot.setVisibility(View.VISIBLE);
            mTvSec.setVisibility(View.GONE);
            mIvBackground.setVisibility(View.GONE);
            mTvMain.setVisibility(View.GONE);
            mTvKillCount.setText(getString(R.string.label_kill_count,mKillCount));
            mCurPosition++;
            mFinish = true;
        }else if(mCurPosition == 32){
            mTalkRoot1.setVisibility(View.VISIBLE);
            mPwdRoot.setVisibility(View.GONE);
            mTalkRoot.setVisibility(View.GONE);
            mEndRoot.setVisibility(View.GONE);
            mTvSec.setVisibility(View.GONE);
            mIvBackground.setVisibility(View.GONE);
            mTvMain.setVisibility(View.GONE);
            mTvA2_1.setText(bean.content_a.get(0));
            mTvB1_1.setText(bean.content_b.get(0));
            mTvB2_1.setText(bean.content_b.get(1));
            mCurPosition++;
        }else if(mCurPosition == 34){
            mTvMain.setVisibility(View.VISIBLE);
            mTalkRoot1.setVisibility(View.GONE);
            mPwdRoot.setVisibility(View.GONE);
            mTalkRoot.setVisibility(View.GONE);
            mEndRoot.setVisibility(View.GONE);
            mTvSec.setVisibility(View.GONE);
            mIvBackground.setVisibility(View.GONE);
            mTvMain.setText(bean.content);
            mTvMain.setTextColor(getResources().getColor(R.color.pink_tag_normal));
            mTvMain.setBackgroundResource(R.drawable.icon_killschoolmaster_talk_len);
            mCurPosition++;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.view_0){
            mPwd += "0";
            checkPwd();
        }
        if(id == R.id.view_1){
            mPwd += "1";
            checkPwd();
        }
        if(id == R.id.view_2){
            mPwd += "2";
            checkPwd();
        }
        if(id == R.id.view_3){
            mPwd += "3";
            checkPwd();
        }
        if(id == R.id.view_4){
            mPwd += "4";
            checkPwd();
        }
        if(id == R.id.view_5){
            mPwd += "5";
            checkPwd();
        }
        if(id == R.id.view_6){
            mPwd += "6";
            checkPwd();
        }
        if(id == R.id.view_7){
            mPwd += "7";
            checkPwd();
        }
        if(id == R.id.view_8){
            mPwd += "8";
            checkPwd();
        }
        if(id == R.id.view_9){
            mPwd += "9";
            checkPwd();
        }
    }

    private void checkPwd(){
        if(mErrorCount > 0){
            int count = mPwdAddRoot.getChildCount();
            if(count == 6){
                if(mPwd.equals("196040")){
                    initPwdRight();
                    mCurPosition = 31;
                    mPwdRoot.setVisibility(View.GONE);
                }else {
                    mPwdAddRoot.removeViews(1,5);
                    mTvError.setVisibility(View.VISIBLE);
                    mTvError.setText(getString(R.string.label_pwd_error_num,--mErrorCount));
                }
            }else {
                mTvError.setVisibility(View.GONE);
                ImageView i = new ImageView(EventActivity.this);
                i.setImageResource(R.drawable.icon_killschoolmaster_number);
                mPwdAddRoot.addView(i);
            }
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if(mPwdRoot.getVisibility() == View.GONE || mErrorCount <= 0){
                if(mErrorCount <= 0 && mCurPosition == 23){
                    mCurPosition++;
                }
                eventAction();
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    private void requestCount(){
        RequestParams params = new RequestParams("http://prize.moemoe.la:8000/principal_samsara");
        Utils.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.optInt("ok") == Otaku.SERVER_OK){
                        mKillCount = jsonObject.optInt("data");
                    }else {
                        mKillCount = 1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mKillCount = 1;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mKillCount = 1;
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
