package com.example.gbous2065;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gbous2065.Adapters.NewsAdapter;
import com.example.gbous2065.Models.News;
import com.example.gbous2065.Network.ApiNewsClient;
import com.example.gbous2065.Network.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.slybeaver.slycalendarview.SlyCalendarDialog;

import static android.app.Activity.RESULT_OK;

public class NewsFragment extends Fragment implements INewsListener, View.OnClickListener {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1234;
    EditText etSearch;
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ImageButton mic_btn;
    FloatingActionButton chooseDateBtn;
    NavigationView navigationView;
    List<News> data;
    List<News> filtered_data;
    List<News> searchList;
    NewsAdapter adapter;
    public static final String date1_pref = "date1";
    public static final String date2_pref = "date2";
    String date1s="", date2s="";
    SharedPreferences mSettings;
    String searchInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_news_fragment, container, false);

        SharedPreferences settings = getContext().getSharedPreferences(date1_pref, Context.MODE_PRIVATE);
        settings.edit().clear().commit();
        SharedPreferences settings2 = getContext().getSharedPreferences(date2_pref, Context.MODE_PRIVATE);
        settings2.edit().clear().commit();

//        nestedScrollView = view.findViewById(R.id.scroll_view);
        recyclerView = view.findViewById(R.id.rv_news);
        progressBar = view.findViewById(R.id.progress_bar);
        etSearch = view.findViewById(R.id.et_search);
        mic_btn = view.findViewById(R.id.img_btn_record);
        chooseDateBtn = view.findViewById(R.id.btn_choose_date);
        data = new ArrayList<>();
        filtered_data = new ArrayList<>();
        searchList = new ArrayList<>();
        mSettings = getContext().getSharedPreferences(date1_pref, Context.MODE_PRIVATE);

        mic_btn.setOnClickListener(this);
        chooseDateBtn.setOnClickListener(this);

        adapter = new NewsAdapter(getContext(),this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        getData();
//        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
//                    page++;
//                    progressBar.setVisibility(View.VISIBLE);
//                    getData(page);
//                }
//            }
//        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchInput = charSequence.toString();
                data.clear();
                filtered_data.clear();
                searchList.clear();
                getData();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    public void chooseDate() {
        SlyCalendarDialog.Callback callback = new SlyCalendarDialog.Callback(){

            @Override
            public void onCancelled() {
                date1s = "";
                date2s = "";
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(date1_pref, "");
                editor.putString(date2_pref, "");
                editor.apply();
                filtered_data.clear();
                data.clear();
                getData();
            }

            @Override
            public void onDataSelected(Calendar firstDate, Calendar secondDate, int hours, int minutes) {
                String firstMonth = String.valueOf(firstDate.get(Calendar.MONTH) + 1);
                String secondMonth = String.valueOf(secondDate.get(Calendar.MONTH) + 1);
                date1s = firstDate.get(Calendar.YEAR) + "-" + firstMonth + "-" + firstDate.get(Calendar.DAY_OF_MONTH);
                date2s = secondDate.get(Calendar.YEAR) + "-" + secondMonth + "-" + secondDate.get(Calendar.DAY_OF_MONTH);

                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(date1_pref, date1s);
                editor.putString(date2_pref, date2s);
                editor.apply();
                filtered_data.clear();
                data.clear();
                getData();
            }
        };
        new SlyCalendarDialog()
                .setSingle(false)
                .setCallback(callback)
                .show(getFragmentManager(), "TAG_SLYCALENDAR");
    }

    public void getData(){
        if(mSettings.contains(date1_pref)) {
            date1s = mSettings.getString(date1_pref, "");
            date2s = mSettings.getString(date2_pref, "");
        }
        ApiService apiService = ApiNewsClient.getClient().create(ApiService.class);
        Call<List<News>> call = apiService.getNews();
        call.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {

                List<News> body = response.body();

                // yet to do
                if(response.isSuccessful()) {
                    if(date1s.equals("") && date2s.equals("")) {
                        filtered_data.clear();
                        data.addAll(body);

                        if(searchInput != null && !searchInput.equals("")){
                            List<News> output = GetDataFilterBySearchInput(data);
                            if(!output.isEmpty()) {
                                searchList.addAll(output);
                                adapter.setData(searchList);
                            }
//                            else{
//                                Toast.makeText(getContext(), "Список закончился!", Toast.LENGTH_SHORT).show();
//                                progressBar.setVisibility(View.GONE);
//                            }
                        }
                        else {
                            adapter.setData(data);
                        }

                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                    else{
                        List<News> output = GetDataFilterByDate(body);
                        if(!output.isEmpty()) {
                            data.clear();
                            filtered_data.addAll(output);
                            date1s = "";
                            date2s = "";
                            adapter.setData(filtered_data);
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
//                        else{
//                            Toast.makeText(getContext(), "Список закончился!", Toast.LENGTH_SHORT).show();
//                            progressBar.setVisibility(View.GONE);
//                        }
                    }

                }
                else{
                    Toast.makeText(getContext(), "Список закончился!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<News> GetDataFilterBySearchInput(List<News> list){
        List<News> filtered_search_list = new ArrayList<>();

        for (News item : list) {
            if(item.getName().toLowerCase().contains(searchInput)){
                filtered_search_list.add(item);
            }
        }
        return filtered_search_list;
    }

    public List<News> GetDataFilterByDate(List<News> list){
        List<News> filtered_list = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dateItem = null, date1 = null, date2 = null;

        try {
            date1 = sdf.parse(date1s);
            date2 = sdf.parse(date2s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (News item : list) {
            String date = item.getPublished_date().split("T")[0];
            try {
                dateItem = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(dateItem.compareTo(date1) > 0 && dateItem.compareTo(date2) < 0){
                filtered_list.add(item);
            }
        }
        return filtered_list;
    }

    public void micSpeach() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }catch (Exception e){
            Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case REQUEST_CODE_SPEECH_INPUT:
                if(resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etSearch.setText(result.get(0));
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_choose_date:
                chooseDate();
                break;
            case R.id.img_btn_record:
                micSpeach();
                break;
        }
    }

    @Override
    public void onNewsClickListener(int position, TextView date, TextView title) {
        Intent intent = new Intent(getContext(), NewsDetailActivity.class);
        intent.putExtra("projectsNews", data.get(position));

        Pair<View, String> p2 = Pair.create((View)date, "dateTN");
        Pair<View, String> p3 = Pair.create((View)title, "titleTN");

        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),p2,p3);

        startActivity(intent, optionsCompat.toBundle());
    }
}
