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
import com.example.gbous2065.Network.ApiNewsClient;
import com.example.gbous2065.Network.ApiService;
import com.example.gbous2065.Network.ApiUserAccountClient;
import com.google.android.material.textfield.TextInputEditText;

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
                        List<UserDoc> userDocList = new ArrayList<>();
                        for (Map<String, UserDoc> item :user.getDocs()) {
                            Set<String> keys = item.keySet();
                            for (String s : keys) {
                                UserDoc userDoc = item.get(s);
                                userDocList.add(userDoc);
                            }
                        }
                        Toast.makeText(getContext(), "Ura!", Toast.LENGTH_SHORT).show();
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
}
