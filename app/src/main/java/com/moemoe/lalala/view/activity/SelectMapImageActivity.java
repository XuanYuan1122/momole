package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerSelectMapImageComponent;
import com.moemoe.lalala.di.modules.CreateMapImageModule;
import com.moemoe.lalala.di.modules.SelectMapImageModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.MapHistoryEntity;
import com.moemoe.lalala.model.entity.MapRoleBase;
import com.moemoe.lalala.model.entity.MapUserImageEntity;
import com.moemoe.lalala.presenter.SelectMapImageContract;
import com.moemoe.lalala.presenter.SelectMapImagePresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.MapSelectItemDecoration;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.MapSelectImageAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;

/**
 *
 * Created by yi on 2017/11/7.
 */

public class SelectMapImageActivity extends BaseAppCompatActivity implements SelectMapImageContract.View{

    public static String SELECT_TYPE = "select_type";
    public static int IS_OFFICIAL = 1;
    public static int IS_HISTORY_SELECT = 2;
    public static int IS_HISTORY_DELETE = 3;
    public static int IS_HISTORY_FAVORITE = 4;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;
    @BindView(R.id.tv_right_menu)
    TextView mTvMenu;
    @BindView(R.id.tv_left_menu)
    TextView mTvLeftMenu;

    @Inject
    SelectMapImagePresenter mPresenter;

    private MapSelectImageAdapter mAdapter;
    private String mSelectUrl = "";
    private boolean isLoading;
    private HashMap<String,Integer> mSelect;
    private String mUseId;
    private boolean isSelect;
    private int mType;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerSelectMapImageComponent.builder()
                .selectMapImageModule(new SelectMapImageModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);

        mType = getIntent().getIntExtra(SELECT_TYPE,IS_OFFICIAL);
        final String uuid = getIntent().getStringExtra(UUID);
        mUseId = getIntent().getStringExtra("use_id");

        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        if(mType == IS_OFFICIAL){
            mAdapter = new MapSelectImageAdapter<MapUserImageEntity>();
        }else {
            mAdapter = new MapSelectImageAdapter<MapHistoryEntity>(mUseId);
        }
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new GridLayoutManager(this,3));
        mListDocs.getRecyclerView().addItemDecoration(new MapSelectItemDecoration());
        mListDocs.setPadding(getResources().getDimensionPixelSize(R.dimen.x50),0,getResources().getDimensionPixelSize(R.dimen.x50),0);
        if(mType == IS_OFFICIAL){
            mPresenter.loadMapSelectList();
            mListDocs.setLoadMoreEnabled(false);
        }else {
            isLoading = true;
            mPresenter.loadMapHistoryList(uuid,0);
            mListDocs.setLoadMoreEnabled(true);
            mListDocs.setPullCallback(new PullCallback() {
                @Override
                public void onLoadMore() {
                    isLoading = true;
                    mPresenter.loadMapHistoryList(uuid,mAdapter.getItemCount());
                }

                @Override
                public void onRefresh() {
                    isLoading = true;
                    mPresenter.loadMapHistoryList(uuid,0);
                }

                @Override
                public boolean isLoading() {
                    return isLoading;
                }

                @Override
                public boolean hasLoadedAllItems() {
                    return false;
                }
            });
        }
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(mType == IS_OFFICIAL){
                    mSelectUrl = ((MapUserImageEntity)mAdapter.getItem(position)).getUrl();
                    int pos = mAdapter.getSelectPosition();
                    if(pos != position){
                        mAdapter.setSelectPosition(position);
                        mAdapter.notifyItemChanged(position);
                        if(pos != -1){
                            mAdapter.notifyItemChanged(pos);
                        }
                    }
                }else if(mType == IS_HISTORY_SELECT){
                    mSelectUrl = ((MapHistoryEntity)mAdapter.getItem(position)).getPicUrl();
                    int pos = mAdapter.getSelectPosition();
                    if(pos != position){
                        mAdapter.setSelectPosition(position);
                        mAdapter.notifyItemChanged(position);
                        if(pos != -1){
                            mAdapter.notifyItemChanged(pos);
                        }
                    }
                }else if(mType == IS_HISTORY_DELETE){
                    if(isSelect){
                        String id = ((MapHistoryEntity)mAdapter.getItem(position)).getId();
                        if(mUseId.equals(id)){
                            showToast("使用中，不可删除");
                        }else {
                            if(mSelect.containsKey(id)){
                                mSelect.remove(id);
                                ((MapHistoryEntity)mAdapter.getItem(position)).setSelect(false);
                                mAdapter.notifyItemChanged(position);
                            }else {
                                mSelect.put(id,position);
                                ((MapHistoryEntity)mAdapter.getItem(position)).setSelect(true);
                                mAdapter.notifyItemChanged(position);
                            }
                        }
                    }
                }else if(mType == IS_HISTORY_FAVORITE){
                    MapHistoryEntity entity = ((MapHistoryEntity)mAdapter.getItem(position));
                    mPresenter.likeMapRole(entity.isLike(),entity.getId(),position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        if(mType == IS_OFFICIAL){
            mTitle.setText("官方形象");
        }else {
            mTitle.setText("历史形象");
        }

        mTvMenu.setVisibility(View.VISIBLE);
        mTvLeftMenu.setVisibility(View.GONE);
        if(mType == IS_OFFICIAL || mType == IS_HISTORY_SELECT){
            mTvMenu.setText("选择");
            mTvMenu.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(!TextUtils.isEmpty(mSelectUrl)){
                        Intent i = new Intent();
                        i.putExtra("url",mSelectUrl);
                        setResult(RESULT_OK,i);
                        finish();
                    }else {
                        showToast("还没选择呢");
                    }
                }
            });
        }else if(mType == IS_HISTORY_DELETE){
            ViewUtils.setLeftMargins(mTvLeftMenu,getResources().getDimensionPixelSize(R.dimen.x36));
            mTvLeftMenu.setText("放弃");
            mTvLeftMenu.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
            mTvLeftMenu.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    mTvLeftMenu.setVisibility(View.GONE);
                    mIvBack.setVisibility(View.VISIBLE);
                    isSelect = false;
                }
            });
            mTvMenu.setText("删除");
            mTvMenu.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(isSelect){
                        if(mSelect.size() > 0){
                            final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                            alertDialogUtil.createNormalDialog(SelectMapImageActivity.this,"确认删除");
                            alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                                @Override
                                public void CancelOnClick() {
                                    alertDialogUtil.dismissDialog();
                                }

                                @Override
                                public void ConfirmOnClick() {
                                    alertDialogUtil.showDialog();
                                    ArrayList<String> ids = new ArrayList<>();
                                    ids.addAll(mSelect.keySet());
                                    mPresenter.deleteHistoryMapRole(ids);
                                    alertDialogUtil.dismissDialog();
                                }
                            });
                            alertDialogUtil.showDialog();
                        }
                    }else {
                        mSelect = new HashMap<>();
                        isSelect = true;
                        mTvLeftMenu.setVisibility(View.VISIBLE);
                        mIvBack.setVisibility(View.GONE);
                    }
                }
            });
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
        mListDocs.setComplete();
        isLoading = false;
    }

    @Override
    public void onLoadListSuccess(ArrayList<MapUserImageEntity> entities) {
        mAdapter.setList(entities);
        if(entities.size() > 0){
            mSelectUrl = ((MapUserImageEntity)mAdapter.getItem(0)).getUrl();
            mAdapter.setSelectPosition(0);
            mAdapter.notifyItemChanged(0);
        }
    }

    @Override
    public void onLoadHistoryListSuccess(ArrayList<MapHistoryEntity> entities, boolean isPull) {
        mListDocs.setComplete();
        isLoading = false;
        if(entities.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            mListDocs.setLoadMoreEnabled(false);
        }
        if(isPull){
            mAdapter.setList(entities);
        }else {
            mAdapter.addList(entities);
        }
    }

    @Override
    public void onDeleteSuccess() {
        ArrayList<MapHistoryEntity> entities = new ArrayList<>();
        for (int pos : mSelect.values()){
            entities.add((MapHistoryEntity) mAdapter.getItem(pos));
        }
        mAdapter.getList().removeAll(entities);
        mAdapter.notifyDataSetChanged();
        showToast("操作成功");
    }

    @Override
    public void onLikeSuccess(boolean isLike,int position) {
        ((MapHistoryEntity)mAdapter.getItem(position)).setLike(isLike);
        if(isLike){
            ((MapHistoryEntity)mAdapter.getItem(position)).setLikes(((MapHistoryEntity)mAdapter.getItem(position)).getLikes() + 1);
        }else {
            ((MapHistoryEntity)mAdapter.getItem(position)).setLikes(((MapHistoryEntity)mAdapter.getItem(position)).getLikes() - 1);
        }
        mAdapter.notifyItemChanged(position);
    }
}
