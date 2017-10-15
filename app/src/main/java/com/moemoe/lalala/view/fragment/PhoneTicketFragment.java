package com.moemoe.lalala.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerPhoneTicketComponent;
import com.moemoe.lalala.di.modules.PhoneTicketModule;
import com.moemoe.lalala.event.MateBackPressEvent;
import com.moemoe.lalala.model.entity.TabEntity;
import com.moemoe.lalala.presenter.PhoneTicketContract;
import com.moemoe.lalala.presenter.PhoneTicketPresenter;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.view.activity.PhoneMainActivity;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/9/4.
 */

public class PhoneTicketFragment extends BaseFragment implements PhoneTicketContract.View{

    public static final String TAG = "PhoneTicketFragment";

//    @BindView(R.id.iv_back)
//    ImageView mIvBack;
//    @BindView(R.id.tv_toolbar_title)
//    TextView mTvTitle;
//    @BindView(R.id.tv_menu)
//    TextView mTvMenu;
    @BindView(R.id.pager_person_data)
    ViewPager mDataPager;
    @BindView(R.id.indicator_person_data)
    CommonTabLayout mPageIndicator;
    @BindView(R.id.iv_cover)
    ImageView mIvCover;
    @Inject
    PhoneTicketPresenter mPresenter;

    private TabFragmentPagerAdapter mAdapter;
    private String mate;

    public static PhoneTicketFragment newInstance(String mate){
        PhoneTicketFragment fragment = new PhoneTicketFragment();
        Bundle bundle = new Bundle();
        bundle.putString("mate",mate);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_phone_guli;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPhoneTicketComponent.builder()
                .phoneTicketModule(new PhoneTicketModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mate = getArguments().getString("mate");
//        mIvBack.setVisibility(View.VISIBLE);
//        mIvBack.setOnClickListener(new NoDoubleClickListener() {
//            @Override
//            public void onNoDoubleClick(View v) {
//                RxBus.getInstance().post(new MateBackPressEvent());
//            }
//        });
//        mIvBack.setImageResource(R.drawable.btn_phone_back);
//        mTvTitle.setTextColor(ContextCompat.getColor(getContext(),R.color.main_cyan));
//        mTvTitle.setText("录音收集");
//        mTvMenu.setVisibility(View.VISIBLE);
//        mTvMenu.setTextColor(Color.WHITE);
//        mTvMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.x20));
//        mTvMenu.setGravity(Gravity.CENTER);
//        mTvMenu.setBackgroundResource(R.drawable.shape_rect_border_main_background_y22);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.x144),(int)getResources().getDimension(R.dimen.y44));
//        lp.rightMargin = (int) getResources().getDimension(R.dimen.x20);
//        mTvMenu.setLayoutParams(lp);
        if("len".equals(mate)){
            mIvCover.setImageResource(R.drawable.bg_phone_tape_len_incite);
        }
        if("mei".equals(mate)){
            mIvCover.setImageResource(R.drawable.bg_phone_tape_mei_incite);
        }
        if("sari".equals(mate)){
            mIvCover.setImageResource(R.drawable.bg_phone_tape_sha_incite);
        }
        List<BaseFragment> fragmentList = new ArrayList<>();
        fragmentList.add(PhoneLuyinListFragment.newInstance("con",mate));
        fragmentList.add(PhoneLuyinListFragment.newInstance("enc",mate));
        List<String> titles = new ArrayList<>();
        titles.add("鼓励");
        titles.add("安慰");
        String[] mTitles = {"鼓励","安慰"};
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        for (String str : mTitles) {
            mTabEntities.add(new TabEntity(str, R.drawable.ic_personal_bag,R.drawable.ic_personal_bag));
        }
        mAdapter = new TabFragmentPagerAdapter(getChildFragmentManager(),fragmentList,titles);
        mDataPager.setAdapter(mAdapter);
        mPageIndicator.setTabData(mTabEntities);
        mPageIndicator.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mDataPager.setCurrentItem(position);
                if("len".equals(mate)){
                    if(position == 0){
                        mIvCover.setImageResource(R.drawable.bg_phone_tape_len_incite);
                    }else if(position == 1){
                        mIvCover.setImageResource(R.drawable.bg_phone_tape_len_comfort);
                    }
                }
                if("mei".equals(mate)){
                    if(position == 0){
                        mIvCover.setImageResource(R.drawable.bg_phone_tape_mei_incite);
                    }else if(position == 1){
                        mIvCover.setImageResource(R.drawable.bg_phone_tape_mei_comfort);
                    }
                }
                if("sari".equals(mate)){
                    if(position == 0){
                        mIvCover.setImageResource(R.drawable.bg_phone_tape_sha_incite);
                    }else if(position == 1){
                        mIvCover.setImageResource(R.drawable.bg_phone_tape_sha_comfort);
                    }
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        mDataPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPageIndicator.setCurrentTab(position);
                if("len".equals(mate)){
                    if(position == 0){
                        mIvCover.setImageResource(R.drawable.bg_phone_tape_len_incite);
                    }else if(position == 1){
                        mIvCover.setImageResource(R.drawable.bg_phone_tape_len_comfort);
                    }
                }
                if("mei".equals(mate)){
                    if(position == 0){
                        mIvCover.setImageResource(R.drawable.bg_phone_tape_mei_incite);
                    }else if(position == 1){
                        mIvCover.setImageResource(R.drawable.bg_phone_tape_mei_comfort);
                    }
                }
                if("sari".equals(mate)){
                    if(position == 0){
                        mIvCover.setImageResource(R.drawable.bg_phone_tape_sha_incite);
                    }else if(position == 1){
                        mIvCover.setImageResource(R.drawable.bg_phone_tape_sha_comfort);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mPresenter.loadTicketsNum();
    }

    public void release(){
        if(mAdapter != null) mAdapter.release();
        if(mPresenter != null) mPresenter.release();
        super.release();
    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onLoadTicketsNumSuccess(int num) {
        //mTvMenu.setText("录音券: " + num);
        RxBus.getInstance().post(new MateBackPressEvent("录音券: " + num));
    }
}
