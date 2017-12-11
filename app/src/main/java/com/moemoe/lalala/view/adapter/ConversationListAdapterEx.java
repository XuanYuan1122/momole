package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.greendao.gen.GroupNoticeEntityDao;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.PreferenceUtils;

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
    private int showRed = -1;

    public int getShowRed() {
        return showRed;
    }

    public void setShowRed(int showRed) {
        this.showRed = showRed;
    }

    public ConversationListAdapterEx(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public void remove(int position) {
        if(position > 3){
           super.remove(position - 4);
        }
    }

    @Override
    public int findGatheredItem(Conversation.ConversationType type) {
        int index = this.getCount();
        int position = -1;

        while(index-- > 0) {
            UIConversation uiConversation = (UIConversation)this.getItem(index);
            if(uiConversation.getConversationType().equals(type)) {
                position = index;
                break;
            }
        }

        return position;
    }

    @Override
    public int findPosition(Conversation.ConversationType type, String targetId) {
        int index = this.getCount();
        int position = -1;

        while(index-- > 0) {
            if(((UIConversation)this.getItem(index)).getConversationType().equals(type) && ((UIConversation)this.getItem(index)).getConversationTargetId().equals(targetId)) {
                position = index;
                break;
            }
        }

        return position;
    }

    @Override
    protected View newView(Context context, int position, ViewGroup group) {
        View result = mInflater.inflate(io.rong.imkit.R.layout.rc_item_conversation, null);
        ViewHolder holder = new ViewHolder();
        holder.layout = findViewById(result, io.rong.imkit.R.id.rc_item_conversation);
        holder.leftImageLayout = findViewById(result, io.rong.imkit.R.id.rc_item1);
        holder.rightImageLayout = findViewById(result, io.rong.imkit.R.id.rc_item2);
        holder.leftImageView = findViewById(result, io.rong.imkit.R.id.rc_left);
        holder.rightImageView = findViewById(result, io.rong.imkit.R.id.rc_right);
        holder.contentView = findViewById(result, io.rong.imkit.R.id.rc_content);
        holder.unReadMsgCount = findViewById(result, io.rong.imkit.R.id.rc_unread_message);
        holder.unReadMsgCountRight = findViewById(result, io.rong.imkit.R.id.rc_unread_message_right);
        holder.unReadMsgCountIcon = findViewById(result, io.rong.imkit.R.id.rc_unread_message_icon);
        holder.unReadMsgCountRightIcon = findViewById(result, io.rong.imkit.R.id.rc_unread_message_icon_right);
        holder.kiraName = findViewById(result, io.rong.imkit.R.id.tv_kira_name);
        holder.kiraContent = findViewById(result, io.rong.imkit.R.id.tv_kira_content);
        result.setTag(holder);
        return result;
    }

    @Override
    public UIConversation getItem(int position) {
        if(position > 3){
            UIConversation item = super.getItem(position - 4);
            return item;
        }else {
            UIConversation item = new UIConversation();
            item.setConversationTargetId("");
            item.setConversationType(Conversation.ConversationType.PRIVATE);
            if(position == 0){
                item.setUIConversationTitle("len");
            }
            if(position == 1){
                item.setUIConversationTitle("mei");
            }
            if(position == 2){
                item.setUIConversationTitle("sari");
            }
            if(position == 3){
                item.setUIConversationTitle("kira_system");
            }
            return item;
        }
    }

    @Override
    public int getCount() {
        return super.getCount() + 4;
    }

    @Override
    protected void bindView(View v, int position, final UIConversation data) {
        ViewHolder holder = (ViewHolder)v.getTag();
        if(data != null) {
            if(position > 3){
                if (data != null) {
                    if (data.getConversationType().equals(Conversation.ConversationType.DISCUSSION))
                        data.setUnreadType(UIConversation.UnreadRemindType.REMIND_ONLY);
                }
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
            }else {
                holder.leftImageLayout.setVisibility(View.VISIBLE);
                holder.rightImageLayout.setVisibility(View.GONE);
                holder.layout.setBackgroundDrawable(mContext.getResources().getDrawable(io.rong.imkit.R.drawable.btn_white_border_selector_6));
                if(showRed == position){
                    holder.unReadMsgCount.setVisibility(View.VISIBLE);
                    holder.unReadMsgCount.setText("1");
                }else {
                    holder.unReadMsgCount.setVisibility(View.GONE);
                }
                if(position == 0){
                    holder.leftImageView.setAvatar(null, R.drawable.ic_phone_message_len);
                    holder.kiraName.setText("小莲");
                    holder.kiraContent.setText(PreferenceUtils.getLenLastContent(mContext));
                }else if(position == 1){
                    holder.leftImageView.setAvatar(null, R.drawable.ic_phone_message_mei);
                    holder.kiraName.setText("美藤双树");
                    holder.kiraContent.setText(PreferenceUtils.getMeiLastContent(mContext));
                }else if(position == 2){
                    holder.leftImageView.setAvatar(null, R.drawable.ic_phone_message_sari);
                    holder.kiraName.setText("沙利尔");
                    holder.kiraContent.setText(PreferenceUtils.getSariLastContent(mContext));
                }else if(position == 3){
                    holder.leftImageView.setAvatar(null, R.drawable.ic_phone_message_sari);//TODO 通知图标
                    holder.kiraName.setText("群通知");
                    GroupNoticeEntityDao dao = GreenDaoManager.getInstance().getSession().getGroupNoticeEntityDao();
                    long count = dao.queryBuilder()
                            .where(GroupNoticeEntityDao.Properties.IsDeal.eq(false),GroupNoticeEntityDao.Properties.State.eq(true))
                            .count();
                    if(count > 0){
                        holder.unReadMsgCount.setVisibility(View.VISIBLE);
                        holder.unReadMsgCount.setText(String.valueOf(count));
                    }else {
                        holder.unReadMsgCount.setVisibility(View.GONE);
                    }
                    String[] contentAndTime = PreferenceUtils.getLastGroupContentAndTime(mContext);
                    holder.kiraContent.setText(contentAndTime[0]);

                }

                holder.kiraContent.setText(PreferenceUtils.getSariLastContent(mContext));
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
        TextView kiraName;
        TextView kiraContent;

        ViewHolder() {
        }
    }
}
