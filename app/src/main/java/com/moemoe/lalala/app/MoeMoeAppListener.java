package com.moemoe.lalala.app;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
        return null;
    }

    @Override
    public GroupUserInfo getGroupUserInfo(String s, String s1) {
        return null;
    }

    @Override
    public void getGroupMembers(String s, RongIM.IGroupMemberCallback iGroupMemberCallback) {

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
        return false;
    }
}
