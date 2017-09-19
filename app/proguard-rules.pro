# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#glide
-keepattributes EnclosingMethod
#指定代码的压缩级别
-optimizationpasses 5
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#优化  不优化输入的类文件
-dontoptimize
#预校验
-dontpreverify
#混淆时是否记录日志
-verbose
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-keepattributes Signature,*Annotation*
-keepattributes *JavascriptInterface*
-keepattributes SourceFile,LineNumberTable
# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment
#忽略警告
-ignorewarning
##记录生成的日志数据,gradle build时在本项目根目录输出##
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt
########记录生成的日志数据，gradle build时 在本项目根目录输出-end######
#####混淆保护自己项目的部分代码以及引用的第三方jar包library#######
-keep class com.loopj.android.http.**{ *; }
-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
-ignorewarnings
-keep class com.google.i18n.phonenumbers.**{ *; }
-keep class jp.live2d.**{ *; }
-keep class com.nineoldandroids.**{ *; }
-keep class com.umeng.analytics.**{ *; }
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep class com.facebook.rebound.**{*;}
#########sharesdk########
-keep class cn.sharesdk.**{ *;}
-keep class com.sina.**{ *; }
-keep class **.R$* {*;}
-keep class **.R{*;}
-keep class com.mob.**{ *; }
-dontwarn com.mob.**
-dontwarn cn.sharesdk.**
-dontwarn **.R$*
#########sharesdk########
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions,InnerClasses
# OkHttp3
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**
#rxdownload
-keep class zlc.season.rxdownload.** {*;}
-dontwarn sun.misc.Unsafe
-keep class rx.internal.util.unsafe.** { *; }
### greenDAO 3
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
# 支付
-dontwarn com.alipay.**
-keep class com.alipay.** {*;}

-dontwarn  com.ta.utdid2.**
-keep class com.ta.utdid2.** {*;}

-dontwarn  com.ut.device.**
-keep class com.ut.device.** {*;}

-dontwarn  com.tencent.**
-keep class com.tencent.** {*;}

-dontwarn  com.unionpay.**
-keep class com.unionpay.** {*;}

-dontwarn com.pingplusplus.**
-keep class com.pingplusplus.** {*;}

-dontwarn com.baidu.**
-keep class com.baidu.** {*;}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
#如果引用了v4或者v7包
-dontwarn android.support.**
-dontwarn com.squareup.okhttp.**
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
     native <methods>;
 }

 #保持自定义控件类不被混淆
 -keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet);
 }

 #保持自定义控件类不被混淆
 -keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
 }

 -keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet, int);
 }

 #保持 Parcelable 不被混淆
 -keep class * implements android.os.Parcelable {
   public static final android.os.Parcelable$Creator *;
 }

 #保持 Serializable 不被混淆
 -keepnames class * implements java.io.Serializable

 #保持 Serializable 不被混淆并且enum 类也不被混淆
 -keepclassmembers class * implements java.io.Serializable {
     static final long serialVersionUID;
     private static final java.io.ObjectStreamField[] serialPersistentFields;
     !static !transient <fields>;
     !private <fields>;
     !private <methods>;
     private void writeObject(java.io.ObjectOutputStream);
     private void readObject(java.io.ObjectInputStream);
     java.lang.Object writeReplace();
     java.lang.Object readResolve();
 }

 -keepclassmembers class * {
     public void *ButtonClicked(android.view.View);
 }

 #不混淆资源类
 -keepclassmembers class **.R$* {
     public static <fields>;
 }

#保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
-keepclassmembers enum * {
   public static **[] values();
   public static ** valueOf(java.lang.String);
}

-keepclassmembers class com.moemoe.lalala.webview.CustomWebView$JavascriptInterface{
   public *;
}

-keepclassmembers class com.moemoe.lalala.webview.CustomWebView$JavascriptFull{
   public *;
}

-keepclassmembers class com.moemoe.lalala.webview.CustomWebView$InJavaScriptLocalObj{
   public *;
}

-keepclassmembers class com.moemoe.lalala.webview.CustomWebView$NetaJs{
   public *;
}
#避免混淆泛型 如果混淆报错建议关掉
#–keepattributes Signature
#如果用用到Gson解析包的，直接添加下面这几行就能成功混淆，不然会报错。
#gson
#-libraryjars libs/gson-2.2.2.jar
## Gson specific classes
#-keep class sun.misc.Unsafe { *; }
## Application classes that will be serialized/deserialized over Gson
#-keep class com.google.gson.examples.android.model.** { *; }
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic index information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.

# For using GSON @Expose annotation

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }


# Application classes that will be serialized/deserialized over Gson
-keep class com.moemoe.lalala.model.entity.** { *; }
-keep class com.moemoe.lalala.greendao.gen.**{ *; }
-keep class com.moemoe.lalala.utils.MigrationHelper{ *; }

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

##---------------End: proguard configuration for Gson  ----------

-keep public class com.app.** {
    public protected *;
}
-keep public interface com.app.** {
    public protected *;
}
-keepclassmembers class * extends com.app.** {
    public protected *;
}
-keepclassmembers @com.app.annotation.* class * {*;}
-keepclassmembers class * {
    @com.app.annotation.OnEvent <methods>;
}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-dontwarn com.igexin.**
-keep class com.igexin.**{*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule
#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keep class com.youth.banner.** {
    *;
 }
-keep class com.moemoe.lalala.view.widget.adapter.** {
*;
}
-keep public class * extends com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter
-keep public class * extends com.moemoe.lalala.view.widget.adapter.ClickableViewHolder
-keepclassmembers public class * extends com.moemoe.lalala.view.widget.adapter.ClickableViewHolder {
  <init>(android.view.View);
}
#rongyun
# RongCloud SDK
-keep class io.rong.** {*;}
-keep class * implements io.rong.imlib.model.MessageContent {*;}
-dontwarn io.rong.push.**
-dontnote com.xiaomi.**
-dontnote com.google.android.gms.gcm.**
-dontnote io.rong.**
# VoIP
-keep class io.agora.rtc.** {*;}

# Location
-keep class com.amap.api.**{*;}
-keep class com.amap.api.services.**{*;}

# 红包
-keep class com.google.gson.** { *; }
-keep class com.uuhelper.Application.** {*;}
-keep class net.sourceforge.zbar.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.alipay.** {*;}
-keep class com.jrmf360.rylib.** {*;}
-ignorewarnings
-keep class com.moemoe.lalala.broadcast.SealNotificationReceiver {*;}