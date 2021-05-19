package com.example.gbous2065;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gbous2065.Models.News;
import com.example.gbous2065.Utils.PicassoImageGetter;
import com.example.gbous2065.Utils.YoutubeConfig;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class NewsDetailActivity extends YouTubeBaseActivity {

    private TextView tvTitleDetail, tvContentDetail, tvDate;
    private WebView webViewVideo;
    private YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;

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
        setContentView(R.layout.activity_news_detail);

        tvTitleDetail = findViewById(R.id.tv_title_detail);
        tvContentDetail = findViewById(R.id.tv_content_detail);
        tvDate = findViewById(R.id.tv_date_detail);
        youTubePlayerView = findViewById(R.id.youtubePlayer);

        tvContentDetail.setMovementMethod(new ScrollingMovementMethod());
        getIncomingIntent();
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("projectsNews")){
            News news = getIntent().getParcelableExtra("projectsNews");
            PicassoImageGetter imageGetter = new PicassoImageGetter(tvContentDetail, this);
            Spannable htmlContent, htmlTitle;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                htmlContent = (Spannable) Html.fromHtml(news.getContent(), Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
                htmlTitle = (Spannable) Html.fromHtml(news.getName(), Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
            } else {
                htmlContent = (Spannable) Html.fromHtml(news.getContent(), imageGetter, null);
                htmlTitle = (Spannable) Html.fromHtml(news.getName(), imageGetter, null);
            }

            tvTitleDetail.setText(htmlTitle);
            tvContentDetail.setText(htmlContent);
            String[] dateMas =  news.getPublished_date().replace(".", "T").split("T");
            String[] time = dateMas[1].split(":");
            String hours = time[0];
            String mins = time[1];
            //String time = hours + ":" + mins;
            tvDate.setText(dateMas[0] + "\n" + hours + ":" + mins);

            onInitializedListener = new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    Integer startVideoUrlPos = news.getContent().indexOf("www.youtube.com/embed/") + 22;
                    String startVideoUrl = news.getContent().substring(startVideoUrlPos);
                    Integer endVideoUrlPos = startVideoUrl.indexOf('"');
                    String endVideoUrl = startVideoUrl.substring(0, endVideoUrlPos);

                    youTubePlayer.loadVideo(endVideoUrl);
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            };

            if(news.getContent().contains("youtube")) {
                youTubePlayerView.initialize(YoutubeConfig.getApiKey(), onInitializedListener);
            }
            else{
                youTubePlayerView.setVisibility(View.GONE);
            }

        }
    }

    private  Boolean loadState(){
        SharedPreferences sharedPreferences = getSharedPreferences("nightMode", MODE_PRIVATE);
        Boolean state = sharedPreferences.getBoolean("nightMode", false);
        return state;
    }
}