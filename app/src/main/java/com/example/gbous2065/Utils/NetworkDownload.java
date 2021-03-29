package com.example.gbous2065.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.renderscript.Script;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.gbous2065.AdminAccountFragment;
import com.example.gbous2065.Models.AdminAccount;
import com.example.gbous2065.Models.AdminDocHistory;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkDownload {

    public static void getDataAndGo(Context context, FragmentManager fragmentManager, NavigationView navigationView, String mode,
                                    CustomCallback customCallback, String... userInfo){
        String loginEncrypted="", passwordEncrypted="";
        String login, pass;
        SharedPreferences sharedPreferences;
        Boolean isAdmin = false;

        if(userInfo.length > 0){
            login = userInfo[0];
            pass = userInfo[1];

            String savedLogin = Crypto.getSHA256(login);
            String savedPass = Crypto.getSHA256(pass);

            if(savedLogin.equals(context.getResources().getString(R.string.secret))
                    && savedPass.equals(context.getResources().getString(R.string.secret))){
                isAdmin = true;
                sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("admin", isAdmin);
                editor.apply();
            }
        }
        else{
            sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            loginEncrypted = sharedPreferences.getString("login", "");
            passwordEncrypted = sharedPreferences.getString("pass", "");
            isAdmin = sharedPreferences.getBoolean("admin", false);

            if(loginEncrypted.equals(context.getResources().getString(R.string.secret))
                && passwordEncrypted.equals(context.getResources().getString(R.string.secret))){
                isAdmin = true;
            }

            login = Crypto.base64Decode(loginEncrypted);
            pass = Crypto.base64Decode(passwordEncrypted);


        }

        if(isAdmin){

            ApiService apiService = ApiUserAccountClient.getClient().create(ApiService.class);
            Call<AdminAccount> call = apiService.getUserStat();
            call.enqueue(new Callback<AdminAccount>() {
                @Override
                public void onResponse(Call<AdminAccount> call, Response<AdminAccount> response) {
                    AdminAccount statistics = response.body();
                    List<AdminDocHistory> adminDocHistories = new ArrayList<>();
                    for (Map<String, AdminDocHistory> item : statistics.getDocs()) {
                        Set<String> keys = item.keySet();
                        for (String s : keys) {
                            AdminDocHistory adminDocHistory = item.get(s);
                            adminDocHistories.add(adminDocHistory);
                        }
                    }

                    if (mode.equals("login")) {
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new AdminAccountFragment(adminDocHistories)).commit();
                    }
                    else if(mode.equals("cache")){
                        customCallback.onAdminSuccess(adminDocHistories);
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new AdminAccountFragment(adminDocHistories)).commit();
                    }

                    String fullName = "Администратор";
                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.menu_account);
                    navigationView.getMenu().findItem(R.id.nav_employee).setTitle(fullName);
                }

                @Override
                public void onFailure(Call<AdminAccount> call, Throwable t) {
                    Toast.makeText(context, "Неверные данные", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            ApiService apiService = ApiUserAccountClient.getClient().create(ApiService.class);
            Call<UserAccount> call = apiService.getUserInfo(login, pass);
            call.enqueue(new Callback<UserAccount>() {
                @Override
                public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                    SharedPreferences sharedPreferences;
                    UserAccount user = response.body();
                    if (user.getError() != null) {
                        Toast.makeText(context, "Доступ отказан", Toast.LENGTH_SHORT).show();
                    } else if (user.getId() == null) {
                        Toast.makeText(context, "Некорректные данные", Toast.LENGTH_SHORT).show();
                    } else {

                        if (userInfo.length > 0) {
                            // Запись UserId и Токена в FRD
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            Query query = rootRef.child("Tokens").equalTo(user.getId());
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.exists()){
                                        DatabaseReference reference = FirebaseDatabase.getInstance()
                                                .getReference("Tokens").child(user.getId());

                                        FirebaseMessaging.getInstance().getToken()
                                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<String> task) {
                                                        String token = task.getResult();

                                                        HashMap<String, String> hashMap = new HashMap<>();
                                                        hashMap.put("token", token);

                                                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                //Toast.makeText(context, "token: " + token, Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

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
                        for (UserDoc userDoc : userDocList) {
                            String latest = "1900-01-01 09:00:00";
                            String status = "";
                            Integer docId = -1;
                            String title = "";
                            String dateEnd = "";
                            String file = "", subLink = "", unsubLink = "";
                            for (UserDocHistory userDocHistory : userDocHistories) {
                                if (userDocHistory.getId() == userDoc.getId()) {
                                    if (DateTimeDifference.startLaterThanEnd(userDocHistory.getDate(), latest, "login")) {
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
                            if (status.equals("Подписание документа") && docId != -1) {
                                userDocFragment = getUserDocFragment(docId, title, dateEnd, status, latest, file, subLink, unsubLink);
                                subscribedDocs.add(userDocFragment);
                            } else if (docId != -1) {
                                userDocFragment = getUserDocFragment(docId, title, dateEnd, status, latest, file, subLink, unsubLink);
                                unsubscribedDocs.add(userDocFragment);
                            }
                        }


                        if (mode.equals("login")) {
                            fragmentManager.beginTransaction().replace(R.id.fragment_container, new UserAccountFragment(subscribedDocs, unsubscribedDocs)).commit();
                        } else if (mode.equals("cache")) {
                            String fullName = user.getSurname() + " " + user.getName() + " " + user.getPatronymic();
                            SubUnsubCombine subUnsubCombine = new SubUnsubCombine(fullName, subscribedDocs, unsubscribedDocs);
                            customCallback.onSuccess(subUnsubCombine);
                        }

                        String fullName = user.getSurname() + " " + user.getName().substring(0, 1) + ". " + user.getPatronymic().substring(0, 1) + ".";
                        navigationView.getMenu().clear();
                        navigationView.inflateMenu(R.menu.menu_account);
                        navigationView.getMenu().findItem(R.id.nav_employee).setTitle(fullName);
                    }
                }

                @Override
                public void onFailure(Call<UserAccount> call, Throwable t) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
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
