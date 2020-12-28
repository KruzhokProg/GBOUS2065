package com.example.gbous2065.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gbous2065.INewsListener;
import com.example.gbous2065.Models.News;
import com.example.gbous2065.R;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {

    List<News> data;
    Context context;
    INewsListener callback;

    public NewsAdapter(Context context, INewsListener callback) {
        this.context = context;
        this.callback = callback;
    }

    public void setData(List<News> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_projects_news_list_item, parent, false);
        return new NewsViewHolder(view, callback);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsViewHolder h = holder;

        h.title.setText(HtmlCompat.fromHtml(data.get(position).getName(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        if(data.get(position).getAnons() != null) {
            h.anounce.setText(HtmlCompat.fromHtml(data.get(position).getAnons(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
        else{
            h.anounce.setText("");
        }

        String[] dateMas = data.get(position).getPublished_date().split("T");
        h.publishedDate.setText(dateMas[0]);

//        h.cardView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));
    }

    @Override
    public int getItemCount() {
        if(data!=null) {
            return data.size();
        }
        return 0;
    }
}
