package com.moemoe.lalala;

import android.view.View;
import android.widget.ImageView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;

/**
 * Created by Haru on 2016/8/24.
 */
@ContentView(R.layout.ac_select_mate)
public class SelectMateActivity extends BaseActivity {

    @FindView(R.id.iv_back)
    private ImageView mIvBack;

    @Override
    protected void initView() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
