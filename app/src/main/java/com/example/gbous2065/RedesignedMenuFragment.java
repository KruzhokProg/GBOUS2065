package com.example.gbous2065;

import android.Manifest;
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
import android.view.MotionEvent;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

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

    ProgressBar progress_bar_menu_loading;
    SubsamplingScaleImageView imgvMenuFile;
    TextView tvMenuDesc;
    FloatingActionButton floatingActionButtonChooseDate, floatingActionButtonDownloadMenu;
    ImageView imgvZoomMenuPreview;

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
        imgvZoomMenuPreview = view.findViewById(R.id.zoomMenuPreview);
        progress_bar_menu_loading = view.findViewById(R.id.progress_bar_menu_loading);

        floatingActionButtonChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate();
            }
        });

        imgvZoomMenuPreview.setOnTouchListener((v, event) -> {
            v.setVisibility(View.INVISIBLE);
            return true;
        });

        NetworkDownload.getMenuAndGo(getContext(), "", new MenuCallback() {
            @Override
            public void onSuccess(ImageSource menuPreview, String menuDesc, String downloadLink) {
                imgvMenuFile.setImage(menuPreview);
                tvMenuDesc.setText(menuDesc);
                progress_bar_menu_loading.setVisibility(View.INVISIBLE);
                DownloadFile(downloadLink, menuDesc);
            }
        });

        return view;
    }

    public void chooseDate() {
        imgvMenuFile.setVisibility(View.VISIBLE);
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
                        progress_bar_menu_loading.setVisibility(View.INVISIBLE);
                        imgvMenuFile.setImage(menuPreview);
                        tvMenuDesc.setText(menuDesc);
                        imgvMenuFile.setVisibility(View.VISIBLE);
                        DownloadFile(downloadLink, menuDesc);
                    }
                }
            });
        });
    }


    public void DownloadFile(String downloadLink, String menuDesc){
        floatingActionButtonDownloadMenu.setOnClickListener(v -> {

            Dexter.withContext(getContext())
                    .withPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if(multiplePermissionsReport.areAllPermissionsGranted()) {
                                Toast.makeText(getContext(), "Идет загрузка ...", Toast.LENGTH_SHORT).show();
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadLink));
                                request.setTitle(menuDesc);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    request.allowScanningByMediaScanner();
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                }
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, menuDesc); //
                                DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                                request.setMimeType("application/pdf");
                                request.allowScanningByMediaScanner();
                                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                downloadManager.enqueue(request);
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }


                    }).withErrorListener(new PermissionRequestErrorListener() {
                @Override
                public void onError(DexterError dexterError) {
                    Toast.makeText(getContext(), dexterError.toString(), Toast.LENGTH_SHORT).show();
                }
            }).check();

        });


    }

}