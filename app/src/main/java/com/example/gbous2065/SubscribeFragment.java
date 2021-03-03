package com.example.gbous2065;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gbous2065.Adapters.UserDocAdapter;
import com.example.gbous2065.Models.UserDocFragment;
import com.example.gbous2065.Models.UserDocHistory;

import java.util.List;

public class SubscribeFragment extends Fragment {

    List<UserDocFragment> subDocs;
    RecyclerView rv;
    UserDocAdapter adapter;

    public SubscribeFragment() {

    }

    public SubscribeFragment(List<UserDocFragment> subDocs) {
        this.subDocs = subDocs;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_subscribe, container, false);
        rv = view.findViewById(R.id.rvSubDocs);
        UserDocFragment newUserDoc = new UserDocFragment();
        newUserDoc.setTitle("Приказ на подпись");
        newUserDoc.setDateEnd("10-03-2021");
        newUserDoc.setStatus("Подписание документа");
        subDocs.add(newUserDoc);
        adapter = new UserDocAdapter(getContext(), subDocs);
        rv.setAdapter(adapter);
        return view;
    }
}