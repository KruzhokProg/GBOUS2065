package com.example.gbous2065.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gbous2065.IMenuListener;
import com.example.gbous2065.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tv_menu;
    public ImageView imgv_preview;
    IMenuListener menuListener;

    public MenuViewHolder(@NonNull View itemView, IMenuListener menuListener) {
        super(itemView);

        this.menuListener = menuListener;
        tv_menu = itemView.findViewById(R.id.tv_menu);
        imgv_preview = itemView.findViewById(R.id.imgv_preview);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        menuListener.onMenuClickListener(getAdapterPosition());
    }
}
