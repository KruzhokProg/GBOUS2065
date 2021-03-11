package com.example.gbous2065;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.gbous2065.Utils.Crypto;
import com.example.gbous2065.Utils.DateTimeDifference;
import com.example.gbous2065.Utils.NetworkDownload;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAmount;
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
    TextInputEditText etEmail, etPass;
    String email, pass, loginEncrypted, passwordEncrypted;
    String temp="";
    SharedPreferences sharedPreferences;
    NavigationView navigationView;

    public LoginFragment(NavigationView navigationView){
        this.navigationView = navigationView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login_fragment, container, false);

        btnSignIn = view.findViewById(R.id.btnSignIn);
        etEmail = view.findViewById(R.id.etEmail);
        etPass = view.findViewById(R.id.etPass);

//        sharedPreferences = getContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
//        loginEncrypted = sharedPreferences.getString("login", "");
//        passwordEncrypted = sharedPreferences.getString("pass", "");
//
//        if(!loginEncrypted.equals("") && !passwordEncrypted.equals("")){
//            NetworkDownload.getDataAndGo(getContext(), getFragmentManager());
//        }

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email =  etEmail.getText().toString();
                pass =  etPass.getText().toString();
                NetworkDownload.getDataAndGo(getContext(), getFragmentManager(), navigationView,"login",null, email, pass);
            }
        });

        return view;
    }



}
