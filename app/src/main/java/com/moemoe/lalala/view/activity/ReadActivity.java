package com.moemoe.lalala.view.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFileComponent;
import com.moemoe.lalala.di.modules.FileModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BookInfo;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.presenter.FilesContract;
import com.moemoe.lalala.presenter.FilesPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.read.BaseReadView;
import com.moemoe.lalala.view.widget.read.OnReadStateChangeListener;
import com.moemoe.lalala.view.widget.read.OverlappedWidget;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zlc.season.rxdownload2.RxDownload;
import zlc.season.rxdownload2.entity.DownloadStatus;

/**
 * Created by yi on 2017/3/28.
 */

public class ReadActivity extends BaseAppCompatActivity implements FilesContract.View{

    private static final int REQ_SELECT_FOLDER = 5001;

    @BindView(R.id.fl_readWidget)
    FrameLayout mFlReadWidget;
    @BindView(R.id.ll_read_top)
    View mLlTopRoot;
    @BindView(R.id.ll_read_bottom)
    View mLlBottomRoot;
    @BindView(R.id.ll_read_set)
    View mLlSetRoot;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.seekbar_reading_progress)
    SeekBar mSeekBarRead;
    @BindView(R.id.tv_move)
    TextView mTvMove;
    @BindView(R.id.fl_delete_root)
    View mDelRoot;
    @BindView(R.id.fl_edit_root)
    View mEditRoot;

    @Inject
    FilesPresenter mPresenter;

    private Receiver receiver = new Receiver();
    private IntentFilter intentFilter = new IntentFilter();
    private BaseReadView mPageWidget;
    private View decodeView;
    private BookInfo mBook;
    private ArrayList<BookInfo> bookList;
    private String mBookId;
    private boolean mIsFromSD;
    private boolean startRead = false;
    private int currentChapter = 1;
    private RxDownload downloadSub;
    private String mUserId;
    private String mFolderId;
    private boolean change = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_read;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), null);
        DaggerFileComponent.builder()
                .fileModule(new FileModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mBook = getIntent().getParcelableExtra("book");
        bookList = getIntent().getParcelableArrayListExtra("books");
        mUserId = getIntent().getStringExtra("userId");
        mFolderId = getIntent().getStringExtra("folderId");
        if(mBook == null || bookList == null) finish();
        if(mUserId.equals(PreferenceUtils.getUUid())){
            mTvMove.setText("移动");
        }else {
            mTvMove.setText("存到我的书包");
            mDelRoot.setVisibility(View.GONE);
            mEditRoot.setVisibility(View.GONE);
        }
        mBookId = mBook.getId();
        mIsFromSD = mBook.isFromSD();
        mTvTitle.setText(mBook.getTitle());
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        //hideStatusBar();
        decodeView = getWindow().getDecorView();
        decodeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        //init set
        mSeekBarRead.setMax(100);
        mSeekBarRead.setOnSeekBarChangeListener(new SeekBarChangeListener());
        int a = PreferenceUtils.getReadProgress(this,mBookId)[2];
        int b = (int) new File(StorageUtils.getNovRootPath() + mBookId + File.separator + "1.txt").length();
        float progress = (float) a / b * 100;
        mSeekBarRead.setProgress((int) progress);

        mPageWidget = new OverlappedWidget(this, mBookId, new ReadListener());
        registerReceiver(receiver, intentFilter);
        mFlReadWidget.removeAllViews();
        mFlReadWidget.addView(mPageWidget);

        if(mIsFromSD){
            showChapterRead(1);
        }else {
            //目前没做网络阅读
            finish();
        }
        downloadSub = RxDownload.getInstance(this)
                .maxThread(3)
                .maxRetryCount(3)
                .defaultSavePath(StorageUtils.getNovRootPath())
                .retrofit(MoeMoeApplication.getInstance().getNetComponent().getRetrofit());
    }

    public void showChapterRead(int chapter){
        if (!startRead) {
            startRead = true;
            currentChapter = chapter;
            if (!mPageWidget.isPrepared) {
                mPageWidget.init(PreferenceUtils.isNight(this));
            } else {
                mPageWidget.jumpToChapter(currentChapter);
            }
        }
    }

    private synchronized void hideReadBar() {
        mLlTopRoot.setVisibility(View.GONE);
        mLlBottomRoot.setVisibility(View.GONE);
        mLlSetRoot.setVisibility(View.GONE);
       // hideStatusBar();
        decodeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private synchronized void showReadBar() { // 显示工具栏
        mLlTopRoot.setVisibility(View.VISIBLE);
        mLlBottomRoot.setVisibility(View.VISIBLE);
        mLlSetRoot.setVisibility(View.VISIBLE);
        //showStatusBar();
        decodeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private synchronized void toggleReadBar() { // 切换工具栏 隐藏/显示 状态
        if (mLlTopRoot.getVisibility() == View.VISIBLE) {
            hideReadBar();
        } else {
            showReadBar();
        }
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.iv_back,R.id.fl_down_root,R.id.fl_delete_root,R.id.fl_edit_root})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.fl_down_root:
                createDialog();
                downloadRaw();
                break;
            case R.id.fl_delete_root:
                ArrayList<String> ids = new ArrayList<>();
                ids.add(mBookId);
                createDialog();
                mPresenter.deleteFiles(mFolderId, FolderType.ZH.toString(),ids);
                break;
//            case R.id.fl_move_root:
//                Intent i = new Intent(ReadActivity.this,FolderSelectActivity.class);
//                i.putExtra("folderId",mFolderId);
//                startActivityForResult(i,REQ_SELECT_FOLDER);
//                break;
            case R.id.fl_edit_root:
                final EditText editText = new EditText(this);
                new AlertDialog.Builder(this).setTitle("文件名称")
                        .setView(editText)
                        .setPositiveButton(R.string.label_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(!TextUtils.isEmpty(editText.getText().toString())){
                                    createDialog();
                                    mPresenter.modifyFile(FolderType.ZH.toString(),mFolderId,mBookId,editText.getText().toString());
                                    dialogInterface.dismiss();
                                }else {
                                    showToast("文件名不能为空");
                                }
                            }
                        })
                        .setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
                break;
        }
    }

    private void downloadRaw() {
        finalizeDialog();
        showToast("文件已保存在" + StorageUtils.getNovRootPath() + mBook.getTitle());
    }

    @Override
    public void deleteFilesSuccess() {
        finalizeDialog();
        change = true;
        showToast("删除文件成功");
        onBackPressed();
    }

    @Override
    public void moveFilesSuccess() {
        finalizeDialog();
        change = true;
        showToast("移动文件成功");
        onBackPressed();
    }

    @Override
    public void modifyFileSuccess(String name) {
        finalizeDialog();
        change = true;
        mTvTitle.setText(name);
        mBook.setTitle(name);
        showToast("修改文件成功");
    }

    @Override
    public void copyFileSuccess() {
        finalizeDialog();
        change = true;
        showToast("转存文件成功");
    }

    @Override
    public void onFailure(int code, String msg) {
        finalizeDialog();
        showToast("操作失败");
    }

    @Override
    public void onBackPressed() {
        if(change){
            Intent i = new Intent();
            i.putExtra("change",change);
            i.putExtra("number",1);
            setResult(RESULT_OK,i);
        }
        finish();
    }

    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mPageWidget != null) {
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    int level = intent.getIntExtra("level", 0);
                    mPageWidget.setBattery(100 - level);
                }
            }
        }
    }


    private class ReadListener implements OnReadStateChangeListener {

        @Override
        public void onChapterChanged(int chapter) {
            currentChapter = chapter;
           // mTocListAdapter.setCurrentChapter(currentChapter);
            // 加载前一节 与 后三节
//            for (int i = chapter - 1; i <= chapter + 3 && i <= mChapterList.size(); i++) {
//                if (i > 0 && i != chapter
//                        && CacheManager.getInstance().getChapterFile(bookId, i) == null) {
//                    mPresenter.getChapterRead(mChapterList.get(i - 1).link, i);
//                }
//            }
        }

        @Override
        public void onPageChanged(int chapter, int page) {
        }

        @Override
        public void onLoadChapterFailure(int chapter) {
            startRead = false;
            if (FileUtil.getChapterFile(mBookId, chapter) == null)
//                mPresenter.getChapterRead(mChapterList.get(chapter - 1).link, chapter);
                showToast("该文本不存在");
        }

        @Override
        public void onCenterClick() {
            toggleReadBar();
        }

        @Override
        public void onFlip() {
            hideReadBar();
        }

        @Override
        public void onBookFinish(String bookId) {
            if(bookList.size() > 0){
                if(FileUtil.isExists(StorageUtils.getNovRootPath() + bookList.get(0).getId() + File.separator + "1.txt")){
                    mBook = bookList.get(0);
                    mBookId = bookList.get(0).getId();
                    mTvTitle.setText(mBook.getTitle());
                    mPageWidget.setBookId(mBookId);
                    mSeekBarRead.setMax(100);
                    int a = PreferenceUtils.getReadProgress(ReadActivity.this,mBookId)[2];
                    int b = (int) new File(StorageUtils.getNovRootPath() + mBookId + File.separator + "1.txt").length();
                    float progress = (float) a / b * 100;
                    mSeekBarRead.setProgress((int) progress);
                    startRead = false;
                    currentChapter = 1;
                    showChapterRead(1);
                    bookList.remove(0);
                }else {
                    final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                    alertDialogUtil.createPromptNormalDialog(ReadActivity.this, "是否继续阅读下一本");
                    alertDialogUtil.setButtonText(getString(R.string.label_confirm), getString(R.string.label_cancel),0);
                    alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                        @Override
                        public void CancelOnClick() {
                            alertDialogUtil.dismissDialog();
                        }

                        @Override
                        public void ConfirmOnClick() {
                            String temp = "1.txt";
                            File file = new File(StorageUtils.getNovRootPath() + bookList.get(0).getId());
                            if(file.mkdir()){
                                final ProgressDialog dialog = new ProgressDialog(ReadActivity.this);
                                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
                                dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
                                dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                                dialog.setIcon(R.drawable.ic_launcher);// 设置提示的title的图标，默认是没有的
                                dialog.setTitle("下载中");
                                downloadSub.download(ApiService.URL_QINIU +  bookList.get(0).getPath(),temp,StorageUtils.getNovRootPath() + bookList.get(0).getId())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<DownloadStatus>() {
                                            @Override
                                            public void onError(Throwable e) {
                                                dialog.dismiss();
                                                FileUtil.deleteDir(StorageUtils.getNovRootPath() + bookList.get(0).getId());
                                                showToast("下载失败");
                                                downloadSub.deleteServiceDownload(ApiService.URL_QINIU +  bookList.get(0).getPath(),false).subscribe();
                                            }

                                            @Override
                                            public void onComplete() {
                                                dialog.dismiss();
                                                mBook = bookList.get(0);
                                                mBookId = bookList.get(0).getId();
                                                mTvTitle.setText(mBook.getTitle());
                                                mPageWidget.setBookId(mBookId);
                                                mSeekBarRead.setMax(100);
                                                int a = PreferenceUtils.getReadProgress(ReadActivity.this,mBookId)[2];
                                                int b = (int) new File(StorageUtils.getNovRootPath() + mBookId + File.separator + "1.txt").length();
                                                float progress = (float) a / b * 100;
                                                mSeekBarRead.setProgress((int) progress);
                                                startRead = false;
                                                currentChapter = 1;
                                                showChapterRead(1);
                                                bookList.remove(0);
                                                downloadSub.deleteServiceDownload(ApiService.URL_QINIU +  bookList.get(0).getPath(),false).subscribe();
                                            }

                                            @Override
                                            public void onSubscribe(@NonNull Disposable d) {

                                            }

                                            @Override
                                            public void onNext(DownloadStatus downloadStatus) {
                                                dialog.setMax((int) downloadStatus.getTotalSize());
                                                dialog.setProgress((int) downloadStatus.getDownloadSize());
                                            }
                                        });
                                dialog.show();
                            }
                            alertDialogUtil.dismissDialog();
                        }
                    });
                    alertDialogUtil.showDialog();
                }
            }

        }
    }

    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar.getId() == mSeekBarRead.getId() && fromUser) {
                if (mPageWidget != null) {
                    mPageWidget.setPercent(progress);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {

        }
        super.onDestroy();
    }
}

