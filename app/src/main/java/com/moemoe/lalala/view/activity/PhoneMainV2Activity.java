package com.moemoe.lalala.view.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerPhoneMainComponent;
import com.moemoe.lalala.di.modules.PhoneMainModule;
import com.moemoe.lalala.dialog.SignDialog;
import com.moemoe.lalala.event.MateChangeEvent;
import com.moemoe.lalala.event.SystemMessageEvent;
import com.moemoe.lalala.model.entity.DailyTaskEntity;
import com.moemoe.lalala.model.entity.PersonalMainEntity;
import com.moemoe.lalala.model.entity.SignEntity;
import com.moemoe.lalala.presenter.PhoneMainContract;
import com.moemoe.lalala.presenter.PhoneMainPresenter;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.fragment.JuQingChatV2Fragment;
import com.moemoe.lalala.view.fragment.KiraConversationFragment;
import com.moemoe.lalala.view.fragment.KiraConversationListFragment;
import com.moemoe.lalala.view.fragment.PhoneAlarmV2Fragment;
import com.moemoe.lalala.view.fragment.PhoneGroupDetailV2Fragment;
import com.moemoe.lalala.view.fragment.PhoneJuQingV2Fragment;
import com.moemoe.lalala.view.fragment.PhoneMateSelectV2Fragment;
import com.moemoe.lalala.view.fragment.PhoneMenuV2Fragment;
import com.moemoe.lalala.view.fragment.PhoneTicketV2Fragment;

import java.util.Stack;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.model.Conversation;

import static com.moemoe.lalala.utils.StartActivityConstant.REQ_GROUP_DETAIL;

/**
 * 手机界面
 * Created by yi on 2017/9/4.
 */

@SuppressWarnings("deprecation")
public class PhoneMainV2Activity extends BaseAppCompatActivity implements PhoneMainContract.View,IUnReadMessageObserver{

    @BindView(R.id.iv_back_2)
    ImageView mIvBack;
    @BindView(R.id.layout_phone_main)
    View mMainRoot;
    @BindView(R.id.iv_role)
    ImageView mIvRole;
    @BindView(R.id.iv_create_dynamic)
    ImageView mIvCreatDynamic;
    @BindView(R.id.tv_show_text)
    TextView mTvText;
    @BindView(R.id.rl_role_root)
    RelativeLayout mRoleRoot;
    @BindView(R.id.ll_wenzhang_root)
    View mHouShanRoot;
    @BindView(R.id.tv_msg_dot)
    TextView mTvMsgDot;
    @BindView(R.id.tv_sys_msg_dot)
    TextView mTvSysMsgDot;
    @BindView(R.id.app_bar)
    View mAppBar;
    @BindView(R.id.phone_container)
    View mFragmentRoot;
    @BindView(R.id.iv_back)
    ImageView mIvFragmentBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.iv_menu_list)
    ImageView mIvMenu;
    @BindView(R.id.tv_menu)
    TextView mTvMenu;

    @Inject
    PhoneMainPresenter mPresenter;
    private Stack<Fragment> fragmentStack;
    private GestureDetector gestureDetector;
    private boolean mIsSignPress;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_phone_new;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPhoneMainComponent.builder()
                .phoneMainModule(new PhoneMainModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        AndroidBug5497Workaround.assistActivity(this);

        final Conversation.ConversationType[] conversationTypes = {
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP
        };
        RongIM.getInstance().addUnReadMessageCountChangedObserver(this, conversationTypes);
        fragmentStack = new Stack<>();

        int dotNum = PreferenceUtils.getGroupDotNum(this) + PreferenceUtils.getRCDotNum(this) + PreferenceUtils.getJuQIngDotNum(this);
        if(dotNum > 0){
            mTvMsgDot.setVisibility(View.VISIBLE);
            if(dotNum > 999) dotNum = 999;
            mTvMsgDot.setText(String.valueOf(dotNum));
        }else {
            mTvMsgDot.setVisibility(View.GONE);
        }
        gestureDetector = new GestureDetector(this,onGestureListener);
        subscribeSearchChangedEvent();
        ViewUtils.setRoleButton(mIvRole,mTvText);
        if(AppSetting.TXBB){
            mHouShanRoot.setVisibility(View.VISIBLE);
            mHouShanRoot.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent i = new Intent(PhoneMainV2Activity.this,LuntanActivity.class);
                    startActivity(i);
                }
            });
        }else {
            mHouShanRoot.setVisibility(View.INVISIBLE);
            mHouShanRoot.setOnClickListener(null);
        }
        toUri();
    }

    private void toUri(){
        Uri uri = getIntent().getData();
        if(uri != null && uri.getScheme().equals("rong")){
            if(NetworkUtils.checkNetworkAndShowError(this) && DialogUtils.checkLoginAndShowDlg(PhoneMainV2Activity.this)){
                Conversation.ConversationType type = Conversation.ConversationType.valueOf(uri
                    .getLastPathSegment().toUpperCase());
                String path = uri.getPath();
                String targetId = uri.getQueryParameter("targetId");
                if(targetId.contains("juqing")){
                    String[] juQing = targetId.split(":");
                    toFragment(JuQingChatV2Fragment.newInstance(juQing[2],juQing[1]));
                }else {
                    if(path.contains("conversation")){
                        if("conversation/detail".equals(path)){
                            toFragment(PhoneGroupDetailV2Fragment.newInstance(targetId));
                        }else {
                            Uri uri1 = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                                    .appendPath("conversation").appendPath(type.getName())
                                    .appendQueryParameter("targetId", targetId).build();
                            toFragment(KiraConversationFragment.newInstance(targetId,uri.getQueryParameter("title"),type.getName(),uri1));
                        }
                    }else if(path.contains("conversationlist")){
                        Uri uri1 = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                                .appendPath("conversationlist")
                                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
                                .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                                .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
                                .build();
                        toFragment(KiraConversationListFragment.newInstance(uri1));
                    }
                }
            }
        }
    }

    public void toFragment(Fragment fragment){
        mIvRole.setVisibility(View.GONE);
        mMainRoot.setVisibility(View.GONE);
        mIvBack.setVisibility(View.GONE);
        if(fragment instanceof PhoneJuQingV2Fragment){
            mAppBar.setVisibility(View.GONE);
        }else {
            mAppBar.setVisibility(View.VISIBLE);
        }
        mFragmentRoot.setVisibility(View.VISIBLE);

        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(!fragmentStack.empty()){
            Fragment fragmentP = fragmentStack.pop();
            mFragmentTransaction.hide(fragmentP).add(R.id.phone_container,fragment,fragment.getClass().getSimpleName() + "_fragment");
            fragmentStack.push(fragmentP);
        }else {
            mFragmentTransaction.add(R.id.phone_container,fragment,fragment.getClass().getSimpleName() + "_fragment");
        }
        mFragmentTransaction.commit();
        fragmentStack.push(fragment);

        setTitle(((IPhoneFragment)fragment).getTitle());
        setMenu(((IPhoneFragment)fragment).getMenu());
        setBack(((IPhoneFragment)fragment).getBack());
        setTitleColor(((IPhoneFragment)fragment).getTitleColor());
    }

    public void setTitle(String title){
        if(title != null) mTvTitle.setText(title);
    }

    public void setTitleColor(int res){
        if(res != 0){
            mTvTitle.setTextColor(ContextCompat.getColor(this,res));
        }
    }

    public void setMenu(@DrawableRes int res){
        if(res == 0){
            mIvMenu.setVisibility(View.GONE);
        }else {
            mIvMenu.setVisibility(View.VISIBLE);
            mIvMenu.setImageResource(res);
        }
    }

    public void setLuyinMenu(String menu){
        mTvMenu.setVisibility(View.VISIBLE);
        mTvMenu.setTextColor(Color.WHITE);
        mTvMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.x20));
        mTvMenu.setGravity(Gravity.CENTER);
        mTvMenu.setText(menu);
        mTvMenu.setBackgroundResource(R.drawable.shape_rect_border_main_background_y22);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.x144),(int)getResources().getDimension(R.dimen.y44));
        lp.rightMargin = (int) getResources().getDimension(R.dimen.x20);
        mTvMenu.setLayoutParams(lp);
    }

    public void setBack(@DrawableRes int res){
        if(res == 0){
            mIvFragmentBack.setVisibility(View.GONE);
        }else {
            mIvFragmentBack.setVisibility(View.VISIBLE);
            mIvFragmentBack.setImageResource(res);
        }
    }

    @Override
    public void onBackPressed() {
        if(fragmentStack.empty()){
            if(mIvRole.isSelected()){
                clickRole();
            }else {
                super.onBackPressed();
            }
        }else {
            Fragment fragment = fragmentStack.pop();
            if(fragment instanceof PhoneTicketV2Fragment){
                mTvMenu.setVisibility(View.GONE);
            }
            if(!((IPhoneFragment) fragment).onBackPressed()){
                if(fragmentStack.empty()){
                    mIvRole.setVisibility(View.VISIBLE);
                    mMainRoot.setVisibility(View.VISIBLE);
                    mIvBack.setVisibility(View.VISIBLE);
                    mAppBar.setVisibility(View.GONE);
                    mFragmentRoot.setVisibility(View.GONE);
                    FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    mFragmentTransaction.remove(fragment);
                    mFragmentTransaction.commit();
                    fragment = null;
                }else {
                    Fragment fragmentS = fragmentStack.pop();
                    FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    mFragmentTransaction.remove(fragment).show(fragmentS);
                    mFragmentTransaction.commit();
                    fragmentStack.push(fragmentS);
                    fragment = null;
                    setTitle(((IPhoneFragment)fragmentS).getTitle());
                    setMenu(((IPhoneFragment)fragmentS).getMenu());
                    setBack(((IPhoneFragment)fragmentS).getBack());
                    setTitleColor(((IPhoneFragment)fragmentS).getTitleColor());
                }
            }else {
                fragmentStack.push(fragment);
            }
        }
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
        mIvFragmentBack.setOnClickListener(new NoDoubleClickListener(300) {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
        mIvMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Fragment fragmentF = fragmentStack.pop();
                fragmentStack.push(fragmentF);
                ((IPhoneFragment) fragmentF).onMenuClick();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    public void hideRole(){
        ObjectAnimator roleAnimator = ObjectAnimator.ofFloat(mRoleRoot,"translationY",0,mRoleRoot.getHeight()).setDuration(300);
        roleAnimator.setInterpolator(new OvershootInterpolator());
        roleAnimator.start();
    }

    public void showRole(){
        ObjectAnimator roleAnimator = ObjectAnimator.ofFloat(mRoleRoot,"translationY",mRoleRoot.getHeight(),0).setDuration(300);
        roleAnimator.setInterpolator(new OvershootInterpolator());
        roleAnimator.start();
    }

    private static final int FLING_MIN_DISTANCE = 10;
    private static final int FLING_MIN_VELOCITY = 0;
    private boolean isHide;

    GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY){
            if (e1.getY()-e2.getY() > FLING_MIN_DISTANCE
                    && Math.abs(velocityY) > FLING_MIN_VELOCITY) {
                if(!isHide){
                    hideRole();
                    isHide = true;
                }
            } else if (e2.getY()-e1.getY() > FLING_MIN_DISTANCE
                    && Math.abs(velocityY) > FLING_MIN_VELOCITY) {
                if(isHide){
                    showRole();
                    isHide = false;
                }
            }
            return false;
        }
    };

    @Override
    protected void initListeners() {
        mIvRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRole();
            }
        });
        mIvCreatDynamic.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(DialogUtils.checkLoginAndShowDlg(PhoneMainV2Activity.this)){
                    clickRole();
                    Intent i4 = new Intent(PhoneMainV2Activity.this,CreateDynamicActivity.class);
                    i4.putExtra("default_tag","广场");
                    startActivity(i4);
                }
            }
        });
    }

    private void clickRole(){
        mIvRole.setSelected(!mIvRole.isSelected());
        if(mIvRole.isSelected()){
            mIvCreatDynamic.setVisibility(View.VISIBLE);
            mTvText.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mRoleRoot.setLayoutParams(lp);
            mRoleRoot.setBackgroundColor(ContextCompat.getColor(PhoneMainV2Activity.this,R.color.alph_60));
            mRoleRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickRole();
                }
            });
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_END);
            mIvRole.setLayoutParams(lp1);
        }else {
            mIvCreatDynamic.setVisibility(View.GONE);
            mTvText.setVisibility(View.GONE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.addRule(RelativeLayout.ALIGN_PARENT_END);
            mRoleRoot.setLayoutParams(lp);
            mRoleRoot.setBackgroundColor(ContextCompat.getColor(PhoneMainV2Activity.this,R.color.transparent));
            mRoleRoot.setOnClickListener(null);
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mIvRole.setLayoutParams(lp1);
        }
    }

    @Override
    protected void initData() {

    }

    private void subscribeSearchChangedEvent() {
        Disposable sysSubscription = RxBus.getInstance()
                .toObservable(SystemMessageEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Consumer<SystemMessageEvent>() {
                    @Override
                    public void accept(SystemMessageEvent systemMessageEvent) throws Exception {
                        if(PreferenceUtils.getMessageDot(PhoneMainV2Activity.this,"neta") || PreferenceUtils.getMessageDot(PhoneMainV2Activity.this,"system") || PreferenceUtils.getMessageDot(PhoneMainV2Activity.this,"at_user") || PreferenceUtils.getMessageDot(PhoneMainV2Activity.this,"normal")){
                            mTvSysMsgDot.setVisibility(View.VISIBLE);
                            int num = PreferenceUtils.getNetaMsgDotNum(PhoneMainV2Activity.this) + PreferenceUtils.getSysMsgDotNum(PhoneMainV2Activity.this) + PreferenceUtils.getAtUserMsgDotNum(PhoneMainV2Activity.this) + PreferenceUtils.getNormalMsgDotNum(PhoneMainV2Activity.this);
                            if(num > 999) num = 999;
                            mTvSysMsgDot.setText(String.valueOf(num));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        Disposable subscription1 = RxBus.getInstance()
                .toObservable(MateChangeEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Consumer<MateChangeEvent>() {
                    @Override
                    public void accept(MateChangeEvent backSchoolEvent) throws Exception {
                        ViewUtils.setRoleButton(mIvRole,mTvText);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        RxBus.getInstance().addSubscription(this, sysSubscription);
        RxBus.getInstance().addSubscription(this, subscription1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PreferenceUtils.getMessageDot(this,"neta") || PreferenceUtils.getMessageDot(this,"system") || PreferenceUtils.getMessageDot(this,"at_user") || PreferenceUtils.getMessageDot(this,"normal")){//|| showDot){
            mTvSysMsgDot.setVisibility(View.VISIBLE);
            int num = PreferenceUtils.getNetaMsgDotNum(PhoneMainV2Activity.this) + PreferenceUtils.getSysMsgDotNum(PhoneMainV2Activity.this) + PreferenceUtils.getAtUserMsgDotNum(PhoneMainV2Activity.this) + PreferenceUtils.getNormalMsgDotNum(PhoneMainV2Activity.this);
            if(num > 999) num = 999;
            mTvSysMsgDot.setText(String.valueOf(num));
        }else {
            mTvSysMsgDot.setVisibility(View.GONE);
        }
        ViewUtils.setRoleButton(mIvRole,mTvText);
        int dotNum = PreferenceUtils.getGroupDotNum(this) + PreferenceUtils.getRCDotNum(this) + PreferenceUtils.getJuQIngDotNum(this);
        if(dotNum > 0){
            mTvMsgDot.setVisibility(View.VISIBLE);
            if(dotNum > 999) dotNum = 999;
            mTvMsgDot.setText(String.valueOf(dotNum));
        }else {
            mTvMsgDot.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.ll_menu_root,R.id.ll_msg_root,R.id.ll_mate_root,R.id.ll_album_root,R.id.ll_alarm_root,R.id.ll_shop_root,R.id.ll_search_root,R.id.ll_person_root,R.id.ll_sign_root,R.id.ll_bag_root,R.id.ll_live2d_shop_root,R.id.ll_sys_msg_root})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ll_menu_root:
                if(NetworkUtils.checkNetworkAndShowError(this) && DialogUtils.checkLoginAndShowDlg(PhoneMainV2Activity.this)){
                    toFragment(PhoneMenuV2Fragment.newInstance());
                }
                break;
            case R.id.ll_msg_root:
                if(NetworkUtils.checkNetworkAndShowError(this) && DialogUtils.checkLoginAndShowDlg(PhoneMainV2Activity.this)){
                    Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                            .appendPath("conversationlist")
                            .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                            .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
                            .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                            .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                            .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
                            .build();
                    toFragment(KiraConversationListFragment.newInstance(uri));
                }
                break;
            case R.id.ll_mate_root:
                if(NetworkUtils.checkNetworkAndShowError(this) && DialogUtils.checkLoginAndShowDlg(PhoneMainV2Activity.this)) {
                    toFragment(PhoneMateSelectV2Fragment.newInstance());
                }
                break;
            case R.id.ll_album_root:
                if(NetworkUtils.checkNetworkAndShowError(this) && DialogUtils.checkLoginAndShowDlg(PhoneMainV2Activity.this)) {
                    toFragment(PhoneJuQingV2Fragment.newInstance());
                }
                break;
            case R.id.ll_alarm_root:
                if(NetworkUtils.checkNetworkAndShowError(this) && DialogUtils.checkLoginAndShowDlg(PhoneMainV2Activity.this)) {
                    toFragment(PhoneAlarmV2Fragment.newInstance());
                }
                break;
            case R.id.ll_shop_root:
                Intent i7 = new Intent(PhoneMainV2Activity.this,CoinShopActivity.class);
                startActivity(i7);
                break;
            case R.id.ll_search_root:
                Intent i6 = new Intent(PhoneMainV2Activity.this,SearchActivity.class);
                startActivity(i6);
                break;
            case R.id.ll_person_root:
                if(NetworkUtils.checkNetworkAndShowError(this) && DialogUtils.checkLoginAndShowDlg(PhoneMainV2Activity.this)){
                    Intent i1 = new Intent(PhoneMainV2Activity.this,PersonalV2Activity.class);
                    i1.putExtra(UUID,PreferenceUtils.getUUid());
                    startActivity(i1);
                }
                break;
            case R.id.ll_sign_root:
                if(DialogUtils.checkLoginAndShowDlg(PhoneMainV2Activity.this) && !mIsSignPress){
                    mIsSignPress = true;
                    mPresenter.getDailyTask();
                }
                break;
            case R.id.ll_bag_root:
                if(NetworkUtils.checkNetworkAndShowError(this) && DialogUtils.checkLoginAndShowDlg(PhoneMainV2Activity.this)) {
                    if(PreferenceUtils.getAuthorInfo().isOpenBag()){
                        Intent i2 = new Intent(this,NewBagActivity.class);
                        i2.putExtra("uuid",PreferenceUtils.getUUid());
                        startActivity(i2);
                    }else {
                        Intent i2 = new Intent(this,BagOpenActivity.class);
                        startActivity(i2);
                    }
                }
                break;
            case R.id.ll_live2d_shop_root:
                if(DialogUtils.checkLoginAndShowDlg(this)){
                    Intent i8 = new Intent(this,Live2dShopActivity.class);
                    startActivity(i8);
                }
                break;
            case R.id.ll_sys_msg_root:
                if(DialogUtils.checkLoginAndShowDlg(this)){
                    PersonalMsgActivity.startActivity(this,PreferenceUtils.getUUid());
                }
                break;

        }
    }

    @Override
    protected void onDestroy() {
        RxBus.getInstance().unSubscribe(this);
        RongIM.getInstance().removeUnReadMessageCountChangedObserver(this);
        super.onDestroy();
    }

    @Override
    public void onFailure(int code, String msg) {
        mIsSignPress = false;
    }

    @Override
    public void onDailyTaskLoad(DailyTaskEntity entity) {
        mIsSignPress = false;
        SignDialog dialog = new SignDialog(PhoneMainV2Activity.this);
        dialog.setTask(entity);
        dialog.setAnimationEnable(true)
                .setPositiveListener(new SignDialog.OnPositiveListener() {
                    @Override
                    public void onClick(SignDialog dialog) {
                        if(NetworkUtils.checkNetworkAndShowError(PhoneMainV2Activity.this)){
                            mPresenter.signToday(dialog);
                        }
                    }
                }).show();
    }

    public void loadPerson(){
        mPresenter.requestPersonMain();
    }

    @Override
    public void onPersonMainLoad(PersonalMainEntity entity) {
        PersonalLevelActivity.startActivity(this,entity.getLevelName(),entity.getLevelColor(),entity.getScore(),entity.getLevelScoreStart(),entity.getLevelScoreEnd(),entity.getLevel());
    }

    @Override
    public void changeSignState(SignEntity entity, boolean sign) {
        if(sign) showToast(R.string.label_sign_suc);
    }

    @Override
    public void onCountChanged(int i) {
        PreferenceUtils.setRCDotNum(this,i);
        int dotNum = PreferenceUtils.getGroupDotNum(this) + i + PreferenceUtils.getJuQIngDotNum(this);
        if(dotNum > 0){
            mTvMsgDot.setVisibility(View.VISIBLE);
            if(dotNum > 999) dotNum = 999;
            mTvMsgDot.setText(String.valueOf(dotNum));
        }else {
            mTvMsgDot.setVisibility(View.GONE);
        }
    }

    public void onFragmentResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQ_GROUP_DETAIL && resultCode == RESULT_OK){
            if(!fragmentStack.empty()){
                Fragment fragmentP = fragmentStack.pop();
                fragmentStack.push(fragmentP);
                if(fragmentP instanceof KiraConversationFragment){
                    onBackPressed();
                }else{
                    fragmentP.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }
}
