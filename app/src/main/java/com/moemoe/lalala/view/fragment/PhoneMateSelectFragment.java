package com.moemoe.lalala.view.fragment;

import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerPhoneMateComponent;
import com.moemoe.lalala.di.modules.PhoneMateModule;
import com.moemoe.lalala.model.entity.DeskMateEntity;
import com.moemoe.lalala.model.entity.PhoneFukuEntity;
import com.moemoe.lalala.model.entity.PhoneMateEntity;
import com.moemoe.lalala.presenter.PhoneMateContract;
import com.moemoe.lalala.presenter.PhoneMatePresenter;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.activity.PhoneMainActivity;
import com.moemoe.lalala.view.adapter.MateSelectAdapter;
import com.moemoe.lalala.view.adapter.MatefukuAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/9/4.
 */

public class PhoneMateSelectFragment extends BaseFragment implements PhoneMateContract.View{

    public static final String TAG = "PhoneMateSelectFragment";

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.list_2)
    PullAndLoadView mListFuku;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_relationship)
    TextView mTvRelationship;
    @BindView(R.id.tv_extra)
    TextView mTvExtra;
    @BindView(R.id.tv_extra_content)
    TextView mTvExtraContent;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    @BindView(R.id.tv_select_content)
    TextView mTvSelectContent;
    @BindView(R.id.tv_select_fuku)
    TextView mTvSelectFuku;
    @BindView(R.id.ll_fuku_root)
    View mSelectRoot;
    @BindView(R.id.tv_set_mate)
    TextView mTvSelectMate;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;

    @Inject
    PhoneMatePresenter mPresenter;

    MateSelectAdapter mAdapter;
    MatefukuAdapter mAdapter2;
    private String mCurSelect;
    private String mCurFukuId;
    private ArrayList<PhoneMateEntity> mMate;
    private  ArrayList<DeskMateEntity> mHaveMate;

    public static PhoneMateSelectFragment newInstance(){
        return new PhoneMateSelectFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_phone_mate;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        DaggerPhoneMateComponent.builder()
                .phoneMateModule(new PhoneMateModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
        mIvBack.setImageResource(R.drawable.btn_phone_back);
        mTvTitle.setTextColor(ContextCompat.getColor(getContext(),R.color.main_cyan));
        mTvTitle.setText("同桌");

        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setLoadMoreEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mListDocs.setLayoutManager(manager);
        mAdapter = new MateSelectAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);

        mListFuku.getSwipeRefreshLayout().setEnabled(false);
        mListFuku.setLoadMoreEnabled(false);
        LinearLayoutManager manager1 = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mListFuku.setLayoutManager(manager1);
        mAdapter2 = new MatefukuAdapter();
        mListFuku.getRecyclerView().setAdapter(mAdapter2);
        mAdapter2.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                setFukuView(mAdapter2.getItem(position));
                mCurFukuId = mAdapter2.getItem(position).getId();
                mAdapter2.setSelect(position);
                mAdapter2.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mMate = new ArrayList<>();
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position == 0){
                    mCurSelect = "len";
                }
                if(position == 1){
                    mCurSelect = "mei";
                }
                if(position == 2){
                    mCurSelect = "sari";
                }
                setView();
                mAdapter.setSelect(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        ArrayList<Integer> list = new ArrayList<>();
        list.add(R.drawable.btn_deskmate_chose_len);
        list.add(R.drawable.btn_deskmate_chose_mei);
        list.add(R.drawable.btn_deskmate_chose_sha);
        mHaveMate = PreferenceUtils.getAuthorInfo().getDeskMateEntities();
        for(DeskMateEntity entity : mHaveMate){
            if(entity.isDeskmate()){
                if(entity.getRoleOf().equals("len")){
                    mAdapter.setSelect(0);
                    mCurSelect = "len";
                }
                if(entity.getRoleOf().equals("mei")){
                    mAdapter.setSelect(1);
                    mCurSelect = "mei";
                }
                if(entity.getRoleOf().equals("sari")){
                    mAdapter.setSelect(2);
                    mCurSelect = "sari";
                }
            }
        }
        mAdapter.setList(list);
        mSelectRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                DeskMateEntity mate = null;
                for(DeskMateEntity temp : mHaveMate){
                    if(temp.getRoleOf().equals(mCurSelect)){
                        mate = temp;
                        break;
                    }
                }
                if(mate != null){
                    if(mListDocs.getVisibility() == View.VISIBLE){
                        mListDocs.setVisibility(View.GONE);
                        mListFuku.setVisibility(View.VISIBLE);
                        mTvSelectFuku.setText("选择");
                        mTvSelectContent.setVisibility(View.GONE);
                        mTvSelectMate.setVisibility(View.GONE);
                        mTvTitle.setText("服装");
                        mPresenter.loadFukuInfo(mCurSelect);
                    }else {
                        mPresenter.setFuku(mCurSelect,mCurFukuId);
                    }
                }else {
                    ToastUtils.showShortToast(getContext(),"还未拥有角色");
                }
            }
        });
        mTvSelectMate.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                DeskMateEntity mate = null;
                for(DeskMateEntity temp : mHaveMate){
                    if(temp.getRoleOf().equals(mCurSelect)){
                        mate = temp;
                        break;
                    }
                }
                if(mate != null){
                    mPresenter.setMate(mCurSelect);
                }else {
                    ToastUtils.showShortToast(getContext(),"还未拥有角色");
                }
            }
        });
        mPresenter.loadMateInfo();
    }

    private void setFukuView(PhoneFukuEntity entity){
        mTvName.setText(entity.getClothesName());
        mTvRelationship.setBackgroundDrawable(null);
        if(entity.isHave()){
            mTvRelationship.setText("已拥有");
            mTvRelationship.setTextColor(ContextCompat.getColor(getContext(),R.color.green_6fc93a));
        }else {
            mTvRelationship.setText("尚未拥有");
            mTvRelationship.setTextColor(ContextCompat.getColor(getContext(),R.color.gray_929292));
        }
        mTvExtra.setText("获取方式");
        mTvExtraContent.setText(entity.getCondition());
        mTvContent.setText(entity.getDesc());
    }

    private void setView(){
        if(mMate.size() == 0) return;
        PhoneMateEntity entity = null;
        for(PhoneMateEntity temp : mMate){
            if(temp.getRole().equals(mCurSelect)){
                entity = temp;
                break;
            }
        }
        if(entity == null){
            onBackPressed();
            return;
        }
        mTvName.setText(entity.getRoleName());
        DeskMateEntity mate = null;
        for(DeskMateEntity temp : mHaveMate){
            if(temp.getRoleOf().equals(mCurSelect)){
                mate = temp;
                break;
            }
        }
        if(mate != null){
            ArrayList<PhoneMateEntity.RoleLike> likes = entity.getRoleLikes();
            PhoneMateEntity.RoleLike like = null;
            for(PhoneMateEntity.RoleLike temp : likes){
                if(mate.getLikes() >= temp.getStart() && mate.getLikes() < temp.getEnd()){
                    like = temp;
                    break;
                }
            }
            if(like == null){
                onBackPressed();
                return;
            }
            float radius2 = getContext().getResources().getDimension(R.dimen.y18);
            float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
            RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
            ShapeDrawable shapeDrawable2 = new ShapeDrawable();
            shapeDrawable2.setShape(roundRectShape2);
            shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
            shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(like.getLevelColor(), ContextCompat.getColor(getContext(), R.color.main_cyan)));
            mTvRelationship.setBackgroundDrawable(shapeDrawable2);
            mTvRelationship.setText(like.getLevelName());
            mTvExtra.setText("下一级好感度:");
            mTvExtraContent.setTextColor(ContextCompat.getColor(getContext(),R.color.pink_fb7ba2));
            mTvExtraContent.setText(mate.getLikes() + "/" + like.getEnd());
            mTvContent.setText(entity.getDesc());
            mTvSelectContent.setVisibility(View.VISIBLE);
            mTvSelectContent.setText("当前:" + mate.getClothesName());
            mTvSelectFuku.setText("服装");
        }else {
            ArrayList<PhoneMateEntity.RoleLike> likes = entity.getRoleLikes();
            PhoneMateEntity.RoleLike like = null;
            for(PhoneMateEntity.RoleLike temp : likes){
                if(temp.getStart() == 0){
                    like = temp;
                    break;
                }
            }
            if(like == null){
                onBackPressed();
                return;
            }
            float radius2 = getContext().getResources().getDimension(R.dimen.y18);
            float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
            RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
            ShapeDrawable shapeDrawable2 = new ShapeDrawable();
            shapeDrawable2.setShape(roundRectShape2);
            shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
            shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(like.getLevelColor(), ContextCompat.getColor(getContext(), R.color.main_cyan)));
            mTvRelationship.setBackgroundDrawable(shapeDrawable2);
            mTvRelationship.setText(like.getLevelName());
            mTvExtra.setText("下一级好感度:");
            mTvExtraContent.setTextColor(ContextCompat.getColor(getContext(),R.color.pink_fb7ba2));
            mTvExtraContent.setText("0/" + like.getEnd());
            mTvContent.setText(entity.getDesc());
            mTvSelectContent.setVisibility(View.VISIBLE);
            mTvSelectContent.setText("当前:无");
            mTvSelectFuku.setText("服装");
        }
    }

    @Override
    public void release() {
        if(mPresenter != null) mPresenter.release();
        super.release();
    }

    @Override
    public void onBackPressed() {
        if(mListFuku.getVisibility() == View.VISIBLE){
            mListFuku.setVisibility(View.GONE);
            mListDocs.setVisibility(View.VISIBLE);
            mTvSelectMate.setVisibility(View.VISIBLE);
            mTvTitle.setText("同桌");
        }else {
            ((PhoneMainActivity)getContext()).finishCurFragment();
        }
    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onLoadMateSuccess(ArrayList<PhoneMateEntity> entities) {
        mMate = entities;
        setView();
    }

    @Override
    public void onLoadFukuSuccess(ArrayList<PhoneFukuEntity> entities) {
        mAdapter2.setSelect(0);
        mAdapter2.setList(entities);
        if(entities.size() > 0){
            setFukuView(entities.get(0));
        }
    }

    @Override
    public void setMateSuccess() {
        ToastUtils.showShortToast(getContext(),"设置同桌成功");
    }

    @Override
    public void setFukuSuccess() {
        ToastUtils.showShortToast(getContext(),"设置服装成功");
    }
}
