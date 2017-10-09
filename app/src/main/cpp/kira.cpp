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
    JNIEnv *env;
    int attached = (*gJavaVM).AttachCurrentThread(&env,NULL);
    jclass jcls = (*env).FindClass("com/moemoe/lalala/view/activity/MapActivity");
    jmethodID mid = (*env).GetMethodID(jcls,"toNativeView","(Ljava/lang/String;Ljava/lang/String;)V");
    (*env).CallVoidMethod(gCallbackObject,mid,(*env).NewStringUTF(schema),(*env).NewStringUTF(name));
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

extern "C"
JNIEXPORT void JNICALL
toBagFromUnity(char *schema) {
    JNIEnv *env;
    int attached = (*gJavaVM).AttachCurrentThread(&env,NULL);
    jclass jcls = (*env).FindClass("com/moemoe/lalala/view/activity/MapActivity");
    jmethodID mid = (*env).GetMethodID(jcls,"toBagFromUnity","(Ljava/lang/String;)V");
    (*env).CallVoidMethod(gCallbackObject,mid,(*env).NewStringUTF(schema));
}

extern "C"
JNIEXPORT void JNICALL
toDepartmentFromUnity(char *schema,char *name) {
    JNIEnv *env;
    int attached = (*gJavaVM).AttachCurrentThread(&env,NULL);
    jclass jcls = (*env).FindClass("com/moemoe/lalala/view/activity/MapActivity");
    jmethodID mid = (*env).GetMethodID(jcls,"toDepartmentFromUnity","(Ljava/lang/String;)V");
    (*env).CallVoidMethod(gCallbackObject,mid,(*env).NewStringUTF(schema),(*env).NewStringUTF(name));
}

extern "C"
JNIEXPORT void JNICALL
toPersonalFromUnity(char *schema,char *name) {
    JNIEnv *env;
    int attached = (*gJavaVM).AttachCurrentThread(&env,NULL);
    jclass jcls = (*env).FindClass("com/moemoe/lalala/view/activity/MapActivity");
    jmethodID mid = (*env).GetMethodID(jcls,"toPersonalFromUnity","(Ljava/lang/String;)V");
    (*env).CallVoidMethod(gCallbackObject,mid,(*env).NewStringUTF(schema),(*env).NewStringUTF(name));
}
