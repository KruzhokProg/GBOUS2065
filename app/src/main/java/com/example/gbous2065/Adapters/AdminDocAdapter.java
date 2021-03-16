package com.example.gbous2065.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gbous2065.Models.AdminDocHistory;
import com.example.gbous2065.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class AdminDocAdapter  extends RecyclerView.Adapter<AdminDocAdapter.AdminDocViewHolder>{

    List<AdminDocHistory> data;
    Context context;
    boolean isExpanded;

    public AdminDocAdapter(List<AdminDocHistory> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public AdminDocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_admin_account, parent, false);
        context = parent.getContext();
        return new AdminDocViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminDocViewHolder holder, int position) {
        AdminDocHistory stat = data.get(position);
        holder.tvTitle.setText(stat.getTitle());
        holder.tvDate.setText(stat.getDate());
        holder.tvDocSubject.setText(stat.getSubject());
        holder.tvDocType.setText(stat.getType());

        isExpanded = data.get(position).isExpanded();
        if(isExpanded == true) {
            holder.expandableLayoutDocAdmin.setVisibility(View.VISIBLE);
            holder.imgvExpandArrow.setImageResource(R.drawable.ic_arrow_up_black);


            if (stat.getFunctionality() != null) {
                String[] funcMas = stat.getFunctionality().split(", ");
                holder.chipGroupDocFunc.removeAllViews();

                for (String item : funcMas) {
                    Chip chip = new Chip(context);
                    chip.setText(item);
                    chip.setChipBackgroundColorResource(R.color.chipBackGround);
                    chip.setTextColor(context.getResources().getColor(R.color.white));
                    holder.chipGroupDocFunc.addView(chip);
                }
            }


            if(stat.getPlaces() != null){
                String[] placesMas = stat.getPlaces().split(", ");
                holder.chipGroupDocPlaces.removeAllViews();

                for (String item : placesMas) {
                    Chip chip = new Chip(context);
                    chip.setText(item);
                    chip.setChipBackgroundColorResource(R.color.chipBackGround);
                    chip.setTextColor(context.getResources().getColor(R.color.white));
                    holder.chipGroupDocPlaces.addView(chip);
                }
            }


            if(stat.getReceivers() != null){
                String[] receiversMas = stat.getReceivers().split(", ");
                holder.chipGroupDocUsers.removeAllViews();

                for (String item : receiversMas) {
                    Chip chip = new Chip(context);
                    chip.setText(item);
                    chip.setChipBackgroundColorResource(R.color.chipBackGround);
                    chip.setTextColor(context.getResources().getColor(R.color.white));
                    holder.chipGroupDocUsers.addView(chip);
                }
            }
        }
        else{

            holder.expandableLayoutDocAdmin.setVisibility(View.GONE);
            holder.imgvExpandArrow.setImageResource(R.drawable.ic_arrow_down_black);
        }

    }


    @Override
    public int getItemCount() {
        if(data!=null) {
            return data.size();
        }
        return 0;
    }

    public class AdminDocViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle, tvDate, tvDocType, tvDocSubject, tvDocFunc;
        ChipGroup chipGroupDocFunc, chipGroupDocPlaces, chipGroupDocUsers;
        ImageView imgvExpandArrow, imgvPieChartStat;
        ConstraintLayout expandableLayoutDocAdmin, adminDocItemConstraintLayout;


        public AdminDocViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitleAdmin);
            tvDate = itemView.findViewById(R.id.tvDateAdmin);
            tvDocType = itemView.findViewById(R.id.tvAdminDocType);
            tvDocSubject = itemView.findViewById(R.id.tvAdminDocSubject);
            tvDocFunc = itemView.findViewById(R.id.tvAdminDocType);
            chipGroupDocFunc = itemView.findViewById(R.id.chipGroupDocFunc);
            chipGroupDocPlaces = itemView.findViewById(R.id.chipGroupDocPlaces);
            chipGroupDocUsers = itemView.findViewById(R.id.chipGroupDocUsers);
            imgvExpandArrow = itemView.findViewById(R.id.imgvArrowAdminDocExpand);
            imgvPieChartStat = itemView.findViewById(R.id.imgvPieChart);
            expandableLayoutDocAdmin = itemView.findViewById(R.id.expandableLayoutDocAdmin);
            adminDocItemConstraintLayout = itemView.findViewById(R.id.adminDocItemConstraintLayout);

            adminDocItemConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdminDocHistory contact = data.get(getAdapterPosition());
                    contact.setExpanded(!contact.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
