package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerCreateForwardComponent;
import com.moemoe.lalala.di.modules.CreateForwardModule;
import com.moemoe.lalala.model.entity.DynamicContentEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ForwardSendEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.RetweetEntity;
import com.moemoe.lalala.model.entity.ShareArticleEntity;
import com.moemoe.lalala.model.entity.ShareArticleSendEntity;
import com.moemoe.lalala.model.entity.ShareFolderEntity;
import com.moemoe.lalala.model.entity.ShareFolderSendEntity;
import com.moemoe.lalala.model.entity.tag.BaseTag;
import com.moemoe.lalala.model.entity.tag.UserUrlSpan;
import com.moemoe.lalala.presenter.CreateForwardContract;
import com.moemoe.lalala.presenter.CreateForwardPresenter;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileItemDecoration;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.utils.tag.TagControl;
import com.moemoe.lalala.view.adapter.SelectItemAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;

import static com.moemoe.lalala.utils.StartActivityConstant.REQ_ALT_USER;

/**
 * 转发
 * Created by yi on 2017/9/21.
 */

public class CreateForwardActivity extends BaseAppCompatActivity implements CreateForwardContract.View{

    public static final int TYPE_ARTICLE = 0;
    public static final int TYPE_FOLDER = 1;
    public static final int TYPE_DYNAMIC = 2;
    private static final int LIMIT_CONTENT = 150;

    @BindView(R.id.tv_left_menu)
    TextView mTvMenuLeft;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvMenuRight;
    @BindView(R.id.et_content)
    EditText mEtContent;
    @BindView(R.id.tv_content_count)
    TextView mTvContentCount;
    @BindView(R.id.list)
    RecyclerView mRvImg;
    @BindView(R.id.ll_extra_root)
    LinearLayout mExtraRoot;
    @BindView(R.id.cb_comment)
    CheckBox mCbComment;
    @BindView(R.id.iv_add_img)
    ImageView mIvAddImg;
    @Inject
    CreateForwardPresenter mPresenter;

    private int mType;
    private SelectItemAdapter mSelectAdapter;
    private ArrayList<Object> mPaths;
    private String mId;
    private String mRtType;
    private String mUserId;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_create_zhuan_or_comment;
    }

    public static void startActivity(Context context,NewDynamicEntity entity){
        Intent i = new Intent(context,CreateForwardActivity.class);
        i.putExtra("type",TYPE_DYNAMIC);
        i.putExtra("dynamic",entity);
        context.startActivity(i);
    }

    public static void startActivity(Context context,ShareArticleEntity entity){
        Intent i = new Intent(context,CreateForwardActivity.class);
        i.putExtra("type",TYPE_ARTICLE);
        i.putExtra("article",entity);
        context.startActivity(i);
    }

    public static void startActivity(Context context,ShareFolderEntity entity,String mUserId){
        Intent i = new Intent(context,CreateForwardActivity.class);
        i.putExtra("type",TYPE_FOLDER);
        i.putExtra("folder",entity);
        i.putExtra("mUserId",mUserId);
        context.startActivity(i);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);
        DaggerCreateForwardComponent.builder()
                .createForwardModule(new CreateForwardModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mType = getIntent().getIntExtra("type",TYPE_ARTICLE);
        SoftKeyboardUtils.showSoftKeyboard(this,mEtContent);
        mUserId = getIntent().getStringExtra("mUserId");
        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = mEtContent.getText();
                int len = editable.length();
                if (len > LIMIT_CONTENT) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    mEtContent.setText(editable.subSequence(0, LIMIT_CONTENT));
                    editable = mEtContent.getText();
                    int newLen = editable.length();
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    Selection.setSelection(editable, selEndIndex);
                }
                mTvContentCount.setText(mEtContent.getText().length() + "/" + LIMIT_CONTENT);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if(mType == TYPE_DYNAMIC){
            NewDynamicEntity mDynamic = getIntent().getParcelableExtra("dynamic");
            if(mDynamic == null) {
                finish();
                return;
            }
            mId = mDynamic.getId();
            String temp;
            if(!"DYNAMIC".equals(mDynamic.getType())){
                temp = "//<at_user user_id=" + mDynamic.getCreateUser().getUserId() + ">@" + mDynamic.getCreateUser().getUserName() + "</at_user>: " + mDynamic.getText();
            }else {
                temp = "";
            }
            mEtContent.setText(TagControl.getInstance().paresToSpann(this,temp));
            mEtContent.setSelection(0);
            mSelectAdapter = new SelectItemAdapter(this);
            mSelectAdapter.setSelectSize(0);
            mRvImg.setLayoutManager(new GridLayoutManager(this,3));
            mRvImg.addItemDecoration(new FileItemDecoration());
            mRvImg.setAdapter(mSelectAdapter);
            mPaths = new ArrayList<>();
            mIvAddImg.setVisibility(View.VISIBLE);
            createForward(mDynamic);
            mCbComment.setVisibility(View.VISIBLE);
            mCbComment.setText("转发并评论");
        }
        if(mType == TYPE_ARTICLE){
            ShareArticleEntity mArticle = getIntent().getParcelableExtra("article");
            if(mArticle == null) {
                finish();
                return;
            }
            mIvAddImg.setVisibility(View.GONE);
            mId = mArticle.getDocId();
            createArticle(mArticle);
            mCbComment.setVisibility(View.GONE);
        }
        if(mType == TYPE_FOLDER){
            ShareFolderEntity mFolder = getIntent().getParcelableExtra("folder");
            if(mFolder == null) {
                finish();
                return;
            }
            mId = mFolder.getFolderId();
            mIvAddImg.setVisibility(View.GONE);
            createFolder(mFolder);
            mCbComment.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mTvMenuLeft.setVisibility(View.VISIBLE);
        ViewUtils.setLeftMargins(mTvMenuLeft, (int) getResources().getDimension(R.dimen.x36));
        mTvMenuLeft.setTextColor(ContextCompat.getColor(this,R.color.black_1e1e1e));
        mTvMenuLeft.setText(getString(R.string.label_give_up));
        mTvMenuLeft.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                SoftKeyboardUtils.dismissSoftKeyboard(CreateForwardActivity.this);
                finish();
            }
        });
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText("转发");
        mTvMenuRight.setVisibility(View.VISIBLE);
        ViewUtils.setRightMargins(mTvMenuRight,(int) getResources().getDimension(R.dimen.x36));
        mTvMenuRight.setText(getString(R.string.label_menu_publish_doc));
        mTvMenuRight.setTextColor(Color.WHITE);
        mTvMenuRight.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int) getResources().getDimension(R.dimen.x30));
        mTvMenuRight.setWidth((int) getResources().getDimension(R.dimen.x88));
        mTvMenuRight.setHeight((int) getResources().getDimension(R.dimen.y48));
        mTvMenuRight.setBackgroundResource(R.drawable.shape_rect_border_main_background_2);
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    private void createArticle(final ShareArticleEntity entity){
        View article = LayoutInflater.from(this).inflate(R.layout.item_new_wenzhang_zhuan,null);
        TextView title = (TextView) article.findViewById(R.id.tv_title);
        TextView articleContent = (TextView) article.findViewById(R.id.tv_content);
        ImageView cover = (ImageView) article.findViewById(R.id.iv_cover);
        TextView mark = (TextView) article.findViewById(R.id.tv_mark);
        article.findViewById(R.id.tv_folder_name).setVisibility(View.GONE);
        article.findViewById(R.id.tv_tag).setVisibility(View.GONE);
        article.findViewById(R.id.iv_mask).setVisibility(View.GONE);
        int w = (int) (DensityUtil.getScreenWidth(this) - getResources().getDimension(R.dimen.x48));
        int h = (int) getResources().getDimension(R.dimen.y320);
        Glide.with(this)
                .load(StringUtils.getUrl(this,entity.getCover(),w,h,false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .bitmapTransform(new CropTransformation(this,w,h))
                .into(cover);
        mark.setText("文章");
        title.setText(entity.getTitle());
        articleContent.setText(TagControl.getInstance().paresToSpann(this,entity.getContent()));
        ImageView avatar = (ImageView) article.findViewById(R.id.iv_avatar);
        TextView userName = (TextView) article.findViewById(R.id.tv_user_name);
        TextView time = (TextView) article.findViewById(R.id.tv_time);
        int size = (int) this.getResources().getDimension(R.dimen.x44);
        Glide.with(this)
                .load(StringUtils.getUrl(this,entity.getDocCreateUser().getHeadPath(),size,size,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(avatar);
        userName.setText(entity.getDocCreateUser().getUserName());
        time.setText(StringUtils.timeFormate(entity.getCreateTime()));
        mExtraRoot.addView(article);
    }
    
    private void createFolder(ShareFolderEntity entity){
        View folder = LayoutInflater.from(this).inflate(R.layout.item_new_wenzhang_zhuan,null);
        folder.findViewById(R.id.tv_title).setVisibility(View.GONE);
        folder.findViewById(R.id.tv_content).setVisibility(View.GONE);
        ImageView cover = (ImageView) folder.findViewById(R.id.iv_cover);
        TextView mark = (TextView) folder.findViewById(R.id.tv_mark);
        TextView name = (TextView) folder.findViewById(R.id.tv_folder_name);
        TextView tag = (TextView) folder.findViewById(R.id.tv_tag);
        int w = (int) (DensityUtil.getScreenWidth(this) - getResources().getDimension(R.dimen.x48));
        int h = (int) getResources().getDimension(R.dimen.y320);
        Glide.with(this)
                .load(StringUtils.getUrl(this,entity.getFolderCover(),w,h,false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .bitmapTransform(new CropTransformation(this,w,h))
                .into(cover);
        if(entity.getFolderType().equals(FolderType.ZH.toString())){
            mark.setText("综合");
            mark.setBackgroundResource(R.drawable.shape_rect_zonghe);
        }else if(entity.getFolderType().equals(FolderType.TJ.toString())){
            mark.setText("图集");
            mark.setBackgroundResource(R.drawable.shape_rect_tuji);
        }else if(entity.getFolderType().equals(FolderType.MH.toString())){
            mark.setText("漫画");
            mark.setBackgroundResource(R.drawable.shape_rect_manhua);
        }else if(entity.getFolderType().equals(FolderType.XS.toString())){
            mark.setText("小说");
            mark.setBackgroundResource(R.drawable.shape_rect_xiaoshuo);
        }
        name.setText(entity.getFolderName());
        String tagStr = "";
        for(int i = 0;i < entity.getFolderTags().size();i++){
            String tagTmp = entity.getFolderTags().get(i);
            if(i == 0){
                tagStr = tagTmp;
            }else {
                tagStr += " · " + tagTmp;
            }
        }
        tag.setText(tagStr);

        ImageView avatar = (ImageView) folder.findViewById(R.id.iv_avatar);
        TextView userName = (TextView) folder.findViewById(R.id.tv_user_name);
        TextView time = (TextView) folder.findViewById(R.id.tv_time);
        int size = (int) this.getResources().getDimension(R.dimen.x44);
        Glide.with(this)
                .load(StringUtils.getUrl(this,entity.getCreateUser().getHeadPath(),size,size,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(avatar);
        userName.setText(entity.getCreateUser().getUserName());
        time.setText("上一次更新:" + StringUtils.timeFormate(entity.getUpdateTime()));
        mExtraRoot.addView(folder);
    }
    
    private void createDelete(){
        TextView tv = new TextView(this);
        tv.setText("该内容已被删除");
        tv.setTextColor(ContextCompat.getColor(this,R.color.white));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,this.getResources().getDimension(R.dimen.x36));
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(ContextCompat.getColor(this,R.color.gray_e8e8e8));
        int h = (int) getResources().getDimension(R.dimen.y320);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,h);
        tv.setLayoutParams(lp);
        mExtraRoot.addView(tv);
    }

    private void createDynamic(String path,CharSequence content){
        View folder = LayoutInflater.from(this).inflate(R.layout.item_forward_dynamic,null);
        ImageView iv = (ImageView) folder.findViewById(R.id.iv_cover);
        TextView tv = (TextView) folder.findViewById(R.id.tv_content);
        if(TextUtils.isEmpty(path)){
            iv.setVisibility(View.GONE);
        }else {
            int size = (int) getResources().getDimension(R.dimen.x180);
            Glide.with(this)
                    .load(StringUtils.getUrl(this,path,size,size,false,true))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .bitmapTransform(new CropSquareTransformation(this))
                    .into(iv);
        }
        tv.setText(content);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        mExtraRoot.addView(folder);
    }

    private void createForward(NewDynamicEntity entity){
        if("DELETE".equals(entity.getType())){
            createDelete();
            mRtType = "DELETE";
        }
        if("DYNAMIC".equals(entity.getType())){
            mRtType = "DYNAMIC";
            DynamicContentEntity dynamicContentEntity = new Gson().fromJson(entity.getDetail(),DynamicContentEntity.class);
            String path = "";
            if(dynamicContentEntity.getImages() != null && dynamicContentEntity.getImages().size() > 0){
               path = dynamicContentEntity.getImages().get(0).getPath();
            }
            String retweetContent = "@" + entity.getCreateUser().getUserName() + ": " + TagControl.getInstance().paresToSpann(this,entity.getText()).toString();
            String retweetColorStr = "@" + entity.getCreateUser().getUserName() + ":";
            SpannableStringBuilder style1 = new SpannableStringBuilder(retweetContent);
            UserUrlSpan span = new UserUrlSpan(this,entity.getCreateUser().getUserId(),null);
            style1.setSpan(span, retweetContent.indexOf(retweetColorStr), retweetContent.indexOf(retweetColorStr) + retweetColorStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            createDynamic(path,style1);
        }
        if("FOLDER".equals(entity.getType())){
            mRtType = "FOLDER";
            ShareFolderEntity folderEntity = new Gson().fromJson(entity.getDetail(),ShareFolderEntity.class);
            createFolder(folderEntity);
        }
        if("ARTICLE".equals(entity.getType())){
            mRtType = "ARTICLE";
            ShareArticleEntity articleEntity = new Gson().fromJson(entity.getDetail(),ShareArticleEntity.class);
            createArticle(articleEntity);
        }
        if("RETWEET".equals(entity.getType())){
            mRtType = "RETWEET";
            RetweetEntity retweetEntity = new Gson().fromJson(entity.getDetail(),RetweetEntity.class);
            String retweetContent = "@" + retweetEntity.getCreateUserName() + ": " + TagControl.getInstance().paresToSpann(this,retweetEntity.getContent()).toString();
            String retweetColorStr = "@" + retweetEntity.getCreateUserName() + ":";
            SpannableStringBuilder style1 = new SpannableStringBuilder(retweetContent);
            UserUrlSpan span = new UserUrlSpan(this,retweetEntity.getCreateUserId(),null);
            style1.setSpan(span, retweetContent.indexOf(retweetColorStr), retweetContent.indexOf(retweetColorStr) + retweetColorStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            String path = "";
            if(retweetEntity.getImages() != null && retweetEntity.getImages().size() > 0){
                path = retweetEntity.getImages().get(0).getPath();
            }
            createDynamic(path,style1);
        }


    }

    @OnClick({R.id.iv_add_img,R.id.iv_alt_user,R.id.tv_menu})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_add_img:
                if (mPaths.size() < 1){
                    try {
                        ArrayList<String> res = new ArrayList<>();
                        for(Object tmp : mPaths){
                            res.add((String) tmp);
                        }
                        DialogUtils.createImgChooseDlg(CreateForwardActivity.this, null, CreateForwardActivity.this, res, 1).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    showToast("只能选择一张图片");
                }
                break;
            case R.id.iv_alt_user:
                Intent i3 = new Intent(CreateForwardActivity.this,SearchActivity.class);
                i3.putExtra("show_type",SearchActivity.SHOW_USER);
                startActivityForResult(i3,REQ_ALT_USER);
                break;
            case R.id.tv_menu:
                done();
                break;
        }
    }

    private void done(){
        if(!NetworkUtils.isNetworkAvailable(this)){
            showToast(R.string.msg_connection);
            return;
        }
        if(TextUtils.isEmpty(mEtContent.getText())){
            showToast(R.string.msg_doc_content_cannot_null);
            return;
        }
       // String text = TagControl.getInstance().paresToString(mEtContent.getText());
        if(mEtContent.getText().length() > LIMIT_CONTENT){
            showToast("超过字数限制");
            return;
        }
        createDialog();
        if(mType == TYPE_DYNAMIC){
            ForwardSendEntity entity = new ForwardSendEntity();
            entity.rt = mCbComment.isChecked();
            entity.dynamicId = mId;
            entity.etText =  TagControl.getInstance().paresToString(mEtContent.getText());
            entity.type = mRtType;
            if(mPaths.size() > 0){
                Image image = new Image();
                image.setPath((String) mPaths.get(0));
                entity.img = image;
            }else {
                entity.img = new Image();
            }
            mPresenter.createForward(mType,entity);
        }else if(mType == TYPE_ARTICLE){
            ShareArticleSendEntity entity = new ShareArticleSendEntity();
            entity.docId = mId;
            entity.shareText = TagControl.getInstance().paresToString(mEtContent.getText());
            mPresenter.createForward(mType,entity);
        }else if(mType == TYPE_FOLDER){
            ShareFolderSendEntity entity = new ShareFolderSendEntity();
            entity.folderId = mId;
            entity.shareText = TagControl.getInstance().paresToString(mEtContent.getText());
            entity.folderCreateUser = mUserId;
            mPresenter.createForward(mType,entity);
        }
    }

    private void insertTextInCurSelection(String str,String id){
        SpannableStringBuilder lastEditStr = new SpannableStringBuilder(mEtContent.getText());
        int cursorIndex = mEtContent.getSelectionStart();
        BaseTag tag = new BaseTag();
        tag.setTag("at_user");
        tag.setSpan(new UserUrlSpan(this,tag));
        HashMap<String,String> attrs = new HashMap<>();
        attrs.put("user_id",id);
        tag.setAttrs(attrs);
        if(cursorIndex < 0){
            lastEditStr.insert(lastEditStr.length(),str + " ");
            lastEditStr.setSpan(tag.getSpan(),mEtContent.getText().length(),mEtContent.getText().length() + str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else {
            lastEditStr.insert(cursorIndex,str + " ");
            lastEditStr.setSpan(tag.getSpan(),cursorIndex,cursorIndex + str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mEtContent.setText(lastEditStr);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_ALT_USER && resultCode == RESULT_OK){
            if(data != null){
                String userId = data.getStringExtra("user_id");
                String userName = data.getStringExtra("user_name");
                insertTextInCurSelection("@" + userName,userId);
            }
        }else {
            DialogUtils.handleImgChooseResult(this, requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

                @Override
                public void onPhotoGet(ArrayList<String> photoPaths, boolean override) {
                    mPaths.clear();
                    mPaths.addAll(photoPaths);
                    mSelectAdapter.setData(mPaths);
                }
            });
        }
    }

    @Override
    public void onFailure(int code, String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void onCreateForwardSuccess() {
        finalizeDialog();
        showToast("转发成功");
        finish();
    }
}
