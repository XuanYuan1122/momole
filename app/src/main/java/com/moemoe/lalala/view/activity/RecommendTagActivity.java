package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerRecommendTagComponent;
import com.moemoe.lalala.di.modules.RecommendTagModule;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.RecommendTagEntity;
import com.moemoe.lalala.presenter.RecommendTagContract;
import com.moemoe.lalala.presenter.RecommendTagPresenter;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.TagUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.RecommendTagAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *
 * Created by yi on 2017/12/19.
 */

public class RecommendTagActivity extends BaseAppCompatActivity implements RecommendTagContract.View {

    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvSave;
    @BindView(R.id.tv_left_menu)
    TextView mTvDrop;
    @BindView(R.id.et_search)
    EditText mEtSearch;
    @BindView(R.id.list)
    RecyclerView mList;
    @BindView(R.id.tv_recommend_notice)
    TextView mTvNotic;
    @BindView(R.id.tv_add_user)
    TextView mTvAddUser;
    @BindView(R.id.label_1_root)
    View mTag1Root;
    @BindView(R.id.label_2_root)
    View mTag2Root;
    @BindView(R.id.tv_label_add_1)
    TextView mTvTag1;
    @BindView(R.id.tv_label_add_2)
    TextView mTvTag2;

    @Inject
    RecommendTagPresenter mPresenter;

    private RecommendTagAdapter mAdapter;
    private ArrayList<String> mRes;
    private String mFolderType;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_search_tag;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerRecommendTagComponent.builder()
                .recommendTagModule(new RecommendTagModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);
        mFolderType = getIntent().getStringExtra("folderType");
        if(TextUtils.isEmpty(mFolderType)){
            finish();
            return;
        }
        String str = "";
        if(mFolderType.equals(FolderType.ZH.toString())){
            str = "推荐综合标签";
        }else  if(mFolderType.equals(FolderType.MH.toString())){
            str = "推荐漫画标签";
        }else  if(mFolderType.equals(FolderType.TJ.toString())){
            str = "推荐图集标签";
        }else  if(mFolderType.equals(FolderType.XS.toString())){
            str = "推荐小说标签";
        }else if("DOC".equals(mFolderType)){
            str = "推荐帖子标签";
        }
        mTvNotic.setText(str);
        mRes = new ArrayList<>();
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                if(!TextUtils.isEmpty(content)){
                    mPresenter.loadKeyWordTag(content);
                    mTvNotic.setVisibility(View.GONE);
                    mTvAddUser.setVisibility(View.VISIBLE);
                }else {
                    mPresenter.loadRecommendTag(mFolderType);
                    mTvNotic.setVisibility(View.VISIBLE);
                    mTvAddUser.setVisibility(View.GONE);
                }
            }
        });

        mList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecommendTagAdapter();
        mList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                addToTop(mAdapter.getItem(position).getWord());
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mPresenter.loadRecommendTag(mFolderType);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mTvTitle.setText("添加标签");
        mTvSave.setVisibility(View.VISIBLE);
        mTvSave.setText(getString(R.string.label_done));
        ViewUtils.setRightMargins(mTvSave, (int)getResources().getDimension(R.dimen.x36));
        mTvSave.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTvSave.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra("tags",mRes);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        mTvDrop.setVisibility(View.VISIBLE);
        ViewUtils.setLeftMargins(mTvDrop,(int)getResources().getDimension(R.dimen.x36));
        mTvDrop.setTextColor(ContextCompat.getColor(this,R.color.black_1e1e1e));
        mTvDrop.setText(getString(R.string.label_give_up));
        mTvDrop.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R.id.iv_clear,R.id.tv_add_user,R.id.iv_1_close,R.id.iv_2_close})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_clear:
                mEtSearch.setText("");
                mPresenter.loadRecommendTag(mFolderType);
                break;
            case R.id.tv_add_user:
                addToTop(mEtSearch.getText().toString());
                break;
            case R.id.iv_1_close:
                mRes.remove(mTvTag1.getText().toString());
                if(mTag2Root.getVisibility() == View.VISIBLE){
                    mTag2Root.setVisibility(View.GONE);
                    mTvTag1.setText(mTvTag2.getText());
                }else {
                    mTag1Root.setVisibility(View.GONE);
                    mTvTag1.setText("");
                }
                break;
            case R.id.iv_2_close:
                mRes.remove(mTvTag2.getText().toString());
                mTag2Root.setVisibility(View.GONE);
                break;
        }
    }

    private void addToTop(String content){
        if(mRes.size() >= 2){
            showToast("最多只能添加2个标签");
        }else {
            if(TextUtils.isEmpty(content)){
                return;
            }
            if(mRes.contains(content)){
                showToast("已经存在该标签");
                return;
            }
            mRes.add(content);
            if(mTag1Root.getVisibility() == View.GONE){
                mTag1Root.setVisibility(View.VISIBLE);
                mTvTag1.setText(content);
                TagUtils.setBackGround(content,mTvTag1);
            }else {
                mTag2Root.setVisibility(View.VISIBLE);
                mTvTag2.setText(content);
                TagUtils.setBackGround(content,mTvTag2);
            }
        }
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onLoadRecommendTagSuccess(ArrayList<RecommendTagEntity> entities) {
        mAdapter.setList(entities);
    }

    @Override
    public void onLoadKeyWordTagSuccess(ArrayList<RecommendTagEntity> entities) {
        mAdapter.setList(entities);
    }
}
