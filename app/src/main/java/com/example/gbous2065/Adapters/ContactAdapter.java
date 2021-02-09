package com.example.gbous2065.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gbous2065.Models.Contact;
import com.example.gbous2065.Models.News;
import com.example.gbous2065.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    List<Contact> data;

    public ContactAdapter(List<Contact> data) {
        this.data = data;
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

        boolean isExpanded = data.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
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
        public ConstraintLayout expandableLayout;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactPosition = itemView.findViewById(R.id.tvContactPosition);
            tvContactEmail = itemView.findViewById(R.id.tvContactEmail);
            tvContactPhone = itemView.findViewById(R.id.tvContactPhone);
            tvContactAdress = itemView.findViewById(R.id.tvContactAdress);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);

            tvContactPosition.setOnClickListener(new View.OnClickListener() {
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
