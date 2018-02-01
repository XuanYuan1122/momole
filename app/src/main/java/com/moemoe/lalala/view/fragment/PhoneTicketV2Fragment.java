package com.moemoe.lalala.view.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerPhoneTicketComponent;
import com.moemoe.lalala.di.modules.PhoneTicketModule;
import com.moemoe.lalala.model.entity.TabEntity;
import com.moemoe.lalala.presenter.PhoneTicketContract;
import com.moemoe.lalala.presenter.PhoneTicketPresenter;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.activity.IPhoneFragment;
import com.moemoe.lalala.view.activity.PhoneMainV2Activity;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/9/4.
 */

public class PhoneTicketV2Fragment extends BaseFragment implements IPhoneFragment,PhoneTicketContract.View{

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

    public static PhoneTicketV2Fragment newInstance(String mate){
        PhoneTicketV2Fragment fragment = new PhoneTicketV2Fragment();
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
        if("len".equals(mate)){
            mIvCover.setImageResource(R.drawable.bg_phone_tape_len_incite);
        }
        if("mei".equals(mate)){
            mIvCover.setImageResource(R.drawable.bg_phone_tape_mei_incite);
        }
        if("sari".equals(mate)){
            mIvCover.setImageResource(R.drawable.bg_phone_tape_sha_incite);
        }
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
        PreferenceUtils.getAuthorInfo().setTicketNum(num);
        ((PhoneMainV2Activity)getContext()).setLuyinMenu("次元币: " + num);
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
    }

    @Override
    public String getTitle() {
        return "录音收集";
    }

    @Override
    public int getTitleColor() {
        return R.color.main_cyan;
    }

    @Override
    public int getMenu() {
        return 0;
    }

    @Override
    public int getBack() {
        return R.drawable.btn_phone_back;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onMenuClick() {

    }
}
