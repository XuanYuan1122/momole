package com.moemoe.lalala.view.widget.richtext;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.event.RichImgRemoveEvent;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.RichEntity;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.CustomUrlSpan;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.FilesUploadActivity;
import com.moemoe.lalala.view.widget.longimage.LongImageView;
import com.moemoe.lalala.view.widget.view.DocLabelView;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zlc.season.rxdownload.RxDownload;
import zlc.season.rxdownload.entity.DownloadStatus;

/**
 * Created by yi on 2017/5/11.
 */

public class NetaRichEditor extends ScrollView {
    /**
     * 标题限制长度
     */
    private final int TITLE_LIMIT = 30;
    private static final int EDIT_PADDING = 10; // edittext常规padding是10dp
    private int viewTagIndex = 1; // 新生的view都会打一个tag，对每个view来说，这个tag是唯一的。
    private LinearLayout allLayout; // 这个是所有子view的容器，scrollView内部的唯一一个ViewGroup
    private LayoutInflater inflater;
    private OnKeyListener keyListener; // 所有EditText的软键盘监听器
    private OnClickListener btnListener; // 图片右上角红叉按钮监听器
    private OnFocusChangeListener focusListener; // 所有EditText的焦点监听listener
    private OnClickListener editClickListener;
    private EditText lastFocusEdit; // 最近被聚焦的EditText
    private LayoutTransition mTransitioner; // 只在图片View添加或remove时，触发transition动画
    private int editNormalPadding = 0; //
    private int disappearingImageIndex = 0;
    private DocLabelView docLabelView;
    KeyboardListenerLayout mKlCommentBoard;
    private TextWatcher textWatcher;
    private ArrayList<DocTagEntity> mTags;
    private boolean tagFlag;
    private String mTagNameDef;
    private LinearLayout root;
    private RxDownload downloadSub;
    private EditText mEtTitle;
    private TextView mTvTitleCount;
    private ImageView mCover;
    private TextView mTvAddCover;

    public NetaRichEditor(Context context) {
        this(context,null);
    }

    public NetaRichEditor(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NetaRichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflater = LayoutInflater.from(context);
        // 1. 初始化allLayout
        root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        root.setPadding(DensityUtil.dip2px(context,18), DensityUtil.dip2px(context,18), DensityUtil.dip2px(context,18), 0);//设置间距，防止生成图片时文字太靠边，不能用margin，否则有黑边
        addView(root, layoutParams);

        allLayout = new LinearLayout(context);
        allLayout.setOrientation(LinearLayout.VERTICAL);
        allLayout.setBackgroundColor(Color.WHITE);
        allLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
        setupLayoutTransitions();
        root.addView(allLayout);
        // 2. 初始化键盘退格监听
        // 主要用来处理点击回删按钮时，view的一些列合并操作
        keyListener = new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    EditText edit = (EditText) v;
                    onBackspacePress(edit);
                }
                return false;
            }
        };
        editClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) v;
                if(TextUtils.isEmpty(editText.getText())){
                    editText.setSelection(0);
                }else {
                    int sep = checkClickAt(editText.getText(),editText.getSelectionStart());
                    editText.setSelection(editText.getSelectionStart() + sep);
                }
            }
        };
        // 3. 图片叉掉处理
        btnListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                RelativeLayout parentView = (RelativeLayout) v.getParent();
                onImageCloseClick(parentView);
            }
        };

        focusListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    lastFocusEdit = (EditText) v;
                }
            }
        };
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(checkClickAt(s,start) != 0){
                    lastFocusEdit.removeTextChangedListener(this);
                    lastFocusEdit.setText(s);
                    lastFocusEdit.addTextChangedListener(textWatcher);
                }else {
                    CharSequence temp = checkDeleteAt(s,start);
                    if(temp != null){
                        lastFocusEdit.removeTextChangedListener(this);
                        lastFocusEdit.setText(temp);
                        lastFocusEdit.addTextChangedListener(textWatcher);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        downloadSub = RxDownload.getInstance()
                .maxThread(3)
                .maxRetryCount(3)
                .defaultSavePath(StorageUtils.getGalleryDirPath())
                .retrofit(MoeMoeApplication.getInstance().getNetComponent().getRetrofit());
    }

    public void createFirstEdit(){
        LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final EditText firstEdit = createEditText("内容");
        allLayout.addView(firstEdit, firstEditParam);
        allLayout.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(allLayout.getChildCount() == 1){
                    firstEdit.setFocusable(true);
                    firstEdit.setFocusableInTouchMode(true);
                    firstEdit.requestFocus();
                    SoftKeyboardUtils.showSoftKeyboard(getContext(), firstEdit);
                }
            }
        });
        lastFocusEdit = firstEdit;
    }

    private CharSequence checkDeleteAt(CharSequence sequence,int end){
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(sequence);
        CustomUrlSpan[] spen = stringBuilder.getSpans(0,end,CustomUrlSpan.class);
        if(spen.length > 0){
            for (CustomUrlSpan span : spen){
                if(stringBuilder.getSpanEnd(span) == end){
                    stringBuilder.replace(stringBuilder.getSpanStart(span),stringBuilder.getSpanEnd(span),"");
                    stringBuilder.removeSpan(span);
                    return stringBuilder;
                }
            }
            return null;
        }else {
            return null;
        }
    }

    private int checkClickAt(CharSequence sequence,int position){
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(sequence);
        CustomUrlSpan[] spen = stringBuilder.getSpans(0,stringBuilder.length(),CustomUrlSpan.class);
        for(CustomUrlSpan span : spen){
            if(position < stringBuilder.getSpanEnd(span) && position > stringBuilder.getSpanStart(span)){
                return stringBuilder.getSpanEnd(span) - position;
            }
        }
        return 0;
    }

    public void setTop(){
        View topRoot = createTopView();
        mEtTitle = (EditText) topRoot.findViewById(R.id.et_title);
        mCover = (ImageView) topRoot.findViewById(R.id.iv_cover);
        mTvAddCover = (TextView) topRoot.findViewById(R.id.tv_add_cover);
        mTvTitleCount = (TextView) topRoot.findViewById(R.id.ev_title_count);
        mCover.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ArrayList<String> arrayList = new ArrayList<>();
                DialogUtils.createImgChooseDlg((BaseAppCompatActivity)getContext(), null,getContext(), arrayList, 1).show();
            }
        });
        mEtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = mEtTitle.getText();
                int len = editable.length();
                if (len > TITLE_LIMIT) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    String newStr = str.substring(0, TITLE_LIMIT);
                    mEtTitle.setText(newStr);
                    editable = mEtTitle.getText();
                    int newLen = editable.length();
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    Selection.setSelection(editable, selEndIndex);
                }
                mTvTitleCount.setText((TITLE_LIMIT - mEtTitle.getText().length()) + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        root.addView(topRoot,0);
    }

    public void setTitle(String title){
        if(mEtTitle == null)return;
        mEtTitle.setText(title);
    }

    public String getTitle(){
        return mEtTitle == null ? "" : mEtTitle.getText().toString();
    }

    public void setCover(String path){
        if(mCover == null) return;
        int w = (int) (DensityUtil.getScreenWidth(getContext()) - getContext().getResources().getDimension(R.dimen.x36) * 2);
        int h = (int) getContext().getResources().getDimension(R.dimen.y200);
        Glide.with(getContext())
                .load(path)
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .bitmapTransform(new CropTransformation(getContext(),w,h))
                .into(mCover);
        mTvAddCover.setVisibility(GONE);
    }

    public void setLabelAble(){
        mTags = new ArrayList<>();
        View labelRoot = createLabelView();
        docLabelView = (DocLabelView) labelRoot.findViewById(R.id.dv_doc_label_root);
        docLabelView.setContentAndNumList(true,mTags);
        docLabelView.setItemClickListener(new DocLabelView.LabelItemClickListener() {
            @Override
            public void itemClick(int position) {
                if(!tagFlag){
                    if (position < mTags.size()) {
                        deleteLabel(position);
                    } else {
                        SoftKeyboardUtils.dismissSoftKeyboard((Activity) getContext());
                        DocTagEntity docTag = new DocTagEntity();
                        docTag.setLikes(1);
                        docTag.setName("");
                        docTag.setLiked(true);
                        docTag.setEdit(true);
                        mTags.add(docTag);
                        docLabelView.notifyAdapter();
                        tagFlag = true;
                    }
                }
            }
        });
        View view = new View(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.gray_e8e8e8));
        root.addView(view);
        root.addView(labelRoot);
    }

    public void setmKlCommentBoard(KeyboardListenerLayout mKlCommentBoard) {
        this.mKlCommentBoard = mKlCommentBoard;
        mKlCommentBoard.setOnkbdStateListener(new KeyboardListenerLayout.onKeyboardChangeListener() {

            @Override
            public void onKeyBoardStateChange(int state) {
                if (state == KeyboardListenerLayout.KEYBOARD_STATE_HIDE) {
                    tagFlag = false;
                    if(mTags.size() > 0){
                        DocTagEntity entity = mTags.get(mTags.size() - 1);
                        if(!TextUtils.isEmpty(entity.getName())){
                            if(checkLabel(entity.getName())){
                                mTags.get(mTags.size() - 1).setEdit(false);
                            }else {
                                entity.setName("");
                                ToastUtils.showShortToast(getContext(),R.string.msg_tag_already_exit);
                            }
                        }else {
                            mTags.remove(entity);
                        }
                        docLabelView.notifyAdapter();
                    }
                }
            }
        });
    }

    private void deleteLabel(final int position){
        if(!TextUtils.isEmpty(mTagNameDef) && mTagNameDef.equals(mTags.get(position).getName())){
            final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
            alertDialogUtil.createPromptNormalDialog(getContext(), getContext().getString( R.string.label_content_tag_del));
            alertDialogUtil.setButtonText(getContext().getString(R.string.label_confirm), getContext().getString(R.string.label_cancel),0);
            alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                @Override
                public void CancelOnClick() {
                    alertDialogUtil.dismissDialog();
                }

                @Override
                public void ConfirmOnClick() {
                    alertDialogUtil.dismissDialog();
                    mTags.remove(position);
                    docLabelView.notifyAdapter();
                }
            });
            alertDialogUtil.showDialog();
        }else {
            mTags.remove(position);
            docLabelView.notifyAdapter();
        }
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

    /**
     * 初始化transition动画
     */
    private void setupLayoutTransitions(){
        mTransitioner = new LayoutTransition();
        allLayout.setLayoutTransition(mTransitioner);
        mTransitioner.addTransitionListener(new LayoutTransition.TransitionListener() {

            @Override
            public void startTransition(LayoutTransition transition,
                                        ViewGroup container, View view, int transitionType) {
            }

            @Override
            public void endTransition(LayoutTransition transition,
                                      ViewGroup container, View view, int transitionType) {
                if (!transition.isRunning()
                        && transitionType == LayoutTransition.CHANGE_DISAPPEARING) {
                    // transition动画结束，合并EditText
                    // mergeEditText();
                }
            }
        });
        mTransitioner.setDuration(300);
    }

    public DocLabelView getDocLabelView() {
        return docLabelView;
    }

    public KeyboardListenerLayout getmKlCommentBoard() {
        return mKlCommentBoard;
    }

    public void setmTagNameDef(String mTagNameDef) {
        this.mTagNameDef = mTagNameDef;
        if(!TextUtils.isEmpty(mTagNameDef)){
            DocTagEntity DocTag = new DocTagEntity();
            DocTag.setLikes(1);
            DocTag.setName(mTagNameDef);
            DocTag.setLiked(true);
            mTags.add(DocTag);
            docLabelView.notifyAdapter();
        }
    }

    /**
     * 处理软键盘backSpace回退事件
     *
     * @param editTxt 光标所在的文本输入框
     */
    private void onBackspacePress(EditText editTxt) {
        int startSelection = editTxt.getSelectionStart();
        // 只有在光标已经顶到文本输入框的最前方，在判定是否删除之前的图片，或两个View合并
        if (startSelection == 0) {
            int editIndex = allLayout.indexOfChild(editTxt);
            View preView = allLayout.getChildAt(editIndex - 1); // 如果editIndex-1<0,
            // 则返回的是null
            if (null != preView) {
                if (preView instanceof RelativeLayout){
                    // 光标EditText的上一个view对应的是图片

                    View v = allLayout.getChildAt(editIndex - 2);
                    onImageCloseClick(preView);
                    if(v != null && v instanceof EditText){
                        String str1 = editTxt.getText().toString();
                        EditText preEdit = (EditText) v;
                        String str2 = preEdit.getText().toString();
                        allLayout.removeView(editTxt);
                        // 文本合并
                        preEdit.setText(str2 + str1);
                        preEdit.requestFocus();
                        preEdit.setSelection(str2.length(), str2.length());
                        lastFocusEdit = preEdit;
                    }

                }else if (preView instanceof EditText){
                    // 光标EditText的上一个view对应的还是文本框EditText
                    String str1 = editTxt.getText().toString();
                    EditText preEdit = (EditText) preView;
                    String str2 = preEdit.getText().toString();

                    allLayout.removeView(editTxt);
                    // 文本合并
                    preEdit.setText(str2 + str1);
                    preEdit.requestFocus();
                    preEdit.setSelection(str2.length(), str2.length());
                    lastFocusEdit = preEdit;
                }
            }
        }
    }

    /**
     * 处理图片叉掉的点击事件
     *
     * @param view 整个image对应的relativeLayout view
     * @type 删除类型 0代表backspace删除 1代表按红叉按钮删除
     */
    private void onImageCloseClick(View view) {
        disappearingImageIndex = allLayout.indexOfChild(view);
        //删除文件夹里的图片
        List<RichEntity> dataList = buildEditData();
        RichEntity editData = dataList.get(disappearingImageIndex);
//        if (editData.getImage() != null && !TextUtils.isEmpty(editData.getImage().getPath())) {
//            if(!FileUtil.isGif(editData.getImage().getPath())) FileUtil.deleteFile(editData.getImage().getPath());
//        }
        RxBus.getInstance().post(new RichImgRemoveEvent(editData.getImage().getPath()));
        allLayout.removeView(view);
    }

    private View createLabelView(){
        View view = inflater.inflate(R.layout.view_add_label,null);
        return view;
    }

    private View createTopView(){
        View view = inflater.inflate(R.layout.item_rich_top,null);
        return view;
    }

    /**
     * 生成文本输入框
     */
    public EditText createEditText(String hint) {
        EditText editText = (EditText) inflater.inflate(R.layout.rich_edittext, null);
        editText.setOnKeyListener(keyListener);
        editText.setTag(viewTagIndex++);
        editText.setHint(hint);
        editText.addTextChangedListener(textWatcher);
        editText.setOnFocusChangeListener(focusListener);
        editText.setOnClickListener(editClickListener);
        editText.setMovementMethod(LinkMovementMethod.getInstance());
        return editText;
    }

    public void clearAllLayout() {
        allLayout.removeAllViews();
    }

    public int getLastIndex() {
        int lastEditIndex = allLayout.getChildCount();
        return lastEditIndex;
    }

    public boolean hasContent(){
        if(allLayout.getChildCount() == 1){
            View v = allLayout.getChildAt(0);
            if(v instanceof EditText){
                EditText editText = (EditText) v;
                if(editText.getText().length() == 0){
                    return false;
                }else {
                    return true;
                }
            }else {
                return true;
            }
        }else if(allLayout.getChildCount() > 1){
            return true;
        }
        return false;
    }

    /**
     * 生成图片View
     */
    private RelativeLayout createImageLayout() {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.rich_image, null);
        layout.setTag(viewTagIndex++);
        View closeView = layout.findViewById(R.id.image_close);
        closeView.setTag(layout.getTag());
        closeView.setOnClickListener(btnListener);
        return layout;
    }

    public void insertTextInCurSelection(String str,String id){
        SpannableStringBuilder lastEditStr = new SpannableStringBuilder(lastFocusEdit.getText());
        int cursorIndex = lastFocusEdit.getSelectionStart();
        if(cursorIndex < 0){
            lastEditStr.insert(lastEditStr.length(),str + " ");
            CustomUrlSpan span = new CustomUrlSpan(getContext(),"", id);
            lastEditStr.setSpan(span,lastFocusEdit.getText().length(),lastFocusEdit.getText().length() + str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else {
            lastEditStr.insert(cursorIndex,str + " ");
            CustomUrlSpan span = new CustomUrlSpan(getContext(),"", id);
            lastEditStr.setSpan(span,cursorIndex,cursorIndex + str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        lastFocusEdit.setText(lastEditStr);
    }

    /**
     * 插入一张图片
     */
    public void insertImage(String imagePath) {
        String lastEditStr = lastFocusEdit.getText().toString();
        int cursorIndex = lastFocusEdit.getSelectionStart();
        int lastEditIndex = allLayout.indexOfChild(lastFocusEdit);
      //  String editStr1 = "";
        SpannableStringBuilder editStr = new SpannableStringBuilder(lastFocusEdit.getText());
        SpannableStringBuilder editStr1 = new SpannableStringBuilder();
        if(cursorIndex < 0){
            lastEditIndex = allLayout.getChildCount() - 1;
        }else {
            editStr1 = (SpannableStringBuilder) editStr.subSequence(0, cursorIndex);

        }
        if (lastEditStr.length() == 0 || editStr1.length() == 0 || cursorIndex < 0) {
            // 如果EditText为空，或者光标已经顶在了editText的最前面，则直接插入图片，并且EditText下移即可
            if(!TextUtils.isEmpty(lastEditStr)){
                addImageViewAtIndex(lastEditIndex + 1, imagePath);
                if(allLayout.getChildCount() == lastEditIndex + 2){
                    addEditTextAtIndex(lastEditIndex + 2, "");
                }
            }else {
                addImageViewAtIndex(lastEditIndex, imagePath);
            }

        } else {
            // 如果EditText非空且光标不在最顶端，则需要添加新的imageView和EditText
            lastFocusEdit.setText(editStr1);
            addImageViewAtIndex(lastEditIndex + 1, imagePath);
            //String editStr2 = lastEditStr.substring(cursorIndex);
            SpannableStringBuilder editStr2;
            editStr2 = (SpannableStringBuilder) editStr.subSequence(cursorIndex,editStr.length());
            if (editStr2.length() == 0) {
                editStr2 = new SpannableStringBuilder();
            }
            if(allLayout.getChildCount() == lastEditIndex + 2) {
                addEditTextAtIndex(lastEditIndex + 2, editStr2);
            }else if(!TextUtils.isEmpty(editStr2)){
                addEditTextAtIndex(lastEditIndex + 2, editStr2);
            }
            lastFocusEdit.requestFocus();
            lastFocusEdit.setSelection(editStr1.length(), editStr1.length());
        }
        hideKeyBoard();
    }

    /**
     * 隐藏小键盘
     */
    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(lastFocusEdit.getWindowToken(), 0);
    }

    /**
     * 在特定位置插入EditText
     *
     * @param index   位置
     * @param editStr EditText显示的文字
     */
    public void addEditTextAtIndex(final int index, CharSequence editStr) {
        EditText editText2 = createEditText("");
        editText2.setText(editStr);
        editText2.setOnFocusChangeListener(focusListener);
        LinearLayout.LayoutParams editParam = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        allLayout.addView(editText2, index,editParam);
    }

    public void addImageViewAtIndex(final int index, String imagePath,int w,int h,long size){
        final RelativeLayout imageLayout = createImageLayout();
        DataImageView imageView = (DataImageView) imageLayout.findViewById(R.id.edit_imageView);
        final LongImageView longImageView = (LongImageView) imageLayout.findViewById(R.id.edit_longImageView);
        int width;
        int height;
        if(w == 0 && h == 0){
            width = BitmapUtils.getImageSize(imagePath)[0];
            height = BitmapUtils.getImageSize(imagePath)[1];
            size = new File(imagePath).length();
        }else {
            width = w;
            height = h;
        }
        final int[] wh = BitmapUtils.getDocIconSize(width * 2, height * 2, DensityUtil.getScreenWidth(getContext()) - DensityUtil.dip2px(getContext(),36));
        if(wh[1] > 2048){
            imageView.setVisibility(GONE);
            longImageView.setVisibility(VISIBLE);
            ViewGroup.LayoutParams layoutParams = longImageView.getLayoutParams();
            layoutParams.width = wh[0];
            layoutParams.height = wh[1];
            longImageView.setLayoutParams(layoutParams);
            longImageView.requestLayout();
            Image image = new Image();
            image.setH(height);
            image.setW(width);
            image.setPath(imagePath);
            image.setSize(size);
            longImageView.setImage(image);
            if(imagePath.startsWith("image")){
                String temp = EncoderUtils.MD5(ApiService.URL_QINIU + image.getPath()) + ".jpg";
                final File longImage = new File(StorageUtils.getGalleryDirPath(), temp);
                if(longImage.exists()){
                    longImageView.setImage(longImage.getAbsolutePath());
                }else {
                    downloadSub.download(ApiService.URL_QINIU + image.getPath(),temp,null)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<DownloadStatus>() {
                                @Override
                                public void onCompleted() {
                                    BitmapUtils.galleryAddPic(getContext(), longImage.getAbsolutePath());
                                    longImageView.setImage(longImage.getAbsolutePath());
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
                longImageView.setImage(imagePath);
            }
        }else {
            imageView.setVisibility(VISIBLE);
            longImageView.setVisibility(GONE);
            Image image = new Image();
            image.setH(height);
            image.setW(width);
            image.setSize(size);
            image.setPath(imagePath);
            imageView.setImage(image);
            if(imagePath.startsWith("image")){
                imagePath = StringUtils.getUrl(getContext(), ApiService.URL_QINIU + imagePath, wh[0], wh[1], true, true);
            }
            if(FileUtil.isGif(imagePath)){
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.width = wh[0];
                layoutParams.height = wh[1];
                imageView.setLayoutParams(layoutParams);
                imageView.requestLayout();
                Glide.with(getContext())
                        .load(imagePath)
                        .asGif()
                        .override(wh[0], wh[1])
                        .dontAnimate()
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .into(imageView);
            }else {
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.width = wh[0];
                layoutParams.height = wh[1];
                imageView.setLayoutParams(layoutParams);
                imageView.requestLayout();
                Glide.with(getContext())
                        .load(imagePath)
                        .override(wh[0], wh[1])
                        .dontAnimate()
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .into(imageView);
            }
        }
        allLayout.addView(imageLayout, index);
    }

    /**
     * 在特定位置添加ImageView
     */
    public void addImageViewAtIndex(int index, String imagePath) {
        addImageViewAtIndex(index,imagePath,0,0,0);
    }

    /**
     * 对外提供的接口, 生成编辑数据上传
     */
    public List<RichEntity> buildEditData() {
        List<RichEntity> dataList = new ArrayList<>();
        int num = allLayout.getChildCount();
        for (int index = 0; index < num; index++) {
            View itemView = allLayout.getChildAt(index);
            RichEntity itemData = new RichEntity();
            if (itemView instanceof EditText) {
                EditText item = (EditText) itemView;
                if(!TextUtils.isEmpty(item.getText().toString())) {
                    itemData.setInputStr(item.getText());
                }else {
                    itemData.setInputStr("");
                }
                dataList.add(itemData);
            } else if (itemView instanceof RelativeLayout) {
                DataImageView item = (DataImageView) itemView.findViewById(R.id.edit_imageView);
                LongImageView itemL = (LongImageView) itemView.findViewById(R.id.edit_longImageView);
                Image image = new Image();
                if(item.getImage() != null){
                    image.setPath(item.getImage().getPath());
                    image.setH(item.getImage().getH());
                    image.setW(item.getImage().getW());
                    image.setSize(item.getImage().getSize());
                    itemData.setImage(image);
                }else {
                    image.setPath(itemL.getImage().getPath());
                    image.setH(itemL.getImage().getH());
                    image.setW(itemL.getImage().getW());
                    image.setSize(itemL.getImage().getSize());
                    itemData.setImage(image);
                }
                dataList.add(itemData);
            }
        }
        return dataList;
    }

    public ArrayList<DocTagEntity> getmTags() {
        return mTags;
    }

}
