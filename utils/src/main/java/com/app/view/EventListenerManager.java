package com.app.view;

import android.text.TextUtils;
import android.view.View;

import com.app.annotation.OnEvent;
import com.app.common.util.DoubleKeyValueMap;
import com.app.common.util.LogUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Haru on 2016/3/28 0028.
 */
final class EventListenerManager {
    private final static long QUICK_EVENT_TIME_SPAN = 400;
    private final static HashSet<String> AVOID_QUICK_EVENT_SET = new HashSet<>(2);

    static {
        AVOID_QUICK_EVENT_SET.add("onClick");
        AVOID_QUICK_EVENT_SET.add("onItemClick");
    }

    private EventListenerManager(){}

    private final static DoubleKeyValueMap<ViewInfo,Class<?>,Object> listenerCache = new DoubleKeyValueMap<>();

    /**
     *
     * @param finder
     * @param info
     * @param event
     * @param handler
     * @param method 注解的方法
     */
    public static void addEventMethod(ViewFinder finder,ViewInfo info,OnEvent event,Object handler,Method method){
        try {
            View view = finder.findViewByInfo(info);
            if(view != null){
                Class<?> listenerType = event.type();
                String listenerSetter = event.setter();
                if(TextUtils.isEmpty(listenerSetter)){
                    listenerSetter = "set" + listenerType.getSimpleName();
                }
                String methodName = event.method();
                boolean addNewMethod = false;
                Object listener = listenerCache.get(info,listenerType);
                DynamicHandler dynamicHandler = null;

                if(listener != null){
                   dynamicHandler = (DynamicHandler) Proxy.getInvocationHandler(listener);
                    addNewMethod = handler.equals(dynamicHandler.getHandler());
                    if(addNewMethod){
                        dynamicHandler.addMethod(methodName,method);
                    }
                }

                if(!addNewMethod){
                    dynamicHandler = new DynamicHandler(handler);
                    dynamicHandler.addMethod(methodName,method);
                    listener = Proxy.newProxyInstance(listenerType.getClassLoader(),new Class<?>[]{listenerType},dynamicHandler);
                    listenerCache.put(info,listenerType,listener);
                }
                Method setEventListenerMethod = view.getClass().getMethod(listenerSetter,listenerType);
                setEventListenerMethod.invoke(view,listener);
            }
        }catch (Throwable e){
            LogUtil.e(e.getMessage(),e);
        }
    }

    public static class DynamicHandler implements InvocationHandler{

        private WeakReference<Object> handlerRef;
        private final HashMap<String,Method> methodMap = new HashMap<>(1);
        private static long lastClickTime = 0;

        public DynamicHandler(Object handler){
            this.handlerRef = new WeakReference<Object>(handler);
        }

        public void addMethod(String name,Method method){ methodMap.put(name,method);}

        public Object getHandler(){ return handlerRef.get();}

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object handler = handlerRef.get();
            if(handler != null){
                String eventMethod = method.getName();
                if("toString".equals(eventMethod)){
                    return DynamicHandler.class.getSimpleName();
                }
                method = methodMap.get(eventMethod);
                if(method == null && methodMap.size() == 1){
                    for(Map.Entry<String,Method> entry : methodMap.entrySet()){
                        if(TextUtils.isEmpty(entry.getKey())){
                            method = entry.getValue();
                        }
                        break;
                    }
                }
                if(method != null){
                    /* 点击时间过短，视为无效点击*/
                    if(AVOID_QUICK_EVENT_SET.contains(eventMethod)){
                        long timeSpan = System.currentTimeMillis() - lastClickTime;
                        if(timeSpan < QUICK_EVENT_TIME_SPAN){
                            LogUtil.d("onClick cancelled" + timeSpan);
                            return null;
                        }
                        lastClickTime = System.currentTimeMillis();
                    }
                    try {
                        return method.invoke(handler,args);
                    }catch (Throwable e){
                        throw  new RuntimeException("invoke method error:" + handler.getClass().getName() + "#" + method.getName(),e);
                    }
                }else {
                    LogUtil.w("method not impl:" + eventMethod + "(" + handler.getClass().getSimpleName() + ")");
                }
            }
            return null;
        }
    }
}
