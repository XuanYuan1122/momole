package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerNewFileComponent;
import com.moemoe.lalala.di.modules.NewFileModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.CommonFileEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ManHua2Entity;
import com.moemoe.lalala.model.entity.NewFolderEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.presenter.NewFolderItemContract;
import com.moemoe.lalala.presenter.NewFolderItemPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileItemDecoration;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.FileCommonAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.moemoe.lalala.utils.StartActivityConstant.REQ_FILE_UPLOAD;

/**
 * 通常文件列表
 * Created by yi on 2017/8/20.
 */

public class NewFileManHua2Activity extends BaseAppCompatActivity implements NewFolderItemContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_left_menu)
    TextView mTvMenuLeft;
    @BindView(R.id.iv_menu_list)
    ImageView mIvMenu;
    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.tv_menu)
    TextView mTvMenuRight;
    @BindView(R.id.iv_add_folder)
    ImageView mIvAddFolder;

    @Inject
    NewFolderItemPresenter mPresenter;

    private BottomMenuFragment bottomMenuFragment;
    private String mUserId;
    private String mFolderId;
    private String mParentId;
    private String mFolderName;
    private boolean mIsSelect;
    private FileCommonAdapter mAdapter;
    private boolean isLoading = false;
    private HashMap<Integer,CommonFileEntity> mSelectMap;
    private View mBottomView;
    private ArrayList<ManHua2Entity> mManHualist;
    private int mPosition;
    private FileItemDecoration mItemDecoration;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_folder;
    }

    public static void startActivity(Context context,ArrayList<ManHua2Entity> entities,String parentId,String folderId,String userId,int position){
        Intent i = new Intent(context,NewFileManHua2Activity.class);
        i.putExtra(UUID,userId);
        i.putExtra("folderId",folderId);
        i.putExtra("parentId",parentId);
        i.putParcelableArrayListExtra("folders",entities);
        i.putExtra("position",position);
        context.startActivity(i);
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerNewFileComponent.builder()
                .newFileModule(new NewFileModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        mUserId = getIntent().getStringExtra(UUID);
        mFolderId = getIntent().getStringExtra("folderId");
        mParentId = getIntent().getStringExtra("parentId");
        mManHualist = getIntent().getParcelableArrayListExtra("folders");
        mPosition = getIntent().getIntExtra("position",0);
        mIsSelect = false;
        mSelectMap = new HashMap<>();
        mIvAddFolder.setVisibility(View.GONE);
        mListDocs.setPadding(0,0,0,0);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new FileCommonAdapter("");
        mAdapter.setGrid(false);
        mItemDecoration = new FileItemDecoration();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CommonFileEntity entity = mAdapter.getItem(position);
                if(!mIsSelect){
                    //TODO 跳转列表
                }else {
                    if(entity.isSelect()){
                        mSelectMap.remove(position);
                        entity.setSelect(false);
                    }else {
                        mSelectMap.put(position,entity);
                        entity.setSelect(true);
                    }
                    mAdapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadFileList(mUserId,FolderType.MHD.toString(),mFolderId,mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadFileList(mUserId,FolderType.MHD.toString(),mFolderId,0);
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
        initPopupMenus();
        isLoading = true;
        mPresenter.loadFileList(mUserId,FolderType.MHD.toString(),mFolderId,0);
        mFolderName = mManHualist.get(mPosition).getFolderName();
        mTvMenuLeft.setText(mFolderName);
        createBottomView();
    }

    private void initPopupMenus() {
        bottomMenuFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        if(mUserId.equals(PreferenceUtils.getUUid())){
            MenuItem item = new MenuItem(1, "选择");
            items.add(item);
            item = new MenuItem(3, "编辑");
            items.add(item);
        }else {
            MenuItem item = new MenuItem(2, "举报");
            items.add(item);
        }
        bottomMenuFragment.setMenuItems(items);
        bottomMenuFragment.setShowTop(false);
        bottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        bottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if (itemId == 1) {
                    mIvBack.setVisibility(View.GONE);
                    mIvMenu.setVisibility(View.GONE);
                    mTvMenuLeft.setText(getString(R.string.label_give_up));
                    ViewUtils.setLeftMargins(mTvMenuLeft, DensityUtil.dip2px(NewFileManHua2Activity.this,18));
                    ViewUtils.setRightMargins(mTvMenuRight, DensityUtil.dip2px(NewFileManHua2Activity.this,18));
                    mTvMenuRight.setVisibility(View.VISIBLE);
                    mTvMenuRight.setText(getString(R.string.label_delete));
                    mTvMenuRight.setTextColor(ContextCompat.getColor(NewFileManHua2Activity.this,R.color.main_cyan));
                    mIsSelect = !mIsSelect;
                    mAdapter.setSelect(mIsSelect);
                    mListDocs.setPadding(0,DensityUtil.dip2px(NewFileManHua2Activity.this,15),0,0);
                    mListDocs.setLayoutManager(new GridLayoutManager(NewFileManHua2Activity.this,3));
                    mListDocs.getRecyclerView().addItemDecoration(mItemDecoration);
                    mAdapter.setGrid(true);
                    mAdapter.notifyDataSetChanged();
                }else if(itemId == 2){

                }else if (itemId == 3){
                    FilesUploadActivity.startActivityForResult(NewFileManHua2Activity.this,FolderType.MHD.toString(),mFolderId,mParentId,mManHualist.get(mPosition));
                }
            }
        });
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setPadding(DensityUtil.dip2px(this,18),0,DensityUtil.dip2px(this,18),0);
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mIvMenu.setVisibility(View.VISIBLE);
        mIvMenu.setImageResource(R.drawable.btn_menu_black_normal);
        mIvMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(bottomMenuFragment != null) bottomMenuFragment.show(getSupportFragmentManager(),"FileCommon");
            }
        });
        mTvMenuLeft.setTextColor(ContextCompat.getColor(NewFileManHua2Activity.this,R.color.black_1e1e1e));
        mTvMenuLeft.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(mIsSelect){
                    mIsSelect = !mIsSelect;
                    mIvBack.setVisibility(View.VISIBLE);
                    mIvMenu.setVisibility(View.VISIBLE);
                    ViewUtils.setLeftMargins(mTvMenuLeft, 0);
                    mTvMenuLeft.setText(mFolderName);
                    mTvMenuRight.setVisibility(View.GONE);
                    for(CommonFileEntity entity : mAdapter.getList()){
                        entity.setSelect(false);
                    }
                    mSelectMap.clear();
                    mAdapter.setSelect(mIsSelect);
                    mListDocs.setPadding(0,0,0,0);
                    mListDocs.setLayoutManager(new LinearLayoutManager(NewFileManHua2Activity.this));
                    mListDocs.getRecyclerView().removeItemDecoration(mItemDecoration);
                    mAdapter.setGrid(false);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        mTvMenuRight.setVisibility(View.GONE);
        mTvMenuRight.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(mIsSelect){
                    if(mSelectMap.size() > 0){
                        createDialog();
                        ArrayList<String> ids = new ArrayList<>();
                        for(CommonFileEntity id : mSelectMap.values()){
                            ids.add(id.getFileId());
                        }
                        mPresenter.deleteFiles(ids,FolderType.MHD.toString(),mFolderId,mParentId);
                    }
                }
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
    public void onFailure(int code, String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
        isLoading = false;
        mListDocs.setComplete();
    }

    @Override
    public void onLoadFolderSuccess(final NewFolderEntity entity) {

    }

    private void createBottomView(){
        mBottomView = LayoutInflater.from(this).inflate(R.layout.item_folder_next, null);

        View preRoot = mBottomView.findViewById(R.id.ll_pre_root);
        View nextRoot = mBottomView.findViewById(R.id.ll_next_root);
        RelativeLayout preRl = (RelativeLayout) mBottomView.findViewById(R.id.rl_pre_root);
        RelativeLayout nextRl = (RelativeLayout) mBottomView.findViewById(R.id.rl_next_root);
        ImageView ivPre = (ImageView) mBottomView.findViewById(R.id.iv_cover);
        ImageView ivNext = (ImageView) mBottomView.findViewById(R.id.iv_cover_next);
        TextView markPre = (TextView) mBottomView.findViewById(R.id.tv_mark);
        TextView markNext = (TextView) mBottomView.findViewById(R.id.tv_mark_next);
        TextView titlePre = (TextView) mBottomView.findViewById(R.id.tv_title);
        TextView titleNext = (TextView) mBottomView.findViewById(R.id.tv_title_next);

        if(mPosition == mManHualist.size()){
            nextRoot.setVisibility(View.GONE);
            preRoot.setVisibility(View.VISIBLE);
        }else if(mPosition == 0){
            nextRoot.setVisibility(View.VISIBLE);
            preRoot.setVisibility(View.GONE);
        }else {
            nextRoot.setVisibility(View.VISIBLE);
            preRoot.setVisibility(View.VISIBLE);
        }
        int width = (DensityUtil.getScreenWidth(this) - DensityUtil.dip2px(this,42)) / 3;
        int height = DensityUtil.dip2px(this,140);

        preRl.setLayoutParams(new LinearLayoutCompat.LayoutParams(width,height));
        nextRl.setLayoutParams(new LinearLayoutCompat.LayoutParams(width,height));
        if(mPosition > 0){
            Glide.with(this)
                    .load(StringUtils.getUrl(this,mManHualist.get(mPosition - 1).getCover(),width,height, false, true))
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .bitmapTransform(new CropTransformation(this,width,height),new RoundedCornersTransformation(this,DensityUtil.dip2px(this,4),0))
                    .into(ivPre);
            markPre.setText(mManHualist.get(mPosition - 1).getItems() + " P");
            titlePre.setText(mManHualist.get(mPosition - 1).getFolderName());
            preRoot.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    mPosition--;
                    NewFileManHua2Activity.startActivity(NewFileManHua2Activity.this,mManHualist,mParentId,mManHualist.get(mPosition).getFolderId(),mUserId,mPosition);
                    finish();
                }
            });
        }
        if(mPosition < mManHualist.size() - 1){
            Glide.with(this)
                    .load(StringUtils.getUrl(this,mManHualist.get(mPosition + 1).getCover(),width,height, false, true))
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .bitmapTransform(new CropTransformation(this,width,height),new RoundedCornersTransformation(this,DensityUtil.dip2px(this,4),0))
                    .into(ivNext);
            markNext.setText(mManHualist.get(mPosition + 1).getItems() + " P");
            titleNext.setText(mManHualist.get(mPosition + 1).getFolderName());
            nextRoot.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    mPosition++;
                    NewFileManHua2Activity.startActivity(NewFileManHua2Activity.this,mManHualist,mParentId,mManHualist.get(mPosition).getFolderId(),mUserId,mPosition);
                    finish();
                }
            });
        }
    }

    @Override
    public void onLoadFileListSuccess(Object o, boolean isPull) {
        ArrayList<CommonFileEntity> entities = (ArrayList<CommonFileEntity>) o;
        isLoading = false;
        mListDocs.setComplete();
        if(isPull){
            mAdapter.setList(entities);
        }else {
            mAdapter.addList(entities);
        }
        if(entities.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            mListDocs.setLoadMoreEnabled(false);
            if(!mUserId.equals(PreferenceUtils.getUUid()) && mBottomView != null){
                RecyclerView.LayoutManager manager = mListDocs.getRecyclerView().getLayoutManager();
                int last = -1;
                if(manager instanceof GridLayoutManager){
                    last = ((GridLayoutManager)manager).findLastVisibleItemPosition();
                }else if(manager instanceof LinearLayoutManager){
                    last = ((LinearLayoutManager)manager).findLastVisibleItemPosition();
                }
                if(last >= 0){
                    View lastVisibleView = manager.findViewByPosition(last);
                    int[] lastLocation = new int[2] ;
                    lastVisibleView.getLocationOnScreen(lastLocation);
                    int lastY = lastLocation[1] + lastVisibleView.getMeasuredHeight();
                    int[] location = new int[2] ;
                    mListDocs.getRecyclerView().getLocationOnScreen(location);
                    int rvY = location[1] + mListDocs.getRecyclerView().getMeasuredHeight();
                    int topMargin;
                    if(lastY >= rvY){//view超过一屏了
                        topMargin = 0;
                    }else {//view小于一屏
                        topMargin = rvY - lastY;
                    }
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.topMargin = topMargin;
                    mBottomView.setLayoutParams(lp);
                    mAdapter.addFooterView(mBottomView);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_FILE_UPLOAD && resultCode == RESULT_OK){
            mPresenter.loadFileList(mUserId,FolderType.MHD.toString(),mFolderId,0);
        }
    }

    @Override
    public void onDeleteFilesSuccess() {
        finalizeDialog();
        for(CommonFileEntity entity : mSelectMap.values()){
            mAdapter.getList().remove(entity);
        }
        mAdapter.notifyDataSetChanged();
        mSelectMap.clear();
    }

    @Override
    public void onTopFileSuccess() {
        finalizeDialog();
        for (Integer i : mSelectMap.keySet()){
            mAdapter.getList().get(i).setSelect(false);
            mAdapter.notifyItemChanged(i);
        }
        mSelectMap.clear();
    }

    @Override
    public void onFollowFolderSuccess() {
    }

    @Override
    public void onBuyFolderSuccess() {
    }

    @Override
    public void onFollowSuccess(boolean isFollow) {
        TextView tvFollow = (TextView) mBottomView.findViewById(R.id.tv_follow);
        tvFollow.setSelected(isFollow);
    }

    @Override
    public void onLoadManHua2ListSuccess(ArrayList<ManHua2Entity> entities, boolean isPull) {

    }

    @Override
    public void onReFreshSuccess(ArrayList<ShowFolderEntity> entities) {

    }
}
