package com.help.android.utility;

import android.content.Context;
import android.util.Base64;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private final static String BASE_URL = "https://zingakart.com";

    public static ApiClient apiClient;
    private Retrofit retrofit = null;

    public static ApiClient getInstance() {
        if (apiClient == null) {
            apiClient = new ApiClient();
        }
        return apiClient;
    }

//private static Retrofit storeRetrofit = null;

    public Retrofit getClient() {
        return getClient(null);
    }


    private Retrofit getClient(final Context context) {



// concatenate username and password with colon for authentication
        String credentials = "ck_b43e859a93610aa5e966d9fddc941372ae6aa34e" + ":" + "cs_df29e989e345ebc80f861fc900f2a6b1761d98db";
        // create Base64 encodet string
        final String basic =
                "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder requestBuilder = chain.request().newBuilder();
                requestBuilder.header("Content-Type", "application/json");
                requestBuilder.header("Authorization", basic);
                return chain.proceed(requestBuilder.build());
            }
        });
        client.readTimeout(60, TimeUnit.SECONDS);
        client.writeTimeout(60, TimeUnit.SECONDS);
        client.connectTimeout(60, TimeUnit.SECONDS);
        client.addInterceptor(interceptor);
//        client.addInterceptor(new Interceptor() {
//            @Override
//            public okhttp3.Response intercept(Chain chain) throws IOException {
//                Request request = chain.request();
//
//                return chain.proceed(request);
//            }
//        });

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        return retrofit;
    }
}

