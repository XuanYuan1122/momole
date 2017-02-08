package com.moemoe.lalala.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplicationLike;
import com.moemoe.lalala.di.components.DaggerTrashComponent;
import com.moemoe.lalala.di.modules.TrashModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.TagLikeEntity;
import com.moemoe.lalala.model.entity.TagSendEntity;
import com.moemoe.lalala.model.entity.TrashEntity;
import com.moemoe.lalala.presenter.TrashContract;
import com.moemoe.lalala.presenter.TrashPresenter;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.widget.adapter.NewDocLabelAdapter;
import com.moemoe.lalala.view.widget.longimage.LongImageView;
import com.moemoe.lalala.view.widget.view.DocLabelView;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zlc.season.rxdownload.RxDownload;
import zlc.season.rxdownload.entity.DownloadStatus;

/**
 * Created by yi on 2016/12/14.
 */

public class TrashDetailActivity extends BaseAppCompatActivity implements TrashContract.View{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_content)
    TextView mContent;
    @BindView(R.id.iv_image)
    ImageView mIvContent;
    @BindView(R.id.dv_doc_label)
    DocLabelView mDocLabelView;
    @BindView(R.id.tv_like_num)
    TextView mLikeNum;
    @BindView(R.id.tv_dislike_num)
    TextView mDislikeNum;
    @BindView(R.id.tv_favorite)
    TextView mFavorite;
    @BindView(R.id.iv_doc_long_image)
    LongImageView mLongImage;
    @BindView(R.id.edt_comment_input)
    EditText mEdtCommentInput;
    @BindView(R.id.ll_comment_pannel)
    KeyboardListenerLayout mKlCommentBoard;
    @BindView(R.id.iv_comment_send)
    View mTvSendComment;

    @Inject
    TrashPresenter mPresenter;
    private RxDownload downloadSub;
    private String mType;
   // private String mId;
    private NewDocLabelAdapter docLabelAdapter;
    private String mPath;
    private TrashEntity mEntity;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_trash_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(getIntent() == null){
            finish();
            return;
        }
        DaggerTrashComponent.builder()
                .trashModule(new TrashModule(this))
                .netComponent(MoeMoeApplicationLike.getInstance().getNetComponent())
                .build()
                .inject(this);
        mType = getIntent().getStringExtra("type");
      //  mId = getIntent().getStringExtra("id");
        mEntity = getIntent().getParcelableExtra("item");
        downloadSub = RxDownload.getInstance()
                .maxThread(3)
                .maxRetryCount(3)
                .defaultSavePath(StorageUtils.getGalleryDirPath())
                .retrofit(MoeMoeApplicationLike.getInstance().getNetComponent().getRetrofit());
        if("text".equals(mType)){
            mContent.setVisibility(View.VISIBLE);
            mIvContent.setVisibility(View.GONE);
        }else if("image".equals(mType)){
            mContent.setVisibility(View.GONE);
            mIvContent.setVisibility(View.VISIBLE);
        }
        docLabelAdapter = new NewDocLabelAdapter(this,false);
        mDocLabelView.setDocLabelAdapter(docLabelAdapter);
        mPresenter.getTrashDetail(mType,mEntity.getDustbinId());
        mTvTitle.setText("小纸条");
        mTitle.setText(mEntity.getTitle());
        if("text".equals(mType)){
            mContent.setText(mEntity.getContent());
        }else if("image".equals(mType)){
            mPath = mEntity.getImage().getPath();
            createImage(mEntity.getImage());
        }
        mLikeNum.setText(mEntity.getFun() + "");
        mDislikeNum.setText(mEntity.getShit() + "");
        mFavorite.setSelected(mEntity.isMark());
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    private void createLabel(){
        String content = mEdtCommentInput.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            SoftKeyboardUtils.dismissSoftKeyboard(this);
            createDialog();
            TagSendEntity entity = new TagSendEntity(content,mType,mEntity.getDustbinId());
            mPresenter.createTag(entity);
        }else {
            showToast(R.string.msg_doc_comment_not_empty);
        }
    }

    @Override
    public void onCreateTag(String id) {
        finalizeDialog();
        DocTagEntity DocTag = new DocTagEntity();
        DocTag.setLikes(1);
        DocTag.setName(mEdtCommentInput.getText().toString());
        DocTag.setLiked(true);
        DocTag.setId(id);
        docLabelAdapter.getTags().add(DocTag);
        mDocLabelView.notifyAdapter();
    }

    @Override
    protected void initListeners() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mDocLabelView.setItemClickListener(new DocLabelView.LabelItemClickListener() {
            @Override
            public void itemClick(int position) {
                if (position < docLabelAdapter.getCount() - 1) {
                    plusLabel(position);
                } else {
                    mKlCommentBoard.setVisibility(View.VISIBLE);
                    mEdtCommentInput.setText("");
                    mEdtCommentInput.setHint("添加标签吧~~");
                    mEdtCommentInput.requestFocus();
                    SoftKeyboardUtils.showSoftKeyboard(TrashDetailActivity.this, mEdtCommentInput);
                }
            }
        });
        mKlCommentBoard.setOnkbdStateListener(new KeyboardListenerLayout.onKeyboardChangeListener() {

            @Override
            public void onKeyBoardStateChange(int state) {
                if (state == KeyboardListenerLayout.KEYBOARD_STATE_HIDE) {
                    mKlCommentBoard.setVisibility(View.GONE);
                }
            }
        });
        mContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("绅士内容", mContent.getText());
                cmb.setPrimaryClip(mClipData);
                ToastUtils.showCenter(TrashDetailActivity.this, getString(R.string.msg_trash_get));
                return false;
            }
        });
        mEdtCommentInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = mEdtCommentInput.getText();
                int len = editable.length();
                if (len > 15) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    String newStr = str.substring(0, 15);
                    mEdtCommentInput.setText(newStr);
                    editable = mEdtCommentInput.getText();
                    int newLen = editable.length();
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    Selection.setSelection(editable, selEndIndex);
                }
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
        mTvSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLabel();
            }
        });
    }

    private void plusLabel(final int position){
        if (!NetworkUtils.checkNetworkAndShowError(this)) {
            return;
        }
        if (DialogUtils.checkLoginAndShowDlg(this)) {
            final DocTagEntity tagBean = docLabelAdapter.getItem(position);
            TagLikeEntity bean = new TagLikeEntity(tagBean.getId(),mEntity.getDustbinId(),mType);
            createDialog();
            mPresenter.likeTrashTag(bean,tagBean.isLiked(),position);
        }
    }

    @Override
    public void onLikeTag(boolean like, int position) {
        finalizeDialog();
        DocTagEntity tagBean = docLabelAdapter.getItem(position);
        docLabelAdapter.getTags().remove(position);
        tagBean.setLiked(like);
        if(like){
            tagBean.setLikes(tagBean.getLikes() + 1);
            docLabelAdapter.getTags().add(position, tagBean);
        }else {
            tagBean.setLikes(tagBean.getLikes() - 1);
            if (tagBean.getLikes() > 0) {
                docLabelAdapter.getTags().add(position, tagBean);
            }
        }
        mDocLabelView.notifyAdapter();
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.tv_favorite,R.id.iv_image,R.id.iv_doc_long_image})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_favorite:
                TrashEntity entity = new TrashEntity();
                entity.setMark(mFavorite.isSelected());
                entity.setDustbinId(mEntity.getDustbinId());
                mPresenter.favoriteTrash(entity,mType);
                break;
            case R.id.iv_image:
            case R.id.iv_doc_long_image:
                if(!TextUtils.isEmpty(mPath)){
                    ArrayList<Image> temp = new ArrayList<>();
                    Image image = new Image();
                    String str = mPath;
                    image.setPath(str);
                    temp.add(image);
                    Intent intent = new Intent(this, ImageBigSelectActivity.class);
                    intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, temp);
                    intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                            0);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void onLoadListSuccess(ArrayList<TrashEntity> entities) {

    }

    @Override
    public void onFavoriteTrashSuccess(TrashEntity entity) {
        mFavorite.setSelected(entity.isMark());
    }

    @Override
    public void onTop3LoadSuccess(ArrayList<TrashEntity> entities) {

    }

    @Override
    public void onTop3LoadFail(int code,String msg) {

    }

    @Override
    public void onLoadDetailSuccess(ArrayList<DocTagEntity> entities) {
        docLabelAdapter.setData(entities,true);
//        mTitle.setText(entity.getTitle());
//        if("text".equals(mType)){
//            mContent.setText(entity.getContent());
//        }else if("image".equals(mType)){
//            mPath = entity.getImage().getPath();
//            createImage(entity.getImage());
//        }
//        mLikeNum.setText(entity.getFun() + "");
//        mDislikeNum.setText(entity.getShit() + "");
//        mFavorite.setSelected(entity.isMark());
//        docLabelAdapter.setData(entity.getTags(),true);
    }

    @Override
    public void onFailure(int code,String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    private void createImage(Image image){
        final int[] wh = BitmapUtils.getDocIconSize(image.getW(), image.getH(), DensityUtil.getScreenWidth(this) - DensityUtil.dip2px(this,20));
        if(wh[1] > 2048){
            mIvContent.setVisibility(View.GONE);
            mLongImage.setVisibility(View.VISIBLE);
            String temp = EncoderUtils.MD5(ApiService.URL_QINIU + image.getPath()) + ".jpg";
            final File longImage = new File(StorageUtils.getGalleryDirPath(), temp);
            ViewGroup.LayoutParams layoutParams = mLongImage.getLayoutParams();
            layoutParams.width = wh[0];
            layoutParams.height = wh[1];
            mLongImage.setLayoutParams(layoutParams);
            mLongImage.requestLayout();
            if(longImage.exists()){
                mLongImage.setImage(longImage.getAbsolutePath());
            }else {
                downloadSub.download(ApiService.URL_QINIU + image.getPath(),temp,null)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<DownloadStatus>() {
                            @Override
                            public void onCompleted() {
                                BitmapUtils.galleryAddPic(TrashDetailActivity.this, longImage.getAbsolutePath());
                                mLongImage.setImage(longImage.getAbsolutePath());
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(DownloadStatus downloadStatus) {

                            }
                        });
            }
        }else {
            mIvContent.setVisibility(View.VISIBLE);
            mLongImage.setVisibility(View.GONE);
            if(FileUtil.isGif(image.getPath())){
                ViewGroup.LayoutParams layoutParams = mIvContent.getLayoutParams();
                layoutParams.width = wh[0];
                layoutParams.height = wh[1];
                mIvContent.setLayoutParams(layoutParams);
                mIvContent.requestLayout();
                Glide.with(this)
                        .load(ApiService.URL_QINIU + image.getPath())
                        .asGif()
                        .override(wh[0], wh[1])
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .into(mIvContent);
            }else {
                ViewGroup.LayoutParams layoutParams = mIvContent.getLayoutParams();
                layoutParams.width = wh[0];
                layoutParams.height = wh[1];
                mIvContent.setLayoutParams(layoutParams);
                mIvContent.requestLayout();
                Glide.with(this)
                        .load(StringUtils.getUrl(this,ApiService.URL_QINIU + image.getPath(), wh[0], wh[1], true, true))
                        .override(wh[0], wh[1])
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .into(mIvContent);
            }
        }
    }
}
