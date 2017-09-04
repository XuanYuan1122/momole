package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerNewBagComponent;
import com.moemoe.lalala.di.modules.NewBagModule;
import com.moemoe.lalala.model.entity.NewBagEntity;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.presenter.NewBagContract;
import com.moemoe.lalala.presenter.NewBagPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;
import com.moemoe.lalala.view.fragment.BagMyFragment;
import com.moemoe.lalala.view.fragment.BaseFragment;
import com.moemoe.lalala.view.fragment.DynamicFragment;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.moemoe.lalala.utils.StartActivityConstant.REQUEST_CODE_CREATE_DOC;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_CREATE_FOLDER;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_MODIFY_BAG;

/**
 * 新书包主界面
 * Created by yi on 2017/8/15.
 */

public class NewBagActivity extends BaseAppCompatActivity implements NewBagContract.View, AppBarLayout.OnOffsetChangedListener{

    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_menu_list)
    ImageView mIvMenu;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mTitleView;
    @BindView(R.id.tv_use_space)
    TextView mTvSpaceNum;
    @BindView(R.id.iv_bg)
    ImageView mIvBg;
    @BindView(R.id.iv_bag)
    ImageView mIvBag;
    @BindView(R.id.tab_layout)
    SegmentTabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @Inject
    NewBagPresenter mPresenter;

    private BottomMenuFragment bottomFragment;
    private String mUserId;
    private String mBg;
    private String mBagName;
    private BagMyFragment mMyFragment;
    private TabFragmentPagerAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_new_bag;
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        if(mAdapter != null) mAdapter.release();
        super.onDestroy();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerNewBagComponent.builder()
                .newBagModule(new NewBagModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(),null);
        mUserId = getIntent().getStringExtra(UUID);
        if(TextUtils.isEmpty(mUserId)){
            finish();
            return;
        }
        mTitleView.setCollapsedTitleTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTitleView.setExpandedTitleColor(ContextCompat.getColor(this,R.color.black_1e1e1e));
        mTvSpaceNum.setText("");
        String[] mTitles = {"我的", "动态", "收藏"};
        mTabLayout.setTabData(mTitles);

        List<String> titles = new ArrayList<>();
        titles.add(mTitles[0]);
        titles.add(mTitles[1]);
        titles.add(mTitles[2]);
        ArrayList<BaseFragment> fragmentList = new ArrayList<>();
        mMyFragment = BagMyFragment.newInstance("my",mUserId);
        DynamicFragment fragment1 = DynamicFragment.newInstance();
        BagMyFragment fragment2 = BagMyFragment.newInstance("collection",mUserId);
        if(!mUserId.equals(PreferenceUtils.getUUid())){
            mTabLayout.setVisibility(View.GONE);
            fragmentList.add(mMyFragment);
        }else {
            fragmentList.add(mMyFragment);
            fragmentList.add(fragment1);
            fragmentList.add(fragment2);
        }
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragmentList,titles);
        mViewPager.setAdapter(mAdapter);
        mPresenter.loadBagData(mUserId);
        initPopupMenus(mUserId.equals(PreferenceUtils.getUUid()));
    }

    private void initPopupMenus(boolean isSelf) {
        bottomFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        if(isSelf){
            MenuItem item = new MenuItem(0, getString(R.string.label_setting));
            items.add(item);

            item = new MenuItem(1, getString(R.string.label_bag_buy_list));
            items.add(item);

            item = new MenuItem(3, getString(R.string.label_add_space));
            items.add(item);
        }else {
            // 举报
            MenuItem item = new MenuItem(4, getString(R.string.label_jubao));
            items.add(item);
        }

        bottomFragment.setShowTop(false);
        bottomFragment.setMenuItems(items);
        bottomFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        bottomFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if(itemId == 0){
                    Intent i = new Intent(NewBagActivity.this,BagEditActivity.class);
                    i.putExtra("bg",mBg);
                    i.putExtra("name",mBagName);
                    i.putExtra("read_type","");
                    i.putExtra(BagEditActivity.EXTRA_TYPE,BagEditActivity.TYPE_BAG_MODIFY);
                    startActivityForResult(i,REQ_MODIFY_BAG);
                }else if(itemId == 1){
                    Intent i = new Intent(NewBagActivity.this,BagBuyActivity.class);
                    startActivity(i);
                }else if (itemId == 3) {
                    String temp = "neta://com.moemoe.lalala/url_inner_1.0?http://www.moemoe.la/shubao/?token=" + PreferenceUtils.getToken();
                    Uri uri = Uri.parse(temp);
                    IntentUtils.toActivityFromUri(NewBagActivity.this, uri, null);
                }else if (itemId == 4) {
                    Intent intent = new Intent(NewBagActivity.this, JuBaoActivity.class);
                    intent.putExtra(JuBaoActivity.EXTRA_NAME, "");
                    intent.putExtra(JuBaoActivity.EXTRA_CONTENT, "");
                    intent.putExtra(JuBaoActivity.EXTRA_TYPE,3);
                    intent.putExtra(JuBaoActivity.UUID,mUserId);
                    intent.putExtra("userId",mUserId);
                    intent.putExtra(JuBaoActivity.EXTRA_TARGET, REPORT.BAG.toString());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mIvMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(bottomFragment != null) bottomFragment.show(getSupportFragmentManager(),"Bag");
            }
        });
    }

    @Override
    protected void onResume() {
        Glide.with(this).resumeRequests();
        super.onResume();
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        Glide.with(this).pauseRequests();
        super.onPause();
        mAppBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void initListeners() {
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
    }

    @Override
    protected void initData() {

    }

    private boolean isChanged = false;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        int temp = (int) (DensityUtil.dip2px(this,146) - getResources().getDimension(R.dimen.status_bar_height));
        float percent = (float)Math.abs(verticalOffset) / temp;

        if(percent > 0.4){
            if(!isChanged){
                mToolbar.setNavigationIcon(R.drawable.btn_back_blue_normal);
                mIvMenu.setImageResource(R.drawable.btn_menu_normal);
                isChanged = true;
            }
            mToolbar.setAlpha(percent);
            mIvMenu.setAlpha(percent);
        }else {
            if(isChanged){
                mToolbar.setNavigationIcon(R.drawable.btn_back_white_normal);
                mIvMenu.setImageResource(R.drawable.btn_menu_white_normal);
                isChanged = false;
            }
            mToolbar.setAlpha(1 - percent);
            mIvMenu.setAlpha(1 - percent);
        }
    }

    @Override
    public void onFailure(int code, String msg) {

    }

    private int getSize(long size){
        if(size == 0) return 0;
        return (int) (size/1024/1024);
    }

    @Override
    public void onLoadBagSuccess(final NewBagEntity entity) {
        if(entity.getUserId().equals(PreferenceUtils.getUUid())){
            mTvSpaceNum.setText(getString(R.string.label_bag_space,getSize(entity.getUseSize()),getSize(entity.getMaxSize())));
        }else {
            mTvSpaceNum.setText(entity.getUserName());
            mTvSpaceNum.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
            mTvSpaceNum.setCompoundDrawablesWithIntrinsicBounds (null,null,ContextCompat.getDrawable(this,R.drawable.ic_bag_more),null);
            mTvSpaceNum.setCompoundDrawablePadding(DensityUtil.dip2px(this,4));
            mTvSpaceNum.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    ViewUtils.toPersonal(NewBagActivity.this,entity.getUserId());
                }
            });
        }
        mTitleView.setTitle(entity.getBagName());
        mBg = entity.getBg();
        mBagName = entity.getBagName();
        Glide.with(this)
                .load(StringUtils.getUrl(this,entity.getBg(),DensityUtil.getScreenWidth(this),DensityUtil.dip2px(this,120),false,true))
                .override(DensityUtil.getScreenWidth(this), DensityUtil.dip2px(this,120))
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .bitmapTransform(new BlurTransformation(this,10,4),new ColorFilterTransformation(this,R.color.alph_20))
                .into(mIvBg);
        Glide.with(this)
                .load(StringUtils.getUrl(this,entity.getBg(),DensityUtil.dip2px(this,80),DensityUtil.dip2px(this,80),false,true))
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .bitmapTransform(new CropSquareTransformation(this),new RoundedCornersTransformation(this,DensityUtil.dip2px(this,8),0))
                .into(mIvBag);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(requestCode == REQ_MODIFY_BAG && resultCode == RESULT_OK){
            mTitleView.setTitle(data.getStringExtra("name"));
           String temp = data.getStringExtra("bg");
           if(!mBg.equals(temp)){
               mBg = data.getStringExtra("bg");
               Glide.with(this)
                       .load(mBg)
                       .override(DensityUtil.getScreenWidth(this), DensityUtil.dip2px(this,120))
                       .error(R.drawable.btn_cardbg_defbg)
                       .placeholder(R.drawable.btn_cardbg_defbg)
                       .into(mIvBg);
           }
        }else if((requestCode == REQUEST_CODE_CREATE_DOC || requestCode == REQ_CREATE_FOLDER) && resultCode == RESULT_OK){
           if(mMyFragment != null) mMyFragment.onActivityResult(requestCode,resultCode,data);
       }
    }
}
