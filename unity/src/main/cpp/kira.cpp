//
// Created by yi on 2017/9/25.
//

#include <string.h>
#include <jni.h>
#include <android/log.h>

#define  LOG    "JNILOG" // 这个是自定义的LOG的TAG
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG,__VA_ARGS__) // 定义LOGE类型

static JavaVM *gJavaVM;
static JavaVM *gJavaVM1;
static jobject gCallbackObject = NULL;
static jobject gCallbackObject1 = NULL;

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_unity_MapGameActivity_initNDK1(JNIEnv *env, jobject instance) {
    (*env).GetJavaVM(&gJavaVM1);
    gCallbackObject1 = (*env).NewGlobalRef(instance);
    return 1;
}

extern "C"
JNIEXPORT void JNICALL
EventError() {
    JNIEnv *env;
    int attached = (*gJavaVM1).AttachCurrentThread(&env,NULL);
    jclass jcls = (*env).FindClass("com/example/unity/MapGameActivity");
    jmethodID mid = (*env).GetMethodID(jcls,"EventError","()V");
    (*env).CallVoidMethod(gCallbackObject1,mid);
}

extern "C"
JNIEXPORT void JNICALL
EventInit() {
    JNIEnv *env;
    int attached = (*gJavaVM1).AttachCurrentThread(&env,NULL);
    jclass jcls = (*env).FindClass("com/example/unity/MapGameActivity");
    jmethodID mid = (*env).GetMethodID(jcls,"EventInit","()V");
    (*env).CallVoidMethod(gCallbackObject1,mid);
}
