package com.moemoe.lalala.app;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.GroupEntity;
import com.moemoe.lalala.model.entity.UserTopEntity;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.GroupNotificationMessageData;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rong.message.GroupNotificationMessage;

/**
 * 事件监听集合类
 * Created by yi on 2017/9/8.
 */

public class MoeMoeAppListener implements RongIMClient.OnReceiveMessageListener,
        RongIM.UserInfoProvider,
        RongIM.GroupInfoProvider,
        RongIM.GroupUserInfoProvider,
        RongIM.IGroupMembersProvider,
        RongIMClient.ConnectionStatusListener{

    private Context mContext;
    private static MoeMoeAppListener moeMoeAppListener;

    private MoeMoeAppListener(Context context){
        mContext = context;
        initListener();
    }

    public static void init(Context context){
        if(moeMoeAppListener == null){
            synchronized (MoeMoeAppListener.class){
                if(moeMoeAppListener == null){
                    moeMoeAppListener = new MoeMoeAppListener(context);
                }
            }
        }
    }

    public static MoeMoeAppListener getInstance(){return moeMoeAppListener;}

    public Context getContext(){return mContext;}

    private void initListener(){
        RongIM.setConnectionStatusListener(this);
        RongIM.setUserInfoProvider(this, true);
        RongIM.setGroupInfoProvider(this, true);
        RongIM.getInstance().setGroupMembersProvider(this);
        RongIM.setOnReceiveMessageListener(this);
        RongIM.getInstance().enableNewComingMessageIcon(true);
        RongIM.getInstance().enableUnreadMessageIcon(true);
        List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
        IExtensionModule defaultModule = null;
        if (moduleList != null) {
            for (IExtensionModule module : moduleList) {
                if (module instanceof DefaultExtensionModule) {
                    defaultModule = module;
                    break;
                }
            }
            if (defaultModule != null) {
                RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
                RongExtensionManager.getInstance().registerExtensionModule(new MoeMoeExtensionModule());
            }
        }
    }

    @Override
    public Group getGroupInfo(String s) {
        MoeMoeApplication.getInstance().getNetComponent().getApiService().loadGroup(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<GroupEntity>() {
                    @Override
                    public void onSuccess(GroupEntity entity) {
                        Group group = new Group(entity.getId(),entity.getGroupName(),Uri.parse(ApiService.URL_QINIU + entity.getCover()));
                        RongIM.getInstance().refreshGroupInfoCache(group);
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });

        return null;
    }

    @Override
    public GroupUserInfo getGroupUserInfo(final String s, final String s1) {
        MoeMoeApplication.getInstance().getNetComponent().getApiService().loadSampleUserInfo(s1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject jsonObject) {
                        if(jsonObject != null){
                            GroupUserInfo info = new GroupUserInfo(s,s1,jsonObject.get("userName").getAsString());
                            RongIM.getInstance().refreshGroupUserInfoCache(info);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
        return null;
    }

    @Override
    public void getGroupMembers(String s, final RongIM.IGroupMemberCallback iGroupMemberCallback) {
        MoeMoeApplication.getInstance().getNetComponent().getApiService().loadGroupMemberList(s,0,0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<UserTopEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<UserTopEntity> entities) {
                        List<UserInfo> userInfos = new ArrayList<>();
                        if (entities != null) {
                            for (UserTopEntity groupMember : entities) {
                                if (groupMember != null) {
                                    String path = groupMember.getHeadPath();
                                    if(!(path.startsWith("http") || path.startsWith("https"))){
                                        path = ApiService.URL_QINIU + path;
                                    }
                                    UserInfo userInfo = new UserInfo(groupMember.getUserId(), groupMember.getUserName(), Uri.parse(path));
                                    userInfos.add(userInfo);
                                }
                            }
                        }
                        iGroupMemberCallback.onGetGroupMembersResult(userInfos);
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
    }

    @Override
    public UserInfo getUserInfo(String s) {
        MoeMoeApplication.getInstance().getNetComponent().getApiService().loadSampleUserInfo(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject jsonObject) {
                        if(jsonObject != null){
                            String path = jsonObject.get("userIcon").getAsString();
                            if(!(path.startsWith("http") || path.startsWith("https"))){
                                path = ApiService.URL_QINIU + path;
                            }
                            UserInfo info = new UserInfo(jsonObject.get("userId").getAsString(),jsonObject.get("userName").getAsString(),Uri.parse(path));
                            RongIM.getInstance().refreshUserInfoCache(info);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
        return null;
    }

    @Override
    public void onChanged(ConnectionStatus connectionStatus) {

    }

    @Override
    public boolean onReceived(Message message, int i) {
        MessageContent messageContent = message.getContent();
        if (messageContent instanceof GroupNotificationMessage){
            GroupNotificationMessage groupNotificationMessage = (GroupNotificationMessage) messageContent;
            String groupID = message.getTargetId();
            GroupNotificationMessageData data = null;
            try {
                String currentID = RongIM.getInstance().getCurrentUserId();
                data = jsonToBean(groupNotificationMessage.getData());
                if (groupNotificationMessage.getOperation().equals("Dismiss")) {
                    handleGroupDismiss(groupID);
                } else if (groupNotificationMessage.getOperation().equals("Kicked")) {
                    if (data != null) {
                        List<String> memberIdList = data.getTargetUserIds();
                        if (memberIdList != null) {
                            for (String userId : memberIdList) {
                                if (currentID.equals(userId)) {
                                    RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, message.getTargetId(), new RongIMClient.ResultCallback<Boolean>() {
                                        @Override
                                        public void onSuccess(Boolean aBoolean) {
                                        }

                                        @Override
                                        public void onError(RongIMClient.ErrorCode e) {
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    private void handleGroupDismiss(final String groupID) {
        RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, groupID, new RongIMClient.ResultCallback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, groupID, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, groupID, null);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode e) {
                        int i = 0;
                    }
                });
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {

            }
        });
    }

    private GroupNotificationMessageData jsonToBean(String data) {
        GroupNotificationMessageData dataEntity = new GroupNotificationMessageData();
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("operatorNickname")) {
                dataEntity.setOperatorNickname(jsonObject.getString("operatorNickname"));
            }
            if (jsonObject.has("targetGroupName")) {
                dataEntity.setTargetGroupName(jsonObject.getString("targetGroupName"));
            }
            if (jsonObject.has("timestamp")) {
                dataEntity.setTimestamp(jsonObject.getLong("timestamp"));
            }
            if (jsonObject.has("targetUserIds")) {
                JSONArray jsonArray = jsonObject.getJSONArray("targetUserIds");
                for (int i = 0; i < jsonArray.length(); i++) {
                    dataEntity.getTargetUserIds().add(jsonArray.getString(i));
                }
            }
            if (jsonObject.has("targetUserDisplayNames")) {
                JSONArray jsonArray = jsonObject.getJSONArray("targetUserDisplayNames");
                for (int i = 0; i < jsonArray.length(); i++) {
                    dataEntity.getTargetUserDisplayNames().add(jsonArray.getString(i));
                }
            }
            if (jsonObject.has("oldCreatorId")) {
                dataEntity.setOldCreatorId(jsonObject.getString("oldCreatorId"));
            }
            if (jsonObject.has("oldCreatorName")) {
                dataEntity.setOldCreatorName(jsonObject.getString("oldCreatorName"));
            }
            if (jsonObject.has("newCreatorId")) {
                dataEntity.setNewCreatorId(jsonObject.getString("newCreatorId"));
            }
            if (jsonObject.has("newCreatorName")) {
                dataEntity.setNewCreatorName(jsonObject.getString("newCreatorName"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataEntity;
    }
}
