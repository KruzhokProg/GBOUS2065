package com.example.gbous2065;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.gbous2065.Models.FileDownload;
import com.example.gbous2065.Network.ApiYandexClient;
import com.example.gbous2065.Network.ApiService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.gbous2065.Network.ApiYandexClient.ACCESS_TOKEN;

public class MenuDetail extends AppCompatActivity {

    private static final String TAG = "file";
    SubsamplingScaleImageView imgvMenuDetail;
    ImageView zoomLogo;
    Button btnDownload;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        if(loadState() == true){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            setTheme(R.style.DarkTheme_GBOUS2065);
//        }
//        else{
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//            setTheme(R.style.Theme_GBOUS2065);
//        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        imgvMenuDetail = findViewById(R.id.imgvMenuDetail);
        zoomLogo = findViewById(R.id.zoomLogo);
        btnDownload= findViewById(R.id.btnDownload);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = getIntent().getExtras().getString("path");
                Call<FileDownload> callGetDownloadInfo = apiService.getFileDownloadInfo(ACCESS_TOKEN, path);
                callGetDownloadInfo.enqueue(new Callback<FileDownload>() {
                    @Override
                    public void onResponse(Call<FileDownload> call, Response<FileDownload> response) {
                        FileDownload data = response.body();
                        String href = data.getHref();
                        Call<ResponseBody> callDownload = apiService.downloadFile(ACCESS_TOKEN, href);
                        callDownload.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                boolean writtenToDisk = writeResponseBodyToDisk(response.body());

                                if(writtenToDisk){
                                    Toast.makeText(MenuDetail.this, "Файл успешно загружен", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(MenuDetail.this, "Ошибка при загрузке", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });

                    }

                    private boolean writeResponseBodyToDisk(ResponseBody body) {

                        File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + path);

                        InputStream inputStream = null;
                        OutputStream outputStream = null;


                        try {
                            byte[] fileReader = new byte[4096];

                            long fileSize = body.contentLength();
                            long fileSizeDownloaded = 0;
                            inputStream = body.byteStream();
                            outputStream = new FileOutputStream(futureStudioIconFile);

                            while (true) {
                                int read = inputStream.read(fileReader);

                                if (read == -1) {
                                    break;
                                }

                                outputStream.write(fileReader, 0, read);

                                fileSizeDownloaded += read;

                                Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                            }

                            outputStream.flush();
                            return true;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        return true;
                    }

                    @Override
                    public void onFailure(Call<FileDownload> call, Throwable t) {

                    }
                });
            }
        });

        zoomLogo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                zoomLogo.setVisibility(View.INVISIBLE);
                return true;
            }
        });

        String preview = getIntent().getExtras().getString("preview");
        preview = preview.replace("size=L", "size=XXXL");


        apiService = ApiYandexClient.getClient().create(ApiService.class);
        Call<ResponseBody> callPreview = apiService.getPreview("OAuth " + ACCESS_TOKEN, preview);
        callPreview.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                        imgvMenuDetail.setImage(ImageSource.bitmap(bmp));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("error", t.getMessage());
            }
        });
    }

    private  Boolean loadState(){
        SharedPreferences sharedPreferences = getSharedPreferences("nightMode", MODE_PRIVATE);
        Boolean state = sharedPreferences.getBoolean("nightMode", false);
        return state;
    }
}