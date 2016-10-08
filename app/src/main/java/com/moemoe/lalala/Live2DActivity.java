package com.moemoe.lalala;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.moemoe.lalala.galgame.Live2DView;

/**
 * Created by Haru on 2016/8/31.
 */
@ContentView(R.layout.ac_live2d)
public class Live2DActivity extends BaseActivity {

    @FindView(R.id.live2d_view)
    private Live2DView mLive2DView;

    @Override
    protected void initView() {

    }
}
