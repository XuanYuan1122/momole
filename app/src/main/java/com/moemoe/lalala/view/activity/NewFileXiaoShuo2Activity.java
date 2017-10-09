package com.moemoe.lalala.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.CommonFileEntity;
import com.moemoe.lalala.model.entity.FileXiaoShuoEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ManHua2Entity;
import com.moemoe.lalala.model.entity.NewFolderEntity;
import com.moemoe.lalala.presenter.NewFolderItemContract;
import com.moemoe.lalala.presenter.NewFolderItemPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.FileCommonAdapter;
import com.moemoe.lalala.view.adapter.XiaoShuoAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import zlc.season.rxdownload2.RxDownload;
import zlc.season.rxdownload2.entity.DownloadStatus;

/**
 * 通常文件列表
 * Created by yi on 2017/8/20.
 */

public class NewFileXiaoShuo2Activity extends BaseAppCompatActivity{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_left_menu)
    TextView mTvMenuLeft;
    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_add_folder)
    ImageView mIvAddFolder;

    private String mFolderName;
    private XiaoShuoAdapter mAdapter;
    private View mBottomView;
    private ArrayList<FileXiaoShuoEntity> mManHualist;
    private int mPosition;
    private RxDownload downloadSub;
    private int mbBufferLen;
    private MappedByteBuffer mbBuff;
    private int curEndPos;
    private String charset;
    private boolean isLoading = false;
    private String mUserId;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_folder;
    }

    public static void startActivity(Context context,ArrayList<FileXiaoShuoEntity> entities,String userId,int position){
        Intent i = new Intent(context,NewFileXiaoShuo2Activity.class);
        i.putExtra(UUID,userId);
        i.putParcelableArrayListExtra("folders",entities);
        i.putExtra("position",position);
        context.startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        downloadSub = RxDownload.getInstance(this)
                .maxThread(3)
                .maxRetryCount(3)
                .defaultSavePath(StorageUtils.getNovRootPath())
                .retrofit(MoeMoeApplication.getInstance().getNetComponent().getRetrofit());
        mUserId = getIntent().getStringExtra(UUID);
        mManHualist = getIntent().getParcelableArrayListExtra("folders");
        mPosition = getIntent().getIntExtra("position",0);
        mIvAddFolder.setVisibility(View.GONE);
        mListDocs.setPadding(0,0,0,0);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new XiaoShuoAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.getRecyclerView().setVerticalScrollBarEnabled(true);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setLoadMoreEnabled(true);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                loadTxt();
            }

            @Override
            public void onRefresh() {

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
        mFolderName = mManHualist.get(mPosition).getFileName();
        mTvMenuLeft.setText(mFolderName);
        createBottomView();
        initTxt();
    }

    private void initTxt(){
        File file = new File(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId(),mManHualist.get(mPosition).getFileName());
        long length = file.length();
        if(length > 10){
            mbBufferLen = (int) length;
            try {
                mbBuff = new RandomAccessFile(file,"r")
                        .getChannel()
                        .map(FileChannel.MapMode.READ_ONLY,0,length);
                charset = FileUtil.getCharset(file.getAbsolutePath());
                byte[] parabuffer = readParagraphForward(curEndPos);
                curEndPos += parabuffer.length;
                String strParagraph = "";
                try {
                    strParagraph = new String(parabuffer, charset);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                strParagraph = strParagraph.replaceAll("\r\n", "  ")
                        .replaceAll("\n", " "); // 段落中的换行符去掉，绘制的时候再换行
                mAdapter.addItem(strParagraph);
                mAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadTxt(){
        byte[] parabuffer = readParagraphForward(curEndPos);
        curEndPos += parabuffer.length;
        String strParagraph = "";
        try {
            strParagraph = new String(parabuffer, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        strParagraph = strParagraph.replaceAll("\r\n", "  ")
                .replaceAll("\n", " "); // 段落中的换行符去掉，绘制的时候再换行
        if(TextUtils.isEmpty(strParagraph)){
            mAdapter.setEnableLoadMore(false);
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
        }else {
            mAdapter.addItem(strParagraph);
            mAdapter.notifyDataSetChanged();
        }
        isLoading = false;
        mListDocs.setComplete();
    }

    /**
     * 读取下一段落
     *
     * @param curEndPos 当前页结束位置指针
     * @return
     */
    private byte[] readParagraphForward(int curEndPos) {
        byte b0;
        int i = curEndPos;
        while (i < mbBufferLen) {
            b0 = mbBuff.get(i++);
//            if (b0 == 0x0a) {
//                break;
//            }
            if(i - curEndPos >= 1024 * 30){
                break;
            }
        }
        int nParaSize = i - curEndPos;
        byte[] buf = new byte[nParaSize];
        for (i = 0; i < nParaSize; i++) {
            buf[i] = mbBuff.get(curEndPos + i);
        }
        return buf;
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
        mTvMenuLeft.setTextColor(ContextCompat.getColor(NewFileXiaoShuo2Activity.this,R.color.black_1e1e1e));
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

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
        markPre.setVisibility(View.GONE);
        markNext.setVisibility(View.GONE);
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
            titlePre.setText(mManHualist.get(mPosition - 1).getFileName());
            preRoot.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    mPosition--;
                    if(FileUtil.isExists(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId() + File.separator + mManHualist.get(mPosition).getFileName())){
                        NewFileXiaoShuo2Activity.startActivity(NewFileXiaoShuo2Activity.this,mManHualist,mUserId,mPosition);
                    }else {
                        File file = new File(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId());
                        if(file.mkdir()){
                            final ProgressDialog dialog = new ProgressDialog(NewFileXiaoShuo2Activity.this);
                            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
                            dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
                            dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                            dialog.setIcon(R.drawable.ic_launcher);// 设置提示的title的图标，默认是没有的
                            dialog.setTitle("下载中");
                            downloadSub.download(ApiService.URL_QINIU +  mManHualist.get(mPosition).getPath(),mManHualist.get(mPosition).getFileName(),StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<DownloadStatus>() {
                                        @Override
                                        public void onError(Throwable e) {
                                            dialog.dismiss();
                                            FileUtil.deleteDir(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId());
                                            showToast("下载失败");
                                        }

                                        @Override
                                        public void onComplete() {
                                            dialog.dismiss();
                                            NewFileXiaoShuo2Activity.startActivity(NewFileXiaoShuo2Activity.this,mManHualist,mUserId,mPosition);
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
                    }
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
            titleNext.setText(mManHualist.get(mPosition + 1).getFileName());
            nextRoot.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    mPosition++;
                    if(FileUtil.isExists(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId() + File.separator + mManHualist.get(mPosition).getFileName())){
                        NewFileXiaoShuo2Activity.startActivity(NewFileXiaoShuo2Activity.this,mManHualist,mUserId,mPosition);
                    }else {
                        File file = new File(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId());
                        if(file.mkdir()){
                            final ProgressDialog dialog = new ProgressDialog(NewFileXiaoShuo2Activity.this);
                            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
                            dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
                            dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                            dialog.setIcon(R.drawable.ic_launcher);// 设置提示的title的图标，默认是没有的
                            dialog.setTitle("下载中");
                            downloadSub.download(ApiService.URL_QINIU +  mManHualist.get(mPosition).getPath(),mManHualist.get(mPosition).getFileName(),StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<DownloadStatus>() {

                                        @Override
                                        public void onError(Throwable e) {
                                            dialog.dismiss();
                                            FileUtil.deleteDir(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId());
                                            showToast("下载失败");
                                        }

                                        @Override
                                        public void onComplete() {
                                            dialog.dismiss();
                                            NewFileXiaoShuo2Activity.startActivity(NewFileXiaoShuo2Activity.this,mManHualist,mUserId,mPosition);
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
                    }
                    finish();
                }
            });
        }
    }
}
