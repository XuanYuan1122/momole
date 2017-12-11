package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerNewFolderComponent;
import com.moemoe.lalala.di.modules.NewFolderModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.WenZhangFolderEntity;
import com.moemoe.lalala.presenter.NewFolderContract;
import com.moemoe.lalala.presenter.NewFolderPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FolderDecoration;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.BagWenZhangAdapter;
import com.moemoe.lalala.view.fragment.BaseFragment;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;

import static com.moemoe.lalala.utils.StartActivityConstant.REQUEST_CODE_CREATE_DOC;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_WEN_ZHANG;

/**
 * 新文件夹界面
 * Created by yi on 2017/8/18.
 */

public class NewFolderWenZhangActivity extends BaseAppCompatActivity implements NewFolderContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_left_menu)
    TextView mTvMenuLeft;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.iv_menu_list)
    ImageView mIvMenu;
    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_add_folder)
    ImageView mIvAdd;
    @Inject
    NewFolderPresenter mPresenter;

    private BagWenZhangAdapter mAdapter;
    private String mFolderType;
    private String mUserId;
    private boolean isLoading = false;
    private BottomMenuFragment bottomMenuFragment;
    private boolean mIsSelect;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_folder;
    }

    public static void startActivity(Context context,String userId,String folderType,String type,boolean select){
        Intent i = new Intent(context,NewFolderWenZhangActivity.class);
        i.putExtra(UUID,userId);
        i.putExtra("folderType",folderType);
        i.putExtra("type",type);
        i.putExtra("select",select);
        context.startActivity(i);
    }

    public static void startActivityForResult(BaseAppCompatActivity context,String userId,String folderType,String type,boolean select){
        Intent i = new Intent(context,NewFolderWenZhangActivity.class);
        i.putExtra(UUID,userId);
        i.putExtra("folderType",folderType);
        i.putExtra("type",type);
        i.putExtra("select",select);
        context.startActivityForResult(i,REQ_WEN_ZHANG);
    }

    public static void startActivityForResult(BaseFragment context, String userId, String folderType, String type, boolean select){
        Intent i = new Intent(context.getContext(),NewFolderWenZhangActivity.class);
        i.putExtra(UUID,userId);
        i.putExtra("folderType",folderType);
        i.putExtra("type",type);
        i.putExtra("select",select);
        context.startActivityForResult(i,REQ_WEN_ZHANG);
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerNewFolderComponent.builder()
                .newFolderModule(new NewFolderModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        mUserId = getIntent().getStringExtra(UUID);
        final String type = getIntent().getStringExtra("type");
        mFolderType = getIntent().getStringExtra("folderType");
        mIsSelect = getIntent().getBooleanExtra("select",false);
        String title = "";
       // mSelectMap = new HashMap<>();
        title = "文章";
        mTitle.setText(title);
        if(mUserId.equals(PreferenceUtils.getUUid())){
            mIvAdd.setVisibility(View.VISIBLE);
            mIvAdd.setImageResource(R.drawable.btn_add_folder_item);
            mIvAdd.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent intent = new Intent(NewFolderWenZhangActivity.this, CreateRichDocActivity.class);
                    intent.putExtra(CreateRichDocActivity.TYPE_QIU_MING_SHAN,3);
                    intent.putExtra(CreateRichDocActivity.TYPE_TAG_NAME_DEFAULT,"书包");
                    intent.putExtra("from_name","书包");
                    intent.putExtra("from_schema","neta://com.moemoe.lalala/bag_2.0");
                    startActivityForResult(intent, REQUEST_CODE_CREATE_DOC);
                }
            });
        }else {
            mIvAdd.setVisibility(View.GONE);
        }
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new BagWenZhangAdapter();
       // mAdapter.setSelect(mIsSelect);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        RecyclerView.LayoutManager manager;
        if(mFolderType.equals(FolderType.WZ.toString())){
            manager = new LinearLayoutManager(this);
        }else {
            manager = new GridLayoutManager(this,3);
            mListDocs.getRecyclerView().addItemDecoration(new FolderDecoration());
        }
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                WenZhangFolderEntity entity = mAdapter.getItem(position);
                if(!mIsSelect){
                    if (!TextUtils.isEmpty(entity.getSchema())) {
                        Uri uri = Uri.parse(entity.getSchema());
                        IntentUtils.toActivityFromUri(NewFolderWenZhangActivity.this, uri,view);
                    }
                }else {
//                    if(entity.isSelect()){
//                        mSelectMap.remove(position);
//                        entity.setSelect(false);
//                    }else {
//                        mSelectMap.put(position,entity);
//                        entity.setSelect(true);
//                    }
//                    mAdapter.notifyItemChanged(position);
                    Intent i = new Intent();
                    i.putExtra("docId",entity.getId());
                    setResult(RESULT_OK,i);
                    finish();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.setLayoutManager(manager);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadFolderList(mFolderType,mAdapter.getItemCount(),mUserId,type);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadFolderList(mFolderType,0,mUserId,type);
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
        if(mUserId.equals(PreferenceUtils.getUUid())){
            if(!type.equals("my")){
                mIvMenu.setVisibility(View.GONE);
                mIvBack.setVisibility(View.VISIBLE);
            }else {
                initPopupMenus();
                if(mIsSelect){
                    mIvBack.setVisibility(View.GONE);
                    mIvMenu.setVisibility(View.GONE);
                    mTvMenuLeft.setVisibility(View.VISIBLE);
                    ViewUtils.setLeftMargins(mTvMenuLeft, (int)getResources().getDimension(R.dimen.x36));
                    mTvMenuLeft.setText(getString(R.string.label_give_up));
                    mTvMenuLeft.setTextColor(ContextCompat.getColor(NewFolderWenZhangActivity.this,R.color.black_1e1e1e));
//                    mTvMenuRight.setVisibility(View.VISIBLE);
//                    ViewUtils.setRightMargins(mTvMenuRight, DensityUtil.dip2px(NewFolderWenZhangActivity.this,18));
//                    mTvMenuRight.setText(getString(R.string.label_done));
//                    mTvMenuRight.setTextColor(ContextCompat.getColor(NewFolderWenZhangActivity.this,R.color.main_cyan));
                }else {
                    mIvBack.setVisibility(View.VISIBLE);
                    mIvMenu.setVisibility(View.VISIBLE);
                }
            }
        }else {
            mIvMenu.setVisibility(View.GONE);
        }
        mPresenter.loadFolderList(mFolderType,0,mUserId,type);
    }

    private void initPopupMenus() {
        bottomMenuFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
//        MenuItem item = new MenuItem(1, "选择");
//        items.add(item);
        MenuItem item = new MenuItem(2, "投稿箱");
        items.add(item);
        bottomMenuFragment.setMenuItems(items);
        bottomMenuFragment.setShowTop(false);
        bottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        bottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
//                if (itemId == 1) {
//                    mIvBack.setVisibility(View.GONE);
//                    mIvMenu.setVisibility(View.GONE);
//                    mTvMenuLeft.setVisibility(View.VISIBLE);
//                    ViewUtils.setLeftMargins(mTvMenuLeft, DensityUtil.dip2px(NewFolderWenZhangActivity.this,18));
//                    mTvMenuLeft.setText(getString(R.string.label_give_up));
//                    mTvMenuLeft.setTextColor(ContextCompat.getColor(NewFolderWenZhangActivity.this,R.color.black_1e1e1e));
//                    mTvMenuRight.setVisibility(View.VISIBLE);
//                    ViewUtils.setRightMargins(mTvMenuRight, DensityUtil.dip2px(NewFolderWenZhangActivity.this,18));
//                    mTvMenuRight.setText(getString(R.string.label_delete));
//                    mTvMenuRight.setTextColor(ContextCompat.getColor(NewFolderWenZhangActivity.this,R.color.main_cyan));
//                    mIsSelect = !mIsSelect;
//                    mAdapter.setSelect(mIsSelect);
//                    mAdapter.notifyDataSetChanged();
//                }
                if(itemId == 2){
                    Intent i = new Intent(NewFolderWenZhangActivity.this,SubmissionHistoryActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mIvMenu.setImageResource(R.drawable.btn_menu_black_normal);
        mIvMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(bottomMenuFragment != null) bottomMenuFragment.show(getSupportFragmentManager(),"Folder");
            }
        });
        mTvMenuLeft.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
//                mIvBack.setVisibility(View.VISIBLE);
//                mIvMenu.setVisibility(View.VISIBLE);
//                mTvMenuLeft.setVisibility(View.GONE);
//                mTvMenuRight.setVisibility(View.GONE);
//                mIsSelect = !mIsSelect;
//                for(WenZhangFolderEntity entity : mAdapter.getList()){
//                    entity.setSelect(false);
//                }
//                mSelectMap.clear();
//                mAdapter.setSelect(mIsSelect);
//                mAdapter.notifyDataSetChanged();
                finish();
            }
        });
//        mTvMenuRight.setOnClickListener(new NoDoubleClickListener() {
//            @Override
//            public void onNoDoubleClick(View v) {
////                if(mSelectMap.size() > 0){
////                    createDialog();
////                    ArrayList<String> ids = new ArrayList<>();
////                    for(WenZhangFolderEntity id : mSelectMap.values()){
////                        ids.add(id.getId());
////                    }
////                    mPresenter.deleteFolders(ids,mFolderType);
////                }
//                if(mSelectMap.size() == 0){
//
//                }
//            }
//        });
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
    public void onLoadFolderListSuccess(Object o, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        ArrayList<WenZhangFolderEntity> entities = (ArrayList<WenZhangFolderEntity>) o;
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
    public void onDeleteFoldersSuccess() {
//        finalizeDialog();
//        for(WenZhangFolderEntity entity : mSelectMap.values()){
//            mAdapter.getList().remove(entity);
//        }
//        mAdapter.notifyDataSetChanged();
//        mSelectMap.clear();
    }

    @Override
    public void onTopFolderSuccess() {
//        finalizeDialog();
//        for (Integer i : mSelectMap.keySet()){
//            mAdapter.getList().get(i).setSelect(false);
//            WenZhangFolderEntity entity = mAdapter.getList().remove((int)i);
//            mAdapter.getList().add(0,entity);
//            mAdapter.notifyItemRangeChanged(0,i + 1);
//        }
//        mSelectMap.clear();
    }
}
