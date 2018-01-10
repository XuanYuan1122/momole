package com.moemoe.lalala.view.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
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
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ManHua2Entity;
import com.moemoe.lalala.model.entity.NewFolderEntity;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.model.entity.ShareFolderEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.UserTopEntity;
import com.moemoe.lalala.presenter.NewFolderItemContract;
import com.moemoe.lalala.presenter.NewFolderItemPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FolderDecoration;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.FileManHuaAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.moemoe.lalala.utils.StartActivityConstant.REQ_FILE_UPLOAD;

/**
 * 通常文件列表
 * Created by yi on 2017/8/20.
 */

public class NewFileManHuaActivity extends BaseAppCompatActivity implements NewFolderItemContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_left_menu)
    TextView mTvMenuLeft;
    @BindView(R.id.tv_right_menu)
    TextView mTvTop;
    @BindView(R.id.iv_menu_list)
    ImageView mIvMenu;
    @BindView(R.id.tv_menu)
    TextView mTvMenuRight;
    @BindView(R.id.ll_bottom_root)
    View mBottomRoot;
    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_add_folder)
    ImageView mIvAdd;
    @BindView(R.id.tv_buy_num)
    TextView mTvBuyNum;
    @BindView(R.id.tv_follow_num)
    TextView mTvFollowNum;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.tv_left_tag_1)
    TextView mTvTag1;
    @BindView(R.id.tv_left_tag_2)
    TextView mTvTag2;
    @BindView(R.id.ll_top_root)
    View mLlTopRoot;
    @BindView(R.id.iv_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.tv_user_name)
    TextView mTvUserName;
    @BindView(R.id.tv_to_bag)
    TextView mTvToBag;

    @Inject
    NewFolderItemPresenter mPresenter;

    private BottomMenuFragment bottomMenuFragment;
    private String mFolderType;
    private String mUserId;
    private String mFolderId;
    private String mFolderName;
    private String mTag1;
    private String mTag2;
    private boolean mIsSelect;
    private boolean mIsFollow;
    private FileManHuaAdapter mAdapter;
    private boolean isLoading = false;
    private HashMap<Integer,ManHua2Entity> mSelectMap;
    private View mBottomView;
    private AlertDialogUtil alertDialogUtil;
    private NewFolderEntity mFolderInfo;
    private int mCurPage = 1;
    private int[] mBackGround = { R.drawable.shape_rect_label_cyan,
            R.drawable.shape_rect_label_yellow,
            R.drawable.shape_rect_label_orange,
            R.drawable.shape_rect_label_pink,
            R.drawable.shape_rect_border_green_y8,
            R.drawable.shape_rect_label_purple,
            R.drawable.shape_rect_label_tab_blue};

    @Override
    protected int getLayoutId() {
        return R.layout.ac_folder;
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    public static void startActivity(Context context, String folderType, String folderId, String userId){
        Intent i = new Intent(context,NewFileManHuaActivity.class);
        i.putExtra(UUID,userId);
        i.putExtra("folderType",folderType);
        i.putExtra("folderId",folderId);
        context.startActivity(i);
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
        mFolderType = getIntent().getStringExtra("folderType");
        mFolderId = getIntent().getStringExtra("folderId");
        mBottomRoot.setVisibility(View.VISIBLE);
        mIsSelect = false;
        mSelectMap = new HashMap<>();
        if(mUserId.equals(PreferenceUtils.getUUid())){
            mIvAdd.setImageResource(R.drawable.btn_add_folder_item);
        }else {
            mIvAdd.setImageResource(R.drawable.btn_follow_folder_item);
        }
        mTvTop.setText("置顶");
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new FileManHuaAdapter();
        mListDocs.setLayoutManager(new GridLayoutManager(this,3));
        mListDocs.getRecyclerView().addItemDecoration(new FolderDecoration());
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ManHua2Entity entity = mAdapter.getItem(position);
                if(!mIsSelect){
                    NewFileManHua2Activity.startActivity(NewFileManHuaActivity.this,mAdapter.getList(),mFolderId,entity.getFolderId(),mUserId,position);
                }else {
                    if(entity.isSelect()){
                        mSelectMap.remove(position);
                        entity.setSelect(false);
                    }else {
                        mSelectMap.put(position,entity);
                        entity.setSelect(true);
                    }
                    mAdapter.notifyItemChanged(position);
                    if(mSelectMap.size() > 1){
                        mTvTop.setEnabled(false);
                        mTvTop.setTextColor(ContextCompat.getColor(NewFileManHuaActivity.this,R.color.gray_929292));
                    }else {
                        mTvTop.setEnabled(true);
                        mTvTop.setTextColor(ContextCompat.getColor(NewFileManHuaActivity.this,R.color.main_cyan));
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isChange = false;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isChange) {
                    if (dy > 10) {
                        sendBtnOut();
                        isChange = false;
                    }
                } else {
                    if (dy < -10) {
                        sendBtnIn();
                        isChange = true;
                    }
                }
            }
        });
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadFileList(mUserId,mFolderType,mFolderId,mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadFileList(mUserId,mFolderType,mFolderId,0);
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
        mPresenter.loadFolderInfo(mUserId,mFolderType,mFolderId);
    }

    private void sendBtnIn(){
        ObjectAnimator sendPostIn = ObjectAnimator.ofFloat(mIvAdd,"translationY",mIvAdd.getHeight()+ (int)getResources().getDimension(R.dimen.y32),0).setDuration(300);
        sendPostIn.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(sendPostIn);
        set.start();
    }

    private void sendBtnOut(){
        ObjectAnimator sendPostOut = ObjectAnimator.ofFloat(mIvAdd,"translationY",0,mIvAdd.getHeight() + (int)getResources().getDimension(R.dimen.y32)).setDuration(300);
        sendPostOut.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(sendPostOut);
        set.start();
    }

    private void initPopupMenus() {
        bottomMenuFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        if(mUserId.equals(PreferenceUtils.getUUid())){
            MenuItem item = new MenuItem(1, "选择");
            items.add(item);
            item = new MenuItem(3, "修改");
            items.add(item);
        }else {
            MenuItem item = new MenuItem(2, "举报");
            items.add(item);
        }
        MenuItem item = new MenuItem(4, "转发到动态");
        items.add(item);
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
                    ViewUtils.setLeftMargins(mTvMenuLeft, (int)getResources().getDimension(R.dimen.x36));
                    ViewUtils.setRightMargins(mTvMenuRight, (int)getResources().getDimension(R.dimen.x36));
                    mTvMenuRight.setVisibility(View.VISIBLE);
                    mTvMenuRight.setText(getString(R.string.label_delete));
                    mTvMenuRight.setTextColor(ContextCompat.getColor(NewFileManHuaActivity.this,R.color.main_cyan));
                    mTvMenuRight.setBackgroundDrawable(null);
                    mTvTop.setVisibility(View.VISIBLE);
                    mIsSelect = !mIsSelect;
                    mAdapter.setSelect(mIsSelect);
                    mAdapter.notifyDataSetChanged();
                    mTvTag1.setVisibility(View.GONE);
                    mTvTag2.setVisibility(View.GONE);
                }else if(itemId == 2){
                    Intent intent = new Intent(NewFileManHuaActivity.this, JuBaoActivity.class);
                    intent.putExtra(JuBaoActivity.EXTRA_NAME, "");
                    intent.putExtra(JuBaoActivity.EXTRA_CONTENT, "");
                    intent.putExtra(JuBaoActivity.EXTRA_TYPE,3);
                    intent.putExtra(JuBaoActivity.UUID,mFolderId);
                    intent.putExtra("userId",mUserId);
                    intent.putExtra(JuBaoActivity.EXTRA_TARGET, REPORT.FOLDER.toString());
                    startActivity(intent);
                }else if(itemId == 3){
                    NewFolderEditActivity.startActivity(NewFileManHuaActivity.this,"modify",mFolderType,mFolderInfo);
                }else if(itemId == 4){
                    if(mFolderInfo == null) return;
                    ShareFolderEntity entity = new ShareFolderEntity();
                    entity.setFolderCover(mFolderInfo.getCover());
                    entity.setFolderId(mFolderInfo.getFolderId());
                    entity.setFolderName(mFolderInfo.getFolderName());
                    entity.setFolderTags(mFolderInfo.getTexts());
                    entity.setFolderType(mFolderInfo.getType());
                    entity.setUpdateTime(mFolderInfo.getCreateTime());
                    UserTopEntity entity1 = new UserTopEntity();
                    entity1.setUserName(mFolderInfo.getCreateUserName());
                    entity1.setUserId(mFolderInfo.getCreateUserId());
                    entity1.setSex("N");
                    entity1.setBadge(null);
                    entity1.setHeadPath(mFolderInfo.getUserIcon().getPath());
                    entity.setCreateUser(entity1);
                    CreateForwardActivity.startActivity(NewFileManHuaActivity.this,entity,mUserId);
                }
            }
        });
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setPadding((int)getResources().getDimension(R.dimen.x36),0,(int)getResources().getDimension(R.dimen.x36),0);
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
        mTvMenuLeft.setTextColor(ContextCompat.getColor(NewFileManHuaActivity.this,R.color.black_1e1e1e));
        mTvMenuLeft.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(mIsSelect){
                    mIsSelect = !mIsSelect;
                    mIvBack.setVisibility(View.VISIBLE);
                    mIvMenu.setVisibility(View.VISIBLE);
                    mTvMenuLeft.setText(mFolderName);
                    ViewUtils.setLeftMargins(mTvMenuLeft,0);
                    if(!TextUtils.isEmpty(mTag1)){
                        mTvTag1.setVisibility(View.VISIBLE);
                        mTvTag1.setText(mTag1);
                    }
                    mTvTag2.setVisibility(View.VISIBLE);
                    mTvTag2.setText(mTag2);
                    mTvMenuRight.setVisibility(View.GONE);
                    mTvTop.setVisibility(View.GONE);
                    for(ManHua2Entity entity : mAdapter.getList()){
                        entity.setSelect(false);
                    }
                    mSelectMap.clear();
                    mAdapter.setSelect(mIsSelect);
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
                        for(ManHua2Entity id : mSelectMap.values()){
                            ids.add(id.getFolderId());
                        }
                        mPresenter.deleteFiles(ids,mFolderType,mFolderId,"");
                    }
                }
            }
        });
        mTvTop.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(mSelectMap.size() == 1){
                    createDialog();
                    for(ManHua2Entity entity : mSelectMap.values()){
                        mPresenter.topFile(mFolderId,mFolderType,entity.getFolderId());
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
        mFolderInfo = entity;
        mFolderName = entity.getFolderName();
        mTvMenuLeft.setText(mFolderName);
        mTvBuyNum.setText(entity.getBuyNum() + "");
        mTvFollowNum.setText(entity.getFavoriteNum() + "");
        mTvTime.setText(StringUtils.timeFormat(entity.getCreateTime()));
        String tagStr = "";
        for(int i = 0;i < entity.getTexts().size();i++){
            String tagTmp = entity.getTexts().get(i);
            if(i == 0){
                tagStr = tagTmp;
            }else {
                tagStr += " · " + tagTmp;
            }
        }
        mTag1 = tagStr;
        if(!TextUtils.isEmpty(mTag1)){
            mTvTag1.setVisibility(View.VISIBLE);
            mTvTag1.setText(mTag1);
        }
        if(entity.getCoin() > 0){
            mTag2 = "售价 " + entity.getCoin() + "节操";
            mTvTag2.setTextColor(ContextCompat.getColor(this,R.color.pink_fb7ba2));
        }else {
            mTag2 = "无料";
            mTvTag2.setTextColor(ContextCompat.getColor(this,R.color.green_93d856));
        }
        mTvTag2.setText(mTag2);
        mIsFollow = entity.isFavorite();
        if(!mUserId.equals(PreferenceUtils.getUUid())){
            mIvAdd.setImageResource(entity.isFavorite()?R.drawable.btn_unfollow_folder_item:R.drawable.btn_follow_folder_item);
            mIvAdd.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(mIsFollow){
                        mPresenter.removeFollowFolder(mUserId,mFolderType,mFolderId);
                    }else {
                        mPresenter.followFolder(mUserId,mFolderType,mFolderId);
                    }
                }
            });
        }else {
            mIvAdd.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    FilesUploadActivity.startActivityForResult(NewFileManHuaActivity.this,mFolderType,"",mFolderId);
                }
            });
        }
        if(!mUserId.equals(PreferenceUtils.getUUid())){
            mLlTopRoot.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(StringUtils.getUrl(this,entity.getUserIcon().getPath(),(int)getResources().getDimension(R.dimen.y80),(int)getResources().getDimension(R.dimen.y80),false,true))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(mIvAvatar);
            mIvAvatar.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    ViewUtils.toPersonal(NewFileManHuaActivity.this,entity.getCreateUserId());
                }
            });
            mTvUserName.setText(entity.getCreateUserName());
            mTvToBag.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent i2 = new Intent(NewFileManHuaActivity.this,NewBagActivity.class);
                    i2.putExtra("uuid",mUserId);
                    startActivity(i2);
                }
            });
        }
        if(!mUserId.equals(PreferenceUtils.getUUid()) && (entity.getTopList().size() > 0 || entity.getRecommendList().size() > 0)){
            createBottomView(entity);
        }
        mPresenter.loadFileList(mUserId,mFolderType,mFolderId,0);
        mAdapter.setBuy(!entity.isBuy() && entity.getCoin() > 0 && !mUserId.equals(PreferenceUtils.getUUid()));
        if(!entity.isBuy() && entity.getCoin() > 0 && !mUserId.equals(PreferenceUtils.getUUid())){
            alertDialogUtil = AlertDialogUtil.getInstance();
            alertDialogUtil.createBuyFolderDialog(NewFileManHuaActivity.this, entity.getCoin(),entity.getNowNum(),entity.getMaxNum(),entity.getFolderId(),entity.getType(),entity.getCover(),entity.getCreateUserId());
            alertDialogUtil.setButtonText("确定购买", "返回",0);
            alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                @Override
                public void CancelOnClick() {
                    alertDialogUtil.dismissDialog();
                    finish();
                }

                @Override
                public void ConfirmOnClick() {
                    mPresenter.buyFolder(mUserId,mFolderType,mFolderId);

                }
            });
            alertDialogUtil.showDialog();
        }
    }

    private void createBottomView(final NewFolderEntity entity){
        mBottomView = LayoutInflater.from(this).inflate(R.layout.item_folder_recommend, null);
        ImageView ivUser = mBottomView.findViewById(R.id.iv_avatar);
        TextView tvUser = mBottomView.findViewById(R.id.tv_user_name);
        final TextView tvFollow = mBottomView.findViewById(R.id.tv_follow);
        LinearLayout folderRoot = mBottomView.findViewById(R.id.ll_folder_root);
        LinearLayout recommendTopRoot = mBottomView.findViewById(R.id.ll_recommend_top_root);
        LinearLayout recommendRoot = mBottomView.findViewById(R.id.ll_recommend_root);
        TextView tvRefresh = mBottomView.findViewById(R.id.tv_refresh);
        mBottomView.findViewById(R.id.ll_user_root).setVisibility(View.GONE);
        Glide.with(this)
                .load(StringUtils.getUrl(this,entity.getUserIcon().getPath(),(int)getResources().getDimension(R.dimen.y80),(int)getResources().getDimension(R.dimen.y80),false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(ivUser);
        ivUser.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(NewFileManHuaActivity.this,entity.getCreateUserId());
            }
        });
        tvUser.setText(entity.getCreateUserName());
        tvFollow.setSelected(entity.isFollow());
        tvFollow.setText(entity.isFollow() ? "已关注" : "关注");
        tvFollow.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mPresenter.followUser(entity.getCreateUserId(),tvFollow.isSelected());
            }
        });
        tvRefresh.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mPresenter.refreshRecommend(mFolderName,mCurPage,mFolderId);
            }
        });
        if(entity.getTopList().size() > 0){
            folderRoot.setVisibility(View.VISIBLE);
            for (int n = 0;n < entity.getTopList().size();n++){
                final ShowFolderEntity item = entity.getTopList().get(n);
                View v = LayoutInflater.from(this).inflate(R.layout.item_bag_cover, null);
                ImageView iv = v.findViewById(R.id.iv_cover);
                TextView mark = v.findViewById(R.id.tv_mark);
                TextView title = v.findViewById(R.id.tv_title);
                TextView tag =  v.findViewById(R.id.tv_tag);
                title.setText(item.getFolderName());
                String tagStr1 = "";
                for(int i = 0;i < item.getTexts().size();i++){
                    String tagTmp = item.getTexts().get(i);
                    if(i == 0){
                        tagStr1 = tagTmp;
                    }else {
                        tagStr1 += " · " + tagTmp;
                    }
                }
                tag.setText(tagStr1);
                if(item.getType().equals(FolderType.ZH.toString())){
                    mark.setText("综合");
                    mark.setBackgroundResource(R.drawable.shape_rect_zonghe);
                }else if(item.getType().equals(FolderType.TJ.toString())){
                    mark.setText("图集");
                    mark.setBackgroundResource(R.drawable.shape_rect_tuji);
                }else if(item.getType().equals(FolderType.MH.toString())){
                    mark.setText("漫画");
                    mark.setBackgroundResource(R.drawable.shape_rect_manhua);
                }else if(item.getType().equals(FolderType.XS.toString())){
                    mark.setText("小说");
                    mark.setBackgroundResource(R.drawable.shape_rect_xiaoshuo);
                }
                int width = (DensityUtil.getScreenWidth(this) - (int)getResources().getDimension(R.dimen.x84)) / 3;
                int height = (int)getResources().getDimension(R.dimen.y280);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,height);
                RecyclerView.LayoutParams lp2;
                if(n == 1 || n == 2){
                    lp2 = new RecyclerView.LayoutParams(width + (int)getResources().getDimension(R.dimen.x18),height);
                    v.setPadding((int)getResources().getDimension(R.dimen.x18),0,0,0);
                }else {
                    lp2 = new RecyclerView.LayoutParams(width,height);
                    v.setPadding(0,0,0,0);
                }
                v.setLayoutParams(lp2);
                iv.setLayoutParams(lp);
                Glide.with(this)
                        .load(StringUtils.getUrl(this,item.getCover(),width,height, false, true))
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .bitmapTransform(new CropTransformation(this,width,height),new RoundedCornersTransformation(this,(int)getResources().getDimension(R.dimen.y8),0))
                        .into(iv);
                iv.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        if(item.getType().equals(FolderType.ZH.toString())){
                            NewFileManHuaActivity.startActivity(NewFileManHuaActivity.this,FolderType.ZH.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals(FolderType.TJ.toString())){
                            NewFileManHuaActivity.startActivity(NewFileManHuaActivity.this,FolderType.TJ.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals(FolderType.MH.toString())){
                            NewFileManHuaActivity.startActivity(NewFileManHuaActivity.this,FolderType.MH.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals(FolderType.XS.toString())){
                            NewFileXiaoshuoActivity.startActivity(NewFileManHuaActivity.this,FolderType.XS.toString(),item.getFolderId(),item.getCreateUser());
                        }
                    }
                });
                folderRoot.addView(v);
            }
        }else {
            folderRoot.setVisibility(View.GONE);
        }

        if(entity.getRecommendList().size() > 0){
            recommendTopRoot.setVisibility(View.VISIBLE);
            recommendRoot.setVisibility(View.VISIBLE);
            for (int n = 0;n < entity.getRecommendList().size();n++){
                final ShowFolderEntity item = entity.getRecommendList().get(n);
                View v = LayoutInflater.from(this).inflate(R.layout.item_hot_bag_new, null);

                ImageView iv = v.findViewById(R.id.iv_cover);
                TextView mark = v.findViewById(R.id.tv_mark);
                TextView bagCoin = v.findViewById(R.id.tv_bag_coin);
                TextView bagNum = v.findViewById(R.id.tv_bag_num);
                TextView title = v.findViewById(R.id.tv_bag_title);
                TextView tag1 = v.findViewById(R.id.tv_tag_1);
                TextView tag2 = v.findViewById(R.id.tv_tag_2);
                title.setText(item.getFolderName());
                if(item.getCoin() == 0){
                    bagCoin.setText("免费");
                }else {
                    bagCoin.setText(item.getCoin() + "节操");
                }
                if(item.getItems() > 0){
                    bagNum.setText(item.getItems() + "项");
                }else {
                    bagNum.setText("");
                }
                if(item.getTexts().size() == 2){
                    tag1.setVisibility(View.VISIBLE);
                    tag2.setVisibility(View.VISIBLE);
                    int index = StringUtils.getHashOfString(item.getTexts().get(0), mBackGround.length);
                    tag1.setBackgroundResource(mBackGround[index]);
                    tag1.setText(item.getTexts().get(0));
                    int index2 = StringUtils.getHashOfString(item.getTexts().get(1), mBackGround.length);
                    tag2.setBackgroundResource(mBackGround[index2]);
                    tag2.setText(item.getTexts().get(1));
                }else if(item.getTexts().size() == 1){
                    tag1.setVisibility(View.VISIBLE);
                    tag2.setVisibility(View.GONE);
                    int index = StringUtils.getHashOfString(item.getTexts().get(0), mBackGround.length);
                    tag1.setBackgroundResource(mBackGround[index]);
                    tag1.setText(item.getTexts().get(0));
                }else {
                    tag1.setVisibility(View.GONE);
                    tag2.setVisibility(View.GONE);
                }
                if(item.getType().equals(FolderType.ZH.toString())){
                    mark.setText("综合");
                    mark.setBackgroundResource(R.drawable.shape_rect_zonghe);
                }else if(item.getType().equals(FolderType.TJ.toString())){
                    mark.setText("图集");
                    mark.setBackgroundResource(R.drawable.shape_rect_tuji);
                }else if(item.getType().equals(FolderType.MH.toString())){
                    mark.setText("漫画");
                    mark.setBackgroundResource(R.drawable.shape_rect_manhua);
                }else if(item.getType().equals(FolderType.XS.toString())){
                    mark.setText("小说");
                    mark.setBackgroundResource(R.drawable.shape_rect_xiaoshuo);
                }
                int width = (DensityUtil.getScreenWidth(this) - (int)getResources().getDimension(R.dimen.x84)) / 3;
                int height = (int)getResources().getDimension(R.dimen.y290);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,height);
                RecyclerView.LayoutParams lp2;
                if(n == 1 || n == 2){
                    lp2 = new RecyclerView.LayoutParams(width + (int)getResources().getDimension(R.dimen.x18),ViewGroup.LayoutParams.WRAP_CONTENT);
                    v.setPadding((int)getResources().getDimension(R.dimen.x18),0,0,0);
                }else {
                    lp2 = new RecyclerView.LayoutParams(width,ViewGroup.LayoutParams.WRAP_CONTENT);
                    v.setPadding(0,0,0,0);
                }
                v.setLayoutParams(lp2);
                iv.setLayoutParams(lp);
                Glide.with(this)
                        .load(StringUtils.getUrl(this,item.getCover(),width,height, false, true))
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .bitmapTransform(new CropTransformation(this,width,height),new RoundedCornersTransformation(this,(int)getResources().getDimension(R.dimen.y8),0))
                        .into(iv);
                iv.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        if(item.getType().equals(FolderType.ZH.toString())){
                            NewFileManHuaActivity.startActivity(NewFileManHuaActivity.this,FolderType.ZH.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals(FolderType.TJ.toString())){
                            NewFileManHuaActivity.startActivity(NewFileManHuaActivity.this,FolderType.TJ.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals(FolderType.MH.toString())){
                            NewFileManHuaActivity.startActivity(NewFileManHuaActivity.this,FolderType.MH.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals(FolderType.XS.toString())){
                            NewFileXiaoshuoActivity.startActivity(NewFileManHuaActivity.this,FolderType.XS.toString(),item.getFolderId(),item.getCreateUser());
                        }
                    }
                });
                recommendRoot.addView(v);
            }
        }else {
            recommendTopRoot.setVisibility(View.GONE);
            recommendRoot.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoadFileListSuccess(Object o, boolean isPull) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_FILE_UPLOAD && resultCode == RESULT_OK){
            mPresenter.loadFileList(mUserId,mFolderType,mFolderId,0);
        }
    }

    @Override
    public void onDeleteFilesSuccess() {
        finalizeDialog();
        for(ManHua2Entity entity : mSelectMap.values()){
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
            ManHua2Entity entity = mAdapter.getList().remove((int)i);
            mAdapter.getList().add(0,entity);
            mAdapter.notifyItemRangeChanged(0,i + 1);
        }
        mSelectMap.clear();
    }

    @Override
    public void onFollowFolderSuccess() {
        mIsFollow = !mIsFollow;
        mIvAdd.setImageResource(mIsFollow?R.drawable.btn_unfollow_folder_item:R.drawable.btn_follow_folder_item);
    }

    @Override
    public void onBuyFolderSuccess() {
        alertDialogUtil.dismissDialog();
        mAdapter.setBuy(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFollowSuccess(boolean isFollow) {
        TextView tvFollow = (TextView) mBottomView.findViewById(R.id.tv_follow);
        tvFollow.setSelected(isFollow);
        tvFollow.setText(isFollow ? "已关注" : "关注");
    }

    @Override
    public void onLoadManHua2ListSuccess(ArrayList<ManHua2Entity> entities, boolean isPull) {
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
            if(mAdapter.getFooterLayoutCount() != 1){
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
                        mListDocs.setLoadMoreEnabled(false);
                    }
                }
            }
        }
    }

    @Override
    public void onReFreshSuccess(ArrayList<ShowFolderEntity> entities) {
        mCurPage++;
        LinearLayout recommendRoot = (LinearLayout) mBottomView.findViewById(R.id.ll_recommend_root);
        if(entities.size() > 0){
            recommendRoot.setVisibility(View.VISIBLE);
            for (int n = 0;n < entities.size();n++){
                final ShowFolderEntity item = entities.get(n);
                View v = LayoutInflater.from(this).inflate(R.layout.item_bag_cover, null);
                ImageView iv = (ImageView) v.findViewById(R.id.iv_cover);
                TextView mark = (TextView) v.findViewById(R.id.tv_mark);
                TextView title = (TextView) v.findViewById(R.id.tv_title);
                TextView tag = (TextView) v.findViewById(R.id.tv_tag);
                title.setText(item.getFolderName());
                String tagStr1 = "";
                for(int i = 0;i < item.getTexts().size();i++){
                    String tagTmp = item.getTexts().get(i);
                    if(i == 0){
                        tagStr1 = tagTmp;
                    }else {
                        tagStr1 += " · " + tagTmp;
                    }
                }
                tag.setText(tagStr1);
                if(item.getType().equals(FolderType.ZH.toString())){
                    mark.setText("综合");
                    mark.setBackgroundResource(R.drawable.shape_rect_zonghe);
                }else if(item.getType().equals(FolderType.TJ.toString())){
                    mark.setText("图集");
                    mark.setBackgroundResource(R.drawable.shape_rect_tuji);
                }else if(item.getType().equals(FolderType.MH.toString())){
                    mark.setText("漫画");
                    mark.setBackgroundResource(R.drawable.shape_rect_manhua);
                }else if(item.getType().equals(FolderType.XS.toString())){
                    mark.setText("小说");
                    mark.setBackgroundResource(R.drawable.shape_rect_xiaoshuo);
                }
                int width = (DensityUtil.getScreenWidth(this) - (int)getResources().getDimension(R.dimen.x84)) / 3;
                int height = (int)getResources().getDimension(R.dimen.y280);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,height);
                RecyclerView.LayoutParams lp2;
                if(n == 1 || n == 2){
                    lp2 = new RecyclerView.LayoutParams(width + (int)getResources().getDimension(R.dimen.x18),height);
                    v.setPadding((int)getResources().getDimension(R.dimen.x18),0,0,0);
                }else {
                    lp2 = new RecyclerView.LayoutParams(width,height);
                    v.setPadding(0,0,0,0);
                }
                v.setLayoutParams(lp2);
                iv.setLayoutParams(lp);
                Glide.with(this)
                        .load(StringUtils.getUrl(this,item.getCover(),width,height, false, true))
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .bitmapTransform(new CropTransformation(this,width,height),new RoundedCornersTransformation(this,(int)getResources().getDimension(R.dimen.y8),0))
                        .into(iv);
                iv.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        if(item.getType().equals(FolderType.ZH.toString())){
                            NewFileCommonActivity.startActivity(NewFileManHuaActivity.this,FolderType.ZH.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals(FolderType.TJ.toString())){
                            NewFileCommonActivity.startActivity(NewFileManHuaActivity.this,FolderType.TJ.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals(FolderType.MH.toString())){
                            NewFileManHuaActivity.startActivity(NewFileManHuaActivity.this,FolderType.MH.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals(FolderType.XS.toString())){
                            NewFileXiaoshuoActivity.startActivity(NewFileManHuaActivity.this,FolderType.XS.toString(),item.getFolderId(),item.getCreateUser());
                        }
                    }
                });
                recommendRoot.addView(v);
            }
        }else {
            recommendRoot.setVisibility(View.GONE);
        }
    }
}
