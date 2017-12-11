package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerSubmissionComponent;
import com.moemoe.lalala.di.modules.AddAddressModule;
import com.moemoe.lalala.di.modules.SubmissionModule;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.SendSubmissionEntity;
import com.moemoe.lalala.model.entity.SubmissionDepartmentEntity;
import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.AddressPresenter;
import com.moemoe.lalala.presenter.SubmissionContract;
import com.moemoe.lalala.presenter.SubmissionPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yi on 2017/7/17.
 */

public class SubmissionActivity extends BaseAppCompatActivity implements SubmissionContract.View{

    @BindView(R.id.tv_left_menu)
    TextView mTvMenuLeft;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_menu)
    TextView mMenu;
    @BindView(R.id.tv_department)
    TextView mTvDepartmentName;
    @BindView(R.id.tv_doc_name)
    TextView mTvDocName;


    @Inject
    SubmissionPresenter mPresenter;

    private String docId;
    private String departmentId;
    private BottomMenuFragment bottomMenuFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_submission;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerSubmissionComponent.builder()
                .submissionModule(new SubmissionModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        docId = getIntent().getStringExtra(UUID);
        String docName = getIntent().getStringExtra("doc_name");
        if(TextUtils.isEmpty(docId)){
            finish();
        }
        mTvDocName.setText(docName);
        createDialog();
        mPresenter.loadDepartment();
    }

    @OnClick({R.id.ll_submission_root})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ll_submission_root:
                if(bottomMenuFragment != null) bottomMenuFragment.show(getSupportFragmentManager(),"SubmissionActivity");
                break;
        }
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mTvMenuLeft.setVisibility(View.VISIBLE);
        ViewUtils.setLeftMargins(mTvMenuLeft,(int)getResources().getDimension(R.dimen.x36));
        mTvMenuLeft.setTextColor(ContextCompat.getColor(this,R.color.black_1e1e1e));
        mTvMenuLeft.setText(getString(R.string.label_give_up));
        mTvMenuLeft.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTitle.setText("投稿");
        mMenu.setVisibility(View.VISIBLE);
        ViewUtils.setRightMargins(mMenu, (int)getResources().getDimension(R.dimen.x36));
        mMenu.setText(getString(R.string.label_done));
        mMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                SendSubmissionEntity e = new SendSubmissionEntity(departmentId,docId);
                mPresenter.submission(e);
            }
        });
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    @Override
    public void onFailure(int code, String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this, code, msg);
    }

    @Override
    public void onLoadDepartmentSuccess(final ArrayList<SubmissionDepartmentEntity> entities) {
        finalizeDialog();
        if(entities.size() > 0){
            SubmissionDepartmentEntity entity = entities.get(0);
            departmentId = entity.getId();
            mTvDepartmentName.setText(entity.getName());
            bottomMenuFragment = new BottomMenuFragment();
            ArrayList<MenuItem> items = new ArrayList<>();
            for(int i = 0;i < entities.size();i++){
                SubmissionDepartmentEntity e = entities.get(i);
                MenuItem item = new MenuItem(i,e.getName());
                items.add(item);
            }
            bottomMenuFragment.setShowTop(false);
            bottomMenuFragment.setShowCancel(false);
            bottomMenuFragment.setCancelable(false);
            bottomMenuFragment.setMenuItems(items);
            bottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
            bottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
                @Override
                public void OnMenuItemClick(int itemId) {
                    SubmissionDepartmentEntity e = entities.get(itemId);
                    departmentId = e.getId();
                    mTvDepartmentName.setText(e.getName());
                }
            });
        }else {
            showToast("没有可投稿的学部");
            finish();
        }
    }

    @Override
    public void onSubmissionSuccess() {
        finalizeDialog();
        showToast("投稿成功");
        finish();
    }
}
