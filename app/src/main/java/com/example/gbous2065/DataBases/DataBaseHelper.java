package com.example.gbous2065.DataBases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.gbous2065.Models.LessonInfo;
import com.example.gbous2065.Models.Schedule;
import com.example.gbous2065.Models.Weekdays;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    static final String dbName = "ScheduleDB";

    // таблица Weekdays
    static final String weekdaysTable = "Weekdays";
    static final String colWeekdayName = "name";

    // таблица ClassInfo
    static final String classInfoTable = "ClassInfo";
    static final String colClassInfoId = "id";
    static final String colClassInfoGrade = "grade";
    static final String colClassInfoLetter = "letter";

    // таблица Subject
    static final String subjectTable = "Subject";
    static final String colSubjectName = "name";

    // таблица Schedule
    static final String scheduleTable = "Schedule";
    static final String colScheduleId = "id";
    static final String colScheduleWeekday = "weekday";
    static final String colScheduleClassInfoId = "classInfoId";
    static final String colScheduleSubject = "subject";
    static final String colScheduleRoom = "room";

    public DataBaseHelper(Context context) {
        super(context, dbName, null, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + weekdaysTable + " (" +
                colWeekdayName + " TEXT PRIMARY KEY);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + classInfoTable + " (" +
                colClassInfoId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                colClassInfoGrade + " INTEGER NOT NULL, " +
                colClassInfoLetter + " TEXT NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + subjectTable + " (" +
                colSubjectName + " TEXT PRIMARY KEY);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + scheduleTable + " (" +
                colScheduleId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                colScheduleClassInfoId + " INTEGER NOT NULL, " +
                colScheduleSubject + " TEXT NOT NULL," +
                colScheduleWeekday +" TEXT NOT NULL, " +
                colScheduleRoom + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + colScheduleWeekday + ") REFERENCES " + weekdaysTable + "(" + colWeekdayName + "), " +
                "FOREIGN KEY (" + colScheduleSubject + ") REFERENCES " + subjectTable + "(" + colSubjectName + "), " +
                "FOREIGN KEY (" + colScheduleClassInfoId + ") REFERENCES " + classInfoTable + "(" + colClassInfoId + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+scheduleTable);
        db.execSQL("DROP TABLE IF EXISTS "+weekdaysTable);
        db.execSQL("DROP TABLE IF EXISTS "+classInfoTable);
        db.execSQL("DROP TABLE IF EXISTS "+subjectTable);
        onCreate(db);
    }

    public Boolean isScheduleCached(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT COUNT(*) as 'count' FROM " + scheduleTable;
        Cursor c = db.rawQuery(selectQuery, null);
        if(c!=null)
            c.moveToFirst();
        Integer count = c.getInt(c.getColumnIndex("count"));
        return (count != 0 ? true : false);
    }

    public HashSet<Integer> getGrades(){
        HashSet<Integer> grades = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + colClassInfoGrade + " FROM " + classInfoTable;
        Cursor c = db.rawQuery(selectQuery, null);
        if(c!=null) {
            c.moveToFirst();
            do {
                grades.add(c.getInt(c.getColumnIndex(colClassInfoGrade)));
            }while(c.moveToNext());
        }
        return  grades;
    }

    public HashSet<String> getLetters(Integer grade){
        HashSet<String> letters = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + colClassInfoLetter + " FROM " + classInfoTable + " WHERE " + colClassInfoGrade + "=?";
        Cursor c = db.rawQuery(selectQuery, new String[]{grade.toString()});
        if(c!=null){
            c.moveToFirst();
            do{
                letters.add(c.getString(c.getColumnIndex(colClassInfoLetter)));
            }while(c.moveToNext());
        }
        return letters;
    }

    public List<LessonInfo> getSchedule(String grade, String letter, String weekday){
        List<LessonInfo> lessonInfoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + colScheduleSubject + "," + colScheduleRoom + " FROM " + scheduleTable +
                " JOIN " + classInfoTable + " ON " + colScheduleClassInfoId + " = " + classInfoTable + "." + colClassInfoId +
                " WHERE " + colClassInfoGrade + "=? AND " + colClassInfoLetter + "=? AND " + colScheduleWeekday + "=?";
        Cursor c = db.rawQuery(selectQuery, new String[]{grade, letter, weekday});
        if (c!=null){
            c.moveToFirst();
            do{
                LessonInfo lessonInfo = new LessonInfo();
                lessonInfo.setName(c.getString(c.getColumnIndex(colScheduleSubject)));
                lessonInfo.setRoom(c.getString(c.getColumnIndex(colScheduleRoom)));
                lessonInfoList.add(lessonInfo);
            }while(c.moveToNext());
        }

        return lessonInfoList;
    }

    public void saveWeekdays(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] weekdays_mas = new String[]{"Понедельник","Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
        String selectQuery = "SELECT COUNT(*) as 'count' FROM " + weekdaysTable;
        Cursor c = db.rawQuery(selectQuery, null);
        if(c!=null){
            c.moveToFirst();
            Integer count = c.getInt(c.getColumnIndex("count"));
            if(count == 0) {
                db = this.getWritableDatabase();
                for (String weekday: weekdays_mas) {
                    ContentValues cv = new ContentValues();
                    cv.put(colWeekdayName, weekday);
                    db.insert(weekdaysTable, null, cv);
                }
            }
        }
    }


    public void saveSubjects(List<Schedule> scheduleList){

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT COUNT(*) as 'count' FROM " + subjectTable;
        Cursor c = db.rawQuery(selectQuery, null);
        HashSet<String> uniqueLessons = new HashSet<>();;

        if(c != null) {
            c.moveToFirst();
            Integer count = c.getInt(c.getColumnIndex("count"));
            if(count == 0) {
            db = this.getWritableDatabase();
            for (Schedule item : scheduleList) {
                for (Weekdays weekday : item.getWeekdays()) {
                    for (LessonInfo lessonInfo : weekday.getLessons()) {
                        uniqueLessons.add(lessonInfo.getName());
                    }
                }
            }
            }

            for (String lesson : uniqueLessons) {
                ContentValues cv = new ContentValues();
                cv.put(colSubjectName, lesson);
                db.insert(subjectTable, null, cv);
            }
        }
    }

    public void saveClassInfo(List<Schedule> scheduleList){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT COUNT(*) as 'count' FROM " + classInfoTable;
        Cursor c = db.rawQuery(selectQuery, null);

        if(c != null) {
            c.moveToFirst();
            Integer count = c.getInt(c.getColumnIndex("count"));
            if(count == 0) {
                db = this.getWritableDatabase();
                for (Schedule item : scheduleList) {
                    ContentValues cv = new ContentValues();
                    cv.put(colClassInfoGrade, item.getGrade());
                    cv.put(colClassInfoLetter, item.getLetter());
                    db.insert(classInfoTable, null, cv);
                }
            }
        }
    }

    public void saveSchedule(List<Schedule> scheduleList){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT COUNT(*) as 'count' FROM " + scheduleTable;
        Cursor c = db.rawQuery(selectQuery, null);

        if(c != null) {
            c.moveToFirst();
            Integer count = c.getInt(c.getColumnIndex("count"));
            c.close();
            if (count == 0) {
                for (Schedule item : scheduleList) {
                    for (Weekdays weekday : item.getWeekdays()) {
                        for (LessonInfo lessonInfo : weekday.getLessons()) {

                            // вытаскиваем ClassInfoId из таблицы ClassInfo
                            String query = "SELECT * FROM " + classInfoTable +
                                    " WHERE " + colClassInfoGrade + " = " + item.getGrade() +" AND "
                                    + colClassInfoLetter + " = '" + item.getLetter() + "'";
                            Cursor cursor = db.rawQuery(query, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                Integer classInfoId = cursor.getInt(cursor.getColumnIndex(colClassInfoId));

                                ContentValues cv = new ContentValues();
                                cv.put(colScheduleWeekday, weekday.getWeekday());
                                cv.put(colScheduleClassInfoId, classInfoId);
                                cv.put(colScheduleSubject, lessonInfo.getName());
                                cv.put(colScheduleRoom, lessonInfo.getRoom());
                                db = this.getWritableDatabase();
                                db.insert(scheduleTable, null, cv);
                            }
                        }
                    }
                }
            }
        }
    }
}
