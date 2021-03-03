package com.example.gbous2065.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gbous2065.R;

public class UserDocViewHolder extends RecyclerView.ViewHolder {

    TextView tvName, tvDateEnd, tvExpired;
    ImageView imgvSeeDoc;
    ConstraintLayout layout;

    public UserDocViewHolder(@NonNull View itemView) {
        super(itemView);

        tvName = itemView.findViewById(R.id.tvDocNameRvItem);
        tvDateEnd = itemView.findViewById(R.id.tvEndDateRvItem);
        imgvSeeDoc = itemView.findViewById(R.id.imgvSeeDocRvItem);
        layout = itemView.findViewById(R.id.layoutRvItem);
        tvExpired = itemView.findViewById(R.id.tvExpiredItem);
    }
}
