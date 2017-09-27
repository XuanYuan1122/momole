//
// Created by yi on 2017/9/25.
//

#include <string.h>
#include <jni.h>
#include <android/log.h>

#define  LOG    "JNILOG" // 这个是自定义的LOG的TAG
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG,__VA_ARGS__) // 定义LOGE类型

static JavaVM *gJavaVM;
static jobject gCallbackObject = NULL;

extern "C"
JNIEXPORT jint JNICALL
Java_com_moemoe_lalala_view_activity_MapActivity_initNDK(JNIEnv *env, jobject instance) {
    (*env).GetJavaVM(&gJavaVM);
    gCallbackObject = (*env).NewGlobalRef(instance);
    return 1;
}

extern "C"
JNIEXPORT void JNICALL
toNativeView(char *schema,char *name) {
    LOGE("我是log %s",name);
    JNIEnv *env;
    int attached = (*gJavaVM).AttachCurrentThread(&env,NULL);
    LOGE("我是log2 %d",attached);
    jclass jcls = (*env).FindClass("com/moemoe/lalala/view/activity/MapActivity");
    LOGE("我是log3 %s",name);
    jmethodID mid = (*env).GetMethodID(jcls,"toNativeView","(Ljava/lang/String;Ljava/lang/String;)V");
    LOGE("我是log4 %s",name);
    (*env).CallVoidMethod(gCallbackObject,mid,(*env).NewStringUTF(schema),(*env).NewStringUTF(name));
    LOGE("我是log5 %s",name);
}

extern "C"
JNIEXPORT void JNICALL
changeButtonState() {
    JNIEnv *env;
    int attached = (*gJavaVM).AttachCurrentThread(&env,NULL);
    jclass jcls = (*env).FindClass("com/moemoe/lalala/view/activity/MapActivity");
    jmethodID mid = (*env).GetMethodID(jcls,"changeButtonState","()V");
    (*env).CallVoidMethod(gCallbackObject,mid);
}

extern "C"
JNIEXPORT void JNICALL
StartLoad() {
    JNIEnv *env;
    int attached = (*gJavaVM).AttachCurrentThread(&env,NULL);
    jclass jcls = (*env).FindClass("com/moemoe/lalala/view/activity/MapActivity");
    jmethodID mid = (*env).GetMethodID(jcls,"StartLoad","()V");
    (*env).CallVoidMethod(gCallbackObject,mid);
}

