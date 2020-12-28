package com.example.gbous2065.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gbous2065.Models.LessonInfo;
import com.example.gbous2065.R;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleViewHolder> {

    private List<LessonInfo> lessons;
    private Context context;

    public ScheduleAdapter(Context context) {
        this.context = context;
    }

    public void setLessons(List<LessonInfo> lessons) {
        this.lessons = lessons;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_schedule_item, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        holder.tv_num_lesson.setText( String.valueOf(position+1) + ".");
        holder.tv_name_lesson.setText(lessons.get(position).getName());
        holder.tv_room_lesson.setText(lessons.get(position).getRoom());
    }

    @Override
    public int getItemCount() {
        if(lessons!=null) {
            return lessons.size();
        }
        return 0;
    }
}
