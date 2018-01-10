package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.tablayout.SlidingTabLayout;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerLuntan2Component;
import com.moemoe.lalala.di.components.DaggerLuntanComponent;
import com.moemoe.lalala.di.modules.Luntan2Module;
import com.moemoe.lalala.di.modules.LuntanModule;
import com.moemoe.lalala.model.entity.DepartmentGroupEntity;
import com.moemoe.lalala.model.entity.DocResponse;
import com.moemoe.lalala.model.entity.LuntanTabEntity;
import com.moemoe.lalala.presenter.Luntan2Contract;
import com.moemoe.lalala.presenter.Luntan2Presenter;
import com.moemoe.lalala.presenter.LuntanContract;
import com.moemoe.lalala.presenter.LuntanPresenter;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.MenuVItemDecoration;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.activity.CreateRichDocActivity;
import com.moemoe.lalala.view.activity.NewDocDetailActivity;
import com.moemoe.lalala.view.adapter.OldDocAdapter;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.rong.imlib.model.Conversation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.app.Activity.RESULT_OK;
import static com.moemoe.lalala.utils.StartActivityConstant.REQUEST_CODE_CREATE_DOC;

/**
 *
 * Created by yi on 2017/9/4.
 */

public class LuntanAllFragment extends BaseFragment  implements LuntanContract.View {

    @BindView(R.id.tab_layout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.pager_person_data)
    ViewPager mPager;

    @Inject
    LuntanPresenter mPresenter;

    private TabFragmentPagerAdapter mAdapter;

    public static LuntanAllFragment newInstance(){
        return new LuntanAllFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_luntanall;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerLuntanComponent.builder()
                .luntanModule(new LuntanModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mPresenter.loadTabList();
    }

    @Override
    public void onFailure(int code, String msg) {
        ErrorCodeUtils.showErrorMsgByCode(getContext(),code,msg);
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        if(mAdapter != null) mAdapter.release();
        super.release();
    }

    @Override
    public void onLoadTabListSuccess(ArrayList<LuntanTabEntity> entities) {
        List<BaseFragment> fragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for(LuntanTabEntity entity : entities){
            fragmentList.add(LuntanFragment.newInstance(entity.getId(),entity.getName(),entity.isCanDoc()));
            titles.add(entity.getName());
        }
        mAdapter = new TabFragmentPagerAdapter(getChildFragmentManager(),fragmentList,titles);
        mPager.setAdapter(mAdapter);
        mTabLayout.setViewPager(mPager);
    }
}
