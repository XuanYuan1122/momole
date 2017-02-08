package com.moemoe.lalala.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.moemoe.lalala.R;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/2.
 */

public class SelectMateActivity extends BaseAppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView mIvBack;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_select_mate;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }
}
