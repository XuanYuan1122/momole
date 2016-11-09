package com.moemoe.lalala.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.moemoe.lalala.R;
import com.moemoe.lalala.galgame.Live2DDefine;
import com.moemoe.lalala.galgame.Live2DManager;
import com.moemoe.lalala.galgame.Live2DView;

/**
 * Created by Haru on 2016/7/25 0025.
 */
@ContentView(R.layout.frag_live2d)
public class Live2DFragment extends BaseFragment {
    @FindView(R.id.live2d)
    private FrameLayout live2d;
    private Live2DManager live2DMgr ;

    public static Live2DFragment newInstance(){
        Live2DFragment fragment = new Live2DFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        live2DMgr = new Live2DManager(Live2DDefine.MODEL_LEN);
        Live2DView live2DView = live2DMgr.createView(getActivity()) ;
        live2d.addView(live2DView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
