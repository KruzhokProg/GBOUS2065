package com.example.gbous2065;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import androidx.biometric.BiometricPrompt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gbous2065.Models.UserDoc;
import com.example.gbous2065.Models.UserDocFragment;
import com.github.barteksc.pdfviewer.PDFView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;
import java.util.concurrent.Executor;

import cz.msebera.android.httpclient.Header;

public class DisplayDocActivity extends AppCompatActivity {

    PDFView pdfView;
    AsyncHttpClient asyncHttpClient;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    UserDoc userDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_doc);

        pdfView = findViewById(R.id.pdfViewer);
        asyncHttpClient = new AsyncHttpClient();
        userDoc = (UserDoc)getIntent().getExtras().getParcelable("docInfo");

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // Диалоговаое окно Логин и Пароль
                LayoutInflater layoutInflater = LayoutInflater.from(DisplayDocActivity.this);
                View view = layoutInflater.inflate(R.layout.dialog_subscribe, null);
                EditText etLoginDialog = view.findViewById(R.id.etLoginDialog);
                EditText etPassDialog = view.findViewById(R.id.etPassDialog);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DisplayDocActivity.this);
                alertDialogBuilder.setView(view);
                alertDialogBuilder.setTitle("Подтвердить");

                alertDialogBuilder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(DisplayDocActivity.this, "Да", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialogBuilder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(DisplayDocActivity.this, "Нет", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.editTextColor));
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.editTextColor));
                    }
                });
                alertDialog.show();

            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Подпись документа")
                .setNegativeButtonText("Использовать Логин и Пароль")
                .build();

        UserDocFragment userDoc = (UserDocFragment) getIntent().getExtras().get("docInfo");
        String url = userDoc.getFileUrl();
        asyncHttpClient.get(url, new FileAsyncHttpResponseHandler(this) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                Toast.makeText(DisplayDocActivity.this, "Error in Downloading Excel File", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", throwable.getMessage());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                if (file != null) {
                    pdfView.fromFile(file).load();
                }
            }
        });

    }

    public void btnSubDocOnClick(View view) {
        biometricPrompt.authenticate(promptInfo);
    }
}