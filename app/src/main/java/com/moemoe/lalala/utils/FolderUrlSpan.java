package com.moemoe.lalala.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.view.activity.NewFileCommonActivity;
import com.moemoe.lalala.view.activity.NewFileManHuaActivity;
import com.moemoe.lalala.view.activity.NewFileXiaoshuoActivity;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public class FolderUrlSpan extends ClickableSpan {

    private Context mContext;
    private String folderId;
    private String userId;
    private String folderType;

    public FolderUrlSpan(Context context, String userId, String folderId,String folderType){
        mContext = context;
        this.userId = userId;
        this.folderId = folderId;
        this. folderType = folderType;
    }
    @Override
    public void onClick(View widget) {
        if(folderType.equals(FolderType.MH.toString())){
            NewFileManHuaActivity.startActivity(mContext,folderType,folderId,userId);
        }else if(folderType.equals(FolderType.XS.toString())){
            NewFileXiaoshuoActivity.startActivity(mContext,folderType,folderId,userId);
        }else {
            NewFileCommonActivity.startActivity(mContext,folderType,folderId,userId);
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(ContextCompat.getColor(mContext,R.color.main_cyan));
        ds.setUnderlineText(false);
    }

}