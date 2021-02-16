package com.example.gbous2065.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gbous2065.Models.Contact;
import com.example.gbous2065.Models.News;
import com.example.gbous2065.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{

    List<Contact> data;
    boolean isExpanded;
    IContactMap callback;

    public ContactAdapter(List<Contact> data, IContactMap callback) {
        this.data = data;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = data.get(position);
        holder.tvContactPosition.setText(contact.getPosition());
        holder.tvContactName.setText(contact.getName());
        holder.tvContactEmail.setText(contact.getEmail());
        holder.tvContactPhone.setText(contact.getPhone());
        holder.tvContactAdress.setText(contact.getAdress());

        isExpanded = data.get(position).isExpanded();
        if(isExpanded == true) {
            holder.expandableLayout.setVisibility(View.VISIBLE);
            holder.imgvArrowContact.setImageResource(R.drawable.ic_arrow_up);
        }
        else{
            holder.expandableLayout.setVisibility(View.GONE);
            holder.imgvArrowContact.setImageResource(R.drawable.ic_arrow_down);
        }

        //holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        if(data!=null) {
            return data.size();
        }
        return 0;
    }


    public class ContactViewHolder extends RecyclerView.ViewHolder {

        public TextView tvContactPosition;
        public TextView tvContactName;
        public TextView tvContactEmail;
        public TextView tvContactPhone;
        public TextView tvContactAdress;
        public ImageView imgvArrowContact;
        public ConstraintLayout expandableLayout;
        public ConstraintLayout contactItemConstraintLayout;
        public ImageView imgvMap;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactPosition = itemView.findViewById(R.id.tvContactPosition);
            tvContactEmail = itemView.findViewById(R.id.tvContactEmail);
            tvContactPhone = itemView.findViewById(R.id.tvContactPhone);
            tvContactAdress = itemView.findViewById(R.id.tvContactAdress);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            imgvArrowContact = itemView.findViewById(R.id.imgvArrowContactExpand);
            contactItemConstraintLayout = itemView.findViewById(R.id.contactItemConstraintLayout);
            imgvMap = itemView.findViewById(R.id.imgvContactMap);

            imgvMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.contactMapClick(getAdapterPosition());
                }
            });

            contactItemConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contact contact = data.get(getAdapterPosition());
                    contact.setExpanded(!contact.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
