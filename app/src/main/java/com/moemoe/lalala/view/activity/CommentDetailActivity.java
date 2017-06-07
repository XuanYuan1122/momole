package com.moemoe.lalala.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerCommentDetailComponent;
import com.moemoe.lalala.di.modules.CommentDetailModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.CommentDetailEntity;
import com.moemoe.lalala.model.entity.CommentDetailRqEntity;
import com.moemoe.lalala.model.entity.CommentSendEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewCommentEntity;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.presenter.CommentDetailContract;
import com.moemoe.lalala.presenter.CommentDetailPresenter;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.GlideCircleTransform;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.adapter.SelectImgAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by yi on 2017/2/13.
 */

public class CommentDetailActivity extends BaseAppCompatActivity implements CommentDetailContract.View{

    @BindView(R.id.iv_back)
    View mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvToolbarTitle;
    @BindView(R.id.ll_doc)
    View mDocRoot;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_del_or_report)
    TextView mTvDelOrReport;
    @BindView(R.id.iv_comment_creator)
    ImageView mIvCreator;
    @BindView(R.id.tv_comment_creator_name)
    TextView mTvCreatorName;
    @BindView(R.id.tv_comment_time)
    TextView mTvTime;
    @BindView(R.id.tv_comment)
    TextView mTvContent;
    @BindView(R.id.rl_level_bg)
    View mIvLevelColor;
    @BindView(R.id.tv_level)
    TextView mTvLevel;
    @BindView(R.id.tv_floor)
    TextView mFloor;
    @BindView(R.id.ll_comment_img)
    LinearLayout llImg;
    @BindView(R.id.tv_huizhang_1)
    TextView tvHuiZhang1;
    @BindView(R.id.tv_huizhang_2)
    TextView tvHuiZhang2;
    @BindView(R.id.tv_huizhang_3)
    TextView tvHuiZhang3;
    @BindView(R.id.fl_huizhang_1)
    View rlHuiZhang1;
    @BindView(R.id.fl_huizhang_2)
    View rlHuiZhang2;
    @BindView(R.id.fl_huizhang_3)
    View rlHuiZhang3;
    @BindView(R.id.comment_root)
    View mCommentRoot;
    @BindView(R.id.edt_comment_input)
    EditText mEdtCommentInput;
    @BindView(R.id.iv_comment_send)
    View mTvSendComment;
    @BindView(R.id.rv_img)
    RecyclerView mRvComment;
    @BindView(R.id.iv_add_img)
    ImageView mIvAddImg;
    View[] huiZhangRoots;
    TextView[] huiZhangTexts;
    @Inject
    CommentDetailPresenter mPresenter;
    private String mSchema;
    private String mCommentId;
    private String mDocId;
    private int mType;
    private NewCommentEntity mCommentEntity;
    private SelectImgAdapter mSelectAdapter;
    private ArrayList<String> mIconPaths = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.ac_comment_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerCommentDetailComponent.builder()
                .commentDetailModule(new CommentDetailModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mSchema = getIntent().getStringExtra("schema");
        if(TextUtils.isEmpty(mSchema)){
            finish();
            return;
        }
        mTvToolbarTitle.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTvToolbarTitle.setText("回复");
        mCommentId = getIntent().getStringExtra("commentId");
        mDocId = mSchema.substring(mSchema.lastIndexOf("?") + 1);
        mType = -1;
        llImg.setVisibility(View.GONE);
        huiZhangRoots = new View[]{rlHuiZhang1,rlHuiZhang2,rlHuiZhang3};
        huiZhangTexts = new TextView[]{tvHuiZhang1,tvHuiZhang2,tvHuiZhang3};
        mFloor.setVisibility(View.GONE);
        mSelectAdapter = new SelectImgAdapter(this);
        LinearLayoutManager selectRvL = new LinearLayoutManager(this);
        selectRvL.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvComment.setLayoutManager(selectRvL);
        mRvComment.setAdapter(mSelectAdapter);
        mRvComment.setVisibility(View.GONE);
        mTvSendComment.setEnabled(false);
        CommentDetailRqEntity entity = new CommentDetailRqEntity(mCommentId,mDocId);
        mPresenter.requestCommentDetail(entity);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mDocRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Uri uri = Uri.parse(mSchema);
                IntentUtils.toActivityFromUri(CommentDetailActivity.this, uri,v);
            }
        });
        mTvDelOrReport.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(mType == 1 || mType == 2){
                    if (mCommentEntity != null){
                        createDialog();
                        if(mType == 1){
                            Intent intent = new Intent(CommentDetailActivity.this, JuBaoActivity.class);
                            intent.putExtra(JuBaoActivity.EXTRA_NAME, mCommentEntity.getFromUserName());
                            intent.putExtra(JuBaoActivity.EXTRA_CONTENT, mCommentEntity.getContent());
                            intent.putExtra(JuBaoActivity.UUID,mCommentEntity.getId());
                            intent.putExtra(JuBaoActivity.EXTRA_TYPE,2);
                            intent.putExtra(JuBaoActivity.EXTRA_DOC_ID,mDocId);
                            intent.putExtra(JuBaoActivity.EXTRA_TARGET, REPORT.DOC_COMMENT.toString());
                            startActivityForResult(intent,6666);
                        }else {
                            mPresenter.deleteComment(mCommentEntity);
                        }
                    }
                }else if(mType == 3){
                    if(mCommentEntity != null){
                        Intent intent = new Intent(CommentDetailActivity.this, JuBaoActivity.class);
                        intent.putExtra(JuBaoActivity.EXTRA_NAME, mCommentEntity.getFromUserName());
                        intent.putExtra(JuBaoActivity.EXTRA_CONTENT, mCommentEntity.getContent());
                        intent.putExtra(JuBaoActivity.UUID,mCommentEntity.getId());
                        intent.putExtra(JuBaoActivity.EXTRA_TARGET, REPORT.DOC_COMMENT.toString());
                        startActivity(intent);
                    }
                }
            }
        });
        mEdtCommentInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    mTvSendComment.setEnabled(false);
                } else {
                    mTvSendComment.setEnabled(true);
                }

            }
        });
        mSelectAdapter.setOnItemClickListener(new SelectImgAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position == mIconPaths.size()){
                    choosePhoto();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onAllDelete() {
                mRvComment.setVisibility(View.GONE);
                mTvSendComment.setEnabled(false);
            }
        });
        mIvAddImg.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                choosePhoto();
            }
        });
        mTvSendComment.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                sendComment();
            }
        });
    }


    private void sendComment() {
        if(!NetworkUtils.checkNetworkAndShowError(this)){
            return;
        }
        if (DialogUtils.checkLoginAndShowDlg(this) && mCommentEntity != null) {
            String content = mEdtCommentInput.getText().toString();
            if(TextUtils.isEmpty(content)){
                showToast(R.string.msg_doc_comment_not_empty);
                return;
            }
            if(TextUtils.isEmpty(content) && mIconPaths.size() == 0){
                showToast(R.string.msg_doc_comment_not_empty);
                return;
            }
            final ArrayList<Image> images = BitmapUtils.handleUploadImage(mIconPaths);
            ArrayList<String> paths = new ArrayList<>();
            if(images != null && images.size() > 0){
                for(int i = 0; i < images.size(); i++){
                    paths.add(images.get(i).getPath());
                }
            }
            SoftKeyboardUtils.dismissSoftKeyboard(this);
            createDialog();
            CommentSendEntity bean = new CommentSendEntity(content,mDocId,null,mCommentEntity.getFromUserId());
            mPresenter.sendComment(paths,bean);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 6666){
            if(resultCode == RESULT_OK && data != null){
                finalizeDialog();
                finish();
            }
        }else {
            DialogUtils.handleImgChooseResult(this, requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

                @Override
                public void onPhotoGet(ArrayList<String> photoPaths, boolean override) {
                    if (override) {
                        mIconPaths = photoPaths;
                    } else {
                        mIconPaths.addAll(photoPaths);
                    }
                    onGetPhotos();
                }
            });
        }
    }

    private void onGetPhotos() {
        if (mIconPaths.size() == 0) {
            // 取消选择了所有图
            mRvComment.setVisibility(View.GONE);
            String content = mEdtCommentInput.getText().toString();
            if(TextUtils.isEmpty(content)){
                mTvSendComment.setEnabled(false);
            }
        }else if(mIconPaths.size() <= 9){
            mTvSendComment.setEnabled(true);
            mRvComment.setVisibility(View.VISIBLE);
            mSelectAdapter.setData(mIconPaths);
        }
    }

    private void choosePhoto() {
        if (mIconPaths.size() < 9) {
            if (AppSetting.IS_EDITOR_VERSION) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                try {
                    startActivityForResult(intent, 2333);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    DialogUtils.createImgChooseDlg(this, null, this, mIconPaths, 9).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            showToast(R.string.msg_create_doc_9_jpg);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onGetDetailSuccess(CommentDetailEntity entity) {
        if(TextUtils.isEmpty(entity.getDocTitle())){
            mTvTitle.setVisibility(View.GONE);
        }else {
            mTvTitle.setVisibility(View.VISIBLE);
            mTvTitle.setText(entity.getDocTitle());
        }
        if(entity.isMyCreate() || entity.getDocComment().getFromUserId().equals(PreferenceUtils.getUUid())){
            mTvDelOrReport.setText("删除评论");
            if(entity.isMyCreate()){
                mType = 1;
            }else {
                mType = 2;
            }
        }else {
            mTvDelOrReport.setText("举报评论");
            mType = 3;
        }
        mCommentEntity = entity.getDocComment();
        Glide.with(this)
                .load(StringUtils.getUrl(this, ApiService.URL_QINIU + mCommentEntity.getFromUserIcon().getPath(), DensityUtil.dip2px(this,35), DensityUtil.dip2px(this,35), false, false))
                .override(DensityUtil.dip2px(this,35), DensityUtil.dip2px(this,35))
                .placeholder(R.drawable.bg_default_circle)
                .error(R.drawable.bg_default_circle)
                .transform(new GlideCircleTransform(this))
                .into(mIvCreator);
        mTvCreatorName.setText(mCommentEntity.getFromUserName());
        mTvTime.setText(StringUtils.timeFormate(mCommentEntity.getCreateTime()));
        if(mCommentEntity.isDeleteFlag()){
            mTvContent.setText(getString(R.string.label_comment_already));
            llImg.setVisibility(View.GONE);
        }else {
            String comm;
            if (!TextUtils.isEmpty(mCommentEntity.getToUserName()) ) {
                comm = "回复 " + (TextUtils.isEmpty(mCommentEntity.getToUserName()) ? "" :mCommentEntity.getToUserName()) + ": "
                        + mCommentEntity.getContent();
            } else {
                comm = mCommentEntity.getContent();
            }
            mTvContent.setText(StringUtils.getUrlClickableText(this, comm));
            mTvContent.setMovementMethod(LinkMovementMethod.getInstance());
            if(mCommentEntity.getImages().size() > 0 && !mCommentEntity.isNewDeleteFlag()){
                llImg.setVisibility(View.VISIBLE);
                llImg.removeAllViews();
                for (int i = 0;i < mCommentEntity.getImages().size();i++){
                    final int pos = i;
                    Image image = mCommentEntity.getImages().get(i);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.topMargin = DensityUtil.dip2px(this,5);
                    if(FileUtil.isGif(image.getPath())){
                        ImageView imageView = new ImageView(this);
                        setGif(image, imageView,params);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(CommentDetailActivity.this, ImageBigSelectActivity.class);
                                intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, mCommentEntity.getImages());
                                intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                                        pos);
                                // 以后可选择 有返回数据
                                startActivity(intent);
                            }
                        });
                        llImg.addView(imageView,llImg.getChildCount(),params);
                    }else {
                        ImageView imageView = new ImageView(this);
                        setImage(image, imageView,params);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(CommentDetailActivity.this, ImageBigSelectActivity.class);
                                intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, mCommentEntity.getImages());
                                intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                                        pos);
                                // 以后可选择 有返回数据
                                startActivity(intent);
                            }
                        });
                        llImg.addView(imageView,llImg.getChildCount(),params);
                    }
                }
            }else {
                llImg.setVisibility(View.GONE);
            }
        }
        mTvLevel.setText(String.valueOf(mCommentEntity.getFromUserLevel()));
        int radius1 = DensityUtil.dip2px(this,5);
        float[] outerR1 = new float[] { radius1, radius1, radius1, radius1, radius1, radius1, radius1, radius1};
        RoundRectShape roundRectShape1 = new RoundRectShape(outerR1, null, null);
        ShapeDrawable shapeDrawable1 = new ShapeDrawable();
        shapeDrawable1.setShape(roundRectShape1);
        shapeDrawable1.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable1.getPaint().setColor(StringUtils.readColorStr(mCommentEntity.getFromUserLevelColor(), ContextCompat.getColor(this, R.color.main_cyan)));
        mIvLevelColor.setBackgroundDrawable(shapeDrawable1);
        Observable.range(0,3)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer i) {
                        huiZhangTexts[i].setVisibility(View.INVISIBLE);
                        huiZhangRoots[i].setVisibility(View.INVISIBLE);
                    }
                });
        if(mCommentEntity.getBadgeList().size() > 0){
            int size = 3;
            if(mCommentEntity.getBadgeList().size() < 3){
                size = mCommentEntity.getBadgeList().size();
            }
            for (int i = 0;i < size;i++){
                huiZhangTexts[i].setVisibility(View.VISIBLE);
                huiZhangRoots[i].setVisibility(View.VISIBLE);
                BadgeEntity badgeEntity = mCommentEntity.getBadgeList().get(i);
                TextView tv = huiZhangTexts[i];
                tv.setText(badgeEntity.getTitle());
                tv.setText(badgeEntity.getTitle());
                tv.setBackgroundResource(R.drawable.bg_badge_cover);
                int px = DensityUtil.dip2px(this,4);
                tv.setPadding(px,0,px,0);
                int radius2 = DensityUtil.dip2px(this,2);
                float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
                RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
                ShapeDrawable shapeDrawable2 = new ShapeDrawable();
                shapeDrawable2.setShape(roundRectShape2);
                shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
                shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(badgeEntity.getColor(), ContextCompat.getColor(this, R.color.main_cyan)));
                huiZhangRoots[i].setBackgroundDrawable(shapeDrawable2);
            }
        }
        mIvCreator.setTag(R.id.id_creator_uuid, mCommentEntity.getFromUserId());
        if(!mCommentEntity.isNewDeleteFlag()){
            mTvContent.setTextColor(ContextCompat.getColor(this,R.color.gray_595e64));
            mCommentRoot.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String content = mCommentEntity.getContent();
                    ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("回复内容", content);
                    cmb.setPrimaryClip(mClipData);
                    ToastUtils.showShortToast(CommentDetailActivity.this, getString(R.string.label_level_copy_success));
                    return false;
                }
            });
        }else {
            mTvContent.setTextColor(ContextCompat.getColor(this,R.color.gray_d7d7d7));
        }
        mIvCreator.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String uuid =  mCommentEntity.getFromUserId();
                if (!TextUtils.isEmpty(uuid) && !uuid.equals(PreferenceUtils.getUUid())) {
                    Intent i = new Intent(CommentDetailActivity.this,NewPersonalActivity.class);
                    i.putExtra(BaseAppCompatActivity.UUID,uuid);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public void onDeleteComment() {
        finalizeDialog();
        showToast(R.string.msg_comment_delete_success);
        finish();
    }

    @Override
    public void onSendComment() {
        finalizeDialog();
        mEdtCommentInput.setText("");
        mIconPaths.clear();
        mSelectAdapter.notifyDataSetChanged();
        mRvComment.setVisibility(View.GONE);
        showToast(R.string.msg_send_comment_success);
    }

    private void setGif(Image image, ImageView gifImageView, LinearLayout.LayoutParams params){
        final int[] wh = BitmapUtils.getDocIconSize(image.getW(), image.getH(), DensityUtil.getScreenWidth(this) - DensityUtil.dip2px(this,66));
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
        final int[] wh = BitmapUtils.getDocIconSize(image.getW(), image.getH(), DensityUtil.getScreenWidth(this) - DensityUtil.dip2px(this,66));
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
    public void onFailure(int code, String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    protected void onDestroy() {
        mPresenter.release();
        super.onDestroy();
    }
}
