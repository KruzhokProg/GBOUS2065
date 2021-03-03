package com.example.gbous2065;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import androidx.biometric.BiometricPrompt;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_doc);

        pdfView = findViewById(R.id.pdfViewer);
        asyncHttpClient = new AsyncHttpClient();

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
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
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
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