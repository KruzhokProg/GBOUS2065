package com.example.gbous2065.Models;


import android.service.media.MediaBrowserService;

import java.util.List;

public interface CustomCallback {

    void onSuccess(SubUnsubCombine value);
    void onAdminSuccess(List<AdminDocHistory> value);
    void onFailure();
}