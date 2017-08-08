package com.moemoe.lalala.view.widget.adapter;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.moemoe.lalala.view.widget.adapter.loadmore.LoadMoreView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 *
 * Created by yi on 2017/6/26.
 */
public abstract class BaseRecyclerViewAdapter<T,K extends ClickableViewHolder> extends RecyclerView.Adapter<K>{

    protected Context context;
    protected ArrayList<T> list;
    protected LayoutInflater mLayoutInflater;
    //loadMore
    private boolean mNextLoadEnable = false;
    private boolean mLoadMoreEnable = false;//是否可上拉刷新
    private boolean mLoading = false;//刷新状态
    private LoadMoreListener mLoadMoreListener;
    private boolean mEnableLoadMoreClick = false;
    private LoadMoreView mLoadMoreView;
    //ItemClick
    private OnItemClickListener onItemClickListener;
    //head footer
    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;
    //empty
    private FrameLayout mEmptyLayout;
    private boolean mIsUseEmpty = true;
    private int mLastPosition = -1;
    private boolean mHeadAndEmptyEnable;
    private boolean mFootAndEmptyEnable;
    public static final int HEADER_VIEW = 0x00000111;
    public static final int LOADING_VIEW = 0x00000222;
    public static final int FOOTER_VIEW = 0x00000333;
    public static final int EMPTY_VIEW = 0x00000555;
    private RecyclerView mRecyclerView;
    private int mLayoutResId;

    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public BaseRecyclerViewAdapter(@LayoutRes int layoutResId, @Nullable ArrayList<T> list){
        this.list = list == null ? new ArrayList<T>() : list;
        if(layoutResId != 0){
            this.mLayoutResId = layoutResId;
        }
    }

    public BaseRecyclerViewAdapter(@Nullable ArrayList<T> list) {
        this(0,list);
    }

    public BaseRecyclerViewAdapter(@LayoutRes int layoutResId){
        this(layoutResId, null);
    }

    public void setOnLoadMoreListener(LoadMoreListener loadMoreListener,  RecyclerView recyclerView){
        this.mLoadMoreListener = loadMoreListener;
        mLoading = false;
        mNextLoadEnable = true;
        mLoadMoreEnable = true;
        if (getRecyclerView() == null) {
            setRecyclerView(recyclerView);
        }
    }

    public void setLoadMoreView(LoadMoreView view){
        mLoadMoreView = view;
    }

    public int getLoadMoreViewCount(){
        if(mLoadMoreListener == null || !mLoadMoreEnable){
            return 0;
        }
        if(!mNextLoadEnable && mLoadMoreView.ismLoadMoreEndGone()){
            return 0;
        }
        if(list.size() == 0){
            return 0;
        }
        return 1;
    }

    public int getLoadMoreViewPosition(){
        return getItemCount() - 1;
    }

    public boolean isLoading(){
        return mLoading;
    }

    public void loadMoreEnd(){
        if(getLoadMoreViewCount() == 0){
            return;
        }
        mLoading = false;
        mNextLoadEnable = false;
        mLoadMoreView.setmLoadMoreStatus(LoadMoreView.STATUS_END);
        notifyItemChanged(getLoadMoreViewPosition());
    }

    public void loadMoreComplete(){
        if(getLoadMoreViewCount() == 0){
            return;
        }
        mLoading = false;
        mNextLoadEnable = true;
        mLoadMoreView.setmLoadMoreStatus(LoadMoreView.STATUS_DEFAULT);
        notifyItemChanged(getLoadMoreViewPosition());
    }

    public void loadMoreFail(){
        if(getLoadMoreViewCount() == 0){
            return;
        }
        mLoading = false;
        mLoadMoreView.setmLoadMoreStatus(LoadMoreView.STATUS_FAIL);
        notifyItemChanged(getLoadMoreViewPosition());
    }

    public void setEnableLoadMore(boolean enable){
        int oldLoadMoreCount = getLoadMoreViewCount();
        mLoadMoreEnable = enable;
        int newLoadMoreCount = getLoadMoreViewCount();
        if(oldLoadMoreCount == 1){
            if(newLoadMoreCount == 0){
                notifyItemRemoved(getLoadMoreViewPosition());
            }
        }else {
            if(newLoadMoreCount == 1){
                mLoadMoreView.setmLoadMoreStatus(LoadMoreView.STATUS_DEFAULT);
                notifyItemInserted(getLoadMoreViewPosition());
            }
        }
    }

    public boolean isLoadMoreEnable(){ return mLoadMoreEnable;}



    @Override
    public K onCreateViewHolder(ViewGroup parent, int viewType) {
        K clickableViewHolder = null;
        this.context = parent.getContext();
        this.mLayoutInflater = LayoutInflater.from(context);
        switch (viewType){
            case LOADING_VIEW:
                clickableViewHolder = getLoadingView(parent);
                break;
            case HEADER_VIEW:
                clickableViewHolder = createBaseViewHolder(mHeaderLayout);
                break;
            case EMPTY_VIEW:
                clickableViewHolder = createBaseViewHolder(mEmptyLayout);
                break;
            case FOOTER_VIEW:
                clickableViewHolder = createBaseViewHolder(mFooterLayout);
                break;
            default:
                clickableViewHolder = onCreateDefViewHolder(parent, viewType);
                bindViewClickListener(clickableViewHolder);
        }
        return clickableViewHolder;
    }

    private K getLoadingView(ViewGroup parent){
        View view = getItemView(mLoadMoreView.getLayoutId(),parent);
        K holder = createBaseViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLoadMoreView.getmLoadMoreStatus() == LoadMoreView.STATUS_FAIL){
                    notifyLoadMoreToLoading();
                }
                if(mEnableLoadMoreClick && mLoadMoreView.getmLoadMoreStatus() == LoadMoreView.STATUS_END){
                    notifyLoadMoreToLoading();
                }
            }
        });
        return holder;
    }

    public void notifyLoadMoreToLoading(){
        if(mLoadMoreView.getmLoadMoreStatus() == LoadMoreView.STATUS_LOADING){
            return;
        }
        mLoadMoreView.setmLoadMoreStatus(LoadMoreView.STATUS_DEFAULT);
        notifyItemChanged(getLoadMoreViewPosition());
    }

    private void bindViewClickListener(final ClickableViewHolder viewHolder){
        if(viewHolder == null){
            return;
        }
        final View view = viewHolder.itemView;
        if(view == null) return;
        if(onItemClickListener != null){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(view, viewHolder.getLayoutPosition() - getHeaderLayoutCount());
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onItemLongClick(view, viewHolder.getLayoutPosition() - getHeaderLayoutCount());
                    return false;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(K holder) {
        super.onViewAttachedToWindow(holder);
        int type = holder.getItemViewType();
        if(isFixedViewType(type)){
            setFullSpan(holder);
        }else {
            //预留执行进入动画接口
        }
    }

    protected void setFullSpan(RecyclerView.ViewHolder holder){
        if(holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams){
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager){
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    if(type == HEADER_VIEW && isHeaderViewAsFlow()){
                        return 1;
                    }
                    if(type == FOOTER_VIEW && isFooterViewAsFlow()){
                        return 1;
                    }
                    if(mSpanSizeLookup == null){
                        return isFixedViewType(type) ? gridLayoutManager.getSpanCount() : 1;
                    }else {
                        return isFixedViewType(type) ? gridLayoutManager.getSpanCount() : mSpanSizeLookup.getSpanSize(gridLayoutManager, position - getHeaderLayoutCount());
                    }
                }
            });
        }
    }

    protected boolean isFixedViewType(int type) {
        return type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type ==
                LOADING_VIEW;
    }

    private boolean headerViewAsFlow, footerViewAsFlow;

    public void setHeaderViewAsFlow(boolean headerViewAsFlow) {
        this.headerViewAsFlow = headerViewAsFlow;
    }

    public boolean isHeaderViewAsFlow() {
        return headerViewAsFlow;
    }

    public void setFooterViewAsFlow(boolean footerViewAsFlow) {
        this.footerViewAsFlow = footerViewAsFlow;
    }

    public boolean isFooterViewAsFlow() {
        return footerViewAsFlow;
    }

    private SpanSizeLookup mSpanSizeLookup;

    public interface SpanSizeLookup {
        int getSpanSize(GridLayoutManager gridLayoutManager, int position);
    }

    /**
     * @param spanSizeLookup instance to be used to query number of spans occupied by each item
     */
    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        this.mSpanSizeLookup = spanSizeLookup;
    }

    @Override
    public void onBindViewHolder(final K holder, final int position) {
        autoLoadMore(position);
        int viewType = holder.getItemViewType();
        switch (viewType){
            case 0:
                convert(holder, list.get(holder.getLayoutPosition() - getHeaderLayoutCount()), holder.getLayoutPosition() - getHeaderLayoutCount());
                break;
            case LOADING_VIEW:
                mLoadMoreView.convert(holder);
                break;
            case HEADER_VIEW:
                break;
            case EMPTY_VIEW:
                break;
            case FOOTER_VIEW:
                break;
            default:
                convert(holder, list.get(holder.getLayoutPosition() - getHeaderLayoutCount()), holder.getLayoutPosition() - getHeaderLayoutCount());
                break;
        }
    }

    private void autoLoadMore(int position){
        if(getLoadMoreViewCount() == 0){
            return;
        }
        if(position < getItemCount() - 1){
            return;
        }
        if(mLoadMoreView.getmLoadMoreStatus() != LoadMoreView.STATUS_DEFAULT){
            return;
        }
        mLoadMoreView.setmLoadMoreStatus(LoadMoreView.STATUS_LOADING);
        if(!mLoading){
            mLoading = true;
            if(getRecyclerView() != null){
                getRecyclerView().post(new Runnable() {
                    @Override
                    public void run() {
                        mLoadMoreListener.onLoadMore();
                    }
                });
            }else {
                mLoadMoreListener.onLoadMore();
            }
        }
    }

    protected abstract void convert(K helper, T item, int position);

    protected View getItemView(@LayoutRes int layoutResId, ViewGroup parent){
        return mLayoutInflater.inflate(layoutResId,parent,false);
    }

    protected K onCreateDefViewHolder(ViewGroup parent, int viewType){
        return createBaseViewHolder(parent,mLayoutResId);
    }

    protected K createBaseViewHolder(ViewGroup parent, int layoutResId) {
        return createBaseViewHolder(getItemView(layoutResId, parent));
    }

    @SuppressWarnings("unchecked")
    protected K createBaseViewHolder(View view){
        Class temp = getClass();
        Class z = null;
        while (z == null && null != temp){
            z = getInstancedGenericKClass(temp);
            temp = temp.getSuperclass();
        }
        K k = createGenericKInstance(z, view);
        return null != k ? k : (K) new ClickableViewHolder(view);
    }

    @SuppressWarnings("unchecked")
    private K createGenericKInstance(Class z, View view){
        try {
            Constructor constructor;
            if(z.isMemberClass() && !Modifier.isStatic(z.getModifiers())){
                constructor = z.getDeclaredConstructor(getClass(), View.class);
                constructor.setAccessible(true);
                return (K) constructor.newInstance(this, view);
            }else {
                constructor = z.getDeclaredConstructor(View.class);
                constructor.setAccessible(true);
                return (K) constructor.newInstance(view);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Class getInstancedGenericKClass(Class z){
        Type type = z.getGenericSuperclass();
        if(type instanceof ParameterizedType){
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            for (Type temp : types){
                if(temp instanceof Class){
                    Class tempClass = (Class) temp;
                    if(ClickableViewHolder.class.isAssignableFrom(tempClass)){
                        return tempClass;
                    }
                }
            }
        }
        return null;
    }

    public int addHeaderView(View header){
        return addHeaderView(header, -1);
    }

    public int addHeaderView(View header, int index){
        return addHeaderView(header, index, LinearLayout.VERTICAL);
    }

    public int getHeaderViewCount(){
        if(mHeaderLayout == null) return 0;
        return mHeaderLayout.getChildCount();
    }

    public LinearLayout getmHeaderLayout(){
        return mHeaderLayout;
    }

    public int addHeaderView(View header, int index, int orientation){
        if(mHeaderLayout == null){
            mHeaderLayout = new LinearLayout(header.getContext());
            if(orientation == LinearLayout.VERTICAL){
                mHeaderLayout.setOrientation(LinearLayout.VERTICAL);
                mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }else {
                mHeaderLayout.setOrientation(LinearLayout.HORIZONTAL);
                mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        final int childCount = mHeaderLayout.getChildCount();
        if(index < 0 || index > childCount){
            index = childCount;
        }
        mHeaderLayout.addView(header,index);
        if(mHeaderLayout.getChildCount() == 1){
            int position = getHeaderViewPosition();
            if(position != -1){
                notifyItemInserted(position);
            }
        }
        return index;
    }

    public int addFooterView(View footer) {
        return addFooterView(footer, -1);
    }

    public int addFooterView(View footer, int index) {
        return addFooterView(footer, index, LinearLayout.VERTICAL);
    }

    public int addFooterView(View footer, int index, int orientation) {
        if (mFooterLayout == null) {
            mFooterLayout = new LinearLayout(footer.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                mFooterLayout.setOrientation(LinearLayout.VERTICAL);
                mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            } else {
                mFooterLayout.setOrientation(LinearLayout.HORIZONTAL);
                mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            }
        }
        final int childCount = mFooterLayout.getChildCount();
        if (index < 0 || index > childCount) {
            index = childCount;
        }
        mFooterLayout.addView(footer, index);
        if (mFooterLayout.getChildCount() == 1) {
            int position = getFooterViewPosition();
            if (position != -1) {
                notifyItemInserted(position);
            }
        }
        return index;
    }

    private int getHeaderViewPosition(){
        if(getEmptyViewCount() == 1){
            if(mHeadAndEmptyEnable){
                return 0;
            }
        }else {
            return 0;
        }
        return  -1;
    }

    private int getFooterViewPosition() {
        if (getEmptyViewCount() == 1) {
            int position = 1;
            if (mHeadAndEmptyEnable && getHeaderLayoutCount() != 0) {
                position++;
            }
            if (mFootAndEmptyEnable) {
                return position;
            }
        } else {
            return getHeaderLayoutCount() + list.size();
        }
        return -1;
    }

    public void removeHeaderView(View header) {
        if (getHeaderLayoutCount() == 0) return;

        mHeaderLayout.removeView(header);
        if (mHeaderLayout.getChildCount() == 0) {
            int position = getHeaderViewPosition();
            if (position != -1) {
                notifyItemRemoved(position);
            }
        }
    }

    public void removeAllHeaderView() {
        if (getHeaderLayoutCount() == 0) return;

        mHeaderLayout.removeAllViews();
        int position = getHeaderViewPosition();
        if (position != -1) {
            notifyItemRemoved(position);
        }
    }

    public void removeFooterView(View footer) {
        if (getFooterLayoutCount() == 0) return;

        mFooterLayout.removeView(footer);
        if (mFooterLayout.getChildCount() == 0) {
            int position = getFooterViewPosition();
            if (position != -1) {
                notifyItemRemoved(position);
            }
        }
    }

    public void removeAllFooterView() {
        if (getFooterLayoutCount() == 0) return;

        mFooterLayout.removeAllViews();
        int position = getFooterViewPosition();
        if (position != -1) {
            notifyItemRemoved(position);
        }
    }

    public void setEmptyView(@LayoutRes int layoutResId, ViewGroup viewGroup){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutResId, viewGroup, false);
        setEmptyView(view);
    }

    public void setEmptyView(View emptyView){
        boolean insert = false;
        if(mEmptyLayout == null){
            mEmptyLayout = new FrameLayout(emptyView.getContext());
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(MATCH_PARENT,MATCH_PARENT);
            ViewGroup.LayoutParams lp = emptyView.getLayoutParams();
            if(lp != null){
                layoutParams.width = lp.width;
                layoutParams.height = lp.height;
            }
            mEmptyLayout.setLayoutParams(layoutParams);
            insert = true;
        }
        mEmptyLayout.removeAllViews();
        mEmptyLayout.addView(emptyView);
        mIsUseEmpty = true;
        if(insert){
            if(getEmptyViewCount() == 1){
                int position = 0;
                if(mHeadAndEmptyEnable && getHeaderLayoutCount() != 0){
                    position++;
                }
                notifyItemInserted(position);
            }
        }
    }

    public void setHeaderFooterEmpty(boolean isHeadAndEmpty, boolean isFootAndEmpty) {
        mHeadAndEmptyEnable = isHeadAndEmpty;
        mFootAndEmptyEnable = isFootAndEmpty;
    }

    public T getItem(int position){
        if(position < list.size())
          return list.get(position);
        else
            return null;
    }

    public void setList(ArrayList<T> list){
        this.list = list == null ? new ArrayList<T>() : list;
        if(mLoadMoreListener != null){
            mNextLoadEnable = true;
            mLoadMoreEnable = true;
            mLoading = false;
            mLoadMoreView.setmLoadMoreStatus(LoadMoreView.STATUS_DEFAULT);
        }
        mLastPosition = -1;
        notifyDataSetChanged();
    }

    public void addItem(@IntRange(from = 0) int position, @NonNull T item){
        list.add(item);
        notifyItemInserted(position + getHeaderLayoutCount());
        compatibilityDataSizeChanged(1);
    }

    public void addItem(@NonNull T item){
        list.add(item);
        notifyItemInserted(list.size() + getHeaderLayoutCount());
        compatibilityDataSizeChanged(1);
    }

    public void addList(ArrayList<T> list){
        int bfSize = getItemCount();
        this.list.addAll(list);
        notifyItemRangeInserted(bfSize,getItemCount() - bfSize);
    }

    private void compatibilityDataSizeChanged(int size) {
        final int dataSize = list == null ? 0 : list.size();
        if (dataSize == size) {
            notifyDataSetChanged();
        }
    }

    public ArrayList<T> getList(){ return list;}

    public int getHeaderLayoutCount(){
        if(mHeaderLayout == null || mHeaderLayout.getChildCount() == 0){
            return 0;
        }
        return 1;
    }

    public int getFooterLayoutCount(){
        if(mFooterLayout == null || mFooterLayout.getChildCount() == 0){
            return 0;
        }
        return 1;
    }

    public int getEmptyViewCount(){
        if(mEmptyLayout == null || mEmptyLayout.getChildCount() == 0){
            return 0;
        }
        if(!mIsUseEmpty){
            return 0;
        }
        if(list.size() != 0){
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        int count;
        if(getEmptyViewCount() == 1){
            count = 1;
            if(mHeadAndEmptyEnable && getHeaderLayoutCount() != 0){
                count++;
            }
            if(mFootAndEmptyEnable && getFooterLayoutCount() != 0){
                count++;
            }
        }else {
            count = getHeaderLayoutCount() + list.size() + getFooterLayoutCount() + getLoadMoreViewCount();
        }
        return count;
    }

    public abstract int getItemType(int position);

    @Override
    public int getItemViewType(int position) {
        if(getEmptyViewCount() == 1){
            boolean header = mHeadAndEmptyEnable && getHeaderLayoutCount() != 0;
            switch (position){
                case 0:
                    if(header){
                        return HEADER_VIEW;
                    }else {
                        return EMPTY_VIEW;
                    }
                case 1:
                    if(header){
                        return EMPTY_VIEW;
                    }else {
                        return FOOTER_VIEW;
                    }
                case 2:
                    return FOOTER_VIEW;
                default:
                    return EMPTY_VIEW;
            }
        }
        int numHeaders = getHeaderLayoutCount();
        if(position < numHeaders){
            return HEADER_VIEW;
        }else {
            int adjPosition = position - numHeaders;
            int adapterCount = list.size();
            if(adjPosition < adapterCount){
                return getItemType(adjPosition);
            }else {
                adjPosition = adjPosition - adapterCount;
                int numFooters = getFooterLayoutCount();
                if(adjPosition < numFooters){
                    return FOOTER_VIEW;
                }else {
                    return LOADING_VIEW;
                }
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface LoadMoreListener{
        void onLoadMore();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
}
