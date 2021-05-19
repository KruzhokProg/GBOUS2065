package com.example.gbous2065;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gbous2065.Adapters.UserDocAdapter;
import com.example.gbous2065.Models.UserDocFragment;

import java.util.List;


public class UnsubscribeFragment extends Fragment {

    List<UserDocFragment> unsubDocs;
    RecyclerView rv;
    UserDocAdapter adapter;

    public UnsubscribeFragment(List<UserDocFragment> unsubDocs) {
        this.unsubDocs = unsubDocs;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unsubscribe, container, false);
        rv = view.findViewById(R.id.rvUnsubDocs);
//        UserDocFragment newUserDoc = new UserDocFragment();
//        newUserDoc.setTitle("Новый приказ на подпись");
//        newUserDoc.setDateEnd("15-03-2021");
//        newUserDoc.setStatus("Открытие докумета");
//        unsubDocs.add(newUserDoc);
        adapter = new UserDocAdapter(getContext(), unsubDocs, "unsub");
        rv.setAdapter(adapter);
        return view;
    }
}