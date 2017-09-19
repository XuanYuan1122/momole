package com.moemoe.lalala.rongyun;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.moemoe.lalala.utils.DialogUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

import io.rong.imkit.IExtensionClickListener;
import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.image.PictureSelectorActivity;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imlib.model.Conversation;

/**
 * Created by yi on 2017/9/11.
 */

public class MoeMoeImagePlugin implements IPluginModule {

    Conversation.ConversationType conversationType;
    String targetId;
    Context context;
    RongExtension rongExtension;

    public MoeMoeImagePlugin(){
    }

    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, io.rong.imkit.R.drawable.rc_ext_plugin_image_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(io.rong.imkit.R.string.rc_plugin_image);
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE"};
        if(PermissionCheckUtil.requestPermissions(fragment, permissions)) {
            this.conversationType = rongExtension.getConversationType();
            this.targetId = rongExtension.getTargetId();
            context = fragment.getContext();
            this.rongExtension = rongExtension;
            try {
                ArrayList<String> temp = new ArrayList<>();
                DialogUtils.createImgChooseDlg(rongExtension, fragment,this, temp, 9).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {
        DialogUtils.handleImgChooseResult(context, i, i1, intent, new DialogUtils.OnPhotoGetListener() {

            @Override
            public void onPhotoGet(final ArrayList<String> photoPaths, boolean override) {
                ArrayList<Uri> res = new ArrayList<>();
                for(String s : photoPaths){
                    res.add(Uri.parse("file://" + s));
                }
                Field fields[]   =   rongExtension.getClass().getDeclaredFields();
                Object  o  = null;
                try{
                    Field.setAccessible(fields,   true);
                    for   (int   i   =   0;   i   <   fields.length;   i++)   {
                        String name = fields[i].getName();
                        if(name.equals("mExtensionClickListener")){
                            o = fields[i].get(rongExtension);
                            break;
                        }
                    }
                    if(o != null && o instanceof IExtensionClickListener){
                        ((IExtensionClickListener) o).onImageResult(res,false);
                    }
                }
                catch(Exception   e){
                    e.printStackTrace();
                }
            }
        });
    }
}
