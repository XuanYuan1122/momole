package com.moemoe.lalala.view.calendar.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.common.util.DensityUtil;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.CalendarEvent;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.calendar.util.DateBean;
import com.moemoe.lalala.view.calendar.util.OtherUtils;
import com.moemoe.lalala.view.calendar.util.SpecialCalendar;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Haru on 2016/4/26 0026.
 */
public class CalendarAdapter extends BaseAdapter{
    private Context context;
    private DateBean[] dateBeans = new DateBean[SpecialCalendar.CALENDAR_ITEM_SIZE];
    private SpecialCalendar specialCalendar = new SpecialCalendar();
    private Resources res = null;
    private int colorDataPosition = -1;
    private DateBean firstDateBean;
    private int firstDatePosition;
    private boolean hasValued = false;

    public CalendarAdapter(Context context, int jumpMonth, int year_c, int month_c) {
        this.context = context;
        this.res = context.getResources();
        dateBeans = specialCalendar.getDateByYearMonth(year_c, month_c, jumpMonth);
        colorDataPosition = specialCalendar.currentPosition;
    }

    @Override
    public int getCount() {
       return dateBeans.length;
    }

    @Override
    public Object getItem(int position) {
        return dateBeans[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public DateBean getFirstDateBean() {
        return firstDateBean;
    }

    public int getFirstDatePosition() {
        return firstDatePosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_calendar, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        DateBean dateBean = dateBeans[position];
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateBean.getDate());
        viewHolder.txDate.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        if (DateBean.CURRENT_MONTH == dateBean.getMonthType()) {
            //如果是当前日期
            if (firstDateBean == null) {
                firstDateBean = dateBean;
                firstDatePosition = position;
            }
            if (colorDataPosition == 0 && !hasValued) {
                colorDataPosition = position;
                hasValued = true;
            }
            viewHolder.txDate.setTextColor(Color.BLACK);
        }else {
            viewHolder.txDate.setTextColor(res.getColor(R.color.gray_txt_main));
        }
        if (firstDateBean != null && colorDataPosition == position) {
            viewHolder.txDate.setTextColor(res.getColor(R.color.white));
            viewHolder.txDate.setBackgroundResource(R.drawable.bg_calendar_item);
        } else if (dateBean.isCurrentDay()) {
            viewHolder.txDate.setBackgroundResource(R.drawable.bg_calendar_item_today);
        } else {
            viewHolder.txDate.setBackgroundColor(res.getColor(android.R.color.transparent));
        }
//        if (dateBean.getTag()) {
//            viewHolder.imvPoint.setImageResource(R.drawable.calendar_item_point);
//        }
        if(dateBean.getTag() != null){
            CalendarEvent event = dateBean.getTag();
            final ViewHolder viewHolder1 = viewHolder;
            viewHolder.imvPoint.setVisibility(View.VISIBLE);
//            Utils.image().bind(viewHolder.imvPoint, StringUtils.getUrl(context, event.img.path, DensityUtil.dip2px(30), DensityUtil.dip2px(30), false, false), new ImageOptions.Builder()
//                    .setSize(DensityUtil.dip2px(30), DensityUtil.dip2px(30))
//                    .build());
            Picasso.with(context)
                    .load( StringUtils.getUrl(context, event.img.path, DensityUtil.dip2px(30), DensityUtil.dip2px(30), false, false))
                    .resize(DensityUtil.dip2px(30), DensityUtil.dip2px(30))
                    .config(Bitmap.Config.RGB_565)
                    .into(viewHolder.imvPoint);
        }else {
            viewHolder.imvPoint.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        TextView txDate;
        ImageView imvPoint;

        public ViewHolder(View convertView) {
            txDate = (TextView) convertView.findViewById(R.id.tx_date);
            imvPoint = (ImageView) convertView.findViewById(R.id.imv_point);
        }
    }

    /**
     * @param position 设置点击的日期的颜色位置
     */
    public void setColorDataPosition(int position) {
        colorDataPosition = position;
        notifyDataSetChanged();
    }

    public void setDateList(HashMap<String,CalendarEvent> dateList) {
        if (dateList != null && dateList.size() > 0) {
            for (DateBean dateBean : dateBeans) {
                String formatDate = OtherUtils.formatDate(dateBean.getDate(), OtherUtils.DATE_PATTERN_1);
//                if (dateList.contains(formatDate)) {
//                    dateBean.setTag(true);
//                }
                if(dateList.containsKey(formatDate)){
                    dateBean.setTag(dateList.get(formatDate));
                }
            }
            notifyDataSetChanged();
        }
    }

    public int getColorDataPosition() {
        return colorDataPosition;
    }
}
