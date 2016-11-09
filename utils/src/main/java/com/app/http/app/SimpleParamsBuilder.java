package com.app.http.app;

import com.app.annotation.DELETE;
import com.app.annotation.Field;
import com.app.annotation.FormUrlEncoded;
import com.app.annotation.GET;
import com.app.annotation.HEAD;
import com.app.annotation.HEADER;
import com.app.annotation.HttpRequest;
import com.app.annotation.OPTIONS;
import com.app.annotation.POST;
import com.app.annotation.PUT;
import com.app.annotation.Path;
import com.app.common.util.LogUtil;
import com.app.http.HttpMethod;
import com.app.http.RequestParams;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.cert.X509Certificate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Haru on 2016/4/12 0012.
 * 参数构造器
 */
public class SimpleParamsBuilder implements ParamsBuilder{

    static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
    static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{(" + PARAM + ")\\}");
    static final Pattern PARAM_NAME_REGEX = Pattern.compile(PARAM);

    final Method method;
    final Annotation[] methodAnnotations;
    final Annotation[][] parameterAnnotationsArray;
    final Type[] parameterTypes;
    private HttpMethod httpMethod;
    private String path;
    Set<String> urlParamNames;
    private boolean isFormEncoded;
    private Object[] args;

    public SimpleParamsBuilder(Method method) {
        this.method = method;
        methodAnnotations = method.getAnnotations();
        parameterAnnotationsArray = method.getParameterAnnotations();
        parameterTypes = method.getGenericParameterTypes();
    }

    public void setArgs(Object[] args){
        this.args = args;
    }

    /**
     * 根据@HttpRequest构建请求的url
     *
     * @param httpRequest
     * @return
     */
    @Override
    public String buildUri(HttpRequest httpRequest) {
        return httpRequest.host() + "/" + httpRequest.path();
    }

    /**
     * 根据注解的cacheKeys构建缓存的自定义key,
     * 如果返回null, 默认使用 url 和整个 query string 组成.
     *
     * @param params
     * @param cacheKeys
     * @return
     */
    @Override
    public String buildCacheKey(RequestParams params, String[] cacheKeys) {
        String cacheKey = null;
        if (cacheKeys != null && cacheKeys.length > 0) {
            cacheKey = params.getUri() + "?";
            // 添加cacheKeys对应的queryParams
            for (String key : cacheKeys) {
                String value = params.getStringParameter(key);
                if (value != null) {
                    cacheKey += key + "=" + value + "&";
                }
            }
        }
        return cacheKey;
    }

    /**
     * 自定义SSLSocketFactory
     *
     * @return
     */
    @Override
    public SSLSocketFactory getSSLSocketFactory() {
        return getTrustAllSSLSocketFactory();
    }

    /**
     * 为请求添加通用参数等操作
     *
     * @param params
     */
    @Override
    public void buildParams(RequestParams params) {
        int parameterCount = parameterAnnotationsArray.length;

        for(int i = 0;i < parameterCount;i++){
            Annotation[] parameterAnnotations = parameterAnnotationsArray[i];
            for(Annotation annotation : parameterAnnotations){
                parseParameterAnnotation(i,annotation,params);
            }
        }
        params.setMethod(httpMethod);
        params.setBuildUrl(params.getUri()+path);
    }

    private void parseParameterAnnotation(int i ,Annotation annotation,RequestParams params){
        if(annotation instanceof Path){
            Path path = (Path) annotation;
            String name = path.value();
            validatePathName(name);
            this.path = this.path.replace("{" + name + "}",args[i].toString());
        }else if(annotation instanceof Field){
            if(!isFormEncoded){
                throw new RuntimeException("@Field can only be used with from encoding");
            }
            Field field = (Field) annotation;
            String name = field.value();
            params.addParameter(name,args[i]);
        }else if(annotation instanceof HEAD){
            HEAD header = (HEAD) annotation;
            String name = header.value();
            params.addHeader(name,String.valueOf(args[i]));
        }
    }

    public void buildMethod(){
        for(Annotation annotation : methodAnnotations){
            parseMethodAnnotation(annotation);
        }
    }


    /**
     * 自定义参数签名
     *
     * @param params
     * @param signs
     */
    @Override
    public void buildSign(RequestParams params, String[] signs) {

    }

    private static SSLSocketFactory trustAllSSlSocketFactory;

    public static SSLSocketFactory getTrustAllSSLSocketFactory() {
        if (trustAllSSlSocketFactory == null) {
            synchronized (SimpleParamsBuilder.class) {
                if (trustAllSSlSocketFactory == null) {

                    // 信任所有证书
                    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }};
                    try {
                        SSLContext sslContext = SSLContext.getInstance("TLS");
                        sslContext.init(null, trustAllCerts, null);
                        trustAllSSlSocketFactory = sslContext.getSocketFactory();
                    } catch (Throwable ex) {
                        LogUtil.e(ex.getMessage(), ex);
                    }
                }
            }
        }

        return trustAllSSlSocketFactory;
    }

    private void parseMethodAnnotation(Annotation annotation){
        if(annotation instanceof GET){
            paresHttpMethodAndPath(HttpMethod.GET,((GET)annotation).value());
        }else if(annotation instanceof POST){
            paresHttpMethodAndPath(HttpMethod.POST,((POST)annotation).value());
        }else if(annotation instanceof FormUrlEncoded){
            isFormEncoded = true;
        }else if(annotation instanceof DELETE){
            paresHttpMethodAndPath(HttpMethod.DELETE,((DELETE)annotation).value());
        }else if(annotation instanceof OPTIONS){
            paresHttpMethodAndPath(HttpMethod.OPTIONS,((OPTIONS)annotation).value());
        }else if(annotation instanceof PUT){
            paresHttpMethodAndPath(HttpMethod.PUT,((PUT)annotation).value());
        }
    }

    private void paresHttpMethodAndPath(HttpMethod method,String value){
        this.httpMethod = method;
        if(value.isEmpty()) return;

        this.path = value;
        this.urlParamNames = parsePathParameters(value);
    }

    public HttpMethod getHttpMethod(){
        return httpMethod;
    }

    static Set<String> parsePathParameters(String path){
        Matcher m = PARAM_URL_REGEX.matcher(path);
        Set<String> patterns = new LinkedHashSet<>();
        while (m.find()){
            patterns.add(m.group(1));
        }
        return patterns;
    }

    private void validatePathName(String name){
        if(!PARAM_NAME_REGEX.matcher(name).matches()){
           throw new RuntimeException("path name must match");
        }
        if(!urlParamNames.contains(name)){
            throw new RuntimeException("path name must match");
        }
    }
}
