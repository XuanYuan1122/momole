package com.moemoe.lalala.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerCommentSecListComponent;
import com.moemoe.lalala.di.modules.CommentSecListModule;
import com.moemoe.lalala.di.modules.DynamicModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.CommentV2Entity;
import com.moemoe.lalala.model.entity.CommentV2SecEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.presenter.CommentSecListContract;
import com.moemoe.lalala.presenter.CommentSecListPresenter;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.LevelSpan;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.utils.tag.TagControl;
import com.moemoe.lalala.view.adapter.CommentSecListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by yi on 2017/9/22.
 */

public class CommentSecListActivity extends BaseAppCompatActivity implements CommentSecListContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_left_menu)
    TextView mTvFrom;
    @BindView(R.id.tv_to_comment)
    TextView mTvToComment;
    @BindView(R.id.rv_list)
    PullAndLoadView mRvList;
    @Inject
    CommentSecListPresenter mPresenter;

    private TextView mTvSort;

    private CommentSecListAdapter mAdapter;
    private boolean mIsLoading;
    private CommentV2Entity mComment;
    private boolean sortTime;
    private String mParentId;
    private TextView userLike;
    private BottomMenuFragment fragment;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_comment_sec_list;
    }

    public static void startActivity(Context context, CommentV2Entity comment, String parentId){
        Intent i = new Intent(context,CommentSecListActivity.class);
        i.putExtra("comment",comment);
        i.putExtra("parentId",parentId);
        context.startActivity(i);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerCommentSecListComponent.builder()
                .commentSecListModule(new CommentSecListModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mComment = getIntent().getParcelableExtra("comment");
        mParentId = getIntent().getStringExtra("parentId");
        if(mComment == null){
            finish();
            return;
        }
        fragment = new BottomMenuFragment();
        mRvList.setLoadMoreEnabled(false);
        mRvList.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mRvList.getSwipeRefreshLayout().setEnabled(false);
        mAdapter = new CommentSecListAdapter();
        mRvList.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showCommentMenu(mAdapter.getItem(position),position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvList.setLayoutManager(layoutManager);
        mRvList.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                mPresenter.loadCommentsList(mComment.getCommentId(),sortTime,mAdapter.getList().size());
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
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
        mTvToComment.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                CreateCommentActivity.startActivity(CommentSecListActivity.this,mComment.getCommentId(),true,"");
            }
        });
        setHead();
        mPresenter.loadCommentsList(mComment.getCommentId(),sortTime,0);
    }

    private void showCommentMenu(final CommentV2SecEntity bean, final int position){
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item;
        item = new MenuItem(0,getString(R.string.label_reply));
        items.add(item);

        item = new MenuItem(1,getString(R.string.label_copy_dust));
        items.add(item);

        item = new MenuItem(2,getString(R.string.label_jubao));
        items.add(item);

        if(TextUtils.equals(PreferenceUtils.getUUid(), bean.getCreateUser().getUserId()) ){
            item = new MenuItem(3,getString(R.string.label_delete));
            items.add(item);
        }else if( TextUtils.equals(PreferenceUtils.getUUid(), mComment.getCreateUser().getUserId())){
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
                    CreateCommentActivity.startActivity(CommentSecListActivity.this,bean.getCommentId(),true,bean.getCreateUser().getUserId());
                } else if (itemId == 2) {
                    Intent intent = new Intent(CommentSecListActivity.this, JuBaoActivity.class);
                    intent.putExtra(JuBaoActivity.EXTRA_NAME, bean.getCreateUser().getUserName());
                    intent.putExtra(JuBaoActivity.EXTRA_CONTENT, bean.getContent());
                    intent.putExtra(JuBaoActivity.EXTRA_TYPE, 5);
                    intent.putExtra(JuBaoActivity.EXTRA_PARENT_ID, mParentId);
                    intent.putExtra(JuBaoActivity.UUID,bean.getCommentId());
                    intent.putExtra(JuBaoActivity.EXTRA_TARGET, "COMMENT");
                    startActivity(intent);
                } else if (itemId == 3) {
                    mPresenter.deleteComment(mComment.getCommentId(),bean.getCommentId(),mParentId,position);
                }else if(itemId == 1){
                    String content = bean.getContent();
                    ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("回复内容", content);
                    cmb.setPrimaryClip(mClipData);
                    ToastUtils.showShortToast(CommentSecListActivity.this, getString(R.string.label_level_copy_success));
                }else if(itemId == 4){
                    mPresenter.deleteComment(mComment.getCommentId(),bean.getCommentId(),mParentId,position);
                }
            }
        });
        fragment.show(getSupportFragmentManager(),"DynamicComment");
    }

    public void favoriteComment(String id,boolean isFavorite,int position){
        mPresenter.favoriteComment(mComment.getCommentId(),id,isFavorite,position);
    }

    private void setHead(){
        View v = LayoutInflater.from(this).inflate(R.layout.item_comment_sec_top,null);
        TextView userName = (TextView) v.findViewById(R.id.tv_name);
        TextView userLevel = (TextView) v.findViewById(R.id.tv_level);
        userLike = (TextView) v.findViewById(R.id.tv_favorite);
        TextView userComment = (TextView) v.findViewById(R.id.tv_comment);
        LinearLayout imgRoot = (LinearLayout) v.findViewById(R.id.ll_comment_img);
        TextView userTime = (TextView) v.findViewById(R.id.tv_comment_time);
        int size = (int) getResources().getDimension(R.dimen.x72);
        Glide.with(this)
                .load(StringUtils.getUrl(this,mComment.getCreateUser().getHeadPath(),size,size,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(this))
                .into((ImageView) $(R.id.iv_avatar));
        userName.setText(mComment.getCreateUser().getUserName());
        LevelSpan levelSpan = new LevelSpan(ContextCompat.getColor(this,R.color.white),getResources().getDimension(R.dimen.x12));
        final String content = "LV" + mComment.getCreateUser().getLevel();
        String colorStr = "LV";
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(levelSpan, content.indexOf(colorStr), content.indexOf(colorStr) + colorStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        userLevel.setText(style);
        float radius2 = getResources().getDimension(R.dimen.y4);
        float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
        RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
        ShapeDrawable shapeDrawable2 = new ShapeDrawable();
        shapeDrawable2.setShape(roundRectShape2);
        shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(mComment.getCreateUser().getLevelColor(), ContextCompat.getColor(this, R.color.main_cyan)));
        userLevel.setBackgroundDrawable(shapeDrawable2);
        userLike.setSelected(mComment.isLike());
        userLike.setText(mComment.getLikes() + "");
        userLike.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mPresenter.favoriteComment(mParentId,mComment.getCommentId(),mComment.isLike(),-1);
            }
        });
        userComment.setText(TagControl.getInstance().paresToSpann(this,mComment.getContent()));
        userComment.setMovementMethod(LinkMovementMethod.getInstance());
        if(mComment.getImages().size() > 0){
            imgRoot.setVisibility(View.VISIBLE);
            imgRoot.removeAllViews();
            for (int i = 0;i < mComment.getImages().size();i++){
                final int pos = i;
                Image image = mComment.getImages().get(i);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.topMargin = (int) this.getResources().getDimension(R.dimen.y10);
                if(FileUtil.isGif(image.getPath())){
                    ImageView imageView = new ImageView(this);
                    setGif(image, imageView,params);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CommentSecListActivity.this, ImageBigSelectActivity.class);
                            intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, mComment.getImages());
                            intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                                    pos);
                            startActivity(intent);
                        }
                    });
                    ((LinearLayout)$(R.id.ll_comment_img)).addView(imageView,((LinearLayout)$(R.id.ll_comment_img)).getChildCount(),params);
                }else {
                    ImageView imageView = new ImageView(this);
                    setImage(image, imageView,params);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CommentSecListActivity.this, ImageBigSelectActivity.class);
                            intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, mComment.getImages());
                            intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                                    pos);
                            startActivity(intent);
                        }
                    });
                    ((LinearLayout)$(R.id.ll_comment_img)).addView(imageView,((LinearLayout)$(R.id.ll_comment_img)).getChildCount(),params);
                }
            }
        }else {
            imgRoot.setVisibility(View.GONE);
        }
        userTime.setText(StringUtils.timeFormate(mComment.getCreateTime()));
        v.findViewById(R.id.ll_comment_root).setVisibility(View.GONE);

        mTvSort = (TextView) v.findViewById(R.id.tv_sort);
        mTvSort.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                sortTime = !sortTime;
                mTvSort.setText(sortTime?"时间排序":"热门排序");
                mPresenter.loadCommentsList(mComment.getCommentId(),sortTime,0);
            }
        });
    }

    private void setGif(Image image, ImageView gifImageView, LinearLayout.LayoutParams params){
        final int[] wh = BitmapUtils.getDocIconSizeFromW(image.getW() * 2, image.getH() * 2, (int) (DensityUtil.getScreenWidth(this) - this.getResources().getDimension(R.dimen.x168)));
        params.width = wh[0];
        params.height = wh[1];
        Glide.with(this)
                .load(ApiService.URL_QINIU + image.getPath())
                .asGif()
                .override(wh[0], wh[1])
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .into(gifImageView);
    }

    private void setImage(Image image, final ImageView imageView, LinearLayout.LayoutParams params){
        final int[] wh = BitmapUtils.getDocIconSizeFromW(image.getW() * 2, image.getH() * 2, (int) (DensityUtil.getScreenWidth(this) - this.getResources().getDimension(R.dimen.x168)));
        params.width = wh[0];
        params.height = wh[1];
        Glide.with(this)
                .load(StringUtils.getUrl(this,ApiService.URL_QINIU + image.getPath(), wh[0], wh[1], true, true))
                .override(wh[0], wh[1])
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .into(imageView);
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
        mTvFrom.setText("评论");
        mTvFrom.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
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
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void onLoadCommentsSuccess(ArrayList<CommentV2SecEntity> commentV2Entities, boolean isPull) {
        mIsLoading = false;
        mRvList.setComplete();
        if(commentV2Entities.size() >= ApiService.LENGHT){
            mRvList.setLoadMoreEnabled(true);
        }else {
            mRvList.setLoadMoreEnabled(false);
        }
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
    public void favoriteCommentSuccess(boolean isFavorite, int position) {
        if(position == -1) {
            mComment.setLike(isFavorite);
            userLike.setSelected(isFavorite);
            if(isFavorite){
                mComment.setLikes(mComment.getLikes() + 1);
            }else {
                mComment.setLikes(mComment.getLikes() - 1);
            }
            userLike.setText(mComment.getLikes() + "");
        }else {
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
    }
}
