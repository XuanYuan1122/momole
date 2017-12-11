package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.greendao.gen.GroupNoticeEntityDao;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;

import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Conversation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by yi on 2017/9/7.
 */

public class ConversationListAdapterNew extends ConversationListAdapter {
    LayoutInflater mInflater;
    Context mContext;
    private OnPortraitItemClick mOnPortraitItemClick;
    private String showRed = "";


    public void setShowRed(String showRed) {
        this.showRed = showRed;
    }

    public ConversationListAdapterNew(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

//    @Override
//    public long getItemId(int position) {
//        UIConversation conversation = this.getItem(position);
//        return conversation == null?0L:(long)conversation.hashCode();
//    }
//
//    @Override
//    public void remove(int position) {
//        if(position > 3){
//           super.remove(position - 4);
//        }
//    }
//
//    @Override
//    public int findGatheredItem(Conversation.ConversationType type) {
//        int index = this.getCount();
//        int position = -1;
//
//        while(index-- > 0) {
//            UIConversation uiConversation = this.getItem(index);
//            if(uiConversation.getConversationType().equals(type)) {
//                position = index;
//                break;
//            }
//        }
//
//        return position;
//    }
//
//    @Override
//    public int findPosition(Conversation.ConversationType type, String targetId) {
//        int index = this.getCount();
//        int position = -1;
//
//        while(index-- > 0) {
//            if((this.getItem(index)).getConversationType().equals(type) && (this.getItem(index)).getConversationTargetId().equals(targetId)) {
//                position = index;
//                break;
//            }
//        }
//        return position;
//    }

    @Override
    protected View newView(Context context, int position, ViewGroup group) {
        View result = mInflater.inflate(R.layout.item_msg, null);
        ViewHolder holder = new ViewHolder();
        holder.cover = findViewById(result, R.id.iv_cover);
        holder.name = findViewById(result,R.id.tv_name);
        holder.content = findViewById(result,R.id.tv_content);
        holder.time = findViewById(result,R.id.tv_time);
        holder.dot = findViewById(result,R.id.tv_dot);
        result.setTag(holder);
        return result;
    }

//    @Override
//    public UIConversation getItem(int position) {
//        if(position > 3){
//            return super.getItem(position - 4);
//        }else {
//            UIConversation item = new UIConversation();
//            item.setConversationType(Conversation.ConversationType.PRIVATE);
//            item.setLatestMessageId(-1);
//            item.setTop(true);
//            if(position == 0){
//                item.setConversationTargetId("len");
//                item.setUIConversationTitle("len");
//                item.setUIConversationTime(Long.MAX_VALUE);
//            }
//            if(position == 1){
//                item.setConversationTargetId("mei");
//                item.setUIConversationTitle("mei");
//                item.setUIConversationTime(Long.MAX_VALUE - 1);
//            }
//            if(position == 2){
//                item.setConversationTargetId("sari");
//                item.setUIConversationTitle("sari");
//                item.setUIConversationTime(Long.MAX_VALUE - 2);
//            }
//            if(position == 3){
//                item.setConversationTargetId("kira_system");
//                item.setUIConversationTitle("kira_system");
//                item.setUIConversationTime(Long.MAX_VALUE - 3);
//            }
//            return item;
//        }
//    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    protected void bindView(View v, int position, final UIConversation data) {
        ViewHolder holder = (ViewHolder)v.getTag();
        if(data != null) {
         //   if(position > 3){
            if(data.getConversationTargetId().equals(showRed)){
                holder.dot.setVisibility(View.VISIBLE);
                holder.dot.setText("1");
            }else {
                holder.dot.setVisibility(View.GONE);
            }
            if("len".equals(data.getConversationTargetId())){
                holder.cover.setImageResource(R.drawable.ic_phone_message_len);
                holder.name.setText("小莲");
                holder.content.setText(PreferenceUtils.getLenLastContent(mContext));
            }else if("mei".equals(data.getConversationTargetId())){
                holder.cover.setImageResource(R.drawable.ic_phone_message_mei);
                holder.name.setText("美藤双树");
                holder.content.setText(PreferenceUtils.getMeiLastContent(mContext));
            }else if("sari".equals(data.getConversationTargetId())){
                holder.cover.setImageResource(R.drawable.ic_phone_message_sari);
                holder.name.setText("沙利尔");
                holder.content.setText(PreferenceUtils.getSariLastContent(mContext));
            }else if("kira_system".equals(data.getConversationTargetId())){
                holder.cover.setImageResource(R.drawable.ic_phone_message_notice);
                holder.name.setText("群通知");
                int count = PreferenceUtils.getGroupDotNum(mContext);
                if(count > 0){
                    holder.dot.setVisibility(View.VISIBLE);
                    if(count > 99){
                        count = 99;
                    }
                    holder.dot.setText(String.valueOf(count));
                }else {
                    holder.dot.setVisibility(View.GONE);
                }
                String[] contentAndTime = PreferenceUtils.getLastGroupContentAndTime(mContext);
                holder.content.setText(contentAndTime[0]);
            }else {

                //cover
                int size = (int) mContext.getResources().getDimension(R.dimen.y90);
                int cor = (int) mContext.getResources().getDimension(R.dimen.y8);

                Glide.with(mContext)
                        .load(data.getIconUrl() != null ? data.getIconUrl().toString() : "")
                        .override(size,size)
                        .error(R.drawable.bg_default_square)
                        .placeholder(R.drawable.bg_default_square)
                        .bitmapTransform(new CropSquareTransformation(mContext),new RoundedCornersTransformation(mContext,cor,0))
                        .into(holder.cover);
                holder.cover.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        if(mOnPortraitItemClick != null) {
                            mOnPortraitItemClick.onPortraitItemClick(v, data);
                        }
                    }
                });
                holder.cover.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(mOnPortraitItemClick != null) {
                            mOnPortraitItemClick.onPortraitItemLongClick(v, data);
                        }
                        return true;
                    }
                });
                holder.name.setText(data.getUIConversationTitle());
                holder.time.setText(StringUtils.timeFormat(data.getUIConversationTime()));
                holder.content.setText(data.getConversationContent());
                if(data.getUnReadMessageCount() > 0) {
                    holder.dot.setVisibility(View.VISIBLE);
                    int count = data.getUnReadMessageCount();
                    if(count > 99) count = 99;
                    holder.dot.setText(String.valueOf(count));
                }else {
                    holder.dot.setVisibility(View.GONE);
                }
            }

         //   }else {
//                if(showRed == position){
//                    holder.dot.setVisibility(View.VISIBLE);
//                    holder.dot.setText("1");
//                }else {
//                    holder.dot.setVisibility(View.GONE);
//                }
//                if(position == 0){
//                    holder.cover.setImageResource(R.drawable.ic_phone_message_len);
//                    holder.name.setText("小莲");
//                    holder.content.setText(PreferenceUtils.getLenLastContent(mContext));
//                }else if(position == 1){
//                    holder.cover.setImageResource(R.drawable.ic_phone_message_mei);
//                    holder.name.setText("美藤双树");
//                    holder.content.setText(PreferenceUtils.getMeiLastContent(mContext));
//                }else if(position == 2){
//                    holder.cover.setImageResource(R.drawable.ic_phone_message_sari);
//                    holder.name.setText("沙利尔");
//                    holder.content.setText(PreferenceUtils.getSariLastContent(mContext));
//                }else if(position == 3){
//                    holder.cover.setImageResource(R.drawable.ic_phone_message_notice);
//                    holder.name.setText("群通知");
//                    int count = PreferenceUtils.getGroupDotNum(mContext);
//                    if(count > 0){
//                        holder.dot.setVisibility(View.VISIBLE);
//                        if(count > 99){
//                            count = 99;
//                        }
//                        holder.dot.setText(String.valueOf(count));
//                    }else {
//                        holder.dot.setVisibility(View.GONE);
//                    }
//                    String[] contentAndTime = PreferenceUtils.getLastGroupContentAndTime(mContext);
//                    holder.content.setText(contentAndTime[0]);
//                }
          //  }
        }
    }

    @Override
    public void setOnPortraitItemClick(OnPortraitItemClick onPortraitItemClick) {
        this.mOnPortraitItemClick = onPortraitItemClick;
    }

    class ViewHolder {
        ImageView cover;
        TextView name,content,time,dot;

        ViewHolder() {
        }
    }
}
