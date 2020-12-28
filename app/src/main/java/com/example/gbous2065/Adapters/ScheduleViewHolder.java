package com.example.gbous2065.Adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gbous2065.R;

public class ScheduleViewHolder extends RecyclerView.ViewHolder {

    public TextView tv_num_lesson;
    public TextView tv_name_lesson;
    public TextView tv_room_lesson;

    public ScheduleViewHolder(@NonNull View itemView) {
        super(itemView);

        tv_num_lesson = itemView.findViewById(R.id.tvNumLesson);
        tv_name_lesson = itemView.findViewById(R.id.tvLessonName);
        tv_room_lesson = itemView.findViewById(R.id.tvLessonRoom);
    }
}
