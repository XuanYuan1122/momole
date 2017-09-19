package com.moemoe.lalala.view.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.view.activity.PhoneMainActivity;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yi on 2017/9/4.
 */

public class PhoneTicketFragment extends BaseFragment {

    public static final String TAG = "PhoneTicketFragment";

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_have)
    TextView mTvHave;
    @BindView(R.id.iv_select_1)
    ImageView mIvSelect1;
    @BindView(R.id.iv_select_2)
    ImageView mIvSelect2;
    @BindView(R.id.iv_select_3)
    ImageView mIvSelect3;
    @BindView(R.id.tv_done)
    TextView mTvDone;
    @BindView(R.id.rl_root_1)
    View mRoot1;
    @BindView(R.id.rl_root_2)
    View mRoot2;
    @BindView(R.id.rl_root_3)
    View mRoot3;

    private int select = 1;

    public static PhoneTicketFragment newInstance(){
        return new PhoneTicketFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_phone_guli;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
        mIvBack.setImageResource(R.drawable.btn_phone_back);
        mTvTitle.setTextColor(ContextCompat.getColor(getContext(),R.color.main_cyan));
        mTvTitle.setText("鼓励券");
        mIvSelect1.setVisibility(View.VISIBLE);
        mTvDone.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                //TODO 赠送鼓励券
            }
        });
        mRoot1.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mIvSelect1.setVisibility(View.VISIBLE);
                mIvSelect2.setVisibility(View.GONE);
                mIvSelect3.setVisibility(View.GONE);
                select = 1;
            }
        });
        mRoot2.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mIvSelect1.setVisibility(View.GONE);
                mIvSelect2.setVisibility(View.VISIBLE);
                mIvSelect3.setVisibility(View.GONE);
                select = 2;
            }
        });
        mRoot3.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mIvSelect1.setVisibility(View.GONE);
                mIvSelect2.setVisibility(View.GONE);
                mIvSelect3.setVisibility(View.VISIBLE);
                select = 3;
            }
        });
    }

    public void release(){
        super.release();
    }

    @Override
    public void onBackPressed() {
        ((PhoneMainActivity)getContext()).finishCurFragment();
    }
}
