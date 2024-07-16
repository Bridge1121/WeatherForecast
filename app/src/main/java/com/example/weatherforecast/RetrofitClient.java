package com.example.weatherforecast;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://ws.webxml.com.cn/webservices/ChinaTVprogramWebService.asmx/";
    private static Retrofit retrofit;
    private static Retrofit tokenRetrofit;


    private RetrofitClient() {
        // 私有构造函数，防止外部实例化
    }

    public static Retrofit getTokenInstance(String token) {
        if (tokenRetrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.i("TAG", message);
                }
            }).setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            if (token != null) {
                httpClient.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request newRequest = originalRequest.newBuilder()
                                .header("token", token)
                                .build();

                        return chain.proceed(newRequest);
                    }
                });
            }

            OkHttpClient okHttpClient = httpClient.addInterceptor(interceptor).build();

            Gson gson = new GsonBuilder()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getInstance(String token) {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.i("TAG", message);
                }
            }).setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            if (token != null) {
                httpClient.addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        Request newRequest = originalRequest.newBuilder()
                                .header("token", token)
                                .build();

                        return chain.proceed(newRequest);
                    }
                });
            }

            OkHttpClient okHttpClient = httpClient.addInterceptor(interceptor).build();

            Gson gson = new GsonBuilder()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}