package com.app.view;

import android.app.Activity;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.Utils;
import com.app.ViewInject;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.annotation.OnEvent;
import com.app.common.util.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;

/**
 * Created by Haru on 2016/3/28 0028.
 */
public class ViewInjectImpl implements ViewInject{

    /**
     * 过滤类
     */
    private static final HashSet<Class<?>> IGNORED = new HashSet<>();
    static {
        IGNORED.add(Object.class);
        IGNORED.add(Activity.class);
        IGNORED.add(Fragment.class);
        try{
            IGNORED.add(Class.forName("android.support.v4.app.Fragment"));
            IGNORED.add(Class.forName("android.support.v4.app.FragmentActivity"));
        }catch (Throwable e){

        }
    }

    private static final Object lock = new Object();
    private static ViewInjectImpl instance;

    private ViewInjectImpl(){}

    public static void getInstance(){
        if(instance == null){
            synchronized (lock){
                if(instance == null){
                    instance = new ViewInjectImpl();
                }
            }
        }
        Utils.Control.setViewInject(instance);
    }

    @Override
    public void inject(View view) {
        injectObject(view, view.getClass(), new ViewFinder(view));
    }

    @Override
    public void inject(Activity activity) {
        Class<?> handlerType = activity.getClass();
        try {
            ContentView contentView = findContentView(handlerType);
            if(contentView != null){
                int viewId = contentView.value();
                if(viewId > 0){
                    Method setContentViewMethod = handlerType.getMethod("setContentView",int.class);
                    setContentViewMethod.invoke(activity,viewId);
                }
            }
        }catch (Throwable e){
            LogUtil.e(e.getMessage(),e);
        }
        injectObject(activity,handlerType,new ViewFinder(activity));
    }

    private static ContentView findContentView(Class<?> cls){
        if(cls == null || IGNORED.contains(cls)){
            return null;
        }
        ContentView contentView = cls.getAnnotation(ContentView.class);
        if(contentView == null){
            return findContentView(cls.getSuperclass());
        }
        return contentView;
    }

    @Override
    public void inject(Object handler, View view) {
        injectObject(handler,handler.getClass(),new ViewFinder(view));
    }

    @Override
    public View inject(Object fragment, LayoutInflater inflater, ViewGroup container) {
        View view = null;
        Class<?> handlerType = fragment.getClass();
        try {
            ContentView contentView = findContentView(handlerType);
            if(contentView != null){
                int viewId = contentView.value();
                if(viewId > 0){
                    view = inflater.inflate(viewId,container,false);
                }
            }
        }catch (Throwable e){
            LogUtil.e(e.getMessage(),e);
        }

        injectObject(fragment,handlerType,new ViewFinder(view));
        return view;
    }

    private static void injectObject(Object handler,Class<?> handlerType,ViewFinder finder){
        if(handlerType == null || IGNORED.contains(handlerType)){
            return;
        }
        //view
        Field[] fields = handlerType.getDeclaredFields();
        if(fields != null && fields.length > 0){
            for(Field field : fields){
                Class<?> fieldType = field.getType();
                if(Modifier.isStatic(field.getModifiers()) || //静态不注入
                        Modifier.isFinal(field.getModifiers())||//final不注入
                        fieldType.isPrimitive()||//基本类型不注入
                        fieldType.isArray()){//数组类型不注入
                    continue;
                }
                FindView viewInject = field.getAnnotation(FindView.class);
                if(viewInject != null){
                    try {
                        View view = finder.findViewById(viewInject.value(),viewInject.parentId());
                        if(view != null){
                            field.setAccessible(true);
                            field.set(handler,view);
                        }else {
                            throw new RuntimeException("Invalid id("+viewInject.value()+") for ViewInject!" + handlerType.getSimpleName());
                        }
                    }catch (Throwable e){
                        LogUtil.e(e.getMessage(),e);
                    }
                }
            }
        }

        //event
        Method[] methods = handlerType.getDeclaredMethods();
        if(methods != null && methods.length > 0){
            for(Method method : methods){
                if(Modifier.isStatic(method.getModifiers()) || !Modifier.isPrivate(method.getModifiers())){
                    continue;
                }

                OnEvent event = method.getAnnotation(OnEvent.class);
                if(event != null){
                    try {
                        int[] values = event.value();
                        int[] parentIds = event.parentId();
                        int parentIdLen = parentIds == null ? 0 : parentIds.length;
                        for(int i = 0;i < values.length; i++){
                            int value = values[i];
                            if(value > 0){
                                ViewInfo info = new ViewInfo();
                                info.value = value;
                                info.parenetId = parentIdLen > i? parentIds[i] : 0;
                                method.setAccessible(true);
                                EventListenerManager.addEventMethod(finder,info,event,handler,method);
                            }
                        }
                    }catch (Throwable e){
                        LogUtil.e(e.getMessage(),e);
                    }
                }
            }
        }
        injectObject(handler,handlerType.getSuperclass(),finder);
    }
}
