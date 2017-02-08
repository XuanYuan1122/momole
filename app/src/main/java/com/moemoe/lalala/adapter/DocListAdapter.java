package com.moemoe.lalala.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.FriendsMainActivity;
import com.moemoe.lalala.ImageBigSelectActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.DocListBean;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.DocLabelView;
import com.moemoe.lalala.view.MyRoundedImageView;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.squareup.picasso.Picasso;

import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

;

/**
 * Created by Haru on 2016/5/2 0002.
 */
public class DocListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int VIEW_TYPE_DOC_TOP_HOT = 0;
    private static final int VIEW_TYPE_DOC = 1;
    private OnItemClickListener mOnItemClickListener;
    private LayoutInflater mInflater;
    private ArrayList<DocListBean> mDocData;
    private ArrayList<DocListBean> mTopAndHot;
    private int mSplit;
    private Context mContext;
    public String TAG;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setData(ArrayList<DocListBean> data){
        int bfSize = mDocData.size();
        mDocData.clear();
        mDocData.addAll(data);
        int afSize = mDocData.size();
        if(bfSize == 0){
            notifyItemRangeInserted(1,afSize);
        }else {
            notifyItemRangeChanged(1, afSize);
            if(bfSize - afSize > 0){
                notifyItemRangeRemoved(afSize + 1,bfSize - afSize);
            }
        }
        notifyDataSetChanged();
    }

    public void addData(ArrayList<DocListBean> data){
        int bp = mDocData.size();
        mDocData.addAll(data);
        notifyItemRangeInserted(bp + 1, data.size());
    }

    public void clearTopAndHot(){
        mTopAndHot.clear();
    }

    public void setTopData(ArrayList<DocListBean> data,int split){
        mTopAndHot.addAll(0,data);
        mSplit = split;
        notifyItemChanged(0);
    }

    public void setHotData(ArrayList<DocListBean> data){
        mTopAndHot.addAll(data);
        notifyItemChanged(0);
    }

    public ArrayList<DocListBean> getData(){
        return mDocData;
    }

    public DocListAdapter(Context context,String TAG){
        mDocData = new ArrayList<>();
        mTopAndHot = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.TAG = TAG;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_DOC){
            return new DocViewHolder(mInflater.inflate(R.layout.item_doc_club_class,parent,false));
        }else if(viewType == VIEW_TYPE_DOC_TOP_HOT){
            return new TopAndHotHolder(mInflater.inflate(R.layout.item_calender_type5,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof  TopAndHotHolder){
            final TopAndHotHolder topAndHotHolder = (TopAndHotHolder) viewHolder;
            topAndHotHolder.mAdapter.setDate(mTopAndHot,mContext,mSplit);
            setListViewHeightBasedOnChildren(topAndHotHolder.mLvPerson);
            topAndHotHolder.mLvPerson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(!TextUtils.isEmpty((mTopAndHot.get(position).getDesc().getSchema()))){
                        Uri uri = Uri.parse(mTopAndHot.get(position).getDesc().getSchema());
                        IntentUtils.toActivityFromUri(mContext, uri,view);
                    }
                }
            });
        } else if(viewHolder instanceof DocViewHolder){
            final DocListBean post = getItem(position);
            final DocViewHolder holder = (DocViewHolder) viewHolder;
            if(holder.ivClubCreatorFlag != null){
                holder.ivClubCreatorFlag.setVisibility(View.GONE);
                holder.tvCreatorName.setSelected(false);
            }
            if(holder.docLabel != null && post.getTags() != null){
                holder.docLabel.setDocLabelAdapter(holder.docLabelAdapter);
                holder.docLabelAdapter.setData(post.getTags(),false);
                if(post.getTags().size()>0) {
                    holder.docLabel.setVisibility(View.VISIBLE);
                    holder.vDocSep.setVisibility(View.VISIBLE);
                }else{
                    holder.docLabel.setVisibility(View.GONE);
                    holder.vDocSep.setVisibility(View.GONE);
                }
            }
            holder.ivLevelColor.setBackgroundColor(StringUtils.readColorStr(post.getUserLevelColor(), ContextCompat.getColor(mContext,R.color.main_title_cyan)));
            holder.tvLevel.setText(String.valueOf(post.getUserLevel()));
            holder.tvLevel.setTextColor(StringUtils.readColorStr(post.getUserLevelColor(), ContextCompat.getColor(mContext,R.color.main_title_cyan)));
            holder.tvCreatorName.setText(post.getUserName());
            if(holder.ivCreatorAvatar != null){
                Picasso.with(mContext)
                        .load( StringUtils.getUrl(mContext,Otaku.URL_QINIU +  post.getUserIcon().getPath(), DensityUtil.dip2px(44), DensityUtil.dip2px(44), false, false))
                        .resize(DensityUtil.dip2px(44), DensityUtil.dip2px(44))
                        .placeholder(R.drawable.ic_default_avatar_m)
                        .error(R.drawable.ic_default_avatar_m)
                        .config(Bitmap.Config.RGB_565)
                        .tag(TAG)
                        .into(holder.ivCreatorAvatar);
                holder.ivCreatorAvatar.setTag(R.id.id_creator_uuid, post.getUserId());
            }
            holder.ivClubCreatorFlag.setVisibility(View.GONE);
            holder.tvCreatorName.setSelected(false);
            holder.ivClubCreatorFlag.setVisibility(View.GONE);
            holder.tvCreatorName.setSelected(false);
            if(TextUtils.isEmpty(post.getDesc().getTitle())){
                holder.tvPostTitle.setVisibility(View.GONE);
            }else{
                        holder.tvPostTitle.setVisibility(View.VISIBLE);
                        holder. tvPostTitle.setText(post.getDesc().getTitle());
            }
            // 点赞/评论
            holder.tvCommentNum.setText(StringUtils.getNumberInLengthLimit(post.getDesc().getComments(), 3));
            holder.tvPantsNum.setText(StringUtils.getNumberInLengthLimit(post.getDesc().getLikes(), 3));
            // 时间,内容
            holder.tvPostDate.setText(StringUtils.timeFormate(post.getUpdateTime()));
            holder.tvPostBrief.setText(post.getDesc().getContent());
            // 加载特殊帖子样式：投票，视频
            holder.rlSpecialTypePack.setVisibility(View.GONE);
            // 加载图片
            holder.ivIcon1.setImageBitmap(null);
            holder.ivIcon2.setImageBitmap(null);
            holder.ivIcon3.setImageBitmap(null);
            holder.ivIcon1.setTag(R.id.id_filebean, post);
            holder.ivIcon2.setTag(R.id.id_filebean, post);
            holder. ivIcon3.setTag(R.id.id_filebean, post);
            holder.rlIcon1.setVisibility(View.INVISIBLE);
            holder. rlIcon2.setVisibility(View.INVISIBLE);
            if (holder.rlSpecialTypePack.getVisibility() == View.VISIBLE) {
                holder. rlIcon3.setVisibility(View.GONE);
            } else {
                holder.rlIcon3.setVisibility(View.INVISIBLE);
            }
            holder.tvIconNum.setVisibility(View.GONE);
            if (post.getDesc().getMusic() != null){
                holder.llImagePack.setVisibility(View.VISIBLE);
                holder.rlMusicRoot.setVisibility(View.VISIBLE);
                Picasso.with(mContext)
                        .load(StringUtils.getUrl(mContext,Otaku.URL_QINIU + post.getDesc().getMusic().getCover().getPath(), DensityUtil.dip2px(90), DensityUtil.dip2px(90), false, true))
                        .resize(DensityUtil.dip2px(90), DensityUtil.dip2px(90))
                        .placeholder(R.drawable.ic_default_avatar_l)
                        .error(R.drawable.ic_default_avatar_l)
                        .centerCrop()
                        .config(Bitmap.Config.RGB_565)
                        .tag(TAG)
                        .into(holder.musicImg);
                holder.musicTitle.setText(post.getDesc().getMusic().getName());
            }else {
                holder.rlMusicRoot.setVisibility(View.GONE);
                if (post.getDesc().getImages() != null && post.getDesc().getImages().size() > 0) {
                    holder.llImagePack.setVisibility(View.VISIBLE);
                    holder.rlIcon1.setVisibility(View.VISIBLE);
                    holder.rlIcon1.setVisibility(View.VISIBLE);
                    if (FileUtil.isGif(post.getDesc().getImages().get(0).getPath())) {
                        holder.ivGifIcon1.setVisibility(View.VISIBLE);
                    } else {
                        holder.ivGifIcon1.setVisibility(View.GONE);
                    }
                    Picasso.with(mContext)
                            .load(StringUtils.getUrl(mContext,Otaku.URL_QINIU + post.getDesc().getImages().get(0).getPath(), (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, false, true))
                            .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                            .placeholder(R.drawable.ic_default_club_l)
                            .error(R.drawable.ic_default_club_l)
                            .centerCrop()
                            .config(Bitmap.Config.RGB_565)
                            .tag(TAG)
                            .into(holder.ivIcon1);
                    if(post.getDesc().getImages().size() > 1){
                        holder.rlIcon2.setVisibility(View.VISIBLE);
                        if (FileUtil.isGif(post.getDesc().getImages().get(1).getPath())) {
                            holder.ivGifIcon2.setVisibility(View.VISIBLE);
                        } else {
                            holder.ivGifIcon2.setVisibility(View.GONE);
                        }
                        Picasso.with(mContext)
                                .load(StringUtils.getUrl(mContext,Otaku.URL_QINIU + post.getDesc().getImages().get(1).getPath(), (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, false, true))
                                .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                                .placeholder(R.drawable.ic_default_club_l)
                                .error(R.drawable.ic_default_club_l)
                                .centerCrop()
                                .config(Bitmap.Config.RGB_565)
                                .tag(TAG)
                                .into(holder.ivIcon2);
                    }
                    // 是否显示第三张图
                    if(holder.rlSpecialTypePack.getVisibility() != View.VISIBLE && post.getDesc().getImages().size() > 2){
                        holder.rlIcon3.setVisibility(View.VISIBLE);
                        if (FileUtil.isGif(post.getDesc().getImages().get(2).getPath())) {
                            holder.ivGifIcon3.setVisibility(View.VISIBLE);
                        } else {
                            holder.ivGifIcon3.setVisibility(View.GONE);
                        }
                        Picasso.with(mContext)
                                .load(StringUtils.getUrl(mContext,Otaku.URL_QINIU + post.getDesc().getImages().get(2).getPath(), (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, false, true))
                                .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                                .placeholder(R.drawable.ic_default_club_l)
                                .error(R.drawable.ic_default_club_l)
                                .config(Bitmap.Config.RGB_565)
                                .centerCrop()
                                .tag(TAG)
                                .into(holder.ivIcon3);
                    }
                    // 是否显示  “共xx张图”
                    if(post.getDesc().getImages().size() > 3 || (holder.rlSpecialTypePack.getVisibility() == View.VISIBLE && post.getDesc().getImages().size() > 2)){
                        holder.tvIconNum.setVisibility(View.VISIBLE);
                        holder.tvIconNum.setText(mContext.getString(R.string.label_post_icon_num, post.getDesc().getImages().size()));
                    }
                    //  }
                }else if (holder.rlSpecialTypePack.getVisibility() == View.VISIBLE){
                    // 没有图片，只有投票或者视频
                    holder.llImagePack.setVisibility(View.VISIBLE);
                } else {
                    holder.llImagePack.setVisibility(View.GONE);
                }
            }
            holder.itemView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    int pos = holder.getLayoutPosition();
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(holder.itemView, pos);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return VIEW_TYPE_DOC_TOP_HOT;
        }else {
            return VIEW_TYPE_DOC;
        }
    }

    public DocListBean getItem(int position){
        if(position > 0){
            return  mDocData.get(position - 1);
        }
        return  null;
    }

    @Override
    public int getItemCount() {
        return mDocData.size() + 1;
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    private View.OnClickListener mAvatarListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String uuid = (String) v.getTag(R.id.id_creator_uuid);
            if (!TextUtils.isEmpty(uuid)) {
                Intent intent = new Intent(mContext, FriendsMainActivity.class);
                intent.putExtra(BaseActivity.EXTRA_KEY_UUID, uuid);
                mContext.startActivity(intent);
            }
        }
    };

    private static class TopAndHotHolder extends RecyclerView.ViewHolder{

        ListView mLvPerson;
        TopAndHotAdapter mAdapter;

        TopAndHotHolder(View itemView) {
            super(itemView);
            mLvPerson = (ListView) itemView.findViewById(R.id.lv_timetable);
            mAdapter = new TopAndHotAdapter();
            mLvPerson.setAdapter(mAdapter);
        }
    }

    private static class TopAndHotAdapter extends BaseAdapter {

        private  ArrayList<DocListBean> rssItems;
        private Context context;
        private int split;

        TopAndHotAdapter(){
            rssItems = new ArrayList<>();
        }

        void setDate( ArrayList<DocListBean> rssItems,Context context,int split){
            this.rssItems.clear();
            this.rssItems.addAll(rssItems);
            this.context = context;
            this.split = split;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return rssItems.size();
        }

        @Override
        public DocListBean getItem(int position) {
            return rssItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NormalHolder holder;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_tag_doc_list_top_hot,
                        parent,false);
                holder = new NormalHolder();
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tvChapter = (TextView) convertView.findViewById(R.id.tv_chapter);
                convertView.setTag(holder);
            }else {
                holder = (NormalHolder) convertView.getTag();
            }
            DocListBean rss = getItem(position);
            holder.tvTitle.setText(rss.getDesc().getTitle());
            holder.tvChapter.setVisibility(View.VISIBLE);
            if (position < split){
                holder.tvChapter.setText(R.string.label_top);
            }else {
                holder.tvChapter.setText(R.string.label_hot);
            }
            return convertView;
        }

        class NormalHolder {
            TextView tvTitle;
            TextView tvChapter;
        }
    }

    private class DocViewHolder extends RecyclerView.ViewHolder{

        ImageView ivClubAvatar;
        TextView tvClubName;
        // 瀑布流才有
        ImageView ivCreatorAvatar;
        @ViewInject(R.id.tv_post_creator_name)
        TextView tvCreatorName;
        @ViewInject(R.id.iv_post_owner_flag)
        View ivClubCreatorFlag;
        @ViewInject(R.id.tv_post_update_time)
        TextView tvPostDate;
        @ViewInject(R.id.tv_post_title)
        TextView tvPostTitle;
        @ViewInject(R.id.tv_post_brief)
        TextView tvPostBrief;
        @ViewInject(R.id.ll_image_3)
        View llImagePack;
        @ViewInject(R.id.rl_post_special_flag)
        View rlSpecialTypePack;
        @ViewInject(R.id.rl_post_image_1)
        View rlIcon1;
        @ViewInject(R.id.rl_post_image_2)
        View rlIcon2;
        @ViewInject(R.id.rl_post_image_3)
        View rlIcon3;
        @ViewInject(R.id.iv_post_image_1_gif_flag)
        View ivGifIcon1;
        @ViewInject(R.id.iv_post_image_2_gif_flag)
        View ivGifIcon2;
        @ViewInject(R.id.iv_post_image_3_gif_flag)
        View ivGifIcon3;
        @ViewInject(R.id.iv_post_image_1)
        ImageView ivIcon1;
        @ViewInject(R.id.iv_post_image_2)
        ImageView ivIcon2;
        @ViewInject(R.id.iv_post_image_3)
        ImageView ivIcon3;
        @ViewInject(R.id.tv_post_img_num)
        TextView tvIconNum;
        @ViewInject(R.id.tv_post_comment_num)
        TextView tvCommentNum;
        @ViewInject(R.id.tv_post_pants_num)
        TextView tvPantsNum;
        @ViewInject(R.id.rl_music_root)
        View rlMusicRoot;
        @ViewInject(R.id.iv_item_image)
        MyRoundedImageView musicImg;
        @ViewInject(R.id.tv_music_title)
        TextView musicTitle;
        View ivLevelColor;
        TextView tvLevel;
        ImageView ivIconClassOffical;
        //标签
        View vDocSep;
        DocLabelView docLabel;
        NewDocLabelAdapter docLabelAdapter;

        DocViewHolder(View itemView) {
            super(itemView);
            ivClubAvatar = (ImageView)itemView.findViewById(R.id.iv_post_club);
            tvClubName = (TextView)itemView.findViewById(R.id.tv_club_name);
            ivLevelColor = itemView.findViewById(R.id.iv_level_bg);
            tvLevel = (TextView)itemView.findViewById(R.id.tv_level);
            vDocSep = itemView.findViewById(R.id.view_doc_sep);
            docLabel = (DocLabelView) itemView.findViewById(R.id.dv_doc_label_root);
            ivIconClassOffical = (ImageView) itemView.findViewById(R.id.iv_class_post_img);
            x.view().inject(this, itemView);
            ivCreatorAvatar = (ImageView) itemView.findViewById(R.id.iv_post_creator);
            if (ivCreatorAvatar != null) {
                ivCreatorAvatar.setOnClickListener(mAvatarListener);
            }
            docLabelAdapter = new NewDocLabelAdapter(mContext,true);
            ivIcon1.setOnClickListener(mIconListener);
            ivIcon2.setOnClickListener(mIconListener);
            ivIcon3.setOnClickListener(mIconListener);
        }

        private View.OnClickListener mIconListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int index = 0;
                if (v.equals(ivIcon1)){
                    index = 0;
                } else if (v.equals(ivIcon2)) {
                    index = 1;
                } else if (v.equals(ivIcon3)) {
                    index = 2;
                }else if(v.equals(ivIconClassOffical)){
                    index = 0;
                }

                Context context = v.getContext();
                final DocListBean docBean = (DocListBean) v.getTag(R.id.id_filebean);
                Intent intent = new Intent(context, ImageBigSelectActivity.class);
                intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, docBean.getDesc().getImages());
                intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX, index);
                // 以后可选择 有返回数据
                context.startActivity(intent);

            }
        };
    }

}
