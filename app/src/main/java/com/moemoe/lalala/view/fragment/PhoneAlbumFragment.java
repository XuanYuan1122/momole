package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerPhoneAlbumComponent;
import com.moemoe.lalala.di.modules.PersonalListModule;
import com.moemoe.lalala.di.modules.PhoneAlbumModule;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.PhoneAlbumEntity;
import com.moemoe.lalala.presenter.PhoneAlbumContract;
import com.moemoe.lalala.presenter.PhoneAlbumPresenter;
import com.moemoe.lalala.utils.AlbumDecoration;
import com.moemoe.lalala.utils.FolderDecoration;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.view.activity.ImageBigSelectActivity;
import com.moemoe.lalala.view.activity.PhoneMainActivity;
import com.moemoe.lalala.view.adapter.PhoneAlbumAdapter;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/9/4.
 */

public class PhoneAlbumFragment extends BaseFragment implements PhoneAlbumContract.View{

    public static final String TAG = "PhoneAlbumFragment";

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.ll_bottom_root)
    View mBottomRoot;
    @BindView(R.id.iv_left)
    ImageView mIvLeft;
    @BindView(R.id.iv_right)
    ImageView mIvRight;
    @BindView(R.id.tv_page)
    TextView mTvPage;
    @BindView(R.id.list_2)
    PullAndLoadView mListItem;

    @Inject
    PhoneAlbumPresenter mPresenter;

    private PhoneAlbumAdapter mAdapter;
    private PhoneAlbumAdapter mAdapter2;
    private ArrayList<PhoneAlbumEntity> mCurAlbum;
    private ArrayList<PhoneAlbumEntity> mCurAlbumItem;
    private String mCurTypeId;
    private int mCurType = 1;
    private int mCurAlbumPage = 0;
    private int mAlbumPageCount;
    private int mCurAlbumItemPage = 0;
    private int mAlbumItemPageCount;

    public static PhoneAlbumFragment newInstance(){
        return new PhoneAlbumFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_phone_album;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPhoneAlbumComponent.builder()
                .phoneAlbumModule(new PhoneAlbumModule(this))
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
        mCurAlbum = new ArrayList<>();
        mCurAlbumItem = new ArrayList<>();
        mIvBack.setImageResource(R.drawable.btn_phone_back);
        mTvTitle.setTextColor(ContextCompat.getColor(getContext(),R.color.main_cyan));
        mTvTitle.setText("回忆相簿");
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new GridLayoutManager(getContext(),2));
        mAdapter = new PhoneAlbumAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.getRecyclerView().addItemDecoration(new AlbumDecoration());
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mCurType = 2;
                mCurTypeId = mAdapter.getItem(position).getId();
                mPresenter.loadAlbumItemList(mCurTypeId,0);
                mPresenter.loadAlbumItemCount(mCurTypeId);
                mListDocs.setVisibility(View.GONE);
                mListItem.setVisibility(View.VISIBLE);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mPresenter.loadAlbumList(0);
        mPresenter.loadAlbumCount();
        mListItem.getSwipeRefreshLayout().setEnabled(false);
        mListItem.setLoadMoreEnabled(false);
        mListItem.setLayoutManager(new GridLayoutManager(getContext(),2));
        mAdapter2 = new PhoneAlbumAdapter();
        mListItem.getRecyclerView().setAdapter(mAdapter2);
        mListItem.getRecyclerView().addItemDecoration(new AlbumDecoration());
        mAdapter2.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ArrayList<Image> res = new ArrayList<>();
                res.add(mAdapter2.getItem(position).getCover());
                Intent intent = new Intent(getContext(), ImageBigSelectActivity.class);
                intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, res);
                intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX, 1);
                getContext().startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mIvLeft.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(mCurType == 1){
                    if(mCurAlbumPage == 1 || mCurAlbumPage == 0){
                        return;
                    }
                    ArrayList<PhoneAlbumEntity> res = new ArrayList<>();
                    int n = (mCurAlbumPage - 1) * 6;
                    for(int i = n;i < n + 6;i++){
                        if(i >= mCurAlbum.size()) break;
                        res.add(mCurAlbum.get(i));
                    }
                    mCurAlbumPage--;
                    mAdapter.setList(res);
                }else {
                    if(mCurAlbumItemPage == 1 || mCurAlbumItemPage == 0){
                        return;
                    }
                    ArrayList<PhoneAlbumEntity> res = new ArrayList<>();
                    int n = (mCurAlbumItemPage - 1) * 6;
                    for(int i = n;i < n + 6;i++){
                        if(i >= mCurAlbumItem.size()) break;
                        res.add(mCurAlbumItem.get(i));
                    }
                    mCurAlbumItemPage--;
                    mAdapter2.setList(res);
                }
            }
        });
        mIvRight.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(mCurType == 1){
                    if(mCurAlbumPage == mAlbumPageCount || mCurAlbumPage == 0){
                        return;
                    }

                    int n = mCurAlbumPage * 6;
                    if(n >= mCurAlbum.size()){
                        mPresenter.loadAlbumList(mCurAlbum.size());
                    }else {
                        ArrayList<PhoneAlbumEntity> res = new ArrayList<>();
                        for(int i = n;i< n + 6;i++){
                            if(i >= mCurAlbum.size()) break;
                            res.add(mCurAlbum.get(i));
                        }
                        mCurAlbumPage++;
                        mAdapter.setList(res);
                    }
                }else {
                    if(mCurAlbumItemPage == mAlbumItemPageCount || mCurAlbumItemPage == 0){
                        return;
                    }
                    int n = mCurAlbumItemPage * 6;
                    if(n >= mCurAlbumItem.size()){
                        mPresenter.loadAlbumItemList(mCurTypeId,mCurAlbumItem.size());
                    }else {
                        ArrayList<PhoneAlbumEntity> res = new ArrayList<>();
                        for(int i = n;i< n + 6;i++){
                            if(i >= mCurAlbumItem.size()) break;
                            res.add(mCurAlbumItem.get(i));
                        }
                        mCurAlbumItemPage++;
                        mAdapter2.setList(res);
                    }
                }
            }
        });
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        super.release();
    }

    @Override
    public void onBackPressed() {
        if(mCurType == 2){
            mCurType = 1;
            mListDocs.setVisibility(View.VISIBLE);
            mListItem.setVisibility(View.GONE);
            mAdapter2.setList(new ArrayList<PhoneAlbumEntity>());
            if(mAlbumPageCount > 1){
                mBottomRoot.setVisibility(View.VISIBLE);
                mTvPage.setText(mCurAlbumPage + "/" + mAlbumPageCount);
            }else {
                mBottomRoot.setVisibility(View.GONE);
                mAlbumPageCount = 1;
            }
        }else {
            ((PhoneMainActivity)getContext()).finishCurFragment();
        }
    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onLoadAlbumListSuccess(ArrayList<PhoneAlbumEntity> entities, boolean isPull) {
        mCurAlbum.addAll(entities);
        if(entities.size() <= 6){
            mAdapter.setList(entities);
        }else {
            ArrayList<PhoneAlbumEntity> res = new ArrayList<>();
            for(int i = 0;i<6;i++){
                res.add(entities.get(i));
            }
            mAdapter.setList(res);
        }
        mCurAlbumPage++;
    }

    @Override
    public void onLoadAlbumItemListSuccess(ArrayList<PhoneAlbumEntity> entities, boolean isPull) {
        if(isPull){
            mCurAlbumItem.clear();
        }
        mCurAlbumItem.addAll(entities);
        if(entities.size() <= 6){
            mAdapter2.setList(entities);
        }else {
            ArrayList<PhoneAlbumEntity> res = new ArrayList<>();
            for(int i = 0;i<6;i++){
                res.add(entities.get(i));
            }
            mAdapter2.setList(res);
        }
        mCurAlbumItemPage++;
    }

    @Override
    public void onLoadAlbumCountSuccess(int count) {
        if(count > 6){
            mBottomRoot.setVisibility(View.VISIBLE);
            mAlbumPageCount = (int) Math.ceil((double) count/6);
            mTvPage.setText("1/" + mAlbumPageCount);

        }else {
            mBottomRoot.setVisibility(View.GONE);
            mAlbumPageCount = 1;
        }
    }

    @Override
    public void onLoadAlbumItemCountSuccess(int count) {
        if(count > 6){
            mBottomRoot.setVisibility(View.VISIBLE);
            mAlbumItemPageCount = (int) Math.ceil((double) count/6);
            mTvPage.setText("1/" + mAlbumItemPageCount);
        }else {
            mBottomRoot.setVisibility(View.GONE);
            mAlbumItemPageCount = 1;
        }
    }
}
