package com.example.gbous2065.Network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiYandexClient {
    private static final String BASE_URL = "https://cloud-api.yandex.net";
    public static final String ACCESS_TOKEN = "AgAEA7qixWtmAAa-I0Etuxl6wEwBs0mLa2vI0vk";

    private static Retrofit retrofit = null;

    public static Retrofit getClient(){
        if(retrofit == null){
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
