package com.moemoe.lalala.view.adapter;

import android.content.Intent;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.DownloadEntity;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DownloadController;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.JuBaoActivity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import zlc.season.rxdownload2.RxDownload;
import zlc.season.rxdownload2.entity.DownloadEvent;
import zlc.season.rxdownload2.entity.DownloadFlag;
import zlc.season.rxdownload2.entity.DownloadStatus;

import static zlc.season.rxdownload2.function.Utils.dispose;
import static zlc.season.rxdownload2.function.Utils.empty;
import static zlc.season.rxdownload2.function.Utils.log;

/**
 * Created by yi on 2017/7/21.
 */

public class DownloadListHolder extends ClickableViewHolder {

    ImageView mImg;
    TextView mPercent;
    ProgressBar mProgress;
    TextView mSize;
    TextView mStatusText;
    Button mActionButton;
    TextView mName;
    Button mMore;

    private BaseRecyclerViewAdapter mAdapter;
    private DownloadController mDownloadController;
    private DownloadEntity data;
    private RxDownload mRxDownload;
    private int flag;

    public DownloadListHolder(View itemView,BaseRecyclerViewAdapter adapter) {
        super(itemView);
        mImg = $(R.id.img);
        mPercent = $(R.id.percent);
        mProgress = $(R.id.progress);
        mSize = $(R.id.size);
        mStatusText = $(R.id.status);
        mActionButton = $(R.id.action);
        mName = $(R.id.name);
        mMore = $(R.id.more);
        mAdapter = adapter;
        mRxDownload = RxDownload.getInstance(context).maxThread(1);
        mDownloadController = new DownloadController(mStatusText, mActionButton);
    }

    public void createItem(final DownloadEntity param){
        this.data = param;
        if (empty(param.record.getExtra1())) {
            Glide.with(context)
                    .load(R.mipmap.ic_file_download)
                    .into(mImg);
        } else {
            Glide.with(context)
                    .load(StringUtils.getUrl(context,param.record.getExtra1(), DensityUtil.dip2px(context,50),DensityUtil.dip2px(context,50),false,true))
                    .error(R.mipmap.ic_file_download)
                    .placeholder(R.mipmap.ic_file_download)
                    .into(mImg);
        }
        String name = empty(param.record.getExtra2()) ? param.record.getSaveName() : param.record.getExtra2();
        mName.setText(name);

        Observable<DownloadEvent> replayDownloadStatus = mRxDownload.receiveDownloadStatus(data.record.getUrl())
                .replay()
                .autoConnect();
        Observable<DownloadEvent> sampled = replayDownloadStatus
                .filter(new Predicate<DownloadEvent>() {
                    @Override
                    public boolean test(@NonNull DownloadEvent downloadEvent) throws Exception {
                        return downloadEvent.getFlag() == DownloadFlag.STARTED;
                    }
                })
                .throttleFirst(200, TimeUnit.MILLISECONDS);
        Observable<DownloadEvent> noProgress = replayDownloadStatus
                .filter(new Predicate<DownloadEvent>() {
                    @Override
                    public boolean test(@NonNull DownloadEvent downloadEvent) throws Exception {
                        return downloadEvent.getFlag() != DownloadFlag.STARTED;
                    }
                });
        data.disposable = Observable.merge(sampled, noProgress)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DownloadEvent>() {
                    @Override
                    public void accept(@NonNull DownloadEvent downloadEvent) throws Exception {
                        if (flag != downloadEvent.getFlag()) {
                            flag = downloadEvent.getFlag();
                        }
                        if (downloadEvent.getFlag() == DownloadFlag.FAILED) {
                            Throwable throwable = downloadEvent.getError();
                        }
                        mDownloadController.setEvent(downloadEvent);
                        updateProgressStatus(downloadEvent.getDownloadStatus());
                    }
                });
        mActionButton.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mDownloadController.handleClick(new DownloadController.Callback() {
                    @Override
                    public void startDownload() {
                        start();
                    }

                    @Override
                    public void pauseDownload() {
                        pause();
                    }

                    @Override
                    public void completeDownLoad() {
                        //complete();
                    }

                });
            }
        });
        mMore.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                showMenu();
            }
        });
    }

     private void updateProgressStatus(DownloadStatus status) {
        mProgress.setIndeterminate(status.isChunked);
        mProgress.setMax((int) status.getTotalSize());
        mProgress.setProgress((int) status.getDownloadSize());
        mPercent.setText(status.getPercent());
        mSize.setText(status.getFormatStatusString());
    }

//    private void complete(){
//        if("image".equals(data.record.getExtra3())){
//            BitmapUtils.galleryAddPic(context, data.record.getExtra4());
//            Toast.makeText(context, context.getString(R.string.msg_register_to_gallery_success, data.record.getExtra4()), Toast.LENGTH_SHORT).show();
//        }
//    }

    private void start() {
        mRxDownload.serviceDownload(data.record.getUrl())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Toast.makeText(context, "下载开始", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void pause() {
        mRxDownload.pauseServiceDownload(data.record.getUrl()).subscribe();
    }

    private void delete(boolean deleteFile){
        dispose(data.disposable);
        mRxDownload.deleteServiceDownload(data.record.getUrl(), deleteFile)
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mAdapter.removeItem(getAdapterPosition());
                    }
                })
                .subscribe();
    }

    private void showMenu(){
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(0,"删除记录");
        items.add(item);
        item = new MenuItem(1,"删除记录和文件");
        items.add(item);
        BottomMenuFragment fragment = new BottomMenuFragment();
        fragment.setShowTop(false);
        fragment.setMenuItems(items);
        fragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        fragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if(itemId == 0){
                    delete(false);
                }else if(itemId == 1){
                    delete(true);
                }
            }
        });
        fragment.show(((BaseAppCompatActivity)itemView.getContext()).getSupportFragmentManager(),"CommentMenu");
    }
}
