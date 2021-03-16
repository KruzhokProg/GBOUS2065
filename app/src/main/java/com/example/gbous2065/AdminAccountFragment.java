package com.example.gbous2065;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gbous2065.Adapters.AdminDocAdapter;
import com.example.gbous2065.Models.AdminDocHistory;

import java.util.List;

public class AdminAccountFragment extends Fragment {

    RecyclerView rvUserStatistics;
    List<AdminDocHistory> data;

    public AdminAccountFragment(List<AdminDocHistory> data) {
        this.data = data;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_account, container, false);
        rvUserStatistics = view.findViewById(R.id.rv_admin_data);
        AdminDocAdapter adapter = new AdminDocAdapter(data);
        rvUserStatistics.setAdapter(adapter);
        return view;
    }
}