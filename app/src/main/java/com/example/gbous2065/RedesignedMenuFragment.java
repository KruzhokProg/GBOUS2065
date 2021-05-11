package com.example.gbous2065;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.gbous2065.Models.AllFiles;
import com.example.gbous2065.Models.FileDownload;
import com.example.gbous2065.Models.MenuCallback;
import com.example.gbous2065.Network.ApiService;
import com.example.gbous2065.Network.ApiYandexClient;
import com.example.gbous2065.Utils.NetworkDownload;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.slybeaver.slycalendarview.SlyCalendarDialog;

import static com.example.gbous2065.Network.ApiYandexClient.ACCESS_TOKEN;


public class RedesignedMenuFragment extends Fragment {

    ProgressBar progress_bar_menu;
    SubsamplingScaleImageView imgvMenuFile;
    TextView tvMenuDesc;
    FloatingActionButton floatingActionButtonChooseDate, floatingActionButtonDownloadMenu;

    public RedesignedMenuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_redesigned_menu, container, false);

        imgvMenuFile = view.findViewById(R.id.imgvMenuFile);
        tvMenuDesc = view.findViewById(R.id.tvMenuDesc);
        floatingActionButtonChooseDate = view.findViewById(R.id.btn_choose_date_for_menu);
        floatingActionButtonDownloadMenu = view.findViewById(R.id.btn_download_menu);

        floatingActionButtonChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate();
            }
        });
//        progress_bar_menu = view.findViewById(R.id.progress_bar_menu);
        NetworkDownload.getMenuAndGo(getContext(), "", new MenuCallback() {
            @Override
            public void onSuccess(ImageSource menuPreview, String menuDesc, String downloadLink) {
                imgvMenuFile.setImage(menuPreview);
                tvMenuDesc.setText(menuDesc);
                DownloadFileTest(downloadLink, menuDesc);
            }
        });

        return view;
    }

    public void chooseDate() {
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Выберите дату")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        datePicker.show(getFragmentManager(), "tag");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            Instant instant = Instant.ofEpochMilli((Long)selection);
            LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            String selectedDate = formatter.format(date);
            NetworkDownload.getMenuAndGo(getContext(), selectedDate, new MenuCallback() {
                @Override
                public void onSuccess(ImageSource menuPreview, String menuDesc, String downloadLink) {
                    if(menuPreview == null && menuDesc.isEmpty()){
                        imgvMenuFile.setVisibility(View.INVISIBLE);
                        tvMenuDesc.setText("Нет данных");
                    }
                    else {
                        imgvMenuFile.setVisibility(View.VISIBLE);
                        imgvMenuFile.setImage(menuPreview);
                        tvMenuDesc.setText(menuDesc);
                        DownloadFileTest(downloadLink, menuDesc);
                    }
                }
            });
        });
    }


    public void DownloadFileTest(String downloadLink, String menuDesc){
        floatingActionButtonDownloadMenu.setOnClickListener(v -> {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadLink));
            request.setTitle(menuDesc);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, menuDesc);
            DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            request.setMimeType("application/pdf");
            request.allowScanningByMediaScanner();
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            downloadManager.enqueue(request);
        });
    }

    public void DownloadFile(String downloadLink){

        floatingActionButtonDownloadMenu.setOnClickListener(v -> {

            ApiService apiService = ApiYandexClient.getClient().create(ApiService.class);
            Call<FileDownload> callGetDownloadInfo = apiService.getFileDownloadInfo(ACCESS_TOKEN, downloadLink);
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
                                Toast.makeText(getContext(), "Файл успешно загружен", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getContext(), "Ошибка при загрузке", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });

                }

                private boolean writeResponseBodyToDisk(ResponseBody body) {

                    File futureStudioIconFile = new File( getContext().getExternalFilesDir(null) + File.separator + downloadLink);

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

                            Log.d("donwloading... ", "file download: " + fileSizeDownloaded + " of " + fileSize);

                            Intent pdfViewIntent = new Intent(Intent.ACTION_VIEW);
                            pdfViewIntent.setDataAndType(Uri.fromFile(futureStudioIconFile),"application/pdf");
                            pdfViewIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                            Intent intent = Intent.createChooser(pdfViewIntent, "Open File");
                            getContext().startActivity(intent);
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
                    return false;
                }

                @Override
                public void onFailure(Call<FileDownload> call, Throwable t) {

                }
            });
        });
    }
}