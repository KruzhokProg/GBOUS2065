package com.example.gbous2065.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Script;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.gbous2065.AdminAccountFragment;
import com.example.gbous2065.Models.AdminAccount;
import com.example.gbous2065.Models.AdminDocHistory;
import com.example.gbous2065.Models.AllFiles;
import com.example.gbous2065.Models.CustomCallback;
import com.example.gbous2065.Models.LessonInfo;
import com.example.gbous2065.Models.MenuCallback;
import com.example.gbous2065.Models.Schedule;
import com.example.gbous2065.Models.ScheduleByBuilding;
import com.example.gbous2065.Models.ScheduleCallBack;
import com.example.gbous2065.Models.ScheduleFileInfo;
import com.example.gbous2065.Models.SubUnsubCombine;
import com.example.gbous2065.Models.UserAccount;
import com.example.gbous2065.Models.UserDoc;
import com.example.gbous2065.Models.UserDocFragment;
import com.example.gbous2065.Models.UserDocHistory;
import com.example.gbous2065.Models.Weekdays;
import com.example.gbous2065.Network.ApiService;
import com.example.gbous2065.Network.ApiUserAccountClient;
import com.example.gbous2065.Network.ApiYandexClient;
import com.example.gbous2065.R;
import com.example.gbous2065.RedesignedMenuFragment;
import com.example.gbous2065.UserAccountFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import cz.msebera.android.httpclient.Header;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.gbous2065.Network.ApiYandexClient.ACCESS_TOKEN;

public class NetworkDownload {

    public static void getMenuAndGo(Context context, String selectedDate, MenuCallback menuCallback){

//        List<com.example.gbous2065.Models.File> menuFiles = new ArrayList<>();
        ApiService apiService = ApiYandexClient.getClient().create(ApiService.class);
        Call<AllFiles> call = apiService.getAllFiles("OAuth " + ACCESS_TOKEN, 1000);
        call.enqueue(new Callback<AllFiles>() {
            @Override
            public void onResponse(Call<AllFiles> call, Response<AllFiles> response) {
                AllFiles data = response.body();
                List<com.example.gbous2065.Models.File> files = data.getItems();
                com.example.gbous2065.Models.File lastFile = null;
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date lastDate = new Date(), currentDate = null;
                lastDate.setTime(0);

                for (com.example.gbous2065.Models.File file: files) {
                    String dir = file.getPath().split("/")[1];
                    if(dir.equals("Меню")){

                        Integer startPos = file.getName().indexOf(".");
                        Integer endPos = file.getName().lastIndexOf(".");
                        String data_file = file.getName().substring(startPos-2,endPos);
                        data_file = data_file.replace(".", "-");
                        try {
                            currentDate = sdf.parse(data_file);

                            if(!selectedDate.equals("")){
                                if(selectedDate.equals(data_file)){
                                    lastFile = file;
                                    break;
                                }
                            }
                            else if(currentDate.compareTo(lastDate) > 0){
                                lastDate = currentDate;
                                lastFile = file;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                            // публикация неопубликованных файлов
                            if(file.getPublic_url() == null){
                                String path = file.getPath().split("disk:/")[1];
                                Call<ResponseBody> callPublish = apiService.publishFile("OAuth " + ACCESS_TOKEN, path);
                                callPublish.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        //Toast.makeText(getContext(), "файл " + path + " опубликован", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });
                            }

                    }
                }

                if(lastFile != null) {
                    String preview = lastFile.getPreview();
                    String fileDesc = lastFile.getName();
                    preview = preview.replace("size=S", "size=XXXL");

                    Call<ResponseBody> callPreview = apiService.getPreview("OAuth " + ACCESS_TOKEN, preview);
                    callPreview.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                                    ImageSource imageSource = ImageSource.bitmap(bmp);

                                    menuCallback.onSuccess(imageSource, fileDesc);
//                                    file.setBmp(bmp);
//                                    rendered_data.add(file);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("error", t.getMessage());
                        }
                    });
                }
                else{
//                    Toast.makeText(context, "Нет данных!", Toast.LENGTH_SHORT).show();
                    menuCallback.onSuccess(null, "");
                }

            }

            @Override
            public void onFailure(Call<AllFiles> call, Throwable t) {

            }
        });

    }

    public static void getScheduleAndGo(Context context, ScheduleCallBack scheduleCallBack){

        ApiService apiService = ApiYandexClient.getClient().create(ApiService.class);

        Call<AllFiles> call = apiService.getAllFiles("OAuth " + ACCESS_TOKEN, 1000);
        call.enqueue(new Callback<AllFiles>() {
            @Override
            public void onResponse(Call<AllFiles> call, Response<AllFiles> response) {
                AllFiles data = response.body();
                List<com.example.gbous2065.Models.File> files = data.getItems();
                List<ScheduleFileInfo> scheduleFiles = new ArrayList<>();
                AsyncHttpClient asyncHttpClient;

                String url = "";
                for (com.example.gbous2065.Models.File file : files) {
                    String dir = file.getPath().split("/")[1];
                    if (dir.equals("ТестРасписание")) {
                        url = file.getFile_url();
                        String file_name = file.getName();
                        String building = file_name.split(" ")[file_name.split(" ").length-1];
                        building = building.substring(0,building.indexOf("."));
                        if(building.contains("ш")) {
                            ScheduleFileInfo scheduleFileInfo = new ScheduleFileInfo(building, url);
                            scheduleFiles.add(scheduleFileInfo);
                        }
                    }
                }

                List<ScheduleByBuilding> scheduleByBuildingList = new ArrayList<>();
                List<String> buildings = new ArrayList<>();

                final int[] filesCount = {0};
                for (ScheduleFileInfo info: scheduleFiles) {

                    buildings.add(info.getBuilding());
                    String file_url = info.getUrl();
                    asyncHttpClient = new AsyncHttpClient();
                    asyncHttpClient.get(file_url, new FileAsyncHttpResponseHandler(context) {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                            Toast.makeText(context, "Error in Downloading Excel File", Toast.LENGTH_SHORT).show();
                            Log.d("MainActivity", throwable.getMessage());
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, File file) {

                            List<Schedule> schedules = new ArrayList<>();
                            List<String> grades = new ArrayList<>();
                            List<String> letters = new ArrayList<>();

                            if (file != null) {
                                try {
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
                                                            }
                                                            else if( gradeLetter.length == 4){
                                                                grade = gradeLetter[0] + gradeLetter[1] + gradeLetter[2];
                                                                letter = gradeLetter[3];
                                                            }
                                                            else {
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

                                    //подгрузка букв первого класса
//                                    List<String> shownLetters = new ArrayList<>();
//                                    for (Schedule schedule : schedules) {
//                                        if (schedule.getGrade().equals(grades.get(0))) {
//                                            shownLetters.add(schedule.getLetter());
//                                        }
//                                    }

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            ScheduleByBuilding scheduleByBuilding = new ScheduleByBuilding(info.getBuilding(), schedules);
                            scheduleByBuildingList.add(scheduleByBuilding);
                            filesCount[0]++;
                            if(filesCount[0] == scheduleFiles.size()){
                                scheduleCallBack.onSuccess(scheduleByBuildingList);
                            }
//                                ShowSchedule();
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<AllFiles> call, Throwable t) {
            }
        });

    }

    public static void getDataAndGo(Context context, FragmentManager fragmentManager, NavigationView navigationView, String mode,
                                    CustomCallback customCallback, String... userInfo){
        String loginEncrypted="", passwordEncrypted="";
        String login = null, pass = null;
        SharedPreferences sharedPreferences;
        Boolean isAdmin = false;

        if(!mode.equals("cache") && (userInfo[0].isEmpty() || userInfo[1].isEmpty())){
            Toast.makeText(context, "Заполните все поля!", Toast.LENGTH_SHORT).show();
        }
        else {
            if (userInfo.length > 0) {
                login = userInfo[0];
                pass = userInfo[1];

                String savedLogin = Crypto.getSHA256(login);
                String savedPass = Crypto.getSHA256(pass);

                if (savedLogin.equals(context.getResources().getString(R.string.secret))
                        && savedPass.equals(context.getResources().getString(R.string.secret))) {
                    isAdmin = true;
                    sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("admin", isAdmin);
                    editor.apply();
                }
            } else {
                sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                loginEncrypted = sharedPreferences.getString("login", "");
                passwordEncrypted = sharedPreferences.getString("pass", "");
                isAdmin = sharedPreferences.getBoolean("admin", false);

                if (loginEncrypted.equals(context.getResources().getString(R.string.secret))
                        && passwordEncrypted.equals(context.getResources().getString(R.string.secret))) {
                    isAdmin = true;
                }

                login = Crypto.base64Decode(loginEncrypted);
                pass = Crypto.base64Decode(passwordEncrypted);


            }

            if (isAdmin) {

                ApiService apiService = ApiUserAccountClient.getClient().create(ApiService.class);
                Call<AdminAccount> call = apiService.getUserStat();
                call.enqueue(new Callback<AdminAccount>() {
                    @Override
                    public void onResponse(Call<AdminAccount> call, Response<AdminAccount> response) {
                        AdminAccount statistics = response.body();
                        if (statistics != null) {
                            List<AdminDocHistory> adminDocHistories = new ArrayList<>();
                            for (Map<String, AdminDocHistory> item : statistics.getDocs()) {
                                Set<String> keys = item.keySet();
                                for (String s : keys) {
                                    AdminDocHistory adminDocHistory = item.get(s);
                                    adminDocHistories.add(adminDocHistory);
                                }
                            }

                            if (mode.equals("login")) {
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, new AdminAccountFragment(adminDocHistories,"data")).commit();
                            } else if (mode.equals("cache")) {
                                customCallback.onAdminSuccess(adminDocHistories,"data");
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, new AdminAccountFragment(adminDocHistories,"data")).commit();
                            }

                            String fullName = "Администратор";
                            navigationView.getMenu().clear();
                            navigationView.inflateMenu(R.menu.menu_account);
                            navigationView.getMenu().findItem(R.id.nav_employee).setTitle(fullName);
                        }
                        else{ // если нет данных
                            if (mode.equals("login")) {
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, new AdminAccountFragment(null, "empty")).commit();
                            } else if (mode.equals("cache")) {
                                customCallback.onAdminSuccess(null, "empty");
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, new AdminAccountFragment(null, "empty")).commit();
                            }

                            String fullName = "Администратор";
                            navigationView.getMenu().clear();
                            navigationView.inflateMenu(R.menu.menu_account);
                            navigationView.getMenu().findItem(R.id.nav_employee).setTitle(fullName);
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminAccount> call, Throwable t) {
                        Toast.makeText(context, "Неверные данные", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                ApiService apiService = ApiUserAccountClient.getClient().create(ApiService.class);
                Call<UserAccount> call = apiService.getUserInfo(login, pass);
                String finalLogin = login;
                String finalPass = pass;
                call.enqueue(new Callback<UserAccount>() {
                    @Override
                    public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                        SharedPreferences sharedPreferences;
                        UserAccount user = response.body();
                        if (user.getError() != null) {
                            Toast.makeText(context, "Доступ отказан", Toast.LENGTH_SHORT).show();
                        } else if (user.getId() == null) {
                            Toast.makeText(context, "Некорректные данные", Toast.LENGTH_SHORT).show();
                        } else {

                            if (userInfo.length > 0) {
                                // Запись UserId и Токена в FRD
                                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                Query query = rootRef.child("Tokens").equalTo(user.getId());
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()) {
                                            DatabaseReference reference = FirebaseDatabase.getInstance()
                                                    .getReference("Tokens").child(user.getId());

                                            FirebaseMessaging.getInstance().getToken()
                                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<String> task) {
                                                            String token = task.getResult();

                                                            HashMap<String, String> hashMap = new HashMap<>();
                                                            hashMap.put("token", token);

                                                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    //Toast.makeText(context, "token: " + token, Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                String loginEncrypted = Crypto.base64Encode(finalLogin);
                                String passwordEncrypted = Crypto.base64Encode(finalPass);
                                sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("login", loginEncrypted);
                                editor.putString("pass", passwordEncrypted);
                                editor.apply();
                            }

                            if(user.getDocs() != null) {
                                List<UserDoc> userDocList = new ArrayList<>();
                                List<UserDocHistory> userDocHistories = new ArrayList<>();
                                for (Map<String, UserDoc> item : user.getDocs()) {
                                    Set<String> keys = item.keySet();
                                    for (String s : keys) {
                                        UserDoc userDoc = item.get(s);
                                        userDocList.add(userDoc);
                                    }
                                }

                                for (Map<String, UserDocHistory> item : user.getDocsHistory()) {
                                    Set<String> keys = item.keySet();
                                    for (String s : keys) {
                                        UserDocHistory userDocHistory = item.get(s);
                                        userDocHistories.add(userDocHistory);
                                    }
                                }
                                // Распределяем подписанные и неподписанные документы
                                List<UserDocFragment> subscribedDocs = new ArrayList<>();
                                List<UserDocFragment> unsubscribedDocs = new ArrayList<>();
                                for (UserDoc userDoc : userDocList) {
                                    String latest = "1900-01-01 09:00:00";
                                    String status = "";
                                    Integer docId = -1;
                                    String title = "";
                                    String dateEnd = "";
                                    String file = "", subLink = "", unsubLink = "";
                                    for (UserDocHistory userDocHistory : userDocHistories) {
                                        if (userDocHistory.getId() == userDoc.getId()) {
                                            if (DateTimeDifference.startLaterThanEnd(userDocHistory.getDate(), latest, "login")) {
                                                latest = userDocHistory.getDate();
                                                status = userDocHistory.getTitle();
                                                docId = userDocHistory.getId();
                                                title = userDoc.getTitle();
                                                dateEnd = userDoc.getEndDate();
                                                file = userDoc.getFile();
                                                subLink = userDoc.getSubLink();
                                                unsubLink = userDoc.getUnsubLink();
                                            }
                                        }
                                    }

                                    UserDocFragment userDocFragment;
                                    if (status.equals("Подписание документа") && docId != -1) {
                                        userDocFragment = getUserDocFragment(docId, title, dateEnd, status, latest, file, subLink, unsubLink);
                                        subscribedDocs.add(userDocFragment);
                                    } else if (docId != -1) {
                                        userDocFragment = getUserDocFragment(docId, title, dateEnd, status, latest, file, subLink, unsubLink);
                                        unsubscribedDocs.add(userDocFragment);
                                    }
                                }


                                if (mode.equals("login")) {
                                    fragmentManager.beginTransaction().replace(R.id.fragment_container, new UserAccountFragment(subscribedDocs, unsubscribedDocs)).commit();
                                } else if (mode.equals("cache")) {
                                    String fullName = user.getSurname() + " " + user.getName() + " " + user.getPatronymic();
                                    SubUnsubCombine subUnsubCombine = new SubUnsubCombine(fullName, subscribedDocs, unsubscribedDocs);
                                    customCallback.onSuccess(subUnsubCombine, "data");
                                }

                            }
                            else{
                                if (mode.equals("login")) {
                                    fragmentManager.beginTransaction().replace(R.id.fragment_container, new UserAccountFragment(null, null)).commit();
                                } else if (mode.equals("cache")) {
                                    String fullName = user.getSurname() + " " + user.getName() + " " + user.getPatronymic();
                                    SubUnsubCombine subUnsubCombine = new SubUnsubCombine(fullName, null, null);
                                    customCallback.onSuccess(subUnsubCombine, "empty");
                                }
                            }

                            String fullName = user.getSurname() + " " + user.getName().substring(0, 1) + ". " + user.getPatronymic().substring(0, 1) + ".";
                            navigationView.getMenu().clear();
                            navigationView.inflateMenu(R.menu.menu_account);
                            navigationView.getMenu().findItem(R.id.nav_employee).setTitle(fullName);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserAccount> call, Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private static UserDocFragment getUserDocFragment(Integer docId, String title, String dateEnd, String status,
                                              String latest, String fileUrl, String subLink, String unsubLink){
        UserDocFragment userDocFragment = new UserDocFragment();
        userDocFragment.setId(docId);
        userDocFragment.setTitle(title);
        userDocFragment.setDateEnd(dateEnd);
        userDocFragment.setStatus(status);
        userDocFragment.setDateStatus(latest);
        userDocFragment.setFileUrl(fileUrl);
        userDocFragment.setSubLink(subLink);
        userDocFragment.setUnsubLink(unsubLink);
        return userDocFragment;
    }
}
