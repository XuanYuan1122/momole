package com.moemoe.lalala.view.widget.netamenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.MenuHItemDecoration;
import com.moemoe.lalala.view.adapter.OnItemClickListener;

import java.util.ArrayList;

/**
 * menu
 * Created by yi on 2017/6/6.
 */

public class BottomMenuFragment extends DialogFragment{

    private final String TAG = "BottomMenuFragment";
    public static final int TYPE_HORIZONTAL = 0;
    public static final int TYPE_VERTICAL = 1;

    private boolean showTop;
    private String topContent;
    private int menuType = TYPE_VERTICAL;
    private MenuItemClickListener mClickListener;
    private MenuItemAdapter menuItemAdapter;

    public BottomMenuFragment() {
        // Required empty public constructor
    }


    private ArrayList<MenuItem> menuItems;

    public ArrayList<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(ArrayList<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明

        getDialog().getWindow().setWindowAnimations(R.style.menu_animation);//添加一组进出动画

        View view = inflater.inflate(R.layout.frag_bottom_menu, container, false);

        //view.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.menu_appear));//添加一个加载动画，这样的问题是没办法添加消失动画，有待进一步研究

        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomMenuFragment.this.dismiss();
            }
        });

        if(showTop && !TextUtils.isEmpty(topContent)){
            TextView top = (TextView) view.findViewById(R.id.tv_top);
            top.setVisibility(View.VISIBLE);
            top.setText(topContent);
        }

        final RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv_menu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(menuType == TYPE_HORIZONTAL ? TYPE_HORIZONTAL : TYPE_VERTICAL);
        rv.setLayoutManager(layoutManager);
        if(menuType == TYPE_HORIZONTAL){
            rv.addItemDecoration(new MenuHItemDecoration(DensityUtil.dip2px(getContext(),18),DensityUtil.dip2px(getContext(),12)));
        }
        menuItemAdapter = new MenuItemAdapter(getContext(),menuType, this.menuItems);
        menuItemAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BottomMenuFragment.this.dismiss();
                if(mClickListener != null){
                    mClickListener.OnMenuItemClick(menuItems.get(position).getId());
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        rv.setAdapter(menuItemAdapter);

        return view;
    }

    private int findItemIndex(int id) {
        final int size = menuItems.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = menuItems.get(i);
            if (item.getId() == id) {
                return i;
            }
        }
        return -1;
    }


    public void changeItemTextById(int id, String text,int imgId) {
        int index = findItemIndex(id);
        if (index >= 0) {
            menuItems.remove(index);
            menuItems.add(index, new MenuItem(id, text,imgId));
        }
        menuItemAdapter.notifyItemChanged(index);
    }

    public boolean isShowTop() {
        return showTop;
    }

    public void setShowTop(boolean showTop) {
        this.showTop = showTop;
    }

    public int getMenuType() {
        return menuType;
    }

    public void setMenuType(int menuType) {
        this.menuType = menuType;
    }

    public String getTopContent() {
        return topContent;
    }

    public void setTopContent(String topContent) {
        this.topContent = topContent;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();

        //设置弹出框宽屏显示，适应屏幕宽度
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics( dm );
        getDialog().getWindow().setLayout( dm.widthPixels, getDialog().getWindow().getAttributes().height );

        //移动弹出菜单到底部
        WindowManager.LayoutParams wlp = getDialog().getWindow().getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        // wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(wlp);
    }

    @Override
    public void onStop() {
        this.getView().setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.menu_disappear));
        super.onStop();
    }

    public MenuItemClickListener getmClickListener() {
        return mClickListener;
    }

    public void setmClickListener(MenuItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public interface MenuItemClickListener {
        public void OnMenuItemClick(int itemId);
    }
}
