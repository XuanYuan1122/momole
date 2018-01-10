package com.moemoe.lalala.view.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerDynamicComponent;
import com.moemoe.lalala.di.modules.DynamicModule;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.CommentV2Entity;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.DynamicContentEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.RetweetEntity;
import com.moemoe.lalala.model.entity.ShareArticleEntity;
import com.moemoe.lalala.model.entity.ShareFolderEntity;
import com.moemoe.lalala.model.entity.TabEntity;
import com.moemoe.lalala.model.entity.TagLikeEntity;
import com.moemoe.lalala.presenter.DynamicContract;
import com.moemoe.lalala.presenter.DynamicPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.LevelSpan;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.utils.tag.TagControl;
import com.moemoe.lalala.view.adapter.CommentListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;

import static com.moemoe.lalala.utils.StartActivityConstant.REQ_DELETE_TAG;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_FORWARD_DYNAMIC;

/**
 * 动态详情
 * Created by yi on 2017/9/22.
 */

@SuppressWarnings("deprecation")
@SuppressLint("InflateParams")
public class DynamicActivity extends BaseAppCompatActivity implements DynamicContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_left_menu)
    TextView mTvFrom;
    @BindView(R.id.iv_menu_list)
    ImageView mIvMenu;
    @BindView(R.id.tv_to_comment)
    TextView mTvToComment;
    @BindView(R.id.rv_list)
    PullAndLoadView mRvList;
    @BindView(R.id.rl_ope_root)
    KeyboardListenerLayout mKlCommentBoard;
    @Inject
    DynamicPresenter mPresenter;

    private TextView mTvSort;
//    private DocLabelView docLabel;
//    private NewDocLabelAdapter docLabelAdapter;
    private TextView mCoin;

    private CommentListAdapter mAdapter;
    private boolean mIsLoading;
    private NewDynamicEntity mDynamic;
    private BottomMenuFragment fragment;
    private int commentType = 1;//0 转发 1 评论
    private boolean sortTime = false;
    private boolean tagFlag;
    private ArrayList<DocTagEntity> mTags;
    private ArrayList<CommentV2Entity> mPreComments;
    private int mPrePosition;
    private boolean mAuto;
    private String mId;
    private TextView tvTag;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_dynamic;
    }

    public static void startActivity(Context context,NewDynamicEntity dynamic){
        Intent i = new Intent(context,DynamicActivity.class);
        i.putExtra("dynamic",dynamic);
        context.startActivity(i);
    }

    public static void startActivity(Context context,String id){
        Intent i = new Intent(context,DynamicActivity.class);
        i.putExtra(UUID,id);
        context.startActivity(i);
    }

    public static void startActivity(Context context,NewDynamicEntity dynamic,boolean autoComment){
        Intent i = new Intent(context,DynamicActivity.class);
        i.putExtra("dynamic",dynamic);
        i.putExtra("auto",autoComment);
        context.startActivity(i);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);
        DaggerDynamicComponent.builder()
                .dynamicModule(new DynamicModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mDynamic = getIntent().getParcelableExtra("dynamic");
        mId = getIntent().getStringExtra(UUID);
        mAuto = getIntent().getBooleanExtra("auto",false);
        if(TextUtils.isEmpty(mId)){
            init();
        }else {
            mPresenter.getDynamic(mId);
        }
    }

    private void init(){
        mPreComments = new ArrayList<>();
        fragment = new BottomMenuFragment();
        mRvList.setLoadMoreEnabled(false);
        mRvList.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new CommentListAdapter(mDynamic.getId());
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(commentType == 1) showCommentMenu(mAdapter.getItem(position),position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRvList.getRecyclerView().setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvList.setLayoutManager(layoutManager);
        mRvList.setLoadMoreEnabled(false);
        mRvList.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                mPresenter.loadCommentsList(mDynamic.getId(),commentType,sortTime,mAdapter.getList().size());
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                mPresenter.loadCommentsList(mDynamic.getId(),commentType,sortTime,0);
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        });
        mKlCommentBoard.setOnkbdStateListener(new KeyboardListenerLayout.onKeyboardChangeListener() {

            @Override
            public void onKeyBoardStateChange(int state) {
                if (state == KeyboardListenerLayout.KEYBOARD_STATE_HIDE) {
                    if(tagFlag){
                        tagFlag = false;
//                        if(mTags.size() > 0){
//                            DocTagEntity entity = mTags.get(mTags.size() - 1);
//                            if(!TextUtils.isEmpty(entity.getName())){
//                                if(checkLabel(entity.getName())){
//                                    mTags.get(mTags.size() - 1).setEdit(false);
//                                    if (DialogUtils.checkLoginAndShowDlg(DynamicActivity.this)) {
//                                        createDialog();
//                                        TagSendEntity bean = new TagSendEntity(mDynamic.getId(),entity.getName());
//                                        mPresenter.sendTag(bean);
//                                    }
//                                }else {
//                                    entity.setName("");
//                                    ToastUtils.showShortToast(DynamicActivity.this,R.string.msg_tag_already_exit);
//                                }
//                            }else {
//                                mTags.remove(entity);
//                            }
//                            if(docLabel != null)docLabel.notifyAdapter();
//                        }
                    }
                }
            }
        });
        mTvToComment.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                CreateCommentActivity.startActivity(DynamicActivity.this,mDynamic.getId(),false,"",false);
            }
        });
        mRvList.getRecyclerView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(mAuto){
                    if(mAdapter.getList().size() > 0){
                        mRvList.getRecyclerView().scrollToPosition(1);
                    }
                    mAuto = false;
                }
            }
        });
        setHead();
        mPresenter.loadTags(mDynamic.getId());
        mPresenter.loadCommentsList(mDynamic.getId(),commentType,sortTime,0);
    }

    private boolean checkLabel(String content){
        ArrayList<DocTagEntity> tmp = new ArrayList<>();
        tmp.addAll(mTags);
        if(mTags.size() > 0){
            tmp.remove(tmp.size() - 1);
        }
        for(DocTagEntity tagBean : tmp){
            if(tagBean.getName().equals(content)){
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTvFrom.setVisibility(View.VISIBLE);
        mTvFrom.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        String from = getIntent().getStringExtra("from");
        if(!TextUtils.isEmpty(from)){
            mTvFrom.setText(from);
        }else {
            mTvFrom.setText("动态");
        }
        mTvFrom.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mIvMenu.setVisibility(View.VISIBLE);
        mIvMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                showMenu();
            }
        });

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    public void favoriteComment(String id,boolean isFavorite,int position){
        mPresenter.favoriteComment(mDynamic.getId(),id,isFavorite,position);
    }

    private void setHead(){
        View v = LayoutInflater.from(this).inflate(R.layout.item_new_feed_list,null);
        v.findViewById(R.id.rl_from_top).setVisibility(View.GONE);
        ImageView userHead = v.findViewById(R.id.iv_avatar);
        ImageView ivVip = v.findViewById(R.id.iv_vip);
        TextView userName = v.findViewById(R.id.tv_name);
        ImageView userSex = v.findViewById(R.id.iv_sex);
        TextView level = v.findViewById(R.id.tv_level);
        View huiRoot = v.findViewById(R.id.fl_huizhang_1);
        TextView huiTv = v.findViewById(R.id.tv_huizhang_1);
        v.findViewById(R.id.iv_more).setVisibility(View.GONE);
        TextView time = v.findViewById(R.id.tv_time);
        TextView text = v.findViewById(R.id.tv_content);
        LinearLayout root = v.findViewById(R.id.ll_img_root);
        RelativeLayout root1 = v.findViewById(R.id.rl_card_root);
        LinearLayout cardRoot = v.findViewById(R.id.ll_card_root);


        //user top
        if(mDynamic.getCreateUser().isVip()){
            ivVip.setVisibility(View.VISIBLE);
        }else {
            ivVip.setVisibility(View.GONE);
        }
        int size = (int) getResources().getDimension(R.dimen.x80);
        Glide.with(this)
                .load(StringUtils.getUrl(this, mDynamic.getCreateUser().getHeadPath(),size,size,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(userHead);
        userHead.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(DynamicActivity.this, mDynamic.getCreateUser().getUserId());
            }
        });
        userName.setText(mDynamic.getCreateUser().getUserName());
        userSex.setImageResource(mDynamic.getCreateUser().getSex().equalsIgnoreCase("M")?R.drawable.ic_user_girl:R.drawable.ic_user_boy);
        LevelSpan levelSpan = new LevelSpan(ContextCompat.getColor(DynamicActivity.this,R.color.white),getResources().getDimension(R.dimen.x12));
        String content = "LV" + mDynamic.getCreateUser().getLevel();
        String colorStr = "LV";
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(levelSpan, content.indexOf(colorStr), content.indexOf(colorStr) + colorStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        level. setText(style);
        float radius2 = getResources().getDimension(R.dimen.y4);
        float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
        RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
        ShapeDrawable shapeDrawable2 = new ShapeDrawable();
        shapeDrawable2.setShape(roundRectShape2);
        shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(mDynamic.getCreateUser().getLevelColor(), ContextCompat.getColor(this, R.color.main_cyan)));
        level.setBackgroundDrawable(shapeDrawable2);
        
        View[] huizhang = {huiRoot};
        TextView[] huizhangT = {huiTv};
        if(mDynamic.getCreateUser().getBadge() != null){
            ArrayList<BadgeEntity> badgeEntities = new ArrayList<>();
            badgeEntities.add(mDynamic.getCreateUser().getBadge());
            ViewUtils.badge(this,huizhang,huizhangT,badgeEntities);
        }else {
            huiRoot.setVisibility(View.GONE);
            huiTv.setVisibility(View.GONE);
        }
        time.setText(StringUtils.timeFormat(mDynamic.getCreateTime()));
        //content
        text.setMaxLines(100);
        text.setText(TagControl.getInstance().paresToSpann(this, mDynamic.getText()));
        text.setMovementMethod(LinkMovementMethod.getInstance());
        //extra
        root.setVisibility(View.GONE);
        root.removeAllViews();
        root.setOnClickListener(null);
        root1.setVisibility(View.GONE);
        cardRoot.removeAllViews();
        root1.setOnClickListener(null);
        boolean showHongbao = false;
        if("DELETE".equals(mDynamic.getType())){//已被删除
            root.setVisibility(View.VISIBLE);
            root.setBackgroundColor(Color.WHITE);
            TextView tv = new TextView(this);
            tv.setText("该内容已被删除");
            tv.setTextColor(ContextCompat.getColor(this,R.color.white));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,this.getResources().getDimension(R.dimen.x36));
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundColor(ContextCompat.getColor(this,R.color.gray_e8e8e8));
            int h = (int) getResources().getDimension(R.dimen.y320);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,h);
            tv.setLayoutParams(lp);
            root.addView(tv);
        }else if("DYNAMIC".equals(mDynamic.getType())){
            root.setVisibility(View.VISIBLE);
            root.setBackgroundColor(Color.WHITE);
            DynamicContentEntity dynamicContentEntity = new Gson().fromJson(mDynamic.getDetail(),DynamicContentEntity.class);
            if(dynamicContentEntity.getImages() != null && dynamicContentEntity.getImages().size() > 0){
                setImg(dynamicContentEntity.getImages(),root);
            }else {
                root.setVisibility(View.GONE);
            }
        }else if("FOLDER".equals(mDynamic.getType())){
            root1.setVisibility(View.VISIBLE);
            final ShareFolderEntity folderEntity = new Gson().fromJson(mDynamic.getDetail(),ShareFolderEntity.class);
            View folder = LayoutInflater.from(this).inflate(R.layout.item_new_wenzhang_zhuan,null);
            folder.findViewById(R.id.tv_title).setVisibility(View.GONE);
            folder.findViewById(R.id.tv_content).setVisibility(View.GONE);
            ImageView cover = folder.findViewById(R.id.iv_cover);
            TextView mark = folder.findViewById(R.id.tv_mark);
            TextView name = folder.findViewById(R.id.tv_folder_name);
            TextView tag = folder.findViewById(R.id.tv_tag);
            int w = (int) (DensityUtil.getScreenWidth(this) - getResources().getDimension(R.dimen.x48));
            int h = (int) getResources().getDimension(R.dimen.y400);
            Glide.with(this)
                    .load(StringUtils.getUrl(this,folderEntity.getFolderCover(),w,h,false,true))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .bitmapTransform(new CropTransformation(this,w,h))
                    .into(cover);
            if(folderEntity.getFolderType().equals(FolderType.ZH.toString())){
                mark.setText("综合");
                mark.setBackgroundResource(R.drawable.shape_rect_zonghe);
            }else if(folderEntity.getFolderType().equals(FolderType.TJ.toString())){
                mark.setText("图集");
                mark.setBackgroundResource(R.drawable.shape_rect_tuji);
            }else if(folderEntity.getFolderType().equals(FolderType.MH.toString())){
                mark.setText("漫画");
                mark.setBackgroundResource(R.drawable.shape_rect_manhua);
            }else if(folderEntity.getFolderType().equals(FolderType.XS.toString())){
                mark.setText("小说");
                mark.setBackgroundResource(R.drawable.shape_rect_xiaoshuo);
            }
            name.setText(folderEntity.getFolderName());
            String tagStr = "";
            for(int i = 0;i < folderEntity.getFolderTags().size();i++){
                String tagTmp = folderEntity.getFolderTags().get(i);
                if(i == 0){
                    tagStr = tagTmp;
                }else {
                    tagStr += " · " + tagTmp;
                }
            }
            tag.setText(tagStr);

            ImageView avatar = folder.findViewById(R.id.iv_avatar);
            TextView userName1 = folder.findViewById(R.id.tv_user_name);
            TextView time1 = folder.findViewById(R.id.tv_time);
            size = (int) this.getResources().getDimension(R.dimen.x44);
            Glide.with(this)
                    .load(StringUtils.getUrl(this,folderEntity.getCreateUser().getHeadPath(),size,size,false,true))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(avatar);
            avatar.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    ViewUtils.toPersonal(DynamicActivity.this,folderEntity.getCreateUser().getUserId());
                }
            });
            userName1.setText(folderEntity.getCreateUser().getUserName());
            time1.setText("上一次更新:" + StringUtils.timeFormat(folderEntity.getUpdateTime()));
            cardRoot.addView(folder);

            root1.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(folderEntity.getFolderType().equals(FolderType.ZH.toString())){
                        NewFileCommonActivity.startActivity(DynamicActivity.this,FolderType.ZH.toString(),folderEntity.getFolderId(),folderEntity.getCreateUser().getUserId());
                    }else if(folderEntity.getFolderType().equals(FolderType.TJ.toString())){
                        NewFileCommonActivity.startActivity(DynamicActivity.this,FolderType.TJ.toString(),folderEntity.getFolderId(),folderEntity.getCreateUser().getUserId());
                    }else if(folderEntity.getFolderType().equals(FolderType.MH.toString())){
                        NewFileManHuaActivity.startActivity(DynamicActivity.this,FolderType.MH.toString(),folderEntity.getFolderId(),folderEntity.getCreateUser().getUserId());
                    }else if(folderEntity.getFolderType().equals(FolderType.XS.toString())){
                        NewFileXiaoshuoActivity.startActivity(DynamicActivity.this,FolderType.XS.toString(),folderEntity.getFolderId(),folderEntity.getCreateUser().getUserId());
                    }
                }
            });
        }else if("ARTICLE".equals(mDynamic.getType())){
            root1.setVisibility(View.VISIBLE);
            final ShareArticleEntity folderEntity = new Gson().fromJson(mDynamic.getDetail(),ShareArticleEntity.class);
            View article = LayoutInflater.from(this).inflate(R.layout.item_new_wenzhang_zhuan,null);
            TextView title = article.findViewById(R.id.tv_title);
            TextView articleContent = article.findViewById(R.id.tv_content);
            ImageView cover = article.findViewById(R.id.iv_cover);
            TextView mark = article.findViewById(R.id.tv_mark);
            article.findViewById(R.id.tv_folder_name).setVisibility(View.GONE);
            article.findViewById(R.id.tv_tag).setVisibility(View.GONE);
            int w = (int) (DensityUtil.getScreenWidth(this) - getResources().getDimension(R.dimen.x48));
            int h = (int) getResources().getDimension(R.dimen.y400);
            Glide.with(this)
                    .load(StringUtils.getUrl(this,folderEntity.getCover(),w,h,false,true))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .bitmapTransform(new CropTransformation(this,w,h))
                    .into(cover);
            mark.setText("文章");
            title.setText(folderEntity.getTitle());
            articleContent.setText(folderEntity.getContent());
            ImageView avatar = article.findViewById(R.id.iv_avatar);
            TextView userName1 = article.findViewById(R.id.tv_user_name);
            TextView time1 = article.findViewById(R.id.tv_time);
            size = (int) getResources().getDimension(R.dimen.x44);
            Glide.with(this)
                    .load(StringUtils.getUrl(this,folderEntity.getDocCreateUser().getHeadPath(),size,size,false,true))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(avatar);
            avatar.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    ViewUtils.toPersonal(DynamicActivity.this,folderEntity.getDocCreateUser().getUserId());
                }
            });
            userName1.setText(folderEntity.getDocCreateUser().getUserName());
            time1.setText(StringUtils.timeFormat(folderEntity.getCreateTime()));
            cardRoot.addView(article);
            root1.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if (!TextUtils.isEmpty(folderEntity.getDocId())) {
                        Intent i = new Intent(DynamicActivity.this, NewDocDetailActivity.class);
                        i.putExtra("uuid",folderEntity.getDocId());
                        startActivity(i);
                    }
                }
            });
        }else if("RETWEET".equals(mDynamic.getType())){
            root.setVisibility(View.VISIBLE);
            final RetweetEntity retweetEntity = new Gson().fromJson(mDynamic.getDetail(),RetweetEntity.class);
            if(!TextUtils.isEmpty(retweetEntity.getContent())){
                TextView tv = new TextView(this);
                tv.setTextColor(ContextCompat.getColor(this,R.color.black_1e1e1e));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.x30));
                tv.setLineSpacing(getResources().getDimension(R.dimen.y12),1);
                tv.setMaxLines(10);
                tv.setEllipsize(TextUtils.TruncateAt.END);
                String res = "<at_user user_id="+ retweetEntity.getCreateUserId() + ">" + retweetEntity.getCreateUserName() + ":</at_user>" +  retweetEntity.getContent();
                tv.setText(TagControl.getInstance().paresToSpann(this,res));
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        DynamicActivity.startActivity(DynamicActivity.this,retweetEntity.getOldDynamicId());
                    }
                });
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.topMargin = (int) getResources().getDimension(R.dimen.y24);
                lp.bottomMargin = (int) getResources().getDimension(R.dimen.y24);
                tv.setLayoutParams(lp);
                root.addView(tv);
                root.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        DynamicActivity.startActivity(DynamicActivity.this,retweetEntity.getOldDynamicId());
                    }
                });
            }
            if(retweetEntity.getImages() != null && retweetEntity.getImages().size() > 0){
                setImg(retweetEntity.getImages(),root);
            }else {
                if(TextUtils.isEmpty(retweetEntity.getContent())){
                    root.setVisibility(View.GONE);
                }
            }
            if(retweetEntity.getCoins() > 0){
                showHongbao = true;
            }
            showHongBao(v,true,retweetEntity.getCoins(),retweetEntity.getSurplus(),retweetEntity.getOldDynamicId(),retweetEntity.getCreateUserHead(),retweetEntity.getUsers());
        }
        //coins
        if(!showHongbao){
            showHongBao(v,false,mDynamic.getCoins(),mDynamic.getSurplus(),mDynamic.getId(),mDynamic.getCreateUser().getHeadPath(),mDynamic.getUsers());
        }

        //bottom
        TextView tvForward = v.findViewById(R.id.tv_forward_num);
        tvForward.setCompoundDrawablePadding(0);
        TextView tvComment = v.findViewById(R.id.tv_comment_num);
        tvComment.setCompoundDrawablePadding(0);
        tvTag = v.findViewById(R.id.tv_tag_num);
        tvTag.setCompoundDrawablePadding(0);
        tvTag.setSelected(mDynamic.isThumb());
        View fRoot = v.findViewById(R.id.fl_forward_root);
        View cRoot = v.findViewById(R.id.fl_comment_root);
        View tRoot = v.findViewById(R.id.fl_tag_root);
        if(mDynamic.isTag()){
            tRoot.setVisibility(View.VISIBLE);
            root.setBackgroundColor(Color.TRANSPARENT);
            root1.setBackgroundColor(Color.TRANSPARENT);
          //  docLabel.setBackgroundColor(Color.TRANSPARENT);
            if("ARTICLE".equals(mDynamic.getType()) || "FOLDER".equals(mDynamic.getType())){
                int py = (int) getResources().getDimension(R.dimen.y24);
                root1.setPadding(0,0,0,py);
            }
        }else {
            tRoot.setVisibility(View.INVISIBLE);
            root.setBackgroundColor(ContextCompat.getColor(this,R.color.cyan_eefdff));
            root1.setBackgroundColor(ContextCompat.getColor(this,R.color.cyan_eefdff));
           // docLabel.setBackgroundColor(ContextCompat.getColor(this,R.color.cyan_eefdff));
            if("ARTICLE".equals(mDynamic.getType()) || "FOLDER".equals(mDynamic.getType())){
                int py = (int) getResources().getDimension(R.dimen.y24);
                root1.setPadding(0,py,0,py);
            }
        }
        fRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                CreateForwardActivity.startActivityForResult(DynamicActivity.this,mDynamic);
            }
        });
        cRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                CreateCommentActivity.startActivity(DynamicActivity.this,mDynamic.getId(),false,"",false);
            }
        });
        tRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mPresenter.likeDynamic(mDynamic.getId(),mDynamic.isThumb());
            }
        });
        mAdapter.addHeaderView(v);

        //mCoin
        View coinV = LayoutInflater.from(this).inflate(R.layout.item_dynamic_coin,null);
        mCoin = coinV.findViewById(R.id.tv_got_coin);
        ImageView ivGive = coinV.findViewById(R.id.iv_give_coin);
        mTvSort = coinV.findViewById(R.id.tv_sort);
        mTvSort.setVisibility(View.GONE);
        mCoin.setText(getString(R.string.label_got_coin, mDynamic.getReward()));
        if(mDynamic.getCreateUser().getUserId().equals(PreferenceUtils.getUUid())){
            ivGive.setImageResource(R.drawable.btn_doc_givecoins_given_enabel);
            ivGive.setEnabled(false);
        }else {
            ivGive.setEnabled(true);
            ivGive.setImageResource(R.drawable.btn_give_coin);
            ivGive.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                    alertDialogUtil.createEditDialog(DynamicActivity.this, PreferenceUtils.getAuthorInfo().getCoin(),0);
                    alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                        @Override
                        public void CancelOnClick() {
                            alertDialogUtil.dismissDialog();
                        }

                        @Override
                        public void ConfirmOnClick() {
                            if(DialogUtils.checkLoginAndShowDlg(DynamicActivity.this)){
                                String content = alertDialogUtil.getEditTextContent();
                                if(!TextUtils.isEmpty(content) && Integer.valueOf(content) > 0){
                                    mPresenter.giveCoin(mDynamic.getId(),Integer.valueOf(content));
                                    alertDialogUtil.dismissDialog();
                                }else {
                                    ToastUtils.showShortToast(DynamicActivity.this,R.string.msg_input_err_coin);
                                }
                            }
                        }
                    });
                    alertDialogUtil.showDialog();
                }
            });
        }

        String[] mTitles = {"转发 " +  StringUtils.getNumberInLengthLimit(mDynamic.getRetweets(), 3),"评论 " + StringUtils.getNumberInLengthLimit(mDynamic.getComments(), 3)};
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        for(String title : mTitles){
            mTabEntities.add(new TabEntity(title, R.drawable.ic_personal_bag,R.drawable.ic_personal_bag));
        }
        CommonTabLayout pageIndicator = coinV.findViewById(R.id.indicator_person_data);
        pageIndicator.setTabData(mTabEntities);
        pageIndicator.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                commentType = position;
                if(commentType == 0){
                    mTvSort.setVisibility(View.GONE);
                }else {
                    mTvSort.setVisibility(View.VISIBLE);
                }
                if(mPreComments.size() == 0){
                    mPresenter.loadCommentsList(mDynamic.getId(),commentType,sortTime,0);
                }else {
                    ArrayList<CommentV2Entity> temp = mPreComments;
                    mPreComments = mAdapter.getList();
                    int pre = mPrePosition;
                    if(commentType == 0){
                        mAdapter.setShowFavorite(false);
                    }else {
                        mAdapter.setShowFavorite(true);
                    }
                    mPrePosition = ((LinearLayoutManager)mRvList.getRecyclerView().getLayoutManager()).findFirstVisibleItemPosition();
                    mAdapter.setList(temp);
                    if(pre != -1) mRvList.getRecyclerView().scrollToPosition(pre);
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        pageIndicator.setCurrentTab(1);
        mTvSort.setVisibility(View.VISIBLE);
        mTvSort.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                sortTime = !sortTime;
                mTvSort.setText(sortTime?"时间排序":"热门排序");
                mPresenter.loadCommentsList(mDynamic.getId(),commentType,sortTime,0);
            }
        });
        mAdapter.addHeaderView(coinV);
    }

    private void showHongBao(View v,boolean rt,final int coins, int surplus, final String id, final String icon, final int users){
        //coins
        View flHongbao = v.findViewById(R.id.fl_hongbao_root);
        TextView hongbaoCoin = v.findViewById(R.id.tv_hongbao_coin);
        TextView hongbaoLeftNum = v.findViewById(R.id.tv_left_num);
        TextView hongbaoDesc = v.findViewById(R.id.tv_desc);
        View rlHongbao = v.findViewById(R.id.rl_hongbao_root);
        if(coins > 0){
            if(rt){
                flHongbao.setBackgroundColor(ContextCompat.getColor(this,R.color.cyan_eefdff));
            }else {
                flHongbao.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
            }
            flHongbao.setVisibility(View.VISIBLE);
            hongbaoCoin.setText(String.format(this.getString(R.string.label_hongbao_total_coin),coins));
            if(surplus > 0){
                rlHongbao.setBackgroundColor(ContextCompat.getColor(this,R.color.orange_f2cc2c));
                hongbaoCoin.setTextColor(ContextCompat.getColor(this,R.color.orange_f2cc2c));
                hongbaoLeftNum.setText("剩余：" + surplus);
                hongbaoDesc.setText("转发即可领取红包");
            }else {
                rlHongbao.setBackgroundColor(ContextCompat.getColor(this,R.color.gray_d7d7d7));
                hongbaoCoin.setTextColor(ContextCompat.getColor(this,R.color.gray_d7d7d7));
                hongbaoLeftNum.setText("已被抢完");
                hongbaoDesc.setText(users + "领取了红包");
            }
            flHongbao.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    HongBaoListActivity.startActivity(DynamicActivity.this,id,icon,coins,users);
                }
            });
        }else {
            flHongbao.setOnClickListener(null);
            flHongbao.setVisibility(View.GONE);
        }
    }

    private void plusLabel(int position){
        if (!NetworkUtils.checkNetworkAndShowError(this)) {
            return;
        }
        if (DialogUtils.checkLoginAndShowDlg(this)) {
            final DocTagEntity tagBean = mTags.get(position);
            TagLikeEntity bean = new TagLikeEntity(mDynamic.getId(),tagBean.getId());
            createDialog();
            mPresenter.plusTag(tagBean.isLiked(),position,bean);
        }
    }

    private void setImg(ArrayList<Image> images,LinearLayout root){
        if(images.size() == 1){
            Image image = images.get(0);
            int[] wh;
            if(image.getW() > image.getH()){
                wh = BitmapUtils.getDocIconSizeFromW(image.getW(),image.getH(), (int) getResources().getDimension(R.dimen.x460));
            }else {
                wh = BitmapUtils.getDocIconSizeFromH(image.getW(),image.getH(), (int) getResources().getDimension(R.dimen.x460));
            }
            ImageView iv = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(wh[0],wh[1]);
            lp.bottomMargin = (int) getResources().getDimension(R.dimen.y24);
            iv.setLayoutParams(lp);
            Glide.with(this)
                    .load(StringUtils.getUrl(this,image.getPath(),wh[0],wh[1],false,true))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .into(iv);
            showImg(iv,images,0);
            root.addView(iv);
        }else if(images.size() == 2){
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(lp);
            int w = (int) ((DensityUtil.getScreenWidth(this) - getResources().getDimension(R.dimen.x54)))/2;
            for(int i = 0;i < images.size();i++){
                Image image = images.get(i);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(w,w);
                if(i == 0){
                    lp1.rightMargin = (int) getResources().getDimension(R.dimen.x6);
                }
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(lp1);
                Glide.with(this)
                        .load(StringUtils.getUrl(this,image.getPath(),w,w,false,true))
                        .error(R.drawable.bg_default_square)
                        .placeholder(R.drawable.bg_default_square)
                        .into(iv);
                layout.addView(iv);
                showImg(iv,images,i);
            }
            root.addView(layout);
        }else if(images.size() == 4){
            int w = (int) ((DensityUtil.getScreenWidth(this) - getResources().getDimension(R.dimen.x54)))/2;
            LinearLayout layout = null;
            for(int i = 0;i < images.size();i++){
                Image image = images.get(i);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(w,w);
                if(i == 0 || i == 2){
                    lp1.rightMargin = (int) getResources().getDimension(R.dimen.x6);
                    layout = new LinearLayout(this);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if(i == 2)lp.topMargin = (int) getResources().getDimension(R.dimen.y6);
                    layout.setLayoutParams(lp);
                }
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(lp1);
                Glide.with(this)
                        .load(StringUtils.getUrl(this,image.getPath(),w,w,false,true))
                        .error(R.drawable.bg_default_square)
                        .placeholder(R.drawable.bg_default_square)
                        .into(iv);
                layout.addView(iv);
                showImg(iv,images,i);
                if(i == 1 || i == 3){
                    root.addView(layout);
                }
            }
        }else {
            int w = (int) ((DensityUtil.getScreenWidth(this) - getResources().getDimension(R.dimen.x60)))/3;
            LinearLayout layout = null;
            for(int i = 0;i < images.size();i++){
                Image image = images.get(i);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(w,w);
                if(i == 0 || i == 3 || i == 6){
                    layout = new LinearLayout(this);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layout.setLayoutParams(lp);
                }
                if(i % 3 != 2){
                    lp1.rightMargin = (int) getResources().getDimension(R.dimen.x6);
                }
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(lp1);
                Glide.with(this)
                        .load(StringUtils.getUrl(this,image.getPath(),w,w,false,true))
                        .error(R.drawable.bg_default_square)
                        .placeholder(R.drawable.bg_default_square)
                        .into(iv);
                layout.addView(iv);
                showImg(iv,images,i);
                if(i % 3 == 2 || images.size() == i + 1){
                    root.addView(layout);
                }
            }
        }
    }

    private void showImg(ImageView iv, final ArrayList<Image> list, final int position){
        iv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent intent = new Intent(DynamicActivity.this, ImageBigSelectActivity.class);
                intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, list);
                intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX, position);
                startActivity(intent);
            }
        });
    }

    private void showMenu(){
        ArrayList<MenuItem> items = new ArrayList<>();
        if(mDynamic.getCreateUser().getUserId().equals(PreferenceUtils.getUUid())){
            MenuItem item = new MenuItem(0,"管理标签");
            items.add(item);
            item = new MenuItem(1,"删除");
            items.add(item);
        }else {

            MenuItem item = new MenuItem(2, mDynamic.isFollow() ? "取消关注" : "关注");
            items.add(item);
            item = new MenuItem(3, mDynamic.isCollect() ? "取消收藏" : "收藏");
            items.add(item);
            item = new MenuItem(4,"举报");
            items.add(item);
//            item = new MenuItem(5,"分享");
//            items.add(item);
        }
        fragment.setShowTop(false);
        fragment.setMenuItems(items);
        fragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        fragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if (itemId == 0) {
                    Intent i = new Intent(DynamicActivity.this,TagControlActivity.class);
                    i.putParcelableArrayListExtra("tags",mTags);
                    i.putExtra(UUID,mDynamic.getId());
                    startActivityForResult(i,REQ_DELETE_TAG);
                } else if(itemId == 1){
                    mPresenter.deleteDynamic(mDynamic.getId(),mDynamic.getType());
                } else if(itemId == 2){
                    mPresenter.followUser(mDynamic.getCreateUser().getUserId(),mDynamic.isFollow());
                } else if(itemId == 3){
                    mPresenter.favoriteDynamic(mDynamic.getId(),mDynamic.isCollect());
                } else if(itemId == 4){
                    Intent intent = new Intent(DynamicActivity.this, JuBaoActivity.class);
                    intent.putExtra(JuBaoActivity.EXTRA_NAME, mDynamic.getCreateUser().getUserName());
                    intent.putExtra(JuBaoActivity.EXTRA_CONTENT, mDynamic.getText());
                    intent.putExtra(JuBaoActivity.EXTRA_TYPE, 4);
                    intent.putExtra(JuBaoActivity.UUID,mDynamic.getId());
                    intent.putExtra(JuBaoActivity.EXTRA_TARGET, "DYNAMIC");
                    startActivity(intent);
                } else if(itemId == 5){
                    //TODO 分享 目前没有
                }
            }
        });
        fragment.show(getSupportFragmentManager(),"DynamicActivity");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_DELETE_TAG && resultCode == RESULT_OK){
            ArrayList<DocTagEntity> entities = data.getParcelableArrayListExtra("tags");
            if(entities != null){
                mTags = entities;
            }
        }
    }

    private void showCommentMenu(final CommentV2Entity bean, final int position){
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item;
        item = new MenuItem(0,getString(R.string.label_reply));
        items.add(item);

        if(mAdapter.isShowFavorite()){
            item = new MenuItem(5,"点赞");
            items.add(item);
        }

        item = new MenuItem(1,getString(R.string.label_copy_dust));
        items.add(item);

        item = new MenuItem(2,getString(R.string.label_jubao));
        items.add(item);

        if(TextUtils.equals(PreferenceUtils.getUUid(), bean.getCreateUser().getUserId()) ){
            item = new MenuItem(3,getString(R.string.label_delete));
            items.add(item);
        }else if( TextUtils.equals(PreferenceUtils.getUUid(), mDynamic.getCreateUser().getUserId())){
            item = new MenuItem(4,getString(R.string.label_delete));
            items.add(item);
        }

        fragment.setShowTop(true);
        fragment.setTopContent(bean.getContent());
        fragment.setMenuItems(items);
        fragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        fragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if (itemId == 0) {
                    CreateCommentActivity.startActivity(DynamicActivity.this,bean.getCommentId(),true,"",false);
                } else if (itemId == 2) {
                    Intent intent = new Intent(DynamicActivity.this, JuBaoActivity.class);
                    intent.putExtra(JuBaoActivity.EXTRA_NAME, bean.getCreateUser().getUserName());
                    intent.putExtra(JuBaoActivity.EXTRA_CONTENT, bean.getContent());
                    intent.putExtra(JuBaoActivity.EXTRA_TYPE, 4);
                    intent.putExtra(JuBaoActivity.UUID,bean.getCommentId());
                    intent.putExtra(JuBaoActivity.EXTRA_TARGET, "COMMENT");
                    startActivity(intent);
                } else if (itemId == 3) {
                    mPresenter.deleteComment(mDynamic.getId(),bean.getCommentId(),position);
                }else if(itemId == 1){
                    String content = bean.getContent();
                    ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("回复内容", content);
                    cmb.setPrimaryClip(mClipData);
                    ToastUtils.showShortToast(DynamicActivity.this, DynamicActivity.this.getString(R.string.label_level_copy_success));
                }else if(itemId == 4){
                    mPresenter.deleteComment(mDynamic.getId(),bean.getCommentId(),position);
                }else if(itemId == 5){
                    mPresenter.favoriteComment(mDynamic.getId(),bean.getCommentId(),bean.isLike(),position);
                }
            }
        });
        fragment.show(getSupportFragmentManager(),"DynamicComment");
    }

    @Override
    public void onFailure(int code, String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void onLoadTagsSuccess(ArrayList<DocTagEntity> tagEntities) {
        mTags = tagEntities;
       // if(docLabelAdapter != null) docLabelAdapter.setData(mTags,true);
    }

    @Override
    public void onDeleteDynamicSuccess() {
        showToast("删除动态成功");
        finish();
    }

    @Override
    public void onFollowUserSuccess(boolean isFollow) {
        if(isFollow){
            showToast("关注成功");
        }else {
            showToast("取消关注成功");
        }
        mDynamic.setFollow(isFollow);
    }

    @Override
    public void onFavoriteDynamicSuccess(boolean isFavorite) {
        if(isFavorite){
            showToast("收藏成功");
        }else {
            showToast("取消收藏成功");
        }
       mDynamic.setCollect(isFavorite);
    }

    @Override
    public void onSendTagSuccess(String s,String name) {
        finalizeDialog();
    }

    @Override
    public void onGiveCoinSuccess(int coins) {
        showToast(R.string.label_give_coin_success);
        mDynamic.setReward(mDynamic.getReward() + coins);
        mCoin.setText(getString(R.string.label_got_coin, mDynamic.getReward()));
    }

    @Override
    public void onLoadCommentsSuccess(ArrayList<CommentV2Entity> commentV2Entities,boolean isPull) {
        mIsLoading = false;
        mRvList.setComplete();
        mRvList.setLoadMoreEnabled(true);
        if(isPull){
            mAdapter.setList(commentV2Entities);
        }else {
            mAdapter.addList(commentV2Entities);
        }
    }

    @Override
    public void onDeleteCommentSuccess(int position) {
        showToast("删除评论成功");
        mAdapter.getList().remove(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void favoriteCommentSuccess(boolean isFavorite,int position) {
        if(isFavorite){
            showToast("点赞成功");
        }else {
            showToast("取消点赞成功");
        }
        mAdapter.getList().get(position).setLike(isFavorite);
        if(isFavorite){
            mAdapter.getList().get(position).setLikes( mAdapter.getList().get(position).getLikes() + 1);
        }else {
            mAdapter.getList().get(position).setLikes( mAdapter.getList().get(position).getLikes() - 1);
        }
        mAdapter.notifyItemChanged(position + mAdapter.getHeaderLayoutCount());
    }

    @Override
    public void onPlusTagSuccess(int position, boolean isLike) {
        finalizeDialog();
        DocTagEntity tagBean = mTags.get(position);
        mTags.remove(position);
        tagBean.setLiked(isLike);
        if(isLike){
            tagBean.setLikes(tagBean.getLikes() + 1);
            mTags.add(position, tagBean);
        }else {
            tagBean.setLikes(tagBean.getLikes() - 1);
            if (tagBean.getLikes() > 0) {
                mTags.add(position, tagBean);
            }
        }
       // docLabel.notifyAdapter();
    }

    @Override
    public void onLoadDynamicSuccess(NewDynamicEntity entity) {
        if(entity == null) {
            finish();
            return;
        }
        mDynamic = entity;
        init();
    }

    @Override
    public void onLikeDynamicSuccess(boolean  isLike) {
        tvTag.setSelected(isLike);
        showToast("操作成功");
    }
}
