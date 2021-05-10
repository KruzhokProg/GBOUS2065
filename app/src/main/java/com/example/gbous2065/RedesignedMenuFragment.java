package com.example.gbous2065;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import com.example.gbous2065.Models.File;
import com.example.gbous2065.Models.MenuCallback;
import com.example.gbous2065.Network.ApiService;
import com.example.gbous2065.Network.ApiYandexClient;
import com.example.gbous2065.Utils.NetworkDownload;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    FloatingActionButton floatingActionButton;

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
        floatingActionButton = view.findViewById(R.id.btn_choose_date_for_menu);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate();
            }
        });
//        progress_bar_menu = view.findViewById(R.id.progress_bar_menu);
        NetworkDownload.getMenuAndGo(getContext(), "", new MenuCallback() {
            @Override
            public void onSuccess(ImageSource menuPreview, String menuDesc) {
                imgvMenuFile.setImage(menuPreview);
                tvMenuDesc.setText(menuDesc);
            }
        });

        return view;
    }

    public void chooseDate() {
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Выберите дату")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        datePicker.show(getFragmentManager(), "tag");
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                Instant instant = Instant.ofEpochMilli((Long)selection);
                LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                String selectedDate = formatter.format(date);
                NetworkDownload.getMenuAndGo(getContext(), selectedDate, new MenuCallback() {
                    @Override
                    public void onSuccess(ImageSource menuPreview, String menuDesc) {
                        if(menuPreview == null && menuDesc.isEmpty()){
                            imgvMenuFile.setVisibility(View.INVISIBLE);
                            tvMenuDesc.setText("Нет данных");
                        }
                        else {
                            imgvMenuFile.setVisibility(View.VISIBLE);
                            imgvMenuFile.setImage(menuPreview);
                            tvMenuDesc.setText(menuDesc);
                        }
                    }
                });
            }
        });
    }
}