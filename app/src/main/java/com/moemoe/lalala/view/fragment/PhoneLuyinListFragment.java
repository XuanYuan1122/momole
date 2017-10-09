package com.moemoe.lalala.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerPhoneLuYinComponent;
import com.moemoe.lalala.di.modules.PhoneLuYinModule;
import com.moemoe.lalala.event.MateBackPressEvent;
import com.moemoe.lalala.event.MateLuyinEvent;
import com.moemoe.lalala.event.PhonePlayMusicEvent;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.LuYinEntity;
import com.moemoe.lalala.netamusic.data.model.PlayList;
import com.moemoe.lalala.netamusic.data.model.Song;
import com.moemoe.lalala.netamusic.player.IPlayBack;
import com.moemoe.lalala.netamusic.player.Player;
import com.moemoe.lalala.presenter.PhoneLuYinPresenter;
import com.moemoe.lalala.presenter.PhoneLuyinContract;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.view.activity.PhoneMainActivity;
import com.moemoe.lalala.view.adapter.PhoneLuYinListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2017/9/4.
 */

public class PhoneLuyinListFragment extends BaseFragment implements PhoneLuyinContract.View,IPlayBack.Callback{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @Inject
    PhoneLuYinPresenter mPresenter;
    private PhoneLuYinListAdapter mAdapter;
    private boolean isLoading = false;
    private String mate;
    private Player mPlayer;

    public static PhoneLuyinListFragment newInstance(String type,String mate){
        PhoneLuyinListFragment fragment = new PhoneLuyinListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type",type);
        bundle.putString("mate",mate);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPhoneLuYinComponent.builder()
                .phoneLuYinModule(new PhoneLuYinModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        final String type = getArguments().getString("type");
        mate = getArguments().getString("mate");
        mPlayer = Player.getInstance(getContext());
        mPlayer.registerCallback(this);
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter= new PhoneLuYinListAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                final LuYinEntity entity = mAdapter.getItem(position);
                if(!entity.isFlag()){
                    final AlertDialogUtil dialogUtil = AlertDialogUtil.getInstance();
                    dialogUtil.createPromptNormalDialog(getContext(),"是否消耗录音券解锁");
                    dialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                        @Override
                        public void CancelOnClick() {
                            dialogUtil.dismissDialog();
                        }

                        @Override
                        public void ConfirmOnClick() {
                            mPresenter.unlockLuYin(entity.getId(),position);
                            dialogUtil.dismissDialog();
                        }
                    });
                    dialogUtil.showDialog();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadLuYinList(type,mate,mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
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
        mPresenter.loadLuYinList(type,mate,0);
        subscribeBackOrChangeEvent();
    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }

    private void subscribeBackOrChangeEvent() {
        Disposable subscription = RxBus.getInstance()
                .toObservable(PhonePlayMusicEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Consumer<PhonePlayMusicEvent>() {
                    @Override
                    public void accept(PhonePlayMusicEvent phonePlayMusicEvent) throws Exception {

                        if(phonePlayMusicEvent.isPlay()){
                            Song mMusicInfo = new Song();
                            mMusicInfo.setPath(ApiService.URL_QINIU + phonePlayMusicEvent.getPath());
                            mMusicInfo.setDisplayName(phonePlayMusicEvent.getPath());
                            mMusicInfo.setDuration(phonePlayMusicEvent.getTimestamp());
                            PlayList playList = new PlayList(mMusicInfo);
                            mPlayer.play(playList,0);
                            int position = mAdapter.getPlayingPosition();
                            mAdapter.setPlayingPosition(phonePlayMusicEvent.getPosition());
                            if(position >= 0){
                                mAdapter.notifyItemChanged(position);
                            }
                            mAdapter.notifyItemChanged(phonePlayMusicEvent.getPosition());
                        }else {
                            if(mPlayer.isPlaying()){
                                mPlayer.pause();
                                mAdapter.setPlayingPosition(-1);
                                mAdapter.notifyItemChanged(phonePlayMusicEvent.getPosition());
                            }
                        }
                    }
                }, new Consumer<Throwable>() {

                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        mPlayer.pause();
        mPlayer.unregisterCallback(this);
        RxBus.getInstance().unSubscribe(this);
        super.release();
    }

    @Override
    public void onLoadLuYinListSuccess(ArrayList<LuYinEntity> luYinEntities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if (luYinEntities.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            mListDocs.setLoadMoreEnabled(false);
        }
        if(isPull){
            mAdapter.setList(luYinEntities);
        }else {
            mAdapter.addList(luYinEntities);
        }
    }

    @Override
    public void onUnlockSuccess(int position) {
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onSwitchLast(@Nullable Song last) {

    }

    @Override
    public void onSwitchNext(@Nullable Song next) {

    }

    @Override
    public void onComplete(@Nullable Song next) {
        mPlayer.seekTo(0);
        int position = mAdapter.getPlayingPosition();
        mAdapter.setPlayingPosition(-1);
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {

    }
}
