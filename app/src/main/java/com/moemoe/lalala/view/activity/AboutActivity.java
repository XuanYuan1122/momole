package com.moemoe.lalala.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.NoDoubleClickListener;

import butterknife.BindView;

/**
 * Created by yi on 2016/11/28.
 */

public class AboutActivity extends BaseAppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_about;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mTvTitle.setText(R.string.label_about_neta);
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
}
