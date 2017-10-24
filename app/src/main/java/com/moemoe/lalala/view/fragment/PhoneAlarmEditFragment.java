package com.moemoe.lalala.view.fragment;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.event.AlarmEvent;
import com.moemoe.lalala.greendao.gen.AlarmClockEntityDao;
import com.moemoe.lalala.model.entity.AlarmClockEntity;
import com.moemoe.lalala.model.entity.DeskMateEntity;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.activity.PhoneAlarmActivity;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yi on 2017/9/4.
 */

public class PhoneAlarmEditFragment extends BaseFragment{

    public static final String TAG = "PhoneAlarmEditFragment";
    @BindView(R.id.et_mark)
    EditText mEtMark;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_type)
    TextView mTvType;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.tv_week_1)
    TextView mTvWeek1;
    @BindView(R.id.tv_week_2)
    TextView mTvWeek2;
    @BindView(R.id.tv_week_3)
    TextView mTvWeek3;
    @BindView(R.id.tv_week_4)
    TextView mTvWeek4;
    @BindView(R.id.tv_week_5)
    TextView mTvWeek5;
    @BindView(R.id.tv_week_6)
    TextView mTvWeek6;
    @BindView(R.id.tv_week_7)
    TextView mTvWeek7;
    @BindView(R.id.iv_repeat)
    ImageView mIvRepeat;
    @BindView(R.id.tv_delete)
    TextView mTvDelete;

    /**
     * 闹钟实例
     */
    private AlarmClockEntity mAlarmClock;
    /**
     * 周一按钮状态，默认未选中
     */
    private Boolean isMondayChecked = false;

    /**
     * 周二按钮状态，默认未选中
     */
    private Boolean isTuesdayChecked = false;

    /**
     * 周三按钮状态，默认未选中
     */
    private Boolean isWednesdayChecked = false;

    /**
     * 周四按钮状态，默认未选中
     */
    private Boolean isThursdayChecked = false;

    /**
     * 周五按钮状态，默认未选中
     */
    private Boolean isFridayChecked = false;

    /**
     * 周六按钮状态，默认未选中
     */
    private Boolean isSaturdayChecked = false;

    /**
     * 周日按钮状态，默认未选中
     */
    private Boolean isSundayChecked = false;
    /**
     * 保存重复描述信息String
     */
    private StringBuilder mRepeatStr;
    /**
     * 按键值顺序存放重复描述信息
     */
    private TreeMap<Integer, String> mMap;

    private BottomMenuFragment mBottomMenuFragment;

    private ArrayList<DeskMateEntity> deskMateEntities;


    public static PhoneAlarmEditFragment newInstance(){
        return new PhoneAlarmEditFragment();
    }

    public static PhoneAlarmEditFragment newInstance(AlarmClockEntity entity){
        PhoneAlarmEditFragment fragment = new PhoneAlarmEditFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("alarm",entity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_phone_alarm_edit;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(getArguments() != null){
            mAlarmClock = getArguments().getParcelable("alarm");
        }
        if(mAlarmClock == null){
            mAlarmClock = new AlarmClockEntity();
            mAlarmClock.setId(-1);
            mAlarmClock.setOnOff(true); // 闹钟默认开启
            mAlarmClock.setRepeat("只响一次");
            mAlarmClock.setWeeks(null);
            mTvName.setText("小莲");
            mAlarmClock.setRoleName("小莲");
            mAlarmClock.setRoleId("len");
            mTvType.setText("按时休息");
            mAlarmClock.setRingName("按时休息");
            mAlarmClock.setRingUrl(R.raw.vc_alerm_len_sleep_1);
            Calendar calendar = Calendar.getInstance();
            mAlarmClock.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            // 保存闹钟实例的分钟
            mAlarmClock.setMinute(calendar.get(Calendar.MINUTE));
            mTvTime.setText(StringUtils.formatTime(calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)));
        }else {
            mTvDelete.setVisibility(View.VISIBLE);
            mTvDelete.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    RxBus.getInstance().post(new AlarmEvent(mAlarmClock,2));
                }
            });
            mTvName.setText(mAlarmClock.getRoleName());
            mTvType.setText(mAlarmClock.getRingName());
            mEtMark.setText(mAlarmClock.getTag());
            mTvTime.setText(StringUtils.formatTime(mAlarmClock.getHour(),mAlarmClock.getMinute()));
            if(mAlarmClock.getWeeks() == null){
                mIvRepeat.setSelected(true);
            }else {
                mIvRepeat.setSelected(false);
                if(mAlarmClock.getWeeks().contains("2")){
                    mTvWeek1.setSelected(true);
                    isMondayChecked = true;
                }
                if(mAlarmClock.getWeeks().contains("3")){
                    mTvWeek2.setSelected(true);
                    isTuesdayChecked = true;
                }
                if(mAlarmClock.getWeeks().contains("4")){
                    mTvWeek3.setSelected(true);
                    isWednesdayChecked = true;
                }
                if(mAlarmClock.getWeeks().contains("5")){
                    mTvWeek4.setSelected(true);
                    isThursdayChecked = true;
                }
                if(mAlarmClock.getWeeks().contains("6")){
                    mTvWeek5.setSelected(true);
                    isFridayChecked= true;
                }
                if(mAlarmClock.getWeeks().contains("7")){
                    mTvWeek6.setSelected(true);
                    isSaturdayChecked = true;
                }
                if(mAlarmClock.getWeeks().contains("1")){
                    mTvWeek7.setSelected(true);
                    isSundayChecked = true;
                }
            }
        }
        mRepeatStr = new StringBuilder();
        mMap = new TreeMap<>();
        mEtMark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = mEtMark.getText();
                int len = editable.length();
                if (len > 7) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    String newStr = str.substring(0, 7);
                    mEtMark.setText(newStr);
                    editable = mEtMark.getText();
                    int newLen = editable.length();
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    Selection.setSelection(editable, selEndIndex);
                }
                if(!TextUtils.isEmpty(editable)){
                    mAlarmClock.setTag(editable.toString());
                }else {
                    mAlarmClock.setTag("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 角色选择
     */
    private void showRole(){
        mBottomMenuFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        deskMateEntities = PreferenceUtils.getAuthorInfo().getDeskMateEntities();
        int count = 0;
        for (DeskMateEntity entity : deskMateEntities){
            MenuItem item = new MenuItem(count, entity.getRoleName());
            items.add(item);
            count++;
        }
        mBottomMenuFragment.setMenuItems(items);
        mBottomMenuFragment.setShowTop(false);
        mBottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        mBottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                mTvName.setText(deskMateEntities.get(itemId).getRoleName());
                mAlarmClock.setRoleName(deskMateEntities.get(itemId).getRoleName());
                mAlarmClock.setRoleId(deskMateEntities.get(itemId).getRoleOf());
            }
        });
        mBottomMenuFragment.show(getFragmentManager(),"Alarm");
    }

    /**
     * 铃声type
     */
    private void showType(){
        mBottomMenuFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(1, "按时休息");
        items.add(item);
        item = new MenuItem(2, "起床提醒");
        items.add(item);
        item = new MenuItem(3,"其他事宜");
        items.add(item);
        mBottomMenuFragment.setMenuItems(items);
        mBottomMenuFragment.setShowTop(false);
        mBottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        mBottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if (itemId == 1) {
                    mTvType.setText("按时休息");
                    mAlarmClock.setRingName("按时休息");
                    if(mAlarmClock.getRoleId().equals("mei")){
                        mAlarmClock.setRingUrl(R.raw.vc_alerm_mei_sleep_1);
                    }else if(mAlarmClock.getRoleId().equals("sari")){
                        mAlarmClock.setRingUrl(R.raw.vc_alerm_sari_sleep_1);
                    }else {
                        mAlarmClock.setRingUrl(R.raw.vc_alerm_len_sleep_1);
                    }
                }
                if(itemId == 2){
                    mTvType.setText("起床提醒");
                    mAlarmClock.setRingName("起床提醒");
                    if(mAlarmClock.getRoleId().equals("mei")){
                        mAlarmClock.setRingUrl(R.raw.vc_alerm_mei_wakeup_1);
                    }else if(mAlarmClock.getRoleId().equals("sari")){
                        mAlarmClock.setRingUrl(R.raw.vc_alerm_sari_wakeup_1);
                    }else {
                        mAlarmClock.setRingUrl(R.raw.vc_alerm_len_wakeup_1);
                    }
                }
                if(itemId == 3){
                    mTvType.setText("其他事宜");
                    mAlarmClock.setRingName("其他事宜");
                    if(mAlarmClock.getRoleId().equals("mei")){
                        mAlarmClock.setRingUrl(R.raw.vc_alerm_mei_remind_1);
                    }else if(mAlarmClock.getRoleId().equals("sari")){
                        mAlarmClock.setRingUrl(R.raw.vc_alerm_sari_remind_1);
                    }else {
                        mAlarmClock.setRingUrl(R.raw.vc_alerm_len_remind_1);
                    }
                }
            }
        });
        mBottomMenuFragment.show(getFragmentManager(),"Alarm");
    }

    @OnClick({R.id.tv_type,R.id.tv_name,R.id.tv_time,R.id.tv_week_1,R.id.tv_week_2,R.id.tv_week_3,R.id.tv_week_4,R.id.tv_week_5,R.id.tv_week_6,R.id.tv_week_7,R.id.iv_repeat})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_name:
                showRole();
                break;
            case R.id.tv_type:
                showType();
                break;
            case R.id.tv_time:

                final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                alertDialogUtil.createTimepickerDialog(getContext(),mAlarmClock.getHour(),mAlarmClock.getMinute());
                alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                    @Override
                    public void CancelOnClick() {
                        alertDialogUtil.dismissDialog();
                    }

                    @Override
                    public void ConfirmOnClick() {
                        mAlarmClock.setHour(alertDialogUtil.getHour());
                        // 保存闹钟实例的分钟
                        mAlarmClock.setMinute(alertDialogUtil.getMinute());
                        mTvTime.setText(StringUtils.formatTime(alertDialogUtil
                                .getHour(),alertDialogUtil.getMinute()));
                        alertDialogUtil.dismissDialog();
                    }
                });
                alertDialogUtil.showDialog();
                break;
            case R.id.tv_week_1:
                if(mIvRepeat.isSelected()){
                    mTvWeek1.setSelected(!mTvWeek1.isSelected());
                    isMondayChecked = !isMondayChecked;
                    if(isMondayChecked){
                        mMap.put(1, "一");
                    }else {
                        mMap.remove(1);
                    }
                    setRepeatDescribe();
                }
                break;
            case R.id.tv_week_2:
                if(mIvRepeat.isSelected()){
                    mTvWeek2.setSelected(!mTvWeek2.isSelected());
                    isTuesdayChecked = !isTuesdayChecked;
                    if(isTuesdayChecked){
                        mMap.put(2, "二");
                    }else {
                        mMap.remove(2);
                    }
                    setRepeatDescribe();
                }
                break;
            case R.id.tv_week_3:
                if(mIvRepeat.isSelected()){
                    mTvWeek3.setSelected(!mTvWeek3.isSelected());
                    isWednesdayChecked = !isWednesdayChecked;
                    if(isWednesdayChecked){
                        mMap.put(3, "三");
                    }else {
                        mMap.remove(3);
                    }
                    setRepeatDescribe();
                }
                break;
            case R.id.tv_week_4:
                if(mIvRepeat.isSelected()){
                    mTvWeek4.setSelected(!mTvWeek4.isSelected());
                    isThursdayChecked = !isThursdayChecked;
                    if(isThursdayChecked){
                        mMap.put(4, "四");
                    }else {
                        mMap.remove(4);
                    }
                    setRepeatDescribe();
                }
                break;
            case R.id.tv_week_5:
                if(mIvRepeat.isSelected()){
                    mTvWeek5.setSelected(!mTvWeek5.isSelected());
                    isFridayChecked = !isFridayChecked;
                    if(isFridayChecked){
                        mMap.put(5, "五");
                    }else {
                        mMap.remove(5);
                    }
                    setRepeatDescribe();
                }
                break;
            case R.id.tv_week_6:
                if(mIvRepeat.isSelected()){
                    mTvWeek6.setSelected(!mTvWeek6.isSelected());
                    isSaturdayChecked = !isSaturdayChecked;
                    if(isSaturdayChecked){
                        mMap.put(6, "六");
                    }else {
                        mMap.remove(6);
                    }
                    setRepeatDescribe();
                }
                break;
            case R.id.tv_week_7:
                if(mIvRepeat.isSelected()){
                    mTvWeek7.setSelected(!mTvWeek7.isSelected());
                    isSundayChecked = !isSundayChecked;
                    if(isSundayChecked){
                        mMap.put(7, "日");
                    }else {
                        mMap.remove(7);
                    }
                    setRepeatDescribe();
                }
                break;
            case R.id.iv_repeat:
                mIvRepeat.setSelected(!mIvRepeat.isSelected());
                if(!mIvRepeat.isSelected()){
                    mMap.clear();
                    isMondayChecked = false;
                    isTuesdayChecked = false;
                    isWednesdayChecked = false;
                    isThursdayChecked = false;
                    isFridayChecked = false;
                    isSaturdayChecked = false;
                    isSundayChecked = false;
                    mTvWeek1.setSelected(false);
                    mTvWeek2.setSelected(false);
                    mTvWeek3.setSelected(false);
                    mTvWeek4.setSelected(false);
                    mTvWeek5.setSelected(false);
                    mTvWeek6.setSelected(false);
                    mTvWeek7.setSelected(false);
                    setRepeatDescribe();
                }
                break;
        }
    }

    private void setRepeatDescribe() {
        if (isMondayChecked & isTuesdayChecked & isWednesdayChecked
                & isThursdayChecked & isFridayChecked & isSaturdayChecked
                & isSundayChecked) {
            mAlarmClock.setRepeat("每天");
            // 响铃周期
            mAlarmClock.setWeeks("2,3,4,5,6,7,1");
            // 周一到周五全部选中
        }else if (!isMondayChecked & !isTuesdayChecked & !isWednesdayChecked
                & !isThursdayChecked & !isFridayChecked & !isSaturdayChecked
                & !isSundayChecked) {
            mAlarmClock.setRepeat("只响一次");
            mAlarmClock.setWeeks(null);
        }else {
            mRepeatStr.setLength(0);
            mRepeatStr.append("周");
            Collection<String> col = mMap.values();
            for (String aCol : col) {
                mRepeatStr.append(aCol).append("、");
            }
            // 去掉最后一个"、"
            mRepeatStr.setLength(mRepeatStr.length() - 1);
            mAlarmClock.setRepeat(mRepeatStr.toString());

            mRepeatStr.setLength(0);
            if (isMondayChecked) {
                mRepeatStr.append("2,");
            }
            if (isTuesdayChecked) {
                mRepeatStr.append("3,");
            }
            if (isWednesdayChecked) {
                mRepeatStr.append("4,");
            }
            if (isThursdayChecked) {
                mRepeatStr.append("5,");
            }
            if (isFridayChecked) {
                mRepeatStr.append("6,");
            }
            if (isSaturdayChecked) {
                mRepeatStr.append("7,");
            }
            if (isSundayChecked) {
                mRepeatStr.append("1,");
            }
            mAlarmClock.setWeeks(mRepeatStr.toString());
        }
    }

    public void sendAlarmEvent(boolean isUpdate){

        AlarmClockEntityDao dao = GreenDaoManager.getInstance().getSession().getAlarmClockEntityDao();
        if(mAlarmClock.getId() == -1){
            AlarmClockEntity entity = dao.queryBuilder().orderDesc(AlarmClockEntityDao.Properties.Id).limit(1).unique();
            long id;
            if(entity == null){
                id = 0;
            }else {
                id = entity.getId();
            }
            mAlarmClock.setId(id + 1);
        }
        dao.insertOrReplace(mAlarmClock);
        RxBus.getInstance().post(new AlarmEvent(mAlarmClock,isUpdate?3:1));
    }
}
