package com.moemoe.lalala.view.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadList;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.DownloadEntity;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.TasksManager;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.JuBaoActivity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

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

    /**
     * viewHolder position
     */
    int position;
    /**
     * download id
     */
    int id;
    DownloadEntity data;

    private BaseRecyclerViewAdapter mAdapter;
//    private DownloadController mDownloadController;
//    private DownloadEntity data;
//    private RxDownload mRxDownload;
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
      //  mRxDownload = RxDownload.getInstance(context).maxThread(1);
      //  mDownloadController = new DownloadController(mStatusText, mActionButton);
    }

    public void update(final int id, final int position) {
        this.id = id;
        this.position = position;
    }

    public void updateDownloaded() {
        mProgress.setMax(100);
        mProgress.setProgress(100);
        mPercent.setText("100%");
        mSize.setText("");
        mStatusText.setText("下载完成");
        mActionButton.setText("完成");
    }

    public void updateNotDownloaded(final int status, final long sofar, final long total) {
        if (sofar > 0 && total > 0) {
            final float percent = sofar
                    / (float) total;
            mProgress.setMax(100);
            mProgress.setProgress((int) (percent * 100));
            mPercent.setText(((int) (percent * 100)) + "%");
            mSize.setText(FileUtil.formatFileSizeToString(sofar) + "/" + FileUtil.formatFileSizeToString(total));
        } else {
            mProgress.setMax(1);
            mProgress.setProgress(0);
        }

        switch (status) {
            case FileDownloadStatus.error:
                mStatusText.setText("下载出错");
                break;
            case FileDownloadStatus.paused:
                mStatusText.setText("下载暂停");
                break;
            default:
                mStatusText.setText("");
                break;
        }
        mActionButton.setText("开始");
    }

    public void updateDownloading(final int status, final long sofar, final long total) {
        final float percent = sofar
                / (float) total;
        mProgress.setMax(100);
        mProgress.setProgress((int) (percent * 100));
        mPercent.setText(((int) (percent * 100)) + "%");
        mSize.setText(FileUtil.formatFileSizeToString(sofar) + "/" + FileUtil.formatFileSizeToString(total));
        switch (status) {
            case FileDownloadStatus.pending:
                mStatusText.setText("等待中");
                break;
            case FileDownloadStatus.started:
                mStatusText.setText("开始下载");
                break;
            case FileDownloadStatus.connected:
                mStatusText.setText("连接中");
                break;
            case FileDownloadStatus.progress:
                mStatusText.setText("下载中");
                break;
            default:
                mStatusText.setText("");
                break;
        }

        mActionButton.setText("暂停");
    }

    public void createItem(final DownloadEntity param,int position){

        update(param.getId().intValue(), position);
        mActionButton.setTag(this);
        if (TextUtils.isEmpty(param.getUrl())) {
            Glide.with(context)
                    .load(R.mipmap.ic_file_download)
                    .into(mImg);
        } else {
            Glide.with(context)
                    .load(StringUtils.getUrl(context,param.getUrl(), (int)context.getResources().getDimension(R.dimen.y100),(int)context.getResources().getDimension(R.dimen.y100),false,true))
                    .error(R.mipmap.ic_file_download)
                    .placeholder(R.mipmap.ic_file_download)
                    .into(mImg);
        }
        mName.setText(param.getFileName());
        TasksManager.getImpl()
                .updateViewHolder(id, this);
        final int status = TasksManager.getImpl().getStatus(param.getId().intValue(), param.getPath());
        this.data = param;
        if (status == FileDownloadStatus.pending || status == FileDownloadStatus.started ||
                status == FileDownloadStatus.connected) {
            // start task, but file not created yet
            updateDownloading(status, TasksManager.getImpl().getSoFar(param.getId().intValue())
                    , TasksManager.getImpl().getTotal(param.getId().intValue()));
        } else if (!new File(param.getPath()).exists() &&
                !new File(FileDownloadUtils.getTempPath(param.getPath())).exists()) {
            // not exist file
            updateNotDownloaded(status, 0, 0);
        } else if (TasksManager.getImpl().isDownloaded(status)) {
            // already downloaded and exist
            updateDownloaded();
        } else if (status == FileDownloadStatus.progress) {
            // downloading
            updateDownloading(status, TasksManager.getImpl().getSoFar(param.getId().intValue())
                    , TasksManager.getImpl().getTotal(param.getId().intValue()));
         ///   FileDownloader.getImpl().pause(id);


            BaseDownloadTask.IRunningTask taskI =  FileDownloadList.getImpl().get(id);
            BaseDownloadTask task = taskI.getOrigin();
            TasksManager.getImpl()
                    .addTaskForViewHolder(task);
            TasksManager.getImpl()
                    .updateViewHolder(id, this);
            task.setListener(((DownloadListAdapter)mAdapter).taskDownloadListener);

//            BaseDownloadTask task = FileDownloader.getImpl().create(StringUtils.getUrl(param.getUrl()))
//                    .setPath(param.getPath())
//                    .setCallbackProgressTimes(1000)
//                    .setListener(((DownloadListAdapter)mAdapter).taskDownloadListener);
//            task.start();
        } else {
            // not start
            updateNotDownloaded(status, TasksManager.getImpl().getSoFar(param.getId().intValue())
                    , TasksManager.getImpl().getTotal(param.getId().intValue()));
        }
        mActionButton.setOnClickListener(((DownloadListAdapter)mAdapter).taskActionOnClickListener);
//        if (empty(param.record.getExtra1())) {
//            Glide.with(context)
//                    .load(R.mipmap.ic_file_download)
//                    .into(mImg);
//        } else {
//            Glide.with(context)
//                    .load(StringUtils.getUrl(context,param.record.getExtra1(), DensityUtil.dip2px(context,50),DensityUtil.dip2px(context,50),false,true))
//                    .error(R.mipmap.ic_file_download)
//                    .placeholder(R.mipmap.ic_file_download)
//                    .into(mImg);
//        }
//        String name = empty(param.record.getExtra2()) ? param.record.getSaveName() : param.record.getExtra2();
//        mName.setText(name);
//
//        Observable<DownloadEvent> replayDownloadStatus = mRxDownload.receiveDownloadStatus(data.record.getUrl())
//                .replay()
//                .autoConnect();
//        Observable<DownloadEvent> sampled = replayDownloadStatus
//                .filter(new Predicate<DownloadEvent>() {
//                    @Override
//                    public boolean test(@NonNull DownloadEvent downloadEvent) throws Exception {
//                        return downloadEvent.getFlag() == DownloadFlag.STARTED;
//                    }
//                })
//                .throttleFirst(200, TimeUnit.MILLISECONDS);
//        Observable<DownloadEvent> noProgress = replayDownloadStatus
//                .filter(new Predicate<DownloadEvent>() {
//                    @Override
//                    public boolean test(@NonNull DownloadEvent downloadEvent) throws Exception {
//                        return downloadEvent.getFlag() != DownloadFlag.STARTED;
//                    }
//                });
//        data.disposable = Observable.merge(sampled, noProgress)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<DownloadEvent>() {
//                    @Override
//                    public void accept(@NonNull DownloadEvent downloadEvent) throws Exception {
//                        if (flag != downloadEvent.getFlag()) {
//                            flag = downloadEvent.getFlag();
//                        }
//                        if (downloadEvent.getFlag() == DownloadFlag.FAILED) {
//                            Throwable throwable = downloadEvent.getError();
//                        }
//                        mDownloadController.setEvent(downloadEvent);
//                        updateProgressStatus(downloadEvent.getDownloadStatus());
//                    }
//                });
//        mActionButton.setOnClickListener(new NoDoubleClickListener() {
//            @Override
//            public void onNoDoubleClick(View v) {
//                mDownloadController.handleClick(new DownloadController.Callback() {
//                    @Override
//                    public void startDownload() {
//                        start();
//                    }
//
//                    @Override
//                    public void pauseDownload() {
//                        pause();
//                    }
//
//                    @Override
//                    public void completeDownLoad() {
//                        //complete();
//                    }
//
//                });
//            }
//        });
        mMore.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                showMenu();
            }
        });
    }

//     private void updateProgressStatus(DownloadStatus status) {
//        mProgress.setIndeterminate(status.isChunked);
//        mProgress.setMax((int) status.getTotalSize());
//        mProgress.setProgress((int) status.getDownloadSize());
//        mPercent.setText(status.getPercent());
//        mSize.setText(status.getFormatStatusString());
//    }

//    private void complete(){
//        if("image".equals(data.record.getExtra3())){
//            BitmapUtils.galleryAddPic(context, data.record.getExtra4());
//            Toast.makeText(context, context.getString(R.string.msg_register_to_gallery_success, data.record.getExtra4()), Toast.LENGTH_SHORT).show();
//        }
//    }

    private void start() {
//        mRxDownload.serviceDownload(data.record.getUrl())
//                .subscribe(new Consumer<Object>() {
//                    @Override
//                    public void accept(Object o) throws Exception {
//                        Toast.makeText(context, "下载开始", Toast.LENGTH_SHORT).show();
//                    }
//                });
    }

//    private void pause() {
//        mRxDownload.pauseServiceDownload(data.record.getUrl()).subscribe();
//    }
//
    private void delete(boolean deleteFile){
//        dispose(data.disposable);
//        mRxDownload.deleteServiceDownload(data.record.getUrl(), deleteFile)
//                .doFinally(new Action() {
//                    @Override
//                    public void run() throws Exception {
//                        mAdapter.removeItem(getAdapterPosition());
//                    }
//                })
//                .subscribe();
        if(deleteFile){
            FileUtil.deleteFile(data.getPath());
        }
        TasksManager.getImpl().removeItem(position);
        mAdapter.setList(TasksManager.getImpl().getAll());
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
