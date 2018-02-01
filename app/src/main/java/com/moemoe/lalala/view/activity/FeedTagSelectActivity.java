package com.moemoe.lalala.view.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerSelectTagComponent;
import com.moemoe.lalala.di.modules.SelectTagModule;
import com.moemoe.lalala.event.TagSelectEvent;
import com.moemoe.lalala.model.entity.OfficialTag;
import com.moemoe.lalala.model.entity.SimpleListSend;
import com.moemoe.lalala.model.entity.UserFollowTagEntity;
import com.moemoe.lalala.presenter.SelectTagContract;
import com.moemoe.lalala.presenter.SelectTagPresenter;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.SelectTagAdapter;
import com.moemoe.lalala.view.adapter.SelectTagSecAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * 标签选择
 * Created by yi on 2018/1/22.
 */

public class FeedTagSelectActivity extends BaseAppCompatActivity implements SelectTagContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvSave;
    @BindView(R.id.tv_right_menu)
    TextView mTvTop;
    @BindView(R.id.list)
    RecyclerView mList;
    @BindView(R.id.list_2)
    RecyclerView mList2;
    @BindView(R.id.tv_my_tag)
    TextView mTvMyTag;
    @BindView(R.id.view_end_step)
    View mEndStep;

    @Inject
    SelectTagPresenter mPresenter;
    private ArrayList<OfficialTag.OfficialTagSec> mUserTags;
    private boolean hasChange;
    private SelectTagSecAdapter mRightAdapter;
    private SelectTagAdapter mLeftAdapter;

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ac_feed_select_tags;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        DaggerSelectTagComponent.builder()
                .selectTagModule(new SelectTagModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        mPresenter.loadOfficialTags();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
        mTvTitle.setText("标签");
        ViewUtils.setRightMargins(mTvSave,(int) getResources().getDimension(R.dimen.x36));
        mTvSave.setVisibility(View.VISIBLE);
        mTvSave.setText(getString(R.string.label_save));
        mTvSave.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTvSave.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(hasChange){
                    ArrayList<String> ids = new ArrayList<>();
                    for(UserFollowTagEntity entity : mUserTags.get(0).getTagThi()){
                        ids.add(entity.getId());
                    }
                    createDialog();
                    mPresenter.saveUserTags(new SimpleListSend(ids));
                }
            }
        });
        mTvTop.setVisibility(View.VISIBLE);
        mTvTop.setText("置顶");
        mTvTop.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ArrayList<UserFollowTagEntity> list = new ArrayList<>();
                for(UserFollowTagEntity entity : mUserTags.get(0).getTagThi()){
                    if(entity.isSelect()){
                        list.add(entity);
                    }
                }
                if(list.size() >= 0){
                    hasChange = true;
                    mUserTags.get(0).getTagThi().removeAll(list);
                    mUserTags.get(0).getTagThi().addAll(0,list);
                    mRightAdapter.setList(mUserTags);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void operationTag(TagSelectEvent event){
        hasChange = true;
        switch (event.getOperation()){
            case "add":
                //TODO 执行添加动画
                mUserTags.get(0).getTagThi().add(0,new UserFollowTagEntity(event.getText(),event.getId()));
                mRightAdapter.notifyItemChanged(event.getPosition());
                break;
            case "del":
                for(UserFollowTagEntity entity : mUserTags.get(0).getTagThi()){
                    if(entity.getId().equals(event.getId())){
                        mUserTags.get(0).getTagThi().remove(entity);
                        mRightAdapter.notifyItemChanged(event.getPosition());
                        break;
                    }
                }
                break;
            case "del_user":
                for(UserFollowTagEntity entity : mUserTags.get(0).getTagThi()){
                    if(entity.getId().equals(event.getId())){
                        mUserTags.get(0).getTagThi().remove(entity);
                        mRightAdapter.setList(mUserTags);
                        break;
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(hasChange){
            setResult(RESULT_OK);
        }
        finish();
    }

    @Override
    protected void initListeners() {
        mTvMyTag.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(mEndStep.getVisibility() == View.VISIBLE){
                    mTvTop.setVisibility(View.VISIBLE);
                    mEndStep.setVisibility(View.INVISIBLE);
                    mTvMyTag.setBackgroundColor(ContextCompat.getColor(FeedTagSelectActivity.this,R.color.white));
                    mRightAdapter.setUserIds(null);
                    mRightAdapter.setList(mUserTags);
                    int oldPos = mLeftAdapter.getSelectPosition();
                    mLeftAdapter.setSelectPosition(-1);
                    mLeftAdapter.notifyItemChanged(oldPos);
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onLoadOfficialTags(ArrayList<OfficialTag> tags) {
        if(tags.size() > 0){
            mUserTags = tags.remove(0).getTagSec();
            mLeftAdapter = new SelectTagAdapter();
            mRightAdapter = new SelectTagSecAdapter();

            mEndStep.setVisibility(View.INVISIBLE);
            mTvMyTag.setBackgroundColor(ContextCompat.getColor(FeedTagSelectActivity.this,R.color.white));

            mList.setLayoutManager(new LinearLayoutManager(this));
            mList.setAdapter(mLeftAdapter);
            mLeftAdapter.setList(tags);
            mLeftAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if(mLeftAdapter.getSelectPosition() != position){
                        mTvTop.setVisibility(View.GONE);
                        int oldPos = mLeftAdapter.getSelectPosition();
                        mLeftAdapter.setSelectPosition(position);
                        if(oldPos != -1){
                            mLeftAdapter.notifyItemChanged(oldPos);
                        }
                        mLeftAdapter.notifyItemChanged(position);
                        mRightAdapter.setUserIds(mUserTags.get(0).getTagThi());
                        mRightAdapter.setList(mLeftAdapter.getItem(position).getTagSec());
                        mTvMyTag.setBackgroundColor(ContextCompat.getColor(FeedTagSelectActivity.this,R.color.bg_f6f6f6));
                        mEndStep.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
            mList2.setLayoutManager(new LinearLayoutManager(this));
            mList2.setAdapter(mRightAdapter);
            mRightAdapter.setList(mUserTags);
        }
    }

    @Override
    public void onSaveUserTagsSuccess() {
        finalizeDialog();
        showToast("保存成功");
        onBackPressed();
    }
}
