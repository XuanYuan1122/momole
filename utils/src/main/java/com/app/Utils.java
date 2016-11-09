package com.app;

import android.app.Application;
import android.content.Context;

import com.app.common.TaskController;
import com.app.common.task.TaskControllerImpl;
import com.app.db.DbManagerImpl;
import com.app.http.HttpManagerImpl;
import com.app.image.ImageManagerImpl;
import com.app.view.DbManager;
import com.app.view.ViewInjectImpl;

import java.lang.reflect.Method;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * 总控制类
 * Created by Haru on 2016/3/28 0028.
 */
public final class Utils {
    private Utils(){}

    public static boolean isDebug(){ return Control.debug; }

    public static Application app(){
        if(Control.app == null){
            try {
                Class<?> renderActionClass = Class.forName("com.android.layoutlib.bridge.impl.RenderAction");
                Method method = renderActionClass.getDeclaredMethod("getCurrentContext");
                Context context = (Context) method.invoke(null);
                Control.app = new MockApplication(context);
            }catch (Throwable e){
                throw new RuntimeException("please invoke CommonUtils.Control.init(app) on Application#onCreate()");
            }
        }
        return Control.app;
    }

    public static TaskController task() {
        return Control.taskController;
    }

    public static HttpManager http(){
        if(Control.httpManager == null){
         HttpManagerImpl.registerInstance();
        }
        return Control.httpManager;
    }

    public static ImageManager image(){
        if(Control.imageManager == null){
            ImageManagerImpl.registerInstance();
        }
        return Control.imageManager;
    }

    public static ViewInject view(){
        if(Control.viewInject == null){
            ViewInjectImpl.getInstance();
        }
        return Control.viewInject;
    }

    public static DbManager getDb(DbManager.DaoConfig daoConfig){
        return DbManagerImpl.getInstance(daoConfig);
    }


    public static class Control{
        private static boolean debug;
        private static Application app;
        private static ViewInject viewInject;
        private static HttpManager httpManager;
        private static TaskController taskController;
        private static ImageManager imageManager;

        private Control(){}

        static {
            TaskControllerImpl.registerInstance();
            // 默认信任所有https域名
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }

        public static void init(Application app){
            if(Control.app == null){
                Control.app = app;
            }
        }

        public static void setDebug(boolean debug){Control.debug = debug;}

        public static void setViewInject(ViewInject viewInject){
            Control.viewInject = viewInject;
        }

        public static void setHttpManager(HttpManager httpManager){
            Control.httpManager = httpManager;
        }

        public static void setImageManager(ImageManager imageManager){
            Control.imageManager = imageManager;
        }

        public static void setTaskController(TaskController taskController) {
            if (Control.taskController == null) {
                Control.taskController = taskController;
            }
        }
    }

    private static class MockApplication extends Application{
        public MockApplication(Context context){
            this.attachBaseContext(context);
        }
    }
}
