package com.example.gbous2065;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gbous2065.Adapters.ScheduleAdapter;
import com.example.gbous2065.DataBases.DataBaseHelper;
import com.example.gbous2065.Models.AllFiles;
import com.example.gbous2065.Models.LessonInfo;
import com.example.gbous2065.Models.Schedule;
import com.example.gbous2065.Models.ScheduleByBuilding;
import com.example.gbous2065.Models.ScheduleCallBack;
import com.example.gbous2065.Models.ScheduleFileInfo;
import com.example.gbous2065.Models.Weekdays;
import com.example.gbous2065.Network.ApiService;
import com.example.gbous2065.Network.ApiYandexClient;
import com.example.gbous2065.Utils.NetworkDownload;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.gbous2065.Network.ApiYandexClient.ACCESS_TOKEN;
import static com.yandex.runtime.Runtime.getApplicationContext;

public class ScheduleFragment extends Fragment {

    AsyncHttpClient asyncHttpClient;
//    NiceSpinner spCorpus, spGrade, spLetter, spWeekday;
    AutoCompleteTextView spCorpus, spGrade, spLetter, spWeekday;
    RecyclerView rvSchedule;
    Button btnShowSchedule;
    List<Schedule> schedules;
    List<ScheduleByBuilding> scheduleByBuildingList;
    ScheduleAdapter adapter;
    List<LessonInfo> out;
    ProgressBar pb;
    DataBaseHelper db;
    Switch switchRemember;
    SharedPreferences sharedPreferences;
    ArrayAdapter adapterGrade, adapterLetter, adapterWeekday, adapterBuilding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_schedule_fragment, container, false);

        pb = view.findViewById(R.id.progress_bar_schedule);
        switchRemember = view.findViewById(R.id.switchRemember);
//        spCorpus = view.findViewById(R.id.spCorpus);
//        spGrade = view.findViewById(R.id.spGrade);
//        spLetter = view.findViewById(R.id.spLetter);
//        spWeekday = view.findViewById(R.id.spWeekday);
        spCorpus = view.findViewById(R.id.actvBuildings);
        spGrade = view.findViewById(R.id.actvGrades);
        spLetter = view.findViewById(R.id.actvLetters);
        spWeekday = view.findViewById(R.id.actvWeekdays);
        rvSchedule = view.findViewById(R.id.rvSchedule);
        btnShowSchedule = view.findViewById(R.id.btnShowSchedule);
        adapter = new ScheduleAdapter(getContext());
        out = new ArrayList<>();
        adapter.setLessons(out);
        rvSchedule.setAdapter(adapter);


        db = new DataBaseHelper(getContext());
        sharedPreferences = getContext().getSharedPreferences("remember", Context.MODE_PRIVATE);
        if(!sharedPreferences.getString("building","").isEmpty()) {
            switchRemember.setChecked(false);
            switchRemember.setText("Забыть");
        }
        else {
            switchRemember.setChecked(true);
            switchRemember.setText("Запомнить");
        }

        switchRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
//                    switchRemember.setText("Запомнить");
                    buttonView.setText("Запомнить");

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("building", "");
                    editor.putString("grade", "");
                    editor.putString("letter", "");
                    editor.putString("weekday", "");
                    editor.apply();

                }else{
                    buttonView.setText("Забыть");
                    String building = spCorpus.getEditableText().toString();
                    String grade = spGrade.getEditableText().toString();
                    String letter = spLetter.getEditableText().toString();
                    String weekday = spWeekday.getEditableText().toString();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("building", building);
                    editor.putString("grade", grade);
                    editor.putString("letter", letter);
                    editor.putString("weekday", weekday);
                    editor.apply();
                }
            }
        });

//        switchRemember.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(switchRemember.isChecked()){
//                    switchRemember.setText("Забыть");
//
//                    String building = spCorpus.getEditableText().toString();
//                    String grade = spGrade.getEditableText().toString();
//                    String letter = spLetter.getEditableText().toString();
//                    String weekday = spWeekday.getEditableText().toString();
//
////                    if(building.isEmpty() || grade.isEmpty() || letter.isEmpty() || weekday.isEmpty()){
////                        Toast.makeText(getContext(), "Выберите все пункты!", Toast.LENGTH_SHORT).show();
////                    }
////                    else{
//                    sharedPreferences = getContext().getSharedPreferences("remember", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("building", building);
//                    editor.putString("grade", grade);
//                    editor.putString("letter", letter);
//                    editor.putString("weekday", weekday);
//                    editor.apply();
////                    }
//                }
//                else{
//                    switchRemember.setText("Запомнить");
//
//                    sharedPreferences = getContext().getSharedPreferences("remember", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("building", "");
//                    editor.putString("grade", "");
//                    editor.putString("letter", "");
//                    editor.putString("weekday", "");
//                    editor.apply();
//                }
//            }
//        });
//        if(db.isScheduleCached())
//        {
//            // выгрузка из бд
//            fillCorpusesSpinner();
//            fillWeekdaysSpinner();
//            List<Integer> grades = new ArrayList<Integer>(db.getGrades());
//            spGrade.attachDataSource(grades);
//            List<String> existedLetters = new ArrayList<>(db.getLetters(grades.get(0)));
//            spLetter.attachDataSource(existedLetters);
//            // обработка выбора класса
//            spGrade.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
//                @Override
//                public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
//                    Integer selectedGrade = (Integer) parent.getItemAtPosition(position);
//                    List<String> searchedLetters = new ArrayList<>(db.getLetters(selectedGrade));
//                    spLetter.attachDataSource(searchedLetters);
//                }
//            });
//
//
//            ShowCacheSchedule();
//        }
//        else {

//        ---------------------------

        NetworkDownload.getScheduleAndGo(getContext(), new ScheduleCallBack() {
            @Override
            public void onSuccess(List<ScheduleByBuilding> data) {
                scheduleByBuildingList = data;
                List<String> buildings = new ArrayList<>();
                for (ScheduleByBuilding item: data) {
                    buildings.add(item.getBuilding());
                }

                List<String> weekdays = new ArrayList<>();
                weekdays.add("Понедельник");
                weekdays.add("Вторник");
                weekdays.add("Среда");
                weekdays.add("Четверг");
                weekdays.add("Пятница");

                adapterBuilding = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, buildings);
                spCorpus.setAdapter(adapterBuilding);

                String firstBuilding = (String) adapterBuilding.getItem(0);
                if(!sharedPreferences.getString("building", "").isEmpty()){
                    firstBuilding = sharedPreferences.getString("building", "");
                }

                adapterGrade = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line,getGradesByBuilding(firstBuilding, data));
                spGrade.setAdapter(adapterGrade);

                String firstGrade = (String)adapterGrade.getItem(0);
                if(!sharedPreferences.getString("grade", "").isEmpty()){
                    firstGrade = sharedPreferences.getString("grade", "");
                }

                adapterLetter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, getLettersByGrade(firstGrade, firstBuilding, data));
                spLetter.setAdapter(adapterLetter);

                spCorpus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectedBuilding = (String)parent.getItemAtPosition(position);
                        adapterGrade = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line,getGradesByBuilding(selectedBuilding, data));
                        spGrade.setAdapter(adapterGrade);
                    }
                });

                spGrade.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectedGrade = (String)parent.getItemAtPosition(position);
                        String selectedBuilding = spCorpus.getEditableText().toString();
                        adapterLetter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, getLettersByGrade(selectedGrade, selectedBuilding, data));
                        spLetter.setAdapter(adapterLetter);
                        spLetter.setText((CharSequence) adapterLetter.getItem(0), false);
                    }
                });
                adapterWeekday = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, weekdays);
                spWeekday.setAdapter(adapterWeekday);

                if(!sharedPreferences.getString("building","").isEmpty()){
                    ShowSchedule("remember");
                }

            }
        });


        btnShowSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(db.isScheduleCached()) {
//                    ShowCacheSchedule();
//                }
//                else{
                    ShowSchedule("");
//                }
            }
        });

        return view;
    }

    private List<String> getLettersByGrade(String selectedGrade, String selectedBuilding, List<ScheduleByBuilding> data) {
        HashSet<String> letters = new HashSet<>();
        for (ScheduleByBuilding item: data) {
            if(item.getBuilding().equals(selectedBuilding)){
                for (Schedule schedule: item.getScheduleList()) {
                    if(schedule.getGrade().equals(selectedGrade)){
                        letters.add(schedule.getLetter());
                    }
                }
                break;
            }
        }
        return new ArrayList<>(letters);
    }

    private List<String> getGradesByBuilding(String selectedBuilding, List<ScheduleByBuilding> data) {
        HashSet<String> grades = new HashSet<>();
        for (ScheduleByBuilding item: data) {
            if(item.getBuilding().equals(selectedBuilding)){
                for (Schedule schedule: item.getScheduleList()) {
                    grades.add(schedule.getGrade());
                }
                break;
            }
        }
        return new ArrayList<>(grades);
    }


//    public void fillLettersSpinner(List<Schedule> schedules){
//        spGrade.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
//            @Override
//            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
//                String selectedGrade = (String) parent.getItemAtPosition(position);
//                List<String> searchedLetters = new ArrayList<>();
//                for (Schedule item : schedules) {
//                    if (item.getGrade().equals(selectedGrade)) {
//                        searchedLetters.add(item.getLetter());
//                    }
//                }
//                spLetter.attachDataSource(searchedLetters);
//            }
//        });
//    }

//    public void fillWeekdaysSpinner(){
//        List<String> weekdays = new ArrayList<>();
//        weekdays.add("Понедельник");
//        weekdays.add("Вторник");
//        weekdays.add("Среда");
//        weekdays.add("Четверг");
//        weekdays.add("Пятница");
//        spWeekday.attachDataSource(weekdays);
//        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
//        Date d = new Date();
//        String dayOfTheWeek = sdf.format(d);
//
//        switch (dayOfTheWeek) {
//            case "понедельник":
//                spWeekday.setSelectedIndex(0);
//                break;
//            case "вторник":
//                spWeekday.setSelectedIndex(1);
//                break;
//            case "среда":
//                spWeekday.setSelectedIndex(2);
//                break;
//            case "четверг":
//                spWeekday.setSelectedIndex(3);
//                break;
//            case "пятница":
//                spWeekday.setSelectedIndex(4);
//                break;
//        }
//    }

//    public void fillBuildingsSpinner(List<ScheduleByBuilding> data) {
//        List<String> corpuses = new ArrayList<>();
//        for (ScheduleByBuilding item: data) {
//            corpuses.add(item.getBuilding());
//        }
//        corpuses.add("ш1");
//        corpuses.add("ш2");
//        corpuses.add("ш3");
//        corpuses.add("ш4");
//        spCorpus.attachDataSource(corpuses);
//    }

//    public void ShowCacheSchedule(){
//        String corpus = spCorpus.getSelectedItem().toString();
//        String grade = spGrade.getSelectedItem().toString();
//        String letter = spLetter.getSelectedItem().toString();
//        String weekday = spWeekday.getSelectedItem().toString();
//        out.clear();
//        out.addAll(db.getSchedule(grade, letter, weekday));
//        pb.setVisibility(View.INVISIBLE);
//        adapter.notifyDataSetChanged();
//    }

    public void ShowSchedule(String mode)
    {
        String building, grade, letter, weekday;
        if(mode.equals("remember")){
            sharedPreferences = getContext().getSharedPreferences("remember", Context.MODE_PRIVATE);
            building = sharedPreferences.getString("building", "");
            grade = sharedPreferences.getString("grade", "");
            letter = sharedPreferences.getString("letter", "");
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale("ru"));
            Date d = new Date();
            weekday = sdf.format(d);

            int savedBuildingPos = getAdapterPosition(adapterBuilding, building);
            int savedGradePos = getAdapterPosition(adapterGrade, grade);
            int savedLetterPos = getAdapterPosition(adapterLetter, letter);
            int savedWeekdayPos = getAdapterPosition(adapterWeekday, weekday.substring(0,1).toUpperCase() + weekday.substring(1));

            spCorpus.setText((CharSequence) adapterBuilding.getItem(savedBuildingPos), false);
            spGrade.setText((CharSequence) adapterGrade.getItem(savedGradePos), false);
            spLetter.setText((CharSequence) adapterLetter.getItem(savedLetterPos), false);
            spWeekday.setText((CharSequence) adapterWeekday.getItem(savedWeekdayPos), false);
        }
        else {
            building = spCorpus.getEditableText().toString();
            grade = spGrade.getEditableText().toString();
            letter = spLetter.getEditableText().toString();
            weekday = spWeekday.getEditableText().toString();
        }
        Boolean isFound=false;
        out.clear();
        pb.setVisibility(View.VISIBLE);
        for (ScheduleByBuilding item: scheduleByBuildingList) {
            if(item.getBuilding().equals(building)){
                for (Schedule schedule: item.getScheduleList()) {
                    for (Weekdays itemWeekday: schedule.getWeekdays()) {
                        if(schedule.getGrade().equals(grade) && schedule.getLetter().equals(letter) && itemWeekday.getWeekday().toLowerCase().equals(weekday.toLowerCase())){
                            out.addAll(itemWeekday.getLessons());
                            isFound = true;
                            break;
                        }
                    }
                    if(isFound == true) break;
                }
            }
        }
        pb.setVisibility(View.INVISIBLE);
        adapter.notifyDataSetChanged();
    }

    public Integer getAdapterPosition(ArrayAdapter adapter, String comparement){
        int savedPos = 0;
        for (int i=0; i<adapter.getCount(); i++) {
            if(((CharSequence) adapter.getItem(i)).equals(comparement)){
                savedPos = i;
                break;
            }
        }
        return savedPos;
    }
}
