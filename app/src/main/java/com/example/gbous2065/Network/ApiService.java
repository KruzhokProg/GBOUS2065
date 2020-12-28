package com.example.gbous2065.Network;

import com.example.gbous2065.Models.AllFiles;
import com.example.gbous2065.Models.FileDownload;
import com.example.gbous2065.Models.News;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {
    // news
    @GET("data/getNewsFeeds/40.json")
    Call<List<News>> getNews();

    // menu
    @GET("/v1/disk/resources/files")
    Call<AllFiles> getAllFiles(
            @Header("Authorization") String accessToken,
            @Query("limit") Integer limit
    );

    @PUT("/v1/disk/resources/publish")
    Call<ResponseBody> publishFile(
            @Header("Authorization") String accessToken,
            @Query("path") String path
    );

    @GET("/v1/disk/resources/download")
    Call<FileDownload> getFileDownloadInfo(
            @Header("Authorization") String accessToken,
            @Query("path") String path
    );

    @GET
    Call<ResponseBody> downloadFile(
            @Header("Authorization") String accessToken,
            @Url String download_url
    );

    @GET
    Call<ResponseBody> getPreview(
            @Header("Authorization") String accessToken,
            @Url String preview_url
    );
}
