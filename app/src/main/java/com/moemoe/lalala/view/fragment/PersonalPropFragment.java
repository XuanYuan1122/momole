package com.moemoe.lalala.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.PreferenceUtils;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/15.
 */

public class PersonalPropFragment extends BaseFragment {

    @BindView(R.id.tv_text)
    TextView mTvText;

    public static PersonalPropFragment newInstance(String id){
        PersonalPropFragment fragment = new PersonalPropFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uuid",id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.default_layout;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            return;
        }
        String id = getArguments().getString("uuid");
        mTvText.setVisibility(View.VISIBLE);
        if(id.equals(PreferenceUtils.getUUid())){
            mTvText.setText("《哆啦A梦》或者《Fate》…\n" +
                    "\n" +
                    "我们正在考虑和其中哪一家品牌…\n" +
                    "\n" +
                    "签订“道具”供应协议？");
        }else {
            mTvText.setText("这位同学啥也没有！\n" +
                    "\n" +
                    "看上去很穷对吧？\n" +
                    "\n" +
                    "你自己看上去也一样穷啊…\n" +
                    "\n" +
                    "因为道具功能还没开放。\n");
        }
    }
}
