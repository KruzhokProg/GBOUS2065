package com.example.gbous2065;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.example.gbous2065.Adapters.AdminDocAdapter;
import com.example.gbous2065.Models.AdminDocHistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class AdminAccountFragment extends Fragment {

    RecyclerView rvUserStatistics;
    List<AdminDocHistory> data;
    EditText etAdminSearch;
    Switch switchMode;
    ProgressBar pbLoad;

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
        etAdminSearch = view.findViewById(R.id.et_admin_search);
        rvUserStatistics = view.findViewById(R.id.rv_admin_data);
        switchMode = view.findViewById(R.id.switchMode);
        pbLoad = view.findViewById(R.id.progress_bar_admin);

        //data.sort(AdminDocHistory.compareByDate);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");

        for (AdminDocHistory item : data) {
            try {
                item.setDateReal(sdf.parse(item.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(data, (o1, o2) -> o2.getId() - o1.getId());

        AdminDocAdapter adapter = new AdminDocAdapter(data);
        rvUserStatistics.setAdapter(adapter);

        switchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    etAdminSearch.setHint("Поиск по сотрудникам");
                }
                else{
                    etAdminSearch.setHint("Поиск по приказам");
                }
            }
        });

        etAdminSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // по приказам
                if (!switchMode.isChecked()){
                    List<AdminDocHistory> filtered = new ArrayList<>();
                    for (AdminDocHistory item: data) {
                        if(item.getTitle().toLowerCase().startsWith(s.toString().toLowerCase())){
                            filtered.add(item);
                        }
                    }

                    pbLoad.setVisibility(View.INVISIBLE);
                    AdminDocAdapter adapter = new AdminDocAdapter(filtered);
                    rvUserStatistics.setAdapter(adapter);
                }else{
                    // по сотрудникам
                    List<AdminDocHistory> filtered = new ArrayList<>();
                    for (AdminDocHistory item: data) {
                        if(item.getReceivers() != null) {
                            String[] receivers = item.getReceivers().split(", ");
                            for (String receiver : receivers) {
                                if (receiver.equals(s.toString())) {
                                    filtered.add(item);
                                }
                            }
                        }
                    }
                    pbLoad.setVisibility(View.INVISIBLE);
                    AdminDocAdapter adapter = new AdminDocAdapter(filtered);
                    rvUserStatistics.setAdapter(adapter);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }
}