package com.moemoe.lalala.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.dialog.SignDialog;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AppUpdateEntity;
import com.moemoe.lalala.model.entity.BuildEntity;
import com.moemoe.lalala.model.entity.DailyTaskEntity;
import com.moemoe.lalala.model.entity.MapMarkContainer;
import com.moemoe.lalala.model.entity.MapMarkEntity;
import com.moemoe.lalala.model.entity.PersonalMainEntity;
import com.moemoe.lalala.model.entity.SignEntity;
import com.moemoe.lalala.model.entity.SnowShowEntity;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.widget.map.MapWidget;
import com.moemoe.lalala.view.widget.map.interfaces.Layer;
import com.moemoe.lalala.view.widget.map.model.MapImage;
import com.moemoe.lalala.view.widget.map.model.MapImgLayer;
import com.moemoe.lalala.view.widget.map.model.MapObject;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/27.
 */

public class MapPresenter implements MapContract.Presenter {
    private MapContract.View view;
    private ApiService apiService;

    @Inject
    public MapPresenter(MapContract.View view,ApiService apiService){
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void checkVersion() {
        apiService.checkVersion(AppSetting.CHANNEL,AppSetting.VERSION_CODE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<AppUpdateEntity>() {
                    @Override
                    public void onSuccess(AppUpdateEntity appUpdateEntity) {
                        if(appUpdateEntity.getUpdateStatus() != 0){
                            view.showUpdateDialog(appUpdateEntity);
                        }
                    }

                    @Override
                    public void onFail(int code,String msg) {

                    }
                });
    }

    @Override
    public void requestPersonMain() {
        apiService.getPersonalMain(PreferenceUtils.getUUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<PersonalMainEntity>() {
                    @Override
                    public void onSuccess(PersonalMainEntity personalMainEntity) {
                        view.onPersonMainLoad(personalMainEntity);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void checkBuild(int buildVersion,int appVersion) {
        apiService.checkBuild(buildVersion,appVersion)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<BuildEntity>() {
                    @Override
                    public void onSuccess(BuildEntity s) {
                        view.checkBuildSuccess(s);
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
    }

    @Override
    public void getDailyTask() {
        apiService.getDailyTask()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<DailyTaskEntity>() {
                    @Override
                    public void onSuccess(DailyTaskEntity dailyTaskEntity) {
                        view.onDailyTaskLoad(dailyTaskEntity);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void signToday(final SignDialog dialog) {
        apiService.signToday()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<SignEntity>() {
                    @Override
                    public void onSuccess(SignEntity entity) {
                        view.changeSignState(entity,true);
                        dialog.setIsSign(true)
                                .setSignDay(entity.getDay())
                                .changeSignState();
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void addDayMapMark(Context context, MapWidget map, float scale) {
        MapMarkContainer container = new MapMarkContainer();
        addMapMarkOneDay(context,map,container);
        addMapMarkDay(context,map,container);
        addMapMarkDayEvent(context,map,container);
        view.onMapMarkLoaded(container);
    }

    @Override
    public void addNightMapMark(Context context, MapWidget map, float scale) {
        MapMarkContainer container = new MapMarkContainer();
        addMapMarkOneDay(context,map,container);
        addMapMarkNight(context,map,container);
        view.onMapMarkLoaded(container);
    }

    @Override
    public void addNightEventMapMark(Context context, MapWidget map, float scale) {
        MapMarkContainer container = new MapMarkContainer();
        addMapMarkOneDay(context,map,container);
        addMapMarkNightEvent(context,map,container);
        view.onMapMarkLoaded(container);
    }

    @Override
    public void clickSnowman(final Object o, final int mapX, final int mapY) {
        apiService.clickSnowman()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        view.onSnowmanSuccess(o,mapX,mapY);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        view.onFailure(code,msg);
                    }
                });
    }

    public void addCacheSnowman(Context context, MapWidget map){
        Layer layer = map.getLayerById(233);
        if(layer != null){
            map.removeLayer(233);
        }
        MapImgLayer imgLayer = map.createImgLayer(233);
        if(SnowShowEntity.getPositionCache() != null && SnowShowEntity.getPositionCache().size() > 0){
            for (int i = 0;i < SnowShowEntity.getPositionCache().size();i++){
                SnowShowEntity.PositionInfo info = SnowShowEntity.getCachePosition(i);
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_map_snowboy);
                MapImage mapImage = new MapImage(context,100 + i,info.x,info.y,info.x - drawable.getIntrinsicWidth() /2,info.y - drawable.getIntrinsicHeight() / 2,0,0,true,false);
                mapImage.setImageDrawable(drawable);
                imgLayer.addMapObject(mapImage);
            }
        }
        map.invalidate();
    }

    @Override
    public void addSnowman(Context context, MapWidget map){
        Layer layer = map.getLayerById(233);
        if(layer != null){
            map.removeLayer(233);
        }
        MapImgLayer imgLayer = map.createImgLayer(233);

        SnowShowEntity.PositionInfo info = SnowShowEntity.getOnePosition();
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_map_snowboy);
        MapImage mapImage = new MapImage(context,100,info.x,info.y,info.x - drawable.getIntrinsicWidth() /2,info.y - drawable.getIntrinsicHeight() / 2,0,0,true,false);
        mapImage.setImageDrawable(drawable);
        imgLayer.addMapObject(mapImage);

        info = SnowShowEntity.getOnePosition();
        drawable = ContextCompat.getDrawable(context, R.drawable.ic_map_snowboy);
        mapImage = new MapImage(context,101,info.x,info.y,info.x - drawable.getIntrinsicWidth() /2,info.y - drawable.getIntrinsicHeight() / 2,0,0,true,false);
        mapImage.setImageDrawable(drawable);
        imgLayer.addMapObject(mapImage);

        info = SnowShowEntity.getOnePosition();
        drawable = ContextCompat.getDrawable(context, R.drawable.ic_map_snowboy);
        mapImage = new MapImage(context,102,info.x,info.y,info.x - drawable.getIntrinsicWidth() /2,info.y - drawable.getIntrinsicHeight() / 2,0,0,true,false);
        mapImage.setImageDrawable(drawable);
        imgLayer.addMapObject(mapImage);
        map.invalidate();
    }

    private void addMapMarkOneDay(Context context, MapWidget map, MapMarkContainer container){
        Layer layer = map.createLayer(0);//0 全天可点击事件
        MapMarkEntity entity = new MapMarkEntity("体育馆",2759,440,"neta://com.moemoe.lalala/department_1.0?uuid=393341d4-5f7f-11e6-a5af-d0a637eac7d7&name=体育馆",R.drawable.btn_map_tiyu_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("图书馆",2666,814,"neta://com.moemoe.lalala/department_1.0?uuid=97e18352-5f7f-11e6-ae04-d0a637eac7d7&name=图书馆",R.drawable.btn_map_tushu_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("美图部",2325,1342,"neta://com.moemoe.lalala/department_1.0?uuid=be39718c-5f7f-11e6-81f9-d0a637eac7d7&name=美图部",R.drawable.btn_map_meitu_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("影音部",2449,1606,"neta://com.moemoe.lalala/department_1.0?uuid=a77f9006-5f7f-11e6-ae2c-d0a637eac7d7&name=影音部",R.drawable.btn_map_yingyin_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("新闻部",1953,176,"neta://com.moemoe.lalala/department_1.0?uuid=26f9831a-5f7f-11e6-8f94-d0a637eac7d7&name=新闻部",R.drawable.btn_map_xingwen_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("保健室",1209,682,"neta://com.moemoe.lalala/department_1.0?uuid=10f8433e-5f80-11e6-b42a-d0a637eac7d7&name=保健室",R.drawable.btn_map_baojian_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("茶话部",713,836,"neta://com.moemoe.lalala/department_1.0?uuid=cfda0aa2-5f7f-11e6-b844-d0a637eac7d7&name=女性部",R.drawable.btn_map_nvxing_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("小卖部",1209,1210,"neta://com.moemoe.lalala/department_1.0?uuid=1dd2dba6-5f80-11e6-ad3c-d0a637eac7d7&name=小卖部",R.drawable.btn_map_xiaomaibu_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("游戏部",1953,1892,"neta://com.moemoe.lalala/department_1.0?uuid=e255f9d4-5f7f-11e6-8a65-d0a637eac7d7&name=游戏部",R.drawable.btn_map_youxi_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("秋名山",2866,1958,"neta://com.moemoe.lalala/qiu_1.0",R.drawable.btn_qiu_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("游泳池",1860,968,"neta://com.moemoe.lalala/swim_1.0",R.drawable.btn_swimpool_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("垃圾桶",2138,858,"neta://com.moemoe.lalala/garbage_1.0",R.drawable.btn_map_trash_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("图片垃圾桶",1358,946,"neta://com.moemoe.lalala/garbage_img_1.0",R.drawable.btn_map_trash_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("扭蛋机抽奖",1395,1628,"neta://com.moemoe.lalala/url_inner_1.0?http://prize.moemoe.la:8000/prize/index/",R.drawable.btn_map_trash_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("迷の塔",248,1540,"neta://com.moemoe.lalala/url_inner_1.0?http://neta.facehub.me/",R.drawable.btn_map_tower_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("anitoys",1023,1210,"neta://com.moemoe.lalala/calui_1.0?uuid=6f90946e-7500-11e6-ba28-e0576405f084&name=anitoys",R.drawable.btn_map_trash_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("ALTER",916,1232,"neta://com.moemoe.lalala/calui_1.0?uuid=8b504a9a-7500-11e6-9642-e0576405f084&name=ALTER",R.drawable.btn_map_trash_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("animate",1202,1320,"neta://com.moemoe.lalala/calui_1.0?uuid=9c183758-7500-11e6-b599-e0576405f084&name=animate",R.drawable.btn_map_trash_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("天闻角川",1176,1452,"neta://com.moemoe.lalala/calui_1.0?uuid=a727b466-7500-11e6-a06a-e0576405f084&name=天闻角川",R.drawable.btn_map_trash_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("玛莎多拉",1426,1496,"neta://com.moemoe.lalala/calui_1.0?uuid=be7a3728-7500-11e6-bdd7-e0576405f084&name=玛莎多拉",R.drawable.btn_map_trash_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("口水三国",1332,1672,"neta://com.moemoe.lalala/calui_1.0?uuid=c7e1acf8-7500-11e6-a813-e0576405f084&name=口水三国",R.drawable.btn_map_trash_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("艾漫",961,1408,"neta://com.moemoe.lalala/calui_1.0?uuid=904a0f34-75b0-11e6-af78-e0576405f084&name=艾漫",R.drawable.btn_map_trash_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("歪瓜",1302,1562,"neta://com.moemoe.lalala/calui_1.0?uuid=931730ea-e17f-11e6-806b-525400761152&name=歪瓜",R.drawable.btn_map_trash_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("绘梦",1457,1364,"neta://com.moemoe.lalala/tag_1.0?d6e064ca-c381-11e6-ad64-525400761152",R.drawable.btn_map_trash_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("猫之茗",1829,1804,"neta://com.moemoe.lalala/tag_1.0?52086406-e180-11e6-b58f-525400761152",R.drawable.btn_map_trash_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("哲学",1358,1804,"neta://com.moemoe.lalala/doc_1.0?3a61f262-75b3-11e6-a766-e0576405f084",R.drawable.btn_map_trash_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        ArrayList<String> content = new ArrayList<>();
        content.add("听说塔里有个体力值翻倍的道具，有了那个我体育考试就能及格了！！");
        content.add("唔…某度说，红血瓶+250体力、蓝血瓶+500体力；红水晶+3攻击、蓝水晶+3防御。");
        content.add("注意安全啊莲，尽量加“防御”吧，一定要平安回来！");
        content.add("邱枳实学长和千世大小姐已经摸清楚里面的情况了，尽量找他们获取帮助吧，真不愧是Neta的精英！");
        content.add("最终之战开启后，就无法回头了…");
        content.add("有一种怪物会自爆的说，是很危险的存在，叫做“灰烬…法师”？");
        content.add("如果没有实力战胜怪物，也找不到补给的话，就会被的永远困在里面了，好可怕。");
        content.add("分享可以赚到100金币，但我连进去的勇气都没有…");
        entity = new MapMarkEntity("随机tip对话",775,2090,null,content,R.drawable.btn_map_plot);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("募捐箱纪念碑",2077,2112,"neta://com.moemoe.lalala/doc_1.0?bcfc69a8-b2fd-11e6-954d-525400761152",R.drawable.btn_map_trash_normal);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);
    }

    private void addMapMarkDay(Context context, MapWidget map,MapMarkContainer container){
        Layer layer = map.createLayer(1);//1 白天可点击事件

        MapMarkEntity entity = new MapMarkEntity("希亚",1023,550,null,"“这风尘的造型和媚俗的颜色…嗯？很好看吗？”\n" + "“谢、谢谢…新的一年我也会尽量给大家收集些好东西的…”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("千石",1982,286 ,null,"“只过去了一年时间，Neta居然已经这么繁荣了。”\n" + "“2017的它，又会是什么样子呢？”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("樱木军团",1519,66,null,"“冬天游泳池都没有人了。”\n" + "“听说学校里开了个温泉，是不是…”\n" + "“走着！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("湘北跑步三人组",1540,66,null,"“为什么我们直接变成篮球了？”\n" + "“跑了半年，可以滚了不是很好吗？”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("风纪委员",713,616,null,"“为什么要把本风纪委员的帽子拿掉！！！！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("一拳超人",1581,704,null,"“本来这里站着的是第三个老师，结果他去了拉面店，闪亮的光头还能再坚挺一个寒假！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("麦克雷",2108,748,null,"我一个午时就是四个人头…”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("拉姆",1798,726,null,"“雷姆雷姆，我们人气太高也是没有办法的事情。”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("凸变英雄",1519,1540,null,"“道理我都懂，但咱们绘梦的阵仗为什么这么大？”\n" + "“不知道啊，是不是要支持国漫的说？”\n" + "“嘁、我们这杰出的画风和剧情，需要特别支持？”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("小莲和喵帕斯",1147,1958,null,"“喵…喵帕斯，是这样吧？”\n" + "“只要举起一只手就够了。”\n" + "“这是新年好的意思吗？”\n" + "“清明、劳动、中秋、端午也可以用的，喵帕斯！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("猫之茗角色",1488,1892,null,"“看了一圈，发现本喵是这里最萌的！”\n" + "“但是大家并不知道咱是谁喵…”\n" + "“戳戳游戏部上面那块广告牌就可以了！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("东马",1147,1628,null,"“雪菜碧……嘴啦！”\n" + "“校长就在那边看着，偷懒被抓到要扣薪水的！",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("雪莱",1054,1518,null,"“喂！冬马小……姐姐！”\n" + "“辛苦了，要不要一起去旁边喝杯奶茶？”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("北原春希",992,1694,null,"“一脸懵逼”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("栗山未来",1984,1386,null,"“我还是要说，没有未来的未来不是要我的未来。”\n" + "“可从期末考试的成绩看来，你也不能拥有什么未来。”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("四之宫隼人和六见游马",2852,1540,null,"“凉宫和朝比奈放寒假了，学姐不喜欢我们吗？”\n" + "“才、才、才没有不喜欢！！！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("七岛希",2976,1342,null,"“楼顶上有个奇怪的生物，不知道从哪里冒出来的。”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("五十岚祐辅",2542,1232,null,"“你确定不要认真研究一下新年特别版Neta吗？”\n" + "“我们可是赶工赶得险些不能回家过年呢。”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("齐木楠雄",2790,814,null,"“可惜我帅不过版本大佬？”\n" + "“是……么？”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("黄鸡",2790,660,null,"“恕瑞玛，你的黄鸡回来了！”\n" + "“咕叽咕叽…”\n" + "“咳咳，祝同学们鸡年大吉吧！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("风夏",2480,814,null,"“为什么不让我们登台的说？”\n" + "“因为主编喜欢K-on吧…”\n" + "“气…！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("平泽唯",2325,858,null,"“果咩果咩！跟主编约会耽误了点儿时间！”\n" + "（这句台词好像在录入前被谁偷偷改过）",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("K-on",2201,682,null,"“新年好呀、新年好呀、祝贺大家新年好！”\n" + "“梓喵你为什么要唱儿歌…？”\n" + "“T T，我又不是主唱！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("蕾姆",1705,770,null,"“姐姐姐姐，为什么我们又被派到这里发传单了？”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("人渣的本愿",1302,308,null,"“我们出现在这里会不会败坏了学校的风气？”\n" + "“唔，作为新番之星，总是要露个脸的。”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("夏目贵志",527,1056,null,"“为什么第五季结束那么久了我们才出场？”\n" + "“赶上新年版本不是很好吗，老师。”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("茶话会门口路人",651,1188,null,"“茶话室为什么搬到这里来了？”\n" + "“因为原来的茶话室改建成温泉了。”\n" + "“温泉…是做什么的？”\n" + "“GuanShui、ShuaJingyan之类…的吧…”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("唐服校长",775,1848,null,"“嚯，那边的同学，4.0版本是不是超厉害的啊！”\n" + "“是啊、是啊（敷衍）”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("准备进魔塔的路人",248,1892,null,"“我出发了，这次一定要拿到勋章！”\n" + "“再提醒你一次，不到4000攻5000防千万不要去最后一层！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("滑稽侠",868,1848,null,"“放大画面看我表情。”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("游戏部的纪念碑",1643,1848,null,"【特别鸣谢】雌狼/longo3/秋名山上行人稀/百变爱莉魔术卡/让我静一静/魔王/尤莉叶/狐狸大大/御个小猫/为狐狸展翅的天使）",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);
        entity = new MapMarkEntity("剧情-场景2",2918,1012,"neta://com.moemoe.lalala/url_inner_1.0?http://prize.moemoe.la:8000/netaopera/chap2/",R.drawable.btn_map_plot);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);
        entity = new MapMarkEntity("剧情-场景3",1748,726,"neta://com.moemoe.lalala/url_inner_1.0?http://prize.moemoe.la:8000/netaopera/chap3/",R.drawable.btn_map_plot);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);
        entity = new MapMarkEntity("剧情-场景5",968,704,"neta://com.moemoe.lalala/url_inner_1.0?http://prize.moemoe.la:8000/netaopera/chap5/",R.drawable.btn_map_plot);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);
    }

    private void addMapMarkNight(Context context, MapWidget map,MapMarkContainer container){
        Layer layer = map.createLayer(2);//2 晚上可点击事件
        MapMarkEntity entity = new MapMarkEntity("樱木军团_晚上",1436,66,null,"“冬天游泳池都没有人了。”\n" + "“听说学校里开了个温泉，是不是…”\n" + "“走着！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("湘北跑步三人组_晚上",1540,88,null,"“怎么晚上又要跑步了？不是说好变成篮球偷偷懒么？”\n" + "“画师说，晚上太暗了，变成篮球根本看不清楚。”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("风纪委员_晚上",713,616,null,"“为什么要把本风纪委员的帽子拿掉！！！！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("鸣人和路飞_晚上",403,1122,null,"“那只猫为什么一直看着我们？”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("银时_晚上",310,1892,null,"“虽然不知道你想到那里去了，不过，我告诉你，这是【阿姆斯特朗回旋加速喷气式阿姆斯特朗炮】”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("新八_晚上",186,1892,null,"“啊！你是想让这个APP被腰斩吗？！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("神乐_晚上",217,1980,null,"“这个炮好雄伟的说！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("麦克雷和小美_晚上",124,2024,null,"“小美用起来可真方便啊，明明没有下雪居然也可以堆雪♂人”\n" + "“不洗澡！请不要让我帮你们做奇怪的事情！",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("一拳超人_晚上",1618,704,null,"“本来这里站着的是第三个老师，结果他去了拉面店，闪亮的光头还能再坚挺一个寒假！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("猫之茗角色_晚上",1488,1892,null,"“看了一圈，发现本喵是这里最萌的！”\n" + "“但是大家并不知道咱是谁喵…”\n" + "“戳戳游戏部上面那块广告牌就可以了！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("东马_晚上",1147,1628,null,"“雪菜碧……嘴啦！”\n" + "“校长就在那边看着，偷懒被抓到要扣薪水的！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("雪莱_晚上",1054,1518,null,"“喂！冬马小……姐姐！”\n" + "“辛苦了，要不要一起去旁边喝杯奶茶？”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("北原春希_晚上",992,1694,null,"“一脸懵逼”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("栗山未来_晚上",1984,1386,null,"“我还是要说，没有未来的未来不是要我的未来。”\n" + "“可从期末考试的成绩看来，你也不能拥有什么未来。”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("五十岚祐辅_晚上",2542,1232,null,"“你确定不要认真研究一下新年特别版Neta吗？”\n" + "“我们可是赶工赶得险些不能回家过年呢。”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("坂本君_晚上",2790,814,null,"“你以为坂本又回来了吗？”\n" + "“天真，我是你齐神。”\n" + "（绝妙的花式偷懒！）",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("黄鸡_晚上",2790,660,null,"“恕瑞玛，你的黄鸡回来了！”\n" + "“咕叽咕叽…”\n" + "“咳咳，祝同学们鸡年大吉吧！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("风夏_晚上",2480,814,null,"“为什么不让我们登台的说？”\n" + "“因为主编喜欢K-on吧…”\n" + "“气…！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("K-on_晚上",2201,682,null,"新年好呀新年好呀！\n" + "祝贺大家新年好！\n" + "我们唱歌！我们跳舞！\n" + "祝贺大家新年好！\n" + "（我们还是去隔壁看LiveLive吧…）",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("拉姆_晚上",1798,726,null,"“雷姆雷姆，我们人气太高也是没有办法的事情。”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("蕾姆_晚上",1705,770,null,"“姐姐姐姐，为什么我们又被派到这里发传单了？”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("人渣的本愿_晚上",1302,308,null,"“我们出现在这里会不会败坏了学校的风气？”\n" + "“唔，作为新番之星，总是要露个脸的。”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("夏目贵志_晚上",527,1056,null,"“为什么第五季结束那么久了我们才出场？”\n" + "“赶上新年版本不是很好吗，老师。”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("茶话会门口路人_晚上",651,1188,null,"“茶话室为什么搬到这里来了？”\n" + "“因为原来的茶话室改建成温泉了。”\n" + "“温泉…是做什么的？”\n" + "“GuanShui、ShuaJingyan之类…的吧…”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("唐服校长_晚上",775,1848,null,"“嚯，那边的同学，4.0版本是不是超厉害的啊！”\n" + "“是啊、是啊（敷衍）”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("真选组舞狮_晚上",1178,1936,null,"“为什么要走在我后面？胆子真是小啊…”\n" + "“在王的雕像下总要堤防一手吧，尤其是跟你这个抖S在一起。”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("喵帕斯_晚上",868,1848,null,"“那边…那个人，会给俺们红包吗？”\n" + "“不要靠近奇怪的家伙！！”",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

        entity = new MapMarkEntity("游戏部的纪念碑_晚上",1643,1848,null,"【特别鸣谢】雌狼/longo3/秋名山上行人稀/百变爱莉魔术卡/让我静一静/魔王/尤莉叶/狐狸大大/御个小猫/为狐狸展翅的天使",R.drawable.btn_map_click);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);

    }

    private void addMapMarkDayEvent(Context context, MapWidget map,MapMarkContainer container){
        Layer layer = map.createLayer(3);//3 白天事件6 - 8
        MapMarkEntity entity = new MapMarkEntity("剧情-场景1",1488,682,"neta://com.moemoe.lalala/url_inner_1.0?http://prize.moemoe.la:8000/netaopera/chap1/",R.drawable.btn_map_plot);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);
    }

    private void addMapMarkNightEvent(Context context, MapWidget map,MapMarkContainer container){
        Layer layer = map.createLayer(4);//4 凌晨事件 12 - 1
        MapMarkEntity entity = new MapMarkEntity("剧情-场景4",1748,418,"neta://com.moemoe.lalala/event_1.0",R.drawable.btn_map_plot);
        container.addMark(entity);
        addMarkToMap(context,entity.getId(),entity,layer);
    }

    private void addMarkToMap(Context context,String id,MapMarkEntity entity,Layer layer){
        Drawable drawable = ContextCompat.getDrawable(context, entity.getBg());
        MapObject object = new MapObject(id
                ,drawable
                ,entity.getX() - drawable.getIntrinsicWidth() /2
                ,entity.getY() - drawable.getIntrinsicHeight() / 2
                ,0
                ,0
                ,true
                ,false);
        layer.addMapObject(object);
        entity.setW(drawable.getIntrinsicWidth());
        entity.setH(drawable.getIntrinsicHeight());
    }
}
