package com.example.gbous2065.Models;


import android.service.media.MediaBrowserService;

public interface CustomCallback {

    void onSuccess(SubUnsubCombine value);
    void onFailure();
}