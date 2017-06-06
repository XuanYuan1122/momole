package com.moemoe.lalala.utils;

/**
 * Created by Haru on 2016/5/24 0024.
 */
public class BrowserJsInject {
    /**
     * Js注入
     * @param url 加载的网页地址
     * @return 注入的js内容，若不是需要适配的网址则返回空javascript
     */
    public static String fullScreenByJs(String url){
        String refer = referParser(url);
        if (null != refer) {
            return refer;
        }else {
            return "javascript:(0)";
        }
    }

    /**
     * 对不同的视频网站分析相应的全屏控件
     * @param url 加载的网页地址
     * @return 相应网站全屏按钮的class标识
     */
    public static String referParser(String url){
        if (url.contains("letv")) {//乐视Tv
            return "javascript:document.getElementsByClassName('hv_ico_screen')[0].addEventListener('click',function(){local_obj.playing();return false;});";               //乐视Tv
        }else if (url.contains("youku")) {             //优酷
            return "javascript:document.getElementsByClassName('x-zoomin')[0].addEventListener('click',function(){local_obj.playing();return false;});";                      //优酷
        }else if (url.contains("bilibili")) {//bilibili
            return "javascript:document.getElementsByClassName('icon-widescreen')[0].addEventListener('click',function(){local_obj.playing();return false;});";
        }else if (url.contains("qq")) {//腾讯视频
            return "javascript:document.getElementsByClassName('tvp_fullscreen_button')[0].addEventListener('click',function(){local_obj.playing();return false;});";
        }else if(url.contains("tudou")){//土豆
            return "javascript: var _exit = document.getElementsByClassName('exit')[0]; if(_exit === null || _exit === undefined) {}else{ local_obj.playing()}";
        }

        return null;
    }

}
