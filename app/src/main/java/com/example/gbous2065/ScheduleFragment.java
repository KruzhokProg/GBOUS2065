package com.example.gbous2065;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.example.gbous2065.Models.Weekdays;
import com.example.gbous2065.Network.ApiService;
import com.example.gbous2065.Network.ApiYandexClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.gbous2065.Network.ApiYandexClient.ACCESS_TOKEN;
import static com.yandex.runtime.Runtime.getApplicationContext;

public class ScheduleFragment extends Fragment {

    AsyncHttpClient asyncHttpClient;
    NiceSpinner spCorpus, spGrade, spLetter, spWeekday;
    RecyclerView rvSchedule;
    Button btnShowSchedule;
    List<Schedule> schedules;
    ScheduleAdapter adapter;
    List<LessonInfo> out;
    ProgressBar pb;
    DataBaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_schedule_fragment, container, false);

        pb = view.findViewById(R.id.progress_bar_schedule);
        spCorpus = view.findViewById(R.id.spCorpus);
        spGrade = view.findViewById(R.id.spGrade);
        spLetter = view.findViewById(R.id.spLetter);
        spWeekday = view.findViewById(R.id.spWeekday);
        rvSchedule = view.findViewById(R.id.rvSchedule);
        btnShowSchedule = view.findViewById(R.id.btnShowSchedule);
        adapter = new ScheduleAdapter(getContext());
        out = new ArrayList<>();
        adapter.setLessons(out);
        rvSchedule.setAdapter(adapter);


        db = new DataBaseHelper(getContext());
        if(db.isScheduleCached())
        {
            // выгрузка из бд
            fillCorpusesSpinner();
            fillWeekdaysSpinner();
            List<Integer> grades = new ArrayList<Integer>(db.getGrades());
            spGrade.attachDataSource(grades);
            List<String> existedLetters = new ArrayList<>(db.getLetters(grades.get(0)));
            spLetter.attachDataSource(existedLetters);
            // обработка выбора класса
            spGrade.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
                @Override
                public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                    Integer selectedGrade = (Integer) parent.getItemAtPosition(position);
                    List<String> searchedLetters = new ArrayList<>(db.getLetters(selectedGrade));
                    spLetter.attachDataSource(searchedLetters);
                }
            });


            ShowCacheSchedule();
        }
        else {
            ApiService apiService = ApiYandexClient.getClient().create(ApiService.class);

            Call<AllFiles> call = apiService.getAllFiles("OAuth " + ACCESS_TOKEN, 1000);
            call.enqueue(new Callback<AllFiles>() {
                @Override
                public void onResponse(Call<AllFiles> call, Response<AllFiles> response) {
                    AllFiles data = response.body();
                    List<com.example.gbous2065.Models.File> files = data.getItems();

                    String url = "";
                    for (com.example.gbous2065.Models.File file : files) {
                        String dir = file.getPath().split("/")[1];
                        if (dir.equals("Расписание")) {
                            url = file.getFile_url();
                            break;
                        }
                    }

                    asyncHttpClient = new AsyncHttpClient();
                    asyncHttpClient.get(url, new FileAsyncHttpResponseHandler(getContext()) {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                            Toast.makeText(getContext(), "Error in Downloading Excel File", Toast.LENGTH_SHORT).show();
                            Log.d("MainActivity", throwable.getMessage());
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, File file) {
                            if (file != null) {
                                try {

                                    schedules = new ArrayList<>();
                                    Boolean isSchedulePassed, isMonday, isTuesday, isWednesday, isThursday, isFriday;
                                    Boolean isLessonNum; // начало строки с уроками
                                    Integer colNum; // количество классов в параллели
                                    Integer curNum; // текущий класс в параллели
                                    Integer c1 = 0, c2 = 0;
                                    String[] lessonRoom;
                                    String lesson = "", room = "";

                                    FileInputStream fis = new FileInputStream(file);
                                    XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
                                    Integer numOfSheets = myWorkBook.getNumberOfSheets();
                                    for (int sheetNum = 0; sheetNum < numOfSheets; sheetNum++) {

                                        isSchedulePassed = false;
                                        isMonday = false;
                                        isTuesday = false;
                                        isWednesday = false;
                                        isThursday = false;
                                        isFriday = false;

                                        XSSFSheet mySheet = myWorkBook.getSheetAt(sheetNum);
                                        Iterator<Row> rowIterator = mySheet.iterator();
                                        String content;
                                        colNum = 0; // количество классов в параллели

                                        Integer emptyLessonNum = 0; // количество пропусков в уроках
                                        while (rowIterator.hasNext()) {

                                            isLessonNum = false;
                                            curNum = c1;
                                            emptyLessonNum = 0;
                                            if (colNum != 0) {
                                                isSchedulePassed = false;
                                            }

                                            Row row = rowIterator.next();
                                            Iterator<Cell> cellIterator = row.cellIterator();
                                            while (cellIterator.hasNext()) {
                                                Cell cell = cellIterator.next();
                                                switch (cell.getCellType()) {
                                                    case Cell.CELL_TYPE_STRING:
                                                        content = cell.getStringCellValue();
                                                        content = content.trim().replace("-", "");
                                                        lessonRoom = content.split(" ");
                                                        lessonRoom = Arrays.stream(lessonRoom).filter(x -> !x.isEmpty()).toArray(String[]::new);

                                                        if (lessonRoom.length == 0) {
                                                            lesson = "----------------------";
                                                            room = "";
                                                        } else if (content.contains("физическая культура")) {
                                                            lesson = "физическая культура";
                                                            room = "спортзал";
                                                        } else if (lessonRoom.length > 1) {
//                                                lesson = lessonRoom[0];
//                                                room = lessonRoom[1];
                                                            lesson = "";
                                                            room = "";
                                                            for (int i = 0; i < lessonRoom.length - 1; i++) {
                                                                lesson += lessonRoom[i] + " ";
                                                            }
                                                            room = lessonRoom[lessonRoom.length - 1];
                                                        }

                                                        if (content.contains("Расписание")) {
                                                            isSchedulePassed = true;
                                                        } else if (isSchedulePassed == true) {
                                                            // строка с перечнем классов в параллели
                                                            colNum++;
                                                            Schedule schedule = new Schedule();
                                                            String[] gradeLetter = content.split("");
                                                            String grade, letter;
                                                            if (gradeLetter.length == 2) {
                                                                grade = gradeLetter[0];
                                                                letter = gradeLetter[1];
                                                            } else {
                                                                grade = gradeLetter[0] + gradeLetter[1];
                                                                letter = gradeLetter[2];
                                                            }
                                                            schedule.setGrade(grade);
                                                            schedule.setLetter(letter);
                                                            schedules.add(schedule);
                                                        } else {
                                                            if (content.equals("Пятница") && isFriday == false) {
                                                                isFriday = false;
                                                                for (int i = c1; i < c2; i++) {
                                                                    Weekdays fridayLessons = new Weekdays();
                                                                    fridayLessons.setWeekday("Пятница");
                                                                    schedules.get(i).getWeekdays().add(fridayLessons);
                                                                }
                                                                isFriday = true;
                                                            } else if (isFriday == true) {
                                                                LessonInfo lessonInfo = new LessonInfo(lesson, room);
                                                                schedules.get(curNum).getWeekdays().get(4).getLessons().add(lessonInfo);
                                                                curNum++;
                                                            } else if (content.equals("Четверг") && isThursday == false) {
                                                                isThursday = false;
                                                                for (int i = c1; i < c2; i++) {
                                                                    Weekdays thursdayLessons = new Weekdays();
                                                                    thursdayLessons.setWeekday("Четверг");
                                                                    schedules.get(i).getWeekdays().add(thursdayLessons);
                                                                }
                                                                isThursday = true;
                                                            } else if (isThursday == true) {
                                                                LessonInfo lessonInfo = new LessonInfo(lesson, room);
                                                                schedules.get(curNum).getWeekdays().get(3).getLessons().add(lessonInfo);
                                                                curNum++;
                                                            } else if (content.equals("Среда") && isWednesday == false) {
                                                                isWednesday = false;
                                                                for (int i = c1; i < c2; i++) {
                                                                    Weekdays wednsdayLessons = new Weekdays();
                                                                    wednsdayLessons.setWeekday("Среда");
                                                                    schedules.get(i).getWeekdays().add(wednsdayLessons);
                                                                }
                                                                isWednesday = true;
                                                            } else if (isWednesday == true) {
                                                                LessonInfo lessonInfo = new LessonInfo(lesson, room);
                                                                schedules.get(curNum).getWeekdays().get(2).getLessons().add(lessonInfo);
                                                                curNum++;
                                                            } else if (content.equals("Вторник") && isTuesday == false) {
                                                                isTuesday = false;
                                                                for (int i = c1; i < c2; i++) {
                                                                    Weekdays tuesdayLessons = new Weekdays();
                                                                    tuesdayLessons.setWeekday("Вторник");
                                                                    schedules.get(i).getWeekdays().add(tuesdayLessons);
                                                                }
                                                                isTuesday = true;
                                                            } else if (isTuesday == true) {
                                                                LessonInfo lessonInfo = new LessonInfo(lesson, room);
                                                                schedules.get(curNum).getWeekdays().get(1).getLessons().add(lessonInfo);
                                                                curNum++;
                                                            } else if (content.equals("Понедельник") && isMonday == false) {
                                                                c2 = c1 + colNum;
                                                                for (int i = c1; i < c2; i++) {
                                                                    Weekdays mondayLessons = new Weekdays();
                                                                    mondayLessons.setWeekday("Понедельник");
                                                                    schedules.get(i).getWeekdays().add(mondayLessons);
                                                                }
                                                                isMonday = true;

                                                            } else if (isMonday == true) {
                                                                LessonInfo lessonInfo = new LessonInfo(lesson, room);
                                                                schedules.get(curNum).getWeekdays().get(0).getLessons().add(lessonInfo);
                                                                curNum++;
                                                            }


                                                        }

                                                        break;
                                                    case Cell.CELL_TYPE_NUMERIC:
                                                        content = String.valueOf(cell.getNumericCellValue());
                                                        isLessonNum = true;
                                                        break;
                                                    case Cell.CELL_TYPE_BLANK:
                                                        if (isLessonNum == true) {
                                                            //emptyLessonNum++;
                                                            //curNum = emptyLessonNum;
                                                            curNum++;
                                                        }
                                                }
                                            }
                                        }

                                        c1 += colNum;
                                    }

                                    fillCorpusesSpinner();
                                    fillWeekdaysSpinner();

                                    List<String> grades = new ArrayList<>();

                                    List<String> letters = new ArrayList<>();
                                    for (Schedule schedule : schedules) {
                                        letters.add(schedule.getLetter());
                                        grades.add(schedule.getGrade());
                                    }
                                    // удаляем дубликаты
                                    HashSet<String> hashSet = new HashSet<String>();
                                    hashSet.addAll(letters);
                                    letters.clear();
                                    letters.addAll(hashSet);

                                    hashSet.clear();
                                    hashSet.addAll(grades);
                                    grades.clear();
                                    grades.addAll(hashSet);
                                    spGrade.attachDataSource(grades);

                                    //подгрузка букв первого класса
                                    List<String> shownLetters = new ArrayList<>();
                                    for (Schedule schedule : schedules) {
                                        if (schedule.getGrade().equals(grades.get(0))) {
                                            shownLetters.add(schedule.getLetter());
                                        }
                                    }
                                    spLetter.attachDataSource(shownLetters);
                                    //Обработка подгрузки только существующих данных
                                    fillLettersSpinner(schedules);

                                    // cache
                                    db.saveWeekdays();
                                    db.saveClassInfo(schedules);
                                    db.saveSubjects(schedules);
                                    db.saveSchedule(schedules);
                                    // ---------------------

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                }

                @Override
                public void onFailure(Call<AllFiles> call, Throwable t) {
                }
            });


            Handler h = new Handler();
            h.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            ShowSchedule();
                        }
                    }, 2500
            );


        }

        btnShowSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.isScheduleCached()) {
                    ShowCacheSchedule();
                }
                else{
                    ShowSchedule();
                }
            }
        });

        return view;
    }


    public void fillLettersSpinner(List<Schedule> schedules){
        spGrade.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                String selectedGrade = (String) parent.getItemAtPosition(position);
                List<String> searchedLetters = new ArrayList<>();
                for (Schedule item : schedules) {
                    if (item.getGrade().equals(selectedGrade)) {
                        searchedLetters.add(item.getLetter());
                    }
                }
                spLetter.attachDataSource(searchedLetters);
            }
        });
    }

    public void fillWeekdaysSpinner(){
        List<String> weekdays = new ArrayList<>();
        weekdays.add("Понедельник");
        weekdays.add("Вторник");
        weekdays.add("Среда");
        weekdays.add("Четверг");
        weekdays.add("Пятница");
        spWeekday.attachDataSource(weekdays);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);

        switch (dayOfTheWeek) {
            case "понедельник":
                spWeekday.setSelectedIndex(0);
                break;
            case "вторник":
                spWeekday.setSelectedIndex(1);
                break;
            case "среда":
                spWeekday.setSelectedIndex(2);
                break;
            case "четверг":
                spWeekday.setSelectedIndex(3);
                break;
            case "пятница":
                spWeekday.setSelectedIndex(4);
                break;
        }
    }

    public void fillCorpusesSpinner() {
        List<String> corpuses = new ArrayList<>();
        corpuses.add("ш1");
        corpuses.add("ш2");
        corpuses.add("ш3");
        corpuses.add("ш4");
        spCorpus.attachDataSource(corpuses);
    }
    public void ShowCacheSchedule(){
        String corpus = spCorpus.getSelectedItem().toString();
        String grade = spGrade.getSelectedItem().toString();
        String letter = spLetter.getSelectedItem().toString();
        String weekday = spWeekday.getSelectedItem().toString();
        out.clear();
        out.addAll(db.getSchedule(grade, letter, weekday));
        pb.setVisibility(View.INVISIBLE);
        adapter.notifyDataSetChanged();
    }

    public void ShowSchedule()
    {
        String corpus = spCorpus.getSelectedItem().toString();
        String grade = spGrade.getSelectedItem().toString();
        String letter = spLetter.getSelectedItem().toString();
        String weekday = spWeekday.getSelectedItem().toString();
        Boolean isFound=false;
        out.clear();
        for (Schedule item: schedules) {
            for (Weekdays itemWeekday: item.getWeekdays()) {
                if(item.getGrade().equals(grade) && item.getLetter().equals(letter) && itemWeekday.getWeekday().equals(weekday)){
                    out.addAll(itemWeekday.getLessons());
                    isFound = true;
                    break;
                }
            }
            if(isFound == true) break;
        }
        pb.setVisibility(View.INVISIBLE);
        adapter.notifyDataSetChanged();
    }
}
