package com.example.gbous2065.Network;

import com.example.gbous2065.Models.FcmResponse;
import com.example.gbous2065.Models.NotificationSender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiGoogleService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAuHgaD1Q:APA91bE5txQmH0Kt2SbBibPQhsY06oTvH4AxRTv009irk94SQJUGL4IYiw80r004eRoWajhcrAtLKdJvt4g0JshoruTpNrb_ei9F-_sM4yQEFjDH9Zw6-jOupIRXTG650g4FXrag5TV2"
    })
    @POST("fcm/send")
    Call<FcmResponse> sendNotification(@Body NotificationSender body);
}
