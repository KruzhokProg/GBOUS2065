package com.example.gbous2065.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gbous2065.DisplayDocActivity;
import com.example.gbous2065.Models.UserDocFragment;
import com.example.gbous2065.R;
import com.example.gbous2065.Utils.DateTimeDifference;

import java.time.LocalDate;
import java.util.List;

public class UserDocAdapter extends RecyclerView.Adapter<UserDocViewHolder> {

    Context context;
    List<UserDocFragment> data;

    public UserDocAdapter(Context context, List<UserDocFragment> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public UserDocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_docs_item, parent, false);
        return new UserDocViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDocViewHolder holder, int position) {
        UserDocFragment userDoc = data.get(position);
        holder.tvName.setText(userDoc.getTitle());
        holder.tvDateEnd.setText(userDoc.getDateEnd());

        LocalDate today = LocalDate.now();
        String now = today.toString();
        if(!userDoc.getStatus().equals("Подписание документа")){
            String[] curDateMas = userDoc.getDateEnd().split("-");
            String curDateEnd = curDateMas[2] + "-" +curDateMas[1] + "-" + curDateMas[0];
            if(DateTimeDifference.startLaterThanEnd(now, curDateEnd, "adapter")){
                holder.tvExpired.setVisibility(View.VISIBLE);
            }
        }

        holder.imgvSeeDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DisplayDocActivity.class);
                i.putExtra("docInfo", userDoc);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(data != null){
            return data.size();
        }
        else{
            return 0;
        }
    }
}
