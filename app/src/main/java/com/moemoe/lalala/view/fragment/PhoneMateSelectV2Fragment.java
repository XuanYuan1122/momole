package com.moemoe.lalala.view.fragment;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;

import com.moemoe.lalala.di.components.DaggerPhoneMateComponent;
import com.moemoe.lalala.di.modules.PhoneMateModule;
import com.moemoe.lalala.event.MateChangeEvent;
import com.moemoe.lalala.model.entity.DeskMateEntity;
import com.moemoe.lalala.model.entity.PhoneFukuEntity;
import com.moemoe.lalala.model.entity.PhoneMateEntity;
import com.moemoe.lalala.presenter.PhoneMateContract;
import com.moemoe.lalala.presenter.PhoneMatePresenter;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.activity.IPhoneFragment;
import com.moemoe.lalala.view.activity.PhoneMainV2Activity;
import com.moemoe.lalala.view.adapter.MateSelectAdapter;
import com.moemoe.lalala.view.adapter.MatefukuAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 *
 * Created by yi on 2017/9/4.
 */

public class PhoneMateSelectV2Fragment extends BaseFragment implements IPhoneFragment,PhoneMateContract.View{

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
    @BindView(R.id.tv_col_luyin)
    TextView mTvCol;

    @Inject
    PhoneMatePresenter mPresenter;

    MateSelectAdapter mAdapter;
    MatefukuAdapter mAdapter2;
    private String mCurSelect;
    private String mCurFukuId;
    private ArrayList<PhoneMateEntity> mMate;
    private ArrayList<DeskMateEntity> mHaveMate;
    private boolean haveFuku;

    public static PhoneMateSelectV2Fragment newInstance(){
        return new PhoneMateSelectV2Fragment();
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
        manager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        mListFuku.setLayoutManager(manager1);
        mAdapter2 = new MatefukuAdapter();
        mListFuku.getRecyclerView().setAdapter(mAdapter2);
        mAdapter2.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PhoneFukuEntity entity = mAdapter2.getItem(position);
                if(entity.isHave()){
                    mCurFukuId = mAdapter2.getItem(position).getId();
                }
                setFukuView(mAdapter2.getItem(position));
                haveFuku = mAdapter2.getItem(position).isHave();
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
        list.add(R.drawable.btn_deskmate_chose_sari);
        ArrayList<Integer> haveMate = new ArrayList<>();
        mHaveMate = PreferenceUtils.getAuthorInfo().getDeskMateEntities();
        for(DeskMateEntity entity : mHaveMate){
            if(entity.isDeskmate()){
                if(entity.getRoleOf().equals("len")){
                    mAdapter.setSelect(0);
                    mCurSelect = "len";
                    haveFuku = true;
                    mCurFukuId = entity.getClothesId();
                }
                if(entity.getRoleOf().equals("mei")){
                    mAdapter.setSelect(1);
                    mCurSelect = "mei";
                    haveFuku = true;
                    mCurFukuId = entity.getClothesId();
                }
                if(entity.getRoleOf().equals("sari")){
                    mAdapter.setSelect(2);
                    mCurSelect = "sari";
                    haveFuku = true;
                    mCurFukuId = entity.getClothesId();
                }
            }
            if(entity.getRoleOf().equals("len")){
                haveMate.add(0);
            }
            if(entity.getRoleOf().equals("mei")){
                haveMate.add(1);
            }
            if(entity.getRoleOf().equals("sari")){
                haveMate.add(2);
            }
        }
        mAdapter.setHave(haveMate);
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
                        ((PhoneMainV2Activity)getContext()).setTitle("服装");
                        mPresenter.loadFukuInfo(mCurSelect);
                    }else {
                        if(haveFuku){
                            mPresenter.setFuku(mCurSelect,mCurFukuId);
                        }else {
                            ToastUtils.showShortToast(getContext(),"还未拥该服装");
                        }
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
        mTvCol.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ((PhoneMainV2Activity)getContext()).toFragment(PhoneTicketV2Fragment.newInstance(mCurSelect));
            }
        });
        mPresenter.loadMateInfo();
    }

    private void setFukuView(PhoneFukuEntity entity){
        mTvName.setText(entity.getClothesName());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTvRelationship.setLayoutParams(lp);
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
            ((PhoneMainV2Activity)getContext()).onBackPressed();
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
        int w = (int) getResources().getDimension(R.dimen.x88);
        int h = (int) getResources().getDimension(R.dimen.y36);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, h);
        mTvRelationship.setLayoutParams(lp);
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
                ((PhoneMainV2Activity)getContext()).onBackPressed();
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
            mTvRelationship.setTextColor(Color.WHITE);
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
                ((PhoneMainV2Activity)getContext()).onBackPressed();
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
        changeMate();
        EventBus.getDefault().post(new MateChangeEvent());
    }

    @Override
    public void setFukuSuccess() {
        ToastUtils.showShortToast(getContext(),"设置服装成功");
        changeMate();
        EventBus.getDefault().post(new MateChangeEvent());
    }

    private void changeMate(){
        ArrayList<DeskMateEntity> list = PreferenceUtils.getAuthorInfo().getDeskMateEntities();
        for(DeskMateEntity entity : list){
            if(entity.getRoleOf().equals(mCurSelect)){
                entity.setDeskmate(true);
                entity.setClothesId(mCurFukuId);
            }else {
                entity.setDeskmate(false);
            }
        }
        PreferenceUtils.getAuthorInfo().setDeskMateEntities(list);
    }

    @Override
    public String getTitle() {
        return "同桌";
    }

    @Override
    public int getTitleColor() {
        return R.color.main_cyan;
    }

    @Override
    public int getMenu() {
        return 0;
    }

    @Override
    public int getBack() {
        return R.drawable.btn_phone_back;
    }

    @Override
    public boolean onBackPressed() {
        if(mListFuku.getVisibility() == View.VISIBLE){
            mListFuku.setVisibility(View.GONE);
            mListDocs.setVisibility(View.VISIBLE);
            mTvSelectMate.setVisibility(View.VISIBLE);
            ((PhoneMainV2Activity)getContext()).setTitle("同桌");
            setView();
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onMenuClick() {

    }
}
