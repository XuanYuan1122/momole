package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerPersonalComponent;
import com.moemoe.lalala.di.modules.PersonalModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.TabEntity;
import com.moemoe.lalala.model.entity.UserInfo;
import com.moemoe.lalala.presenter.PersonalContract;
import com.moemoe.lalala.presenter.PersonalPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.LevelSpan;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;
import com.moemoe.lalala.view.fragment.BaseFragment;
import com.moemoe.lalala.view.fragment.NewFollowMainFragment;
import com.moemoe.lalala.view.fragment.OldDocFragment;
import com.moemoe.lalala.view.fragment.PersonalMainFragment;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.moemoe.lalala.utils.StartActivityConstant.REQ_MOTIFY_TAICI;

/**
 *
 * Created by yi on 2016/12/15.
 */

public class PersonalV2Activity extends BaseAppCompatActivity implements PersonalContract.View ,AppBarLayout.OnOffsetChangedListener{

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
    @BindView(R.id.iv_menu_list)
    ImageView mMenuList;
//    @BindView(R.id.iv_red_msg)
//    ImageView mIvRedMsg;
    @BindView(R.id.tv_kira_num)
    TextView mTvKiraNum;
    @BindView(R.id.iv_msg)
    ImageView mIvMsg;
    @BindView(R.id.tv_huiyuan)
    TextView mTvHuiYuan;
    @BindView(R.id.fl_avatar)
    FrameLayout mAvatarRoot;
    @BindView(R.id.iv_vip)
    ImageView mIvVip;
    @BindView(R.id.tv_level)
    TextView mTvLevel;

    @Inject
    PersonalPresenter mPresenter;
    private TabFragmentPagerAdapter mAdapter;
    private boolean mIsSelf;
    private String mUserId;
    private UserInfo mInfo;
    private BottomMenuFragment bottomMenuFragment;
    private PersonalMainFragment mainFragment;
    private NewFollowMainFragment followFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_person;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
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
        mPresenter.requestUserInfo(mUserId);
        mMenuList.setVisibility(View.VISIBLE);

        List<BaseFragment> fragmentList = new ArrayList<>();
        mainFragment = PersonalMainFragment.newInstance(mUserId);
        fragmentList.add(mainFragment);
        followFragment = NewFollowMainFragment.newInstance("my",mUserId);
        fragmentList.add(followFragment);
        fragmentList.add(OldDocFragment.newInstance(mUserId));
        String[] mTitles = {getString(R.string.label_home_page), "动态","文章"};
        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.label_home_page));
        titles.add("动态");
        titles.add("文章");
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        for (String str: mTitles) {
            mTabEntities.add(new TabEntity(str, R.drawable.ic_personal_bag,R.drawable.ic_personal_bag));
        }
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragmentList,titles);
        mViewPager.setAdapter(mAdapter);
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
        if(mIsSelf){
           // subscribeEvent();
            mIvMsg.setVisibility(View.VISIBLE);
        }else {
            mIvMsg.setVisibility(View.GONE);
        }
    }

    public void likeDynamic(String id,boolean isLike,int position){
        if(mViewPager.getCurrentItem() == 1){
            followFragment.likeDynamic(id, isLike, position);
        }
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        if(mAdapter != null)mAdapter.release();
       // RxBus.getInstance().unSubscribe(this);
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
        if(mIsSelf){
            MenuItem item = new MenuItem(1, getString(R.string.label_setting));
            items.add(item);
            item = new MenuItem(2, getString(R.string.label_coin_details));
            items.add(item);
            item = new MenuItem(3, getString(R.string.label_doc_history));
            items.add(item);
            item = new MenuItem(5, "收藏动态");
            items.add(item);
            item = new MenuItem(6, "我的邀请");
            items.add(item);
            item = new MenuItem(7, "填写邀请");
            items.add(item);
        }else {
            if(mInfo.isBlack()){
                MenuItem item = new MenuItem(4, getString(R.string.label_user_cancel_reject));
                items.add(item);
            }else {
                MenuItem item = new MenuItem(4, getString(R.string.label_user_reject));
                items.add(item);
            }
        }
        bottomMenuFragment.setMenuItems(items);
        bottomMenuFragment.setShowTop(false);
        bottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        bottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if (itemId == 1) {
                    Intent i2 = new Intent(PersonalV2Activity.this,SettingActivity.class);
                    startActivityForResult(i2, SettingActivity.REQUEST_SETTING_LOGOUT);
                }
                if(itemId == 2){
                    Intent i = new Intent(PersonalV2Activity.this, CoinDetailActivity.class);
                    startActivity(i);
                }
                if(itemId == 3){
                    Intent i = new Intent(PersonalV2Activity.this,DocHistoryActivity.class);
                    i.putExtra(UUID,mUserId);
                    startActivity(i);
                }
                if(itemId == 4){
                    if(mInfo != null){
                        mPresenter.saveOrCancelBlackUser(mUserId,mInfo.isBlack());
                    }
                }
                if(itemId == 5){
                    Intent i = new Intent(PersonalV2Activity.this,PersonalFavoriteDynamicActivity.class);
                    startActivity(i);
                }
                if(itemId == 6){
                    Intent i = new Intent(PersonalV2Activity.this,InviteActivity.class);
                    i.putExtra("id",mInfo.getUserNo());
                    i.putExtra("name",mInfo.getInviteUserName());
                    startActivity(i);
                }
                if(itemId == 7){
                    Intent i = new Intent(PersonalV2Activity.this,InviteAddActivity.class);
                    i.putExtra("type",1);
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
        }else if(requestCode == PersonalMainFragment.REQ_BADGE || requestCode == REQ_MOTIFY_TAICI){
            mAdapter.getItem(0).onActivityResult(requestCode,resultCode,data);
        }

    }

    @OnClick({R.id.tv_follow,R.id.iv_edit,R.id.iv_menu_list,R.id.iv_avatar,R.id.tv_private_msg})
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
            case R.id.tv_private_msg:
                if(DialogUtils.checkLoginAndShowDlg(PersonalV2Activity.this)){
                    if(!TextUtils.isEmpty(PreferenceUtils.getAuthorInfo().getRcToken()) && !TextUtils.isEmpty(mInfo.getRcTargetId())){
                        RongIM.getInstance().startPrivateChat(PersonalV2Activity.this, mInfo.getRcTargetId(), mInfo.getUserName());
                    }else {
                        showToast("打开私信失败");
                    }
                }
                break;
//            case R.id.iv_msg:
//                if(DialogUtils.checkLoginAndShowDlg(this)){
//                    PersonalMsgActivity.startActivityForResult(this,mUserId);
//                }
//                break;
        }
    }

    @Override
    public void onSaveOrCancelBlackSuccess(boolean isSave) {
        mInfo.setBlack(isSave);
        bottomMenuFragment.changeItemTextById(4,isSave?getString(R.string.label_user_cancel_reject) : getString(R.string.label_user_reject),0);
        showToast("拉黑用户成功");
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
        showToast("获取个人信息失败,请稍后再试!");
        finish();
    }

    private void updateView(UserInfo info){
        if(!(info.getHeadPath().startsWith("http")  || info.getHeadPath().startsWith("https"))){
            info.setHeadPath(ApiService.URL_QINIU + info.getHeadPath());
        }
        io.rong.imlib.model.UserInfo rcInfo = new io.rong.imlib.model.UserInfo(info.getUserId(),info.getUserName(), Uri.parse(info.getHeadPath()));
        RongIM.getInstance().refreshUserInfoCache(rcInfo);
        mInfo = info;
        if(bottomMenuFragment == null){
            initPopupMenus();
        }
        mainFragment.setOpenBag(info.isOpenBag());
        mTvTitle.setText(info.getUserName());
        int hSize = (int) getResources().getDimension(R.dimen.y460);
        Glide.with(this)
                .load(StringUtils.getUrl(this,ApiService.URL_QINIU + info.getBackground(), DensityUtil.getScreenWidth(this),hSize,false,true))
                .override(DensityUtil.getScreenWidth(this),hSize)
                .error(R.drawable.btn_cardbg_defbg)
                .placeholder(R.drawable.btn_cardbg_defbg)
                .into(mIvBackGround);
        int size = (int) getResources().getDimension(R.dimen.x160);
        Glide.with(this)
                .load(StringUtils.getUrl(this,info.getHeadPath(), size,size,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(mIvAvatar);
        mTvName.setText(info.getUserName());
        mTvKiraNum.setText("kira号: " + info.getUserNo());
        if("F".equals(info.getSex())){
            mIvGender.setImageResource(R.drawable.ic_user_boy);
            mIvGender.setVisibility(View.VISIBLE);
        }else if("M".equals(info.getSex())){
            mIvGender.setImageResource(R.drawable.ic_user_girl);
            mIvGender.setVisibility(View.VISIBLE);
        }else{
            mIvGender.setVisibility(View.GONE);
        }
        mFanNum.setText(String.valueOf(info.getFollowers()));
        mDocNum.setText(String.valueOf(info.getFollowCount()));
        mCoinNum.setText(String.valueOf(info.getCoin()));
        if(!TextUtils.isEmpty(info.getVipTime())){
            mIvVip.setVisibility(View.VISIBLE);
        }else{
            mIvVip.setVisibility(View.GONE);
        }
        if(mIsSelf){
            mFollowRoot.setVisibility(View.GONE);
            mEdit.setVisibility(View.VISIBLE);
            mTvHuiYuan.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(info.getVipTime())) {
                mTvHuiYuan.setText("会员到期日: "+info.getVipTime());
                mAvatarRoot.setPadding(0,(int) getResources().getDimension(R.dimen.y40),0,0);
            }else {
                mTvHuiYuan.setVisibility(View.GONE);
                mAvatarRoot.setPadding(0, (int) getResources().getDimension(R.dimen.y90),0,0);
            }
        }else {
            if(!TextUtils.isEmpty(info.getVipTime())){
                mAvatarRoot.setPadding(0,(int) getResources().getDimension(R.dimen.y20),0,0);
            }else {
                mAvatarRoot.setPadding(0,(int) getResources().getDimension(R.dimen.y40),0,0);
            }

            mEdit.setVisibility(View.GONE);
            mFollowRoot.setVisibility(View.VISIBLE);
            mFollow.setSelected(info.isFollowing());
            mTvHuiYuan.setVisibility(View.GONE);
            mFollow.setText(info.isFollowing() ? getString(R.string.label_followed) : getString(R.string.label_follow));
        }

        LevelSpan levelSpan = new LevelSpan(ContextCompat.getColor(this,R.color.white),getResources().getDimension(R.dimen.x12));
        final String content = "LV" + info.getLevel();
        String colorStr = "LV";
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(levelSpan, content.indexOf(colorStr), content.indexOf(colorStr) + colorStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvLevel.setText(style);
        float radius2 = getResources().getDimension(R.dimen.y4);
        float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
        RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
        ShapeDrawable shapeDrawable2 = new ShapeDrawable();
        shapeDrawable2.setShape(roundRectShape2);
        shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(info.getLevelColor(), ContextCompat.getColor(this, R.color.main_cyan)));
        mTvLevel.setBackgroundDrawable(shapeDrawable2);

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
        int temp = (int) (getResources().getDimension(R.dimen.y292) - getResources().getDimension(R.dimen.status_bar_height));
        float percent = (float)Math.abs(verticalOffset) / temp;
        if(percent > 0.4){
            if(!isChanged){
                mToolbar.setNavigationIcon(R.drawable.btn_back_blue_normal_unity);
                mMenuList.setImageResource(R.drawable.btn_menu_normal);
                mIvMsg.setImageResource(R.drawable.btn_person_msg_blue);
                isChanged = true;
            }
            mToolbar.setAlpha(percent);
            mMenuList.setAlpha(percent);
            mIvMsg.setAlpha(percent);
        }else {
            if(isChanged){
                mToolbar.setNavigationIcon(R.drawable.btn_back_white_normal);
                mMenuList.setImageResource(R.drawable.btn_menu_white_normal);
                mIvMsg.setImageResource(R.drawable.btn_person_msg);
                isChanged = false;
            }
            mToolbar.setAlpha(1 - percent);
            mMenuList.setAlpha(1 - percent);
            mIvMsg.setAlpha(1 - percent);
        }
        mTvTitle.setAlpha(percent);
    }
}
