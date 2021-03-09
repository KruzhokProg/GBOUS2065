package com.example.gbous2065.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.service.media.MediaBrowserService;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.example.gbous2065.Models.CustomCallback;
import com.example.gbous2065.Models.SubUnsubCombine;
import com.example.gbous2065.Models.UserAccount;
import com.example.gbous2065.Models.UserDoc;
import com.example.gbous2065.Models.UserDocFragment;
import com.example.gbous2065.Models.UserDocHistory;
import com.example.gbous2065.Network.ApiService;
import com.example.gbous2065.Network.ApiUserAccountClient;
import com.example.gbous2065.R;
import com.example.gbous2065.UserAccountFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkDownload {

    public static void getDataAndGo(Context context, FragmentManager fragmentManager, String mode,
                                    CustomCallback customCallback, String... userInfo){
        String loginEncrypted, passwordEncrypted;
        String login, pass;
        String temp="";
        SharedPreferences sharedPreferences;

        if(userInfo.length > 0){
            login = userInfo[0];
            pass = userInfo[1];
        }
        else{
            sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            loginEncrypted = sharedPreferences.getString("login", "");
            passwordEncrypted = sharedPreferences.getString("pass", "");

            login = Crypto.base64Decode(loginEncrypted);
            pass = Crypto.base64Decode(passwordEncrypted);
        }

        ApiService apiService = ApiUserAccountClient.getClient().create(ApiService.class);
        Call<UserAccount> call =  apiService.getUserInfo(login, pass);
        call.enqueue(new Callback<UserAccount>() {
            @Override
            public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                SharedPreferences sharedPreferences;
                UserAccount user = response.body();
                if(user.getError() != null){
                    Toast.makeText(context, "Доступ отказан", Toast.LENGTH_SHORT).show();
                }
                else if(user.getId() == null) {
                    Toast.makeText(context, "Некорректные данные", Toast.LENGTH_SHORT).show();
                }
                else{

                    if(userInfo.length > 0) {
                        String loginEncrypted = Crypto.base64Encode(login);
                        String passwordEncrypted = Crypto.base64Encode(pass);
                        sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("login", loginEncrypted);
                        editor.putString("pass", passwordEncrypted);
                        editor.apply();
                    }

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


                    if(mode.equals("login")) {
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new UserAccountFragment(subscribedDocs, unsubscribedDocs)).commit();
                    }
                    else if(mode.equals("cache")){
                        SubUnsubCombine subUnsubCombine = new SubUnsubCombine(subscribedDocs, unsubscribedDocs);
                        customCallback.onSuccess(subUnsubCombine);
                    }
                }
            }
            @Override
            public void onFailure(Call<UserAccount> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static UserDocFragment getUserDocFragment(Integer docId, String title, String dateEnd, String status,
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
