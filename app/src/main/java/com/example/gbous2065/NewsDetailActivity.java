package com.example.gbous2065;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gbous2065.Models.News;

public class NewsDetailActivity extends AppCompatActivity {

    private TextView tvTitleDetail, tvContentDetail, tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(loadState() == true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.DarkTheme_GBOUS2065);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.Theme_GBOUS2065);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        tvTitleDetail = findViewById(R.id.tv_title_detail);
        tvContentDetail = findViewById(R.id.tv_content_detail);
        tvDate = findViewById(R.id.tv_date_detail);

        tvContentDetail.setMovementMethod(new ScrollingMovementMethod());
        getIncomingIntent();
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("projectsNews")){
            News news = getIntent().getParcelableExtra("projectsNews");

            tvTitleDetail.setText( Html.fromHtml(news.getName()));
            tvContentDetail.setText(Html.fromHtml(news.getContent()));
            String[] dateMas =  news.getPublished_date().replace(".", "T").split("T");
            String[] time = dateMas[1].split(":");
            String hours = time[0];
            String mins = time[1];
            //String time = hours + ":" + mins;
            tvDate.setText(dateMas[0] + "\n" + hours + ":" + mins);

        }
    }

    private  Boolean loadState(){
        SharedPreferences sharedPreferences = getSharedPreferences("nightMode", MODE_PRIVATE);
        Boolean state = sharedPreferences.getBoolean("nightMode", false);
        return state;
    }
}