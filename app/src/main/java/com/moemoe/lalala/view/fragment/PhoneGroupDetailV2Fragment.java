package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerPhoneGroupDetailComponent;
import com.moemoe.lalala.di.modules.PhoneGroupDetailModule;
import com.moemoe.lalala.model.entity.GroupEntity;
import com.moemoe.lalala.presenter.PhoneGroupDetailContract;
import com.moemoe.lalala.presenter.PhoneGroupDetailPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.activity.IPhoneFragment;
import com.moemoe.lalala.view.activity.PhoneMainV2Activity;
import com.moemoe.lalala.view.activity.SearchActivity;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.app.Activity.RESULT_OK;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_ALT_USER;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_GROUP_DETAIL;

/**
 * 群详情界面
 * Created by yi on 2017/9/4.
 */

public class PhoneGroupDetailV2Fragment extends BaseFragment implements IPhoneFragment,PhoneGroupDetailContract.View{

    @BindView(R.id.iv_cover)
    ImageView mIvCover;
    @BindView(R.id.tv_group_name)
    TextView mTvGroupName;
    @BindView(R.id.tv_group_desc)
    TextView mTvGroupDesc;
    @BindView(R.id.tv_member_num)
    TextView mTvMemberNum;
    @BindView(R.id.iv_member_1)
    ImageView mIvMember1;
    @BindView(R.id.iv_member_2)
    ImageView mIvMember2;
    @BindView(R.id.iv_member_3)
    ImageView mIvMember3;
    @BindView(R.id.iv_member_add)
    ImageView mIvMemberAdd;
    @BindView(R.id.tv_add_or_leave)
    TextView mTvAddOrLeave;
    @BindView(R.id.rl_msg_root)
    View mMsgRoot;
    @BindView(R.id.iv_indicate_img)
    ImageView mIvMian;

    @Inject
    PhoneGroupDetailPresenter mPresenter;
    private boolean isOwn;
    private String id;
    private String userId;

    public static PhoneGroupDetailV2Fragment newInstance(String id){
        PhoneGroupDetailV2Fragment fragment = new PhoneGroupDetailV2Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_phone_group_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPhoneGroupDetailComponent.builder()
                .phoneGroupDetailModule(new PhoneGroupDetailModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        id = getArguments().getString("id");
        mPresenter.loadGroupInfo(id);
    }

    @Override
    public void onFailure(int code, String msg) {
        ErrorCodeUtils.showErrorMsgByCode(getContext(),code,msg);
    }

    @OnClick({R.id.tv_all_member,R.id.iv_all_member})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_all_member:
            case R.id.iv_all_member:
                ((PhoneMainV2Activity)getContext()).toFragment(PhoneGroupMemberListV2Fragment.newInstance(id,userId,isOwn));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPresenter != null) mPresenter.release();
    }

    @Override
    public void onLoadGroupInfoSuccess(final GroupEntity entity) {
        isOwn = entity.getCreateUser().equals(PreferenceUtils.getUUid());
        userId = entity.getCreateUser();
        if(isOwn){
            ((PhoneMainV2Activity)getContext()).setMenu(getMenu());
            mIvMemberAdd.setVisibility(View.VISIBLE);
            mIvMemberAdd.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent i3 = new Intent(getContext(),SearchActivity.class);
                    i3.putExtra("show_type",SearchActivity.SHOW_USER);
                    startActivityForResult(i3,REQ_ALT_USER);
                }
            });
        }else {
            ((PhoneMainV2Activity)getContext()).setMenu(0);
            mIvMemberAdd.setVisibility(View.GONE);
        }
        int h = (int) getResources().getDimension(R.dimen.y260);
        Glide.with(this)
                .load(StringUtils.getUrl(getContext(),entity.getCover(), DensityUtil.getScreenWidth(getContext()),h,false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .into(mIvCover);
        mTvGroupName.setText(entity.getGroupName());
        mTvGroupDesc.setText(entity.getDesc());
        mTvMemberNum.setText(entity.getUsers() + " 人");

        if(entity.getUserList().size() > 0){
            int size = (int) getResources().getDimension(R.dimen.x88);
            mIvMember1.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(StringUtils.getUrl(getContext(),entity.getUserList().get(0).getHeadPath(),size,size,false,true))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .into(mIvMember1);
            mIvMember1.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    ViewUtils.toPersonal(getContext(),entity.getUserList().get(0).getUserId());
                }
            });
            if(entity.getUserList().size() > 1){
                mIvMember2.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(StringUtils.getUrl(getContext(),entity.getUserList().get(1).getHeadPath(),size,size,false,true))
                        .error(R.drawable.bg_default_circle)
                        .placeholder(R.drawable.bg_default_circle)
                        .bitmapTransform(new CropCircleTransformation(getContext()))
                        .into(mIvMember2);
                mIvMember2.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        ViewUtils.toPersonal(getContext(),entity.getUserList().get(1).getUserId());
                    }
                });
            }
            if(entity.getUserList().size() > 2){
                mIvMember3.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(StringUtils.getUrl(getContext(),entity.getUserList().get(2).getHeadPath(),size,size,false,true))
                        .error(R.drawable.bg_default_circle)
                        .placeholder(R.drawable.bg_default_circle)
                        .bitmapTransform(new CropCircleTransformation(getContext()))
                        .into(mIvMember3);
                mIvMember3.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        ViewUtils.toPersonal(getContext(),entity.getUserList().get(2).getUserId());
                    }
                });
            }
        }
        if(isOwn){
            mMsgRoot.setVisibility(View.VISIBLE);
            mTvAddOrLeave.setText("解散该群");
        }else {
            if(entity.isJoin()){
                mMsgRoot.setVisibility(View.VISIBLE);
                mTvAddOrLeave.setText("退出该群");
            }else {
                mMsgRoot.setVisibility(View.GONE);
                mTvAddOrLeave.setText("申请加入");
            }
        }
        RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, id, new RongIMClient.ResultCallback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                if (conversation == null) {
                    return;
                }
                if (conversation.getNotificationStatus() == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB) {
                    mIvMian.setSelected(true);
                } else {
                    mIvMian.setSelected(false);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {

            }
        });
        mTvAddOrLeave.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(isOwn){
                    mPresenter.dismissGroup(entity.getId());
                }else {
                    if(entity.isJoin()){
                        mPresenter.quitGroup(entity.getId());
                    }else {
                        if(entity.isAuthority()){
                            mPresenter.joinAuthor(entity.getId());
                        }else {
                            mPresenter.applyJoinGroup(entity.getId());
                        }
                    }
                }
            }
        });
        mIvMian.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                RongIM.getInstance().setConversationNotificationStatus(Conversation.ConversationType.GROUP, id, mIvMian.isSelected()?Conversation.ConversationNotificationStatus.NOTIFY:Conversation.ConversationNotificationStatus.DO_NOT_DISTURB, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                    @Override
                    public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                        mIvMian.setSelected(!mIvMian.isSelected());
                        if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB) {
                            ToastUtils.showShortToast(getContext(),"设置免打扰成功");
                        } else if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.NOTIFY) {
                            ToastUtils.showShortToast(getContext(),"取消免打扰成功");
                        }

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        ToastUtils.showShortToast(getContext(),"设置失败");
                    }
                });
            }
        });
    }


    @Override
    public void onApplySuccess() {
        ToastUtils.showShortToast(getContext(),"操作成功");
    }

    @Override
    public void onQuitOrDismissSuccess(boolean isQuit) {
        ToastUtils.showShortToast(getContext(),"操作成功");
        RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, id, new RongIMClient.ResultCallback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, id, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, id, null);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode e) {
                    }
                });
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {

            }
        });
        ((PhoneMainV2Activity)getContext()).onBackPressed();
        Intent i = new Intent();
        i.putExtra("is_quit",isQuit);
        ((PhoneMainV2Activity)getContext()).onFragmentResult(REQ_GROUP_DETAIL,-1,i);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_ALT_USER && resultCode == RESULT_OK){
            if(data != null){
                String userId = data.getStringExtra("user_id");
                mPresenter.inviteJoin(userId,id);
            }
        }
    }

    @Override
    public String getTitle() {
        return "群详情";
    }

    @Override
    public int getTitleColor() {
        return R.color.main_cyan;
    }

    @Override
    public int getMenu() {
        return R.drawable.btn_menu_normal;
    }

    @Override
    public int getBack() {
        return R.drawable.btn_phone_back;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onMenuClick() {
        if(isOwn){
            BottomMenuFragment mBottomMenuFragment = new BottomMenuFragment();
            final ArrayList<MenuItem> items = new ArrayList<>();
            MenuItem item = new MenuItem(1, "修改群信息");
            items.add(item);
            mBottomMenuFragment.setMenuItems(items);
            mBottomMenuFragment.setShowTop(false);
            mBottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
            mBottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
                @Override
                public void OnMenuItemClick(int itemId) {
                if(DialogUtils.checkLoginAndShowDlg(getContext())){
                    if (itemId == 1) {
                        ((PhoneMainV2Activity)getContext()).toFragment(PhoneEditGroupV2Fragment.newInstance("update",id));
                    }
                }
                }
            });
            mBottomMenuFragment.show(getChildFragmentManager(),"PhoneGroupDetail");
        }
    }
}
