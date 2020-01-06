package com.example.carassistant.retrofit;


import android.annotation.SuppressLint;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * retrofit 管理器
 * retrofit 就是对okhttp做了一层封装
 */

public class RetrofitManager {

    private static Retrofit mRetrofit;

    private RetrofitManager() {
        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(new Gson()))//使用Gson作为数据转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//使用RxJava作为回调适配器（Observable）
                .client(getOkHttpClient())
                .baseUrl(Constants.SERVER_URL)//地址
                .build();

    }

    public static Retrofit getInstance() {
        if (mRetrofit == null) {
            synchronized (RetrofitManager.class) {
                if (mRetrofit == null) {
                    mRetrofit = new RetrofitManager().getRetrofit();
                }
            }
        }
        return mRetrofit;
    }

    private Retrofit getRetrofit() {
        return mRetrofit;
    }

    /**
     * 重新定义okHttpClient
     *
     * @return OkHttpClient
     */
    private static OkHttpClient getOkHttpClient() {

//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        TrustManager tm = new MyTrustManager();
        SSLContext sslContext = null;
        TrustManagerFactory trustManagerFactory = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            sslContext.init(null, new TrustManager[]{tm}, new SecureRandom());
//            sslContext.init(null, new TrustManager[]{tm}, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert trustManagerFactory != null;
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //使用sslsocket通过代理访问https
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0])
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                //添加请求拦截器
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request oldRequest = chain.request();
                        HttpUrl.Builder builder = oldRequest.url()
                                .newBuilder()
                                .host(oldRequest.url().host())
                                .scheme(oldRequest.url().scheme());
//                                .addQueryParameter("key", "value");

                        Request request = oldRequest.newBuilder()
                                .method(oldRequest.method(),oldRequest.body())
                                .url(builder.build())
                                .build();

                        return chain.proceed(request);
                    }
                })
                //手动创建一个OkHttpClient并设置超时时间
                .connectTimeout(Constants.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                //开启OKHttp的日志拦截
                .addInterceptor(new LogInterceptor())
//                .addInterceptor(new )
                .build();

        return okHttpClient;

    }

    private static class LogInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Log.d("LogUtils--> ", "request:" + request.toString());
            Response response = chain.proceed(chain.request());
            okhttp3.MediaType mediaType = response.body().contentType();
            int code=response.code();
            String content = response.body().string();
            Log.d("LogUtils--> ", "response body:" + content);
            if (response.body() != null) {
                ResponseBody body = ResponseBody.create(mediaType, content);
                return response.newBuilder().body(body).build();
            } else {
                return response;
            }
        }
    }

    /**
     * X509证书信息管理器
     */
    public static class MyTrustManager implements X509TrustManager {

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @SuppressLint("TrustAllX509TrustManager")
        @Override
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] chain, String authType)
                throws java.security.cert.CertificateException {
        }

        @SuppressLint("TrustAllX509TrustManager")
        @Override
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] chain, String authType)
                throws java.security.cert.CertificateException {
        }
        }

}
