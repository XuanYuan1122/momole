package com.moemoe.lalala.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerChatComponent;
import com.moemoe.lalala.di.modules.ChatModule;
import com.moemoe.lalala.event.ChatEvent;
import com.moemoe.lalala.event.PrivateMessageEvent;
import com.moemoe.lalala.greendao.gen.ChatContentDbEntityDao;
import com.moemoe.lalala.greendao.gen.ChatUserEntityDao;
import com.moemoe.lalala.greendao.gen.GroupUserEntityDao;
import com.moemoe.lalala.greendao.gen.PrivateMessageItemEntityDao;
import com.moemoe.lalala.model.entity.ChatContentDbEntity;
import com.moemoe.lalala.model.entity.ChatContentEntity;
import com.moemoe.lalala.model.entity.ChatUserEntity;
import com.moemoe.lalala.model.entity.PrivateMessageItemEntity;
import com.moemoe.lalala.model.entity.SendPrivateMsgEntity;
import com.moemoe.lalala.presenter.ChatContract;
import com.moemoe.lalala.presenter.ChatPresenter;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.ChatAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2017/3/10.
 */

public class ChatActivity extends BaseAppCompatActivity implements ChatContract.View{
    private final int REQ_SECRET = 1004;
    @BindView(R.id.iv_back)
    View mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_menu_list)
    ImageView mIvSet;
    @BindView(R.id.tv_pingbi)
    View mPingBi;
    @BindView(R.id.edt_comment_input)
    EditText mEdtCommentInput;
    @BindView(R.id.ll_comment_pannel)
    KeyboardListenerLayout mKlCommentBoard;
    @BindView(R.id.iv_comment_send)
    View mTvSendComment;
    @Inject
    ChatPresenter mPresenter;

    private ChatAdapter mAdapter;
    private String mTalkId;
    private boolean mIsLoading = false;
    private int mCurOffset;
    private boolean mIsShield;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_comments_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerChatComponent.builder()
                .chatModule(new ChatModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
//        ImmersionBar.with(this)
//                .statusBarView(R.id.top_view)
//                .statusBarDarkFont(true,0.2f)
//                .transparentNavigationBar()
//                .keyboardEnable(true)
//                .init();
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);
        initBase();
        mKlCommentBoard.setVisibility(View.VISIBLE);
        mIvSet.setImageResource(R.drawable.privately_setup_blue);
        mIvSet.setVisibility(View.VISIBLE);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mAdapter = new ChatAdapter(this);
        mListDocs.setLoadMoreEnabled(false);
        LinearLayoutManager m = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return !mIsLoading;
            }

            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
        };
       // m.setStackFromEnd(true);
        mListDocs.setLayoutManager(m);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mEdtCommentInput.setHint("");
        mTvSendComment.setEnabled(false);
        initFromDb();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initBase();
        initFromDb();
    }

    private void initBase(){
        mTalkId = getIntent().getStringExtra("talkId");
        if(TextUtils.isEmpty(mTalkId)){
            finish();
        }
        AppSetting.sCurChatId = mTalkId;
        String title = getIntent().getStringExtra("title");
        RxBus.getInstance().post(new PrivateMessageEvent(false,mTalkId,false));
        mTvTitle.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTvTitle.setText(title);
    }

    private void initFromDb(){
        //从数据库读取数据，若没有则新建并请求是否有历史
        mCurOffset = 0;
        PrivateMessageItemEntityDao messageItemEntityDao = GreenDaoManager.getInstance().getSession().getPrivateMessageItemEntityDao();
        PrivateMessageItemEntity entity = messageItemEntityDao.queryBuilder()
                .where(PrivateMessageItemEntityDao.Properties.TalkId.eq(mTalkId))
                .limit(1)
                .unique();
        if(entity == null) {
            finish();
        }
        entity.setDot(0);
        messageItemEntityDao.update(entity);
        mIsShield = entity.isState();
        if(mIsShield){
            mPingBi.setVisibility(View.VISIBLE);
        }else {
            mPingBi.setVisibility(View.GONE);
        }
        if(entity.isNew()){
            mPresenter.loadTalkHistory(mTalkId);
        }else {
            //查询是否有丢失
            ChatContentDbEntityDao dao = GreenDaoManager.getInstance().getSession().getChatContentDbEntityDao();
            ChatContentDbEntity entity1 = dao.queryBuilder()//读取未读的第一条
                    .orderAsc(ChatContentDbEntityDao.Properties.CreateTime)
                    .where(ChatContentDbEntityDao.Properties.State.eq(0))
                    .limit(1)
                    .unique();
            ChatContentDbEntity entity2 = dao.queryBuilder()//读取已读的最后一条
                    .orderDesc(ChatContentDbEntityDao.Properties.CreateTime)
                    .where(ChatContentDbEntityDao.Properties.State.eq(1))
                    .limit(1).unique();
            if(entity1 != null && entity2 != null){
                if(entity1.getCreateTime().getTime() > entity2.getCreateTime().getTime()){
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
                    mPresenter.findTalk(mTalkId,format.format(entity2.getCreateTime()),format.format(entity1.getCreateTime()));
                }else {
                    readFromDb(mCurOffset);
                }
            }else {
                readFromDb(mCurOffset);
            }
        }
        StringBuilder builder = new StringBuilder("update ").append(ChatContentDbEntityDao.TABLENAME);
        builder.append(" set ").append(ChatContentDbEntityDao.Properties.State.columnName).append(" = 1 ");
        builder.append(" where ").append(ChatContentDbEntityDao.Properties.TalkId.columnName).append(" = ").append("'").append(mTalkId).append("'");
        GreenDaoManager.getInstance().getSession().getDatabase().execSQL(builder.toString());
    }

    private static final Comparator<ChatContentEntity> timeComparator = new Comparator<ChatContentEntity>() {
        @Override
        public int compare(ChatContentEntity lhs, ChatContentEntity rhs) {
            if(lhs.getCreateTime().getTime() > rhs.getCreateTime().getTime()){
                return 1;
            }else if(lhs.getCreateTime().getTime() < rhs.getCreateTime().getTime()){
                return -1;
            }else {
                return 0;
            }
        }
    };

    private boolean readFromDb(int offset) {
        boolean res = false;
        ChatContentDbEntityDao dao = GreenDaoManager.getInstance().getSession().getChatContentDbEntityDao();
        List<ChatContentDbEntity> list = dao.queryBuilder()
                .where(ChatContentDbEntityDao.Properties.TalkId.eq(mTalkId))
                .orderDesc(ChatContentDbEntityDao.Properties.CreateTime)
                .limit(20 * offset)
                .limit(20)
                .list();
        if(list.size() < 20){
            mListDocs.getSwipeRefreshLayout().setEnabled(false);
        }else {
            mCurOffset++;
            mListDocs.getSwipeRefreshLayout().setEnabled(true);
        }
        if(list.size() > 0)  res = true;
        ArrayList<ChatContentEntity> data = new ArrayList<>();
        for(ChatContentDbEntity entity : list){
            data.add(new ChatContentEntity(entity));
        }
        Collections.sort(data,timeComparator);
        if(offset == 0){
            mAdapter.setData(data);
            mListDocs.getRecyclerView().scrollToPosition(mAdapter.getItemCount() - 1);
        }else {
            mAdapter.addData(data);
        }
        mIsLoading = false;
        mListDocs.setComplete();
        return res;
    }

    private ArrayList<Object> addTimeInList(List<ChatContentEntity> list){
        ArrayList<Object> res = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return res;
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
        mEdtCommentInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!mIsShield){
                    if (TextUtils.isEmpty(s.toString())) {
                        mTvSendComment.setEnabled(false);
                    } else {
                        mTvSendComment.setEnabled(true);
                    }
                }
            }
        });
        mIvSet.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent i = new Intent(ChatActivity.this,ChatSettingActivity.class);
                i.putExtra("talk_id",mTalkId);
                i.putExtra("shield",mIsShield);
                startActivityForResult(i,REQ_SECRET);
            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Object o = mAdapter.getItem(position);
                if(o instanceof ChatContentEntity){
                    ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("绅士内容", ((ChatContentEntity) o).getContent());
                    cmb.setPrimaryClip(mClipData);
                    ToastUtils.showShortToast(ChatActivity.this, "复制成功");
                }
            }
        });
        mTvSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendChat();
            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                readFromDb(mCurOffset);
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
        subscribeEvent();
    }

    private void subscribeEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(ChatEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Action1<ChatEvent>() {
                    @Override
                    public void call(ChatEvent event) {
                        if(event.getEntity().getTalkId().equals(mTalkId)){
                            ArrayList<ChatContentEntity> list = new ArrayList<>();
                            event.getEntity().setState(true);
                            ChatContentDbEntityDao dbEntityDao = GreenDaoManager.getInstance().getSession().getChatContentDbEntityDao();
                            dbEntityDao.update(event.getEntity());
                            list.add(new ChatContentEntity(event.getEntity()));
                            mAdapter.addData(list);
                            mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
                            LinearLayoutManager layoutManager = (LinearLayoutManager) mListDocs.getRecyclerView().getLayoutManager();
                            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                            if(lastVisibleItemPosition == mAdapter.getItemCount() - 2){
                                mListDocs.getRecyclerView().scrollToPosition(mAdapter.getItemCount() - 1);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().unSubscribe(this);
        RxBus.getInstance().addSubscription(this, subscription);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_SECRET && resultCode == RESULT_OK){
            mIsShield = data.getBooleanExtra("shield",mIsShield);
            if(mIsShield){
                mPingBi.setVisibility(View.VISIBLE);
            }else {
                mPingBi.setVisibility(View.GONE);
            }
            boolean delete = data.getBooleanExtra("delete",false);
            if (delete){
                //删除相关记录并退出
                //会话列表
                PrivateMessageItemEntityDao dao = GreenDaoManager.getInstance().getSession().getPrivateMessageItemEntityDao();
                dao.deleteByKey(mTalkId);
                //中间表
                GroupUserEntityDao dao1 = GreenDaoManager.getInstance().getSession().getGroupUserEntityDao();
                dao1.queryBuilder()
                        .where(GroupUserEntityDao.Properties.TalkId.eq(mTalkId))
                        .buildDelete()
                        .executeDeleteWithoutDetachingEntities();
                //记录表
                ChatContentDbEntityDao dao2 = GreenDaoManager.getInstance().getSession().getChatContentDbEntityDao();
                dao2.queryBuilder()
                        .where(ChatContentDbEntityDao.Properties.TalkId.eq(mTalkId))
                        .buildDelete()
                        .executeDeleteWithoutDetachingEntities();
                RxBus.getInstance().post(new PrivateMessageEvent(false,mTalkId,true));
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        AppSetting.sCurChatId = "";
        RxBus.getInstance().unSubscribe(this);
        PrivateMessageItemEntityDao messageItemEntityDao = GreenDaoManager.getInstance().getSession().getPrivateMessageItemEntityDao();
        PrivateMessageItemEntity entity = messageItemEntityDao.queryBuilder()
                .where(PrivateMessageItemEntityDao.Properties.TalkId.eq(mTalkId))
                .limit(1)
                .unique();
        if(entity != null){
            entity.setDot(0);
            messageItemEntityDao.update(entity);
        }
        super.onDestroy();
    }

    private void sendChat() {
        if(!NetworkUtils.checkNetworkAndShowError(this)){
            return;
        }
        if (DialogUtils.checkLoginAndShowDlg(this)) {
            String content = mEdtCommentInput.getText().toString();
            if(TextUtils.isEmpty(content)){
                showToast(R.string.msg_doc_comment_not_empty);
                return;
            }
            SoftKeyboardUtils.dismissSoftKeyboard(this);
            createDialog();
            SendPrivateMsgEntity entity = new SendPrivateMsgEntity(content,mTalkId,"TEXT");
            mPresenter.sendMsg(entity);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    public void loadOrFindTalkHistorySuccess(ArrayList<ChatContentEntity> entities) {
        //插入数据库
        updateRes(entities);
    }

    private void updateRes(ArrayList<ChatContentEntity> entities){
        ChatContentDbEntityDao chatContentEntityDao = GreenDaoManager.getInstance().getSession().getChatContentDbEntityDao();
        ArrayList<ChatContentDbEntity> chatContentDbEntities = new ArrayList<>();
        for(ChatContentEntity entity : entities){
            chatContentDbEntities.add(new ChatContentDbEntity(entity));
        }
        if (entities.size() > 0){
            chatContentEntityDao.insertOrReplaceInTx(chatContentDbEntities);
        }
        ChatContentDbEntity contentDbEntity = chatContentEntityDao.queryBuilder()
                .where(ChatContentDbEntityDao.Properties.TalkId.eq(mTalkId))
                .orderDesc(ChatContentDbEntityDao.Properties.CreateTime)
                .limit(1)
                .unique();
        PrivateMessageItemEntityDao messageItemEntityDao = GreenDaoManager.getInstance().getSession().getPrivateMessageItemEntityDao();
        PrivateMessageItemEntity privateMessageItemEntity = messageItemEntityDao.queryBuilder()
                .where(PrivateMessageItemEntityDao.Properties.TalkId.eq(mTalkId))
                .limit(1)
                .unique();
        if(privateMessageItemEntity != null){
            privateMessageItemEntity.setNew(false);
            privateMessageItemEntity.setDot(0);
            if(contentDbEntity != null){
                privateMessageItemEntity.setContent(contentDbEntity.getContent());
                privateMessageItemEntity.setUpdateTime(contentDbEntity.getCreateTime());
            }
            messageItemEntityDao.insertOrReplace(privateMessageItemEntity);
        }
        readFromDb(mCurOffset);
    }

    @Override
    public void sendMsgSuccess(String id) {
        //发送成功存入数据库
        finalizeDialog();
        ChatContentDbEntityDao chatContentEntityDao = GreenDaoManager.getInstance().getSession().getChatContentDbEntityDao();
        ChatContentDbEntity entity = new ChatContentDbEntity();
        entity.setContent(mEdtCommentInput.getText().toString());
        entity.setState(true);
        entity.setId(id);
        entity.setContentType("TEXT");
        entity.setCreateTime(new Date(System.currentTimeMillis()));
        entity.setTalkId(mTalkId);
        entity.setUserId(PreferenceUtils.getUUid());
        ChatUserEntity entity1 = new ChatUserEntity();
        entity1.setUserId(PreferenceUtils.getUUid());
        entity1.setUserName(PreferenceUtils.getAuthorInfo().getUserName());
        entity1.setUserIcon(PreferenceUtils.getAuthorInfo().getHeadPath());
        entity.setUser(entity1);
        chatContentEntityDao.insert(entity);
        ChatUserEntityDao userEntityDao = GreenDaoManager.getInstance().getSession().getChatUserEntityDao();
        userEntityDao.insertOrReplace(entity1);
        mEdtCommentInput.setText("");
        ArrayList<ChatContentEntity> list = new ArrayList<>();
        ChatContentEntity show = new ChatContentEntity(entity);
        list.add(show);
        mAdapter.addData(list);
        mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
        mListDocs.getRecyclerView().scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void loadOrFindTalkFailure() {
        showToast("发生异常，请稍后再试");
        finish();
    }

    @Override
    public void onFailure(int code, String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }
}
