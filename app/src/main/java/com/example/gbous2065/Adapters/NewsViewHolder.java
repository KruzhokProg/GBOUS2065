package com.example.gbous2065.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gbous2065.INewsListener;
import com.example.gbous2065.R;

public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

//    public ImageView img;
    public TextView anounce, publishedDate, title;
    public CardView cardView;
    INewsListener callback;

    public NewsViewHolder(@NonNull View itemView, INewsListener callback) {
        super(itemView);
        this.callback = callback;
        //img = itemView.findViewById(R.id.img_projects_news);
        anounce = itemView.findViewById(R.id.anounce_projects_news);
        publishedDate = itemView.findViewById(R.id.published_date_projects_news);
        title = itemView.findViewById(R.id.title_projects_news);
        cardView = itemView.findViewById(R.id.projectsNewsCardView);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        callback.onNewsClickListener(getAdapterPosition(), publishedDate, title);
    }
}
