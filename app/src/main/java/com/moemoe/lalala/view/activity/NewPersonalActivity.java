package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerPersonalComponent;
import com.moemoe.lalala.di.modules.PersonalModule;
import com.moemoe.lalala.event.PrivateMessageEvent;
import com.moemoe.lalala.event.SystemMessageEvent;
import com.moemoe.lalala.greendao.gen.ChatUserEntityDao;
import com.moemoe.lalala.greendao.gen.GroupUserEntityDao;
import com.moemoe.lalala.greendao.gen.PrivateMessageItemEntityDao;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.ChatUserEntity;
import com.moemoe.lalala.model.entity.CreatePrivateMsgEntity;
import com.moemoe.lalala.model.entity.GroupUserEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.PrivateMessageItemEntity;
import com.moemoe.lalala.model.entity.TabEntity;
import com.moemoe.lalala.model.entity.UserInfo;
import com.moemoe.lalala.presenter.PersonalContract;
import com.moemoe.lalala.presenter.PersonalPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.GlideCircleTransform;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.adapter.PersonalPagerAdapter;
import com.moemoe.lalala.view.fragment.PersonalMainFragment;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 *
 * Created by yi on 2016/12/15.
 */

public class NewPersonalActivity extends BaseAppCompatActivity implements PersonalContract.View ,AppBarLayout.OnOffsetChangedListener{

    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_background)
    ImageView mIvBackGround;
    @BindView(R.id.iv_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.iv_gender)
    ImageView mIvGender;
    @BindView(R.id.tv_fans_num)
    TextView mFanNum;
    @BindView(R.id.tv_doc_num)
    TextView mDocNum;
    @BindView(R.id.tv_coin_num)
    TextView mCoinNum;
    @BindView(R.id.tv_follow)
    TextView mFollow;
    @BindView(R.id.ll_follow_root)
    View mFollowRoot;
    @BindView(R.id.tab_layout)
    CommonTabLayout mTabLayout;
    @BindView(R.id.vp_main)
    ViewPager mViewPager;
    @BindView(R.id.iv_edit)
    ImageView mEdit;
    @BindView(R.id.iv_bag)
    ImageView mIvBag;
    @BindView(R.id.iv_menu_list)
    ImageView mMenuList;

    @Inject
    PersonalPresenter mPresenter;
    private PersonalPagerAdapter mAdapter;
    private boolean mIsSelf;
    private String mUserId;
    private UserInfo mInfo;
    private BottomMenuFragment bottomMenuFragment;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.ac_person;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPersonalComponent.builder()
                .personalModule(new PersonalModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mUserId = getIntent().getStringExtra(UUID);
        if(TextUtils.isEmpty(mUserId)){
            finish();
            return;
        }
        mIsSelf = mUserId.equals(PreferenceUtils.getUUid());
        mIvBag.setVisibility(View.GONE);
        mPresenter.requestUserInfo(mUserId);
        initPopupMenus();
        if(mIsSelf) {
            mMenuList.setVisibility(View.VISIBLE);
            subscribeEvent();
        }else {
            mMenuList.setVisibility(View.GONE);
        }
    }

    private void subscribeEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(PrivateMessageEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Action1<PrivateMessageEvent>() {
                    @Override
                    public void call(PrivateMessageEvent event) {
                        if(mTabLayout.getTabCount() == 7){
                            if(event.isShow()){
                                mTabLayout.showDot(5);
                            }else {
                                mTabLayout.hideMsg(5);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        Subscription sysSubscription = RxBus.getInstance()
                .toObservable(SystemMessageEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Action1<SystemMessageEvent>() {
                    @Override
                    public void call(SystemMessageEvent event) {
                        if(mTabLayout.getTabCount() == 7){
                            if(PreferenceUtils.getMessageDot(NewPersonalActivity.this,"neta") || PreferenceUtils.getMessageDot(NewPersonalActivity.this,"system") || PreferenceUtils.getMessageDot(NewPersonalActivity.this,"at_user")){
                                mTabLayout.showDot(6);
                            }else {
                                mTabLayout.hideMsg(6);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
        RxBus.getInstance().addSubscription(this, sysSubscription);
    }

    @Override
    protected void onDestroy() {
        mPresenter.release();
        RxBus.getInstance().unSubscribe(this);
        super.onDestroy();
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

    private void initPopupMenus() {
        bottomMenuFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(1, getString(R.string.label_setting));
        items.add(item);
        item = new MenuItem(2, getString(R.string.label_coin_details));
        items.add(item);
        item = new MenuItem(3, getString(R.string.label_doc_history));
        items.add(item);
        bottomMenuFragment.setMenuItems(items);
        bottomMenuFragment.setShowTop(false);
        bottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        bottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if (itemId == 1) {
                    Intent i2 = new Intent(NewPersonalActivity.this,SettingActivity.class);
                    startActivityForResult(i2, SettingActivity.REQUEST_SETTING_LOGOUT);
                }
                if(itemId == 2){
                    Intent i = new Intent(NewPersonalActivity.this, CoinDetailActivity.class);
                    startActivity(i);
                }
                if(itemId == 3){
                    Intent i = new Intent(NewPersonalActivity.this,DocHistoryActivity.class);
                    i.putExtra(UUID,mUserId);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == SettingActivity.REQUEST_SETTING_LOGOUT){
            finish();
        }else if (requestCode == NewEditAccountActivity.REQ_EDIT && resultCode == RESULT_OK){
            mInfo = data.getParcelableExtra("info");
            updateView(mInfo);
        }else if(requestCode == PersonalMainFragment.REQ_BADGE){
            mAdapter.getItem(0).onActivityResult(requestCode,resultCode,data);
        }
    }

    @OnClick({R.id.tv_follow,R.id.iv_edit,R.id.iv_menu_list,R.id.iv_avatar,R.id.iv_bag,R.id.tv_private_msg})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_follow:
                mPresenter.followUser(mUserId,mFollow.isSelected());
                break;
            case R.id.iv_edit:
                if(mInfo != null){
                    Intent i = new Intent(this,NewEditAccountActivity.class);
                    i.putExtra("info",mInfo);
                    startActivityForResult(i,NewEditAccountActivity.REQ_EDIT);
                }
                break;
            case R.id.iv_menu_list:
                if(bottomMenuFragment != null) bottomMenuFragment.show(getSupportFragmentManager(),"PersonMenu");
                break;
            case R.id.iv_avatar:
                if (mInfo != null){
                    ArrayList<Image> temp = new ArrayList<>();
                    Image image = new Image();
                    String str = mInfo.getHeadPath().replace(ApiService.URL_QINIU,"");
                    image.setPath(str);
                    temp.add(image);
                    Intent intent = new Intent(this, ImageBigSelectActivity.class);
                    intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, temp);
                    intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                            0);
                    startActivity(intent);
                }
                break;
            case R.id.iv_bag:
                Intent i2 = new Intent(NewPersonalActivity.this,BagActivity.class);
                i2.putExtra(UUID,mUserId);
                startActivity(i2);
                break;
            case R.id.tv_private_msg:
                goToChat();
                break;
        }
    }

    private void goToChat() {
        if(mInfo != null){
            GroupUserEntityDao dao = GreenDaoManager.getInstance().getSession().getGroupUserEntityDao();
            List<GroupUserEntity> list = dao.queryBuilder()
                    .where(GroupUserEntityDao.Properties.UserId.eq(mUserId))
                    .limit(1)
                    .list();
            if(list.size() == 1){
                //存在  直接跳转
                Intent i = new Intent(this, ChatActivity.class);
                i.putExtra("talkId",list.get(0).getTalkId());
                i.putExtra("title",mInfo.getUserName());
                startActivity(i);
            }else {
                mPresenter.createPrivateMsg(mUserId);
            }
        }
    }

    @Override
    public void onCreatePrivateMsgSuccess(CreatePrivateMsgEntity entity) {
        //私信列表
        PrivateMessageItemEntityDao messageItemEntityDao = GreenDaoManager.getInstance().getSession().getPrivateMessageItemEntityDao();

        PrivateMessageItemEntity privateMessageItemEntity = new PrivateMessageItemEntity();
        privateMessageItemEntity.setDot(0);
        privateMessageItemEntity.setUpdateTime(new Date(System.currentTimeMillis()));
        privateMessageItemEntity.setName(mInfo.getUserName());
        privateMessageItemEntity.setIcon(mInfo.getHeadPath());
        privateMessageItemEntity.setContent("");
        privateMessageItemEntity.setTalkId(entity.getTalkId());
        privateMessageItemEntity.setNew(true);
        privateMessageItemEntity.setState(entity.isIgnore());
        messageItemEntityDao.insertOrReplace(privateMessageItemEntity);
        RxBus.getInstance().post(new PrivateMessageEvent(false,entity.getTalkId(),false));
        //列表与用户中间表
        GroupUserEntityDao dao = GreenDaoManager.getInstance().getSession().getGroupUserEntityDao();
        //他人
        GroupUserEntity groupUserEntity = new GroupUserEntity();
        groupUserEntity.setId(null);
        groupUserEntity.setTalkId(entity.getTalkId());
        groupUserEntity.setUserId(mUserId);
        dao.insertOrReplace(groupUserEntity);
        //自己
        GroupUserEntity groupUserEntity1 = new GroupUserEntity();
        groupUserEntity1.setId(null);
        groupUserEntity1.setTalkId(entity.getTalkId());
        groupUserEntity1.setUserId(PreferenceUtils.getUUid());
        dao.insertOrReplace(groupUserEntity1);
        //用户表
        ChatUserEntityDao chatUserEntityDao = GreenDaoManager.getInstance().getSession().getChatUserEntityDao();
        //他人
        ChatUserEntity userEntity = new ChatUserEntity();
        userEntity.setUserIcon(mInfo.getHeadPath());
        userEntity.setUserName(mInfo.getUserName());
        userEntity.setUserId(mUserId);
        chatUserEntityDao.insertOrReplace(userEntity);
        //自己
        ChatUserEntity userEntity1 = new ChatUserEntity();
        userEntity1.setUserIcon(PreferenceUtils.getAuthorInfo().getHeadPath());
        userEntity1.setUserName(PreferenceUtils.getAuthorInfo().getUserName());
        userEntity1.setUserId(PreferenceUtils.getUUid());
        chatUserEntityDao.insertOrReplace(userEntity1);
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("talkId",entity.getTalkId());
        i.putExtra("title",mInfo.getUserName());
        i.putExtra("isNew",false);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAppBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onFailure(int code,String msg) {
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void onLoadUserInfoFail() {
       // Intent i = new Intent(this, LoginActivity.class);
       // startActivity(i);
        showToast("获取个人信息失败,请稍后再试!");
        finish();
    }

    private void updateView(UserInfo info){
        if(!info.getHeadPath().contains("http")){
            info.setHeadPath(ApiService.URL_QINIU + info.getHeadPath());
        }
        if(!mIsSelf && info.isOpenBag()){
            mIvBag.setVisibility(View.VISIBLE);
        }else {
            mIvBag.setVisibility(View.GONE);
        }
        mInfo = info;
        mTvTitle.setText(info.getUserName());
        Glide.with(this)
                .load(StringUtils.getUrl(this,ApiService.URL_QINIU + info.getBackground(), DensityUtil.getScreenWidth(this),DensityUtil.dip2px(this,230),false,true))
                .override(DensityUtil.getScreenWidth(this),DensityUtil.dip2px(this,230))
                .error(R.drawable.btn_cardbg_defbg)
                .placeholder(R.drawable.btn_cardbg_defbg)
                .into(mIvBackGround);
        Glide.with(this)
                .load(StringUtils.getUrl(this,info.getHeadPath(), DensityUtil.dip2px(this,90),DensityUtil.dip2px(this,90),false,true))
                .override(DensityUtil.dip2px(this,90),DensityUtil.dip2px(this,90))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new GlideCircleTransform(this,DensityUtil.dip2px(this,3)))
                .into(mIvAvatar);
        mTvName.setText(info.getUserName());
        if("F".equals(info.getSex())){
            mIvGender.setImageResource(R.drawable.ic_boy);
            mIvGender.setVisibility(View.VISIBLE);
        }else if("M".equals(info.getSex())){
            mIvGender.setImageResource(R.drawable.ic_girl);
            mIvGender.setVisibility(View.VISIBLE);
        }else{
            mIvGender.setVisibility(View.GONE);
        }
        mFanNum.setText(String.valueOf(info.getFollowers()));
        mDocNum.setText(String.valueOf(info.getDocCount()));
        mCoinNum.setText(String.valueOf(info.getCoin()));
        if(mIsSelf){
            mFollowRoot.setVisibility(View.GONE);
            mEdit.setVisibility(View.VISIBLE);
        }else {
            mEdit.setVisibility(View.GONE);
            mFollowRoot.setVisibility(View.VISIBLE);
            mFollow.setSelected(info.isFollowing());
            mFollow.setText(info.isFollowing() ? getString(R.string.label_followed) : getString(R.string.label_follow));
        }
        if(mAdapter == null){
            mAdapter = new PersonalPagerAdapter(getSupportFragmentManager(),this,mIsSelf,mUserId,info.isShowFavorite(),info.isShowFollow(),info.isShowFans());
            mViewPager.setAdapter(mAdapter);
            String[] mTitles = {getString(R.string.label_home_page), getString(R.string.label_doc), getString(R.string.label_favorite), getString(R.string.label_fans),getString(R.string.label_follow),getString(R.string.label_chat),getString(R.string.label_msg)};
            int len;
            if(mIsSelf){
                len = mTitles.length;
            }else {
                len = mTitles.length - 2;
            }
            for (int i = 0; i < len; i++) {
                mTabEntities.add(new TabEntity(mTitles[i], R.drawable.ic_personal_bag,R.drawable.ic_personal_bag));
            }
            mTabLayout.setTabData(mTabEntities);
            mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelect(int position) {
                    mViewPager.setCurrentItem(position);
                }

                @Override
                public void onTabReselect(int position) {
                }
            });

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    mTabLayout.setCurrentTab(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            String type = getIntent().getStringExtra("tab");
            if(!TextUtils.isEmpty(type)){
                if(type.equals("notify")){
                    mViewPager.setCurrentItem(6);
                    mTabLayout.setCurrentTab(6);
                }else if(type.equals("fans")){
                    mViewPager.setCurrentItem(3);
                    mTabLayout.setCurrentTab(3);
                }
            }
        }
        PrivateMessageItemEntityDao dao = GreenDaoManager.getInstance().getSession().getPrivateMessageItemEntityDao();
        List<PrivateMessageItemEntity> list = dao.queryBuilder().list();
        boolean showDot = false;
        for(PrivateMessageItemEntity entity : list){
            if(entity.getDot() > 0){
                showDot = true;
                break;
            }
        }
        if(showDot){
            mTabLayout.showDot(5);
        }else {
            mTabLayout.hideMsg(5);
        }

        if(PreferenceUtils.getMessageDot(this,"neta") || PreferenceUtils.getMessageDot(this,"system") || PreferenceUtils.getMessageDot(this,"at_user")){
            mTabLayout.showDot(6);
        }else {
            mTabLayout.hideMsg(6);
        }
    }


    @Override
    public void onLoadUserInfo(UserInfo info) {
        updateView(info);
    }

    @Override
    public void onFollowSuccess(boolean isFollow) {
        mFollow.setSelected(isFollow);
        mFollow.setText(isFollow ? getString(R.string.label_followed) : getString(R.string.label_follow));
    }

    private boolean isChanged = false;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int temp = (int) (DensityUtil.dip2px(this,146) - getResources().getDimension(R.dimen.status_bar_height));
        float percent = (float)Math.abs(verticalOffset) / temp;

        if(percent > 0.4){
            if(!isChanged){
                mToolbar.setNavigationIcon(R.drawable.btn_back_blue_normal);
                mMenuList.setImageResource(R.drawable.btn_menu_normal);
                isChanged = true;
            }
            mToolbar.setAlpha(percent);
            mMenuList.setAlpha(percent);
        }else {
            if(isChanged){
                mToolbar.setNavigationIcon(R.drawable.btn_back_white_normal);
                mMenuList.setImageResource(R.drawable.btn_menu_white_normal);
                isChanged = false;
            }
            mToolbar.setAlpha(1 - percent);
            mMenuList.setAlpha(1 - percent);
        }
        mTvTitle.setAlpha(percent);
    }
}
