package com.example.gbous2065;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gbous2065.Models.UserAccount;
import com.example.gbous2065.Models.UserDoc;
import com.example.gbous2065.Models.UserDocFragment;
import com.example.gbous2065.Models.UserDocHistory;
import com.example.gbous2065.Network.ApiNewsClient;
import com.example.gbous2065.Network.ApiService;
import com.example.gbous2065.Network.ApiUserAccountClient;
import com.example.gbous2065.Utils.DateTimeDifference;
import com.google.android.material.textfield.TextInputEditText;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    Button btnSignIn;
    ApiService apiService = ApiNewsClient.getClient().create(ApiService.class);
    TextInputEditText etEmail, etPass;
    String email, pass;
    String temp="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login_fragment, container, false);

        btnSignIn = view.findViewById(R.id.btnSignIn);
        etEmail = view.findViewById(R.id.etEmail);
        etPass = view.findViewById(R.id.etPass);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email =  etEmail.getText().toString();
                pass =  etPass.getText().toString();
                ApiService apiService = ApiUserAccountClient.getClient().create(ApiService.class);
                Call<UserAccount> call =  apiService.getUserInfo(email, pass);
                call.enqueue(new Callback<UserAccount>() {
                    @Override
                    public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                        UserAccount user = response.body();
                        if(user.getError() != null){
                            Toast.makeText(getContext(), "Доступ отказан", Toast.LENGTH_SHORT).show();
                        }
                        else if(user.getId() == null) {
                            Toast.makeText(getContext(), "Некорректные данные", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            List<UserDoc> userDocList = new ArrayList<>();
                            List<UserDocHistory> userDocHistories = new ArrayList<>();
                            for (Map<String, UserDoc> item : user.getDocs()) {
                                Set<String> keys = item.keySet();
                                for (String s : keys) {
                                    UserDoc userDoc = item.get(s);
                                    userDocList.add(userDoc);
                                }
                            }

                            for (Map<String, UserDocHistory> item : user.getDocsHistory()) {
                                Set<String> keys = item.keySet();
                                for (String s : keys) {
                                    UserDocHistory userDocHistory = item.get(s);
                                    userDocHistories.add(userDocHistory);
                                }
                            }
                            // Распределяем подписанные и неподписанные документы
                            List<UserDocFragment> subscribedDocs = new ArrayList<>();
                            List<UserDocFragment> unsubscribedDocs = new ArrayList<>();
                            for (UserDoc userDoc: userDocList) {
                                String latest = "1900-01-01 09:00:00";
                                String status = "";
                                Integer docId = -1;
                                String title="";
                                String dateEnd="";
                                String file="", subLink="", unsubLink="";
                                for (UserDocHistory userDocHistory: userDocHistories) {
                                    if (userDocHistory.getId() == userDoc.getId()){
                                        if( DateTimeDifference.startLaterThanEnd(userDocHistory.getDate(), latest, "login") ){
                                            latest = userDocHistory.getDate();
                                            status = userDocHistory.getTitle();
                                            docId = userDocHistory.getId();
                                            title = userDoc.getTitle();
                                            dateEnd = userDoc.getEndDate();
                                            file = userDoc.getFile();
                                            subLink = userDoc.getSubLink();
                                            unsubLink = userDoc.getUnsubLink();
                                        }
                                    }
                                }

                                UserDocFragment userDocFragment;
                                if(status.equals("Подписание документа") && docId != -1){
                                    userDocFragment = getUserDocFragment(docId, title, dateEnd, status, latest, file, subLink, unsubLink);
                                    subscribedDocs.add(userDocFragment);
                                }
                                else if(docId != -1){
                                    userDocFragment = getUserDocFragment(docId, title, dateEnd, status, latest, file, subLink, unsubLink);
                                    unsubscribedDocs.add(userDocFragment);
                                }
                            }

                            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserAccountFragment(subscribedDocs, unsubscribedDocs)).commit();
                        }
                    }
                    @Override
                    public void onFailure(Call<UserAccount> call, Throwable t) {
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        return view;
    }

    public UserDocFragment getUserDocFragment(Integer docId, String title, String dateEnd, String status,
                                              String latest, String fileUrl, String subLink, String unsubLink){
        UserDocFragment userDocFragment = new UserDocFragment();
        userDocFragment.setId(docId);
        userDocFragment.setTitle(title);
        userDocFragment.setDateEnd(dateEnd);
        userDocFragment.setStatus(status);
        userDocFragment.setDateStatus(latest);
        userDocFragment.setFileUrl(fileUrl);
        userDocFragment.setSubLink(subLink);
        userDocFragment.setUnsubLink(unsubLink);
        return userDocFragment;
    }

}
