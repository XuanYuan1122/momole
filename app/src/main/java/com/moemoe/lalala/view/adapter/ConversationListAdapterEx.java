package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;

import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.ProviderContainerView;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Conversation;

/**
 * Created by yi on 2017/9/7.
 */

public class ConversationListAdapterEx extends ConversationListAdapter {
    LayoutInflater mInflater;
    Context mContext;
    private ConversationListAdapter.OnPortraitItemClick mOnPortraitItemClick;

    public ConversationListAdapterEx(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    protected View newView(Context context, int position, ViewGroup group) {
        View result = mInflater.inflate(io.rong.imkit.R.layout.rc_item_conversation, null);
        ViewHolder holder = new ViewHolder();
        holder.layout = findViewById(result, io.rong.imkit.R.id.rc_item_conversation);
        holder.leftImageLayout = findViewById(result, io.rong.imkit.R.id.rc_item1);
        holder.rightImageLayout = findViewById(result, io.rong.imkit.R.id.rc_item2);
        holder.leftImageView = (AsyncImageView)findViewById(result, io.rong.imkit.R.id.rc_left);
        holder.rightImageView = (AsyncImageView)findViewById(result, io.rong.imkit.R.id.rc_right);
        holder.contentView = (ProviderContainerView)findViewById(result, io.rong.imkit.R.id.rc_content);
        holder.unReadMsgCount = (TextView)findViewById(result, io.rong.imkit.R.id.rc_unread_message);
        holder.unReadMsgCountRight = (TextView)findViewById(result, io.rong.imkit.R.id.rc_unread_message_right);
        holder.unReadMsgCountIcon = (ImageView)findViewById(result, io.rong.imkit.R.id.rc_unread_message_icon);
        holder.unReadMsgCountRightIcon = (ImageView)findViewById(result, io.rong.imkit.R.id.rc_unread_message_icon_right);
        result.setTag(holder);
        return result;
    }

    @Override
    protected void bindView(View v, int position, final UIConversation data) {
        if (data != null) {
            if (data.getConversationType().equals(Conversation.ConversationType.DISCUSSION))
                data.setUnreadType(UIConversation.UnreadRemindType.REMIND_ONLY);
        }
      //  super.bindView(v, position, data);



        ViewHolder holder = (ViewHolder)v.getTag();
        if(data != null) {
            IContainerItemProvider.ConversationProvider provider = RongContext.getInstance().getConversationTemplate(data.getConversationType().getName());
            if(provider == null) {
                RLog.e("ConversationListAdapter", "provider is null");
            } else {
                View view = holder.contentView.inflate(provider);
                provider.bindView(view, position, data);
                holder.layout.setBackgroundDrawable(mContext.getResources().getDrawable(io.rong.imkit.R.drawable.btn_white_border_selector_6));
                ConversationProviderTag tag = RongContext.getInstance().getConversationProviderTag(data.getConversationType().getName());
                boolean defaultId = false;
                int defaultId1;
                if(tag.portraitPosition() == 1) {
                    holder.leftImageLayout.setVisibility(View.VISIBLE);
                    if(data.getConversationType().equals(Conversation.ConversationType.GROUP)) {
                        defaultId1 = io.rong.imkit.R.drawable.rc_default_group_portrait;
                    } else if(data.getConversationType().equals(Conversation.ConversationType.DISCUSSION)) {
                        defaultId1 = io.rong.imkit.R.drawable.rc_default_discussion_portrait;
                    } else {
                        defaultId1 = io.rong.imkit.R.drawable.rc_default_portrait;
                    }

                    holder.leftImageLayout.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if(mOnPortraitItemClick != null) {
                                mOnPortraitItemClick.onPortraitItemClick(v, data);
                            }

                        }
                    });
                    holder.leftImageLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            if(mOnPortraitItemClick != null) {
                                mOnPortraitItemClick.onPortraitItemLongClick(v, data);
                            }
                            return true;
                        }
                    });
                    if(data.getConversationGatherState()) {
                        holder.leftImageView.setAvatar((String)null, defaultId1);
                    } else if(data.getIconUrl() != null) {
                        holder.leftImageView.setAvatar(data.getIconUrl().toString(), defaultId1);
                    } else {
                        holder.leftImageView.setAvatar((String)null, defaultId1);
                    }

                    if(data.getUnReadMessageCount() > 0) {
                        holder.unReadMsgCountIcon.setVisibility(View.VISIBLE);
                        if(data.getUnReadType().equals(UIConversation.UnreadRemindType.REMIND_WITH_COUNTING)) {
                            if(data.getUnReadMessageCount() > 99) {
                                holder.unReadMsgCount.setText(this.mContext.getResources().getString(io.rong.imkit.R.string.rc_message_unread_count));
                            } else {
                                holder.unReadMsgCount.setText(Integer.toString(data.getUnReadMessageCount()));
                            }

                            holder.unReadMsgCount.setVisibility(View.VISIBLE);
                           // holder.unReadMsgCountIcon.setImageResource(io.rong.imkit.R.drawable.shape_rect_phone_msg);
                        } else {
                            holder.unReadMsgCount.setVisibility(View.GONE);
                            holder.unReadMsgCountIcon.setImageResource(io.rong.imkit.R.drawable.shape_rect_phone_msg);
                        }
                    } else {
                        holder.unReadMsgCountIcon.setVisibility(View.GONE);
                        holder.unReadMsgCount.setVisibility(View.GONE);
                    }

                    holder.rightImageLayout.setVisibility(View.GONE);
                } else if(tag.portraitPosition() == 2) {
                    holder.rightImageLayout.setVisibility(View.VISIBLE);
                    holder.rightImageLayout.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if(mOnPortraitItemClick != null) {
                                mOnPortraitItemClick.onPortraitItemClick(v, data);
                            }

                        }
                    });
                    holder.rightImageLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            if(mOnPortraitItemClick != null) {
                                mOnPortraitItemClick.onPortraitItemLongClick(v, data);
                            }

                            return true;
                        }
                    });
                    if(data.getConversationType().equals(Conversation.ConversationType.GROUP)) {
                        defaultId1 = io.rong.imkit.R.drawable.rc_default_group_portrait;
                    } else if(data.getConversationType().equals(Conversation.ConversationType.DISCUSSION)) {
                        defaultId1 = io.rong.imkit.R.drawable.rc_default_discussion_portrait;
                    } else {
                        defaultId1 = io.rong.imkit.R.drawable.rc_default_portrait;
                    }

                    if(data.getConversationGatherState()) {
                        holder.rightImageView.setAvatar((String)null, defaultId1);
                    } else if(data.getIconUrl() != null) {
                        holder.rightImageView.setAvatar(data.getIconUrl().toString(), defaultId1);
                    } else {
                        holder.rightImageView.setAvatar((String)null, defaultId1);
                    }

                    if(data.getUnReadMessageCount() > 0) {
                        holder.unReadMsgCountRightIcon.setVisibility(View.VISIBLE);
                        if(data.getUnReadType().equals(UIConversation.UnreadRemindType.REMIND_WITH_COUNTING)) {
                            holder.unReadMsgCount.setVisibility(View.VISIBLE);
                            if(data.getUnReadMessageCount() > 99) {
                                holder.unReadMsgCountRight.setText(this.mContext.getResources().getString(io.rong.imkit.R.string.rc_message_unread_count));
                            } else {
                                holder.unReadMsgCountRight.setText(Integer.toString(data.getUnReadMessageCount()));
                            }

                            holder.unReadMsgCountRightIcon.setImageResource(io.rong.imkit.R.drawable.rc_unread_count_bg);
                        } else {
                            holder.unReadMsgCount.setVisibility(View.GONE);
                            holder.unReadMsgCountRightIcon.setImageResource(io.rong.imkit.R.drawable.rc_unread_remind_without_count);
                        }
                    } else {
                        holder.unReadMsgCountIcon.setVisibility(View.GONE);
                        holder.unReadMsgCount.setVisibility(View.GONE);
                    }

                    holder.leftImageLayout.setVisibility(View.GONE);
                } else {
                    if(tag.portraitPosition() != 3) {
                        throw new IllegalArgumentException("the portrait position is wrong!");
                    }

                    holder.rightImageLayout.setVisibility(View.GONE);
                    holder.leftImageLayout.setVisibility(View.GONE);
                }

            }
        }
    }

    @Override
    public void setOnPortraitItemClick(ConversationListAdapter.OnPortraitItemClick onPortraitItemClick) {
        this.mOnPortraitItemClick = onPortraitItemClick;
    }

    class ViewHolder {
        View layout;
        View leftImageLayout;
        View rightImageLayout;
        AsyncImageView leftImageView;
        TextView unReadMsgCount;
        ImageView unReadMsgCountIcon;
        AsyncImageView rightImageView;
        TextView unReadMsgCountRight;
        ImageView unReadMsgCountRightIcon;
        ProviderContainerView contentView;

        ViewHolder() {
        }
    }
}
