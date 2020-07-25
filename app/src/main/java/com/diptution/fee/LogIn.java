package com.diptution.fee;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import java.util.concurrent.Executor;

public class LogIn extends AppCompatActivity {
    private static final String USER_ID = "admin", PASSWORD = "admin";
    private EditText edtUserId, edtPassword;
    private Vibrator v;
    protected static final long VIBRATION_TIME = 100; //milli sec
    protected static boolean DEFAULT_VIBRATION = true;
    protected static boolean FINGERPRINT_ON = true;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private boolean canUse = false;
    Button biometricLoginButton;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        edtUserId = findViewById(R.id.edtUserId_LogIn);
        edtPassword = findViewById(R.id.edtPassword_logIn);
        biometricLoginButton = findViewById(R.id.biometric_login);
        biometricLoginButton.setVisibility(View.INVISIBLE);

        v = (Vibrator) LogIn.this.getSystemService(Context.VIBRATOR_SERVICE);

        preferences =
                PreferenceManager.getDefaultSharedPreferences(LogIn.this);
        LogIn.DEFAULT_VIBRATION = preferences.getBoolean(getString(R.string.vibration_key), true);


        checkForBioAvailability();
    }

    private void checkForBioAvailability() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                canUse = true;
                LogIn.FINGERPRINT_ON = preferences.getBoolean(getString(R.string.bio_key), true);

                if (FINGERPRINT_ON) {
                    biometricLoginButton.setVisibility(View.VISIBLE);
                }
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Log.e("MY_APP_TAG", "The user hasn't associated " +
                        "any biometric credentials with their account.");
                break;
        }

        BiometricPrompt.AuthenticationCallback callBack = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error! " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Intent adminPage = new Intent(LogIn.this, AdminPage.class);
                startActivity(adminPage);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT).show();
            }
        };

        if (canUse) {
            executor = ContextCompat.getMainExecutor(this);
            biometricPrompt = new BiometricPrompt(LogIn.this, executor, callBack);

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric login for my app")
                    .setSubtitle("Log in using your biometric credential")
                    .setNegativeButtonText("Use account password")
                    .build();

            biometricLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    biometricPrompt.authenticate(promptInfo);
                }
            });
        }
    }

    public void logInToAdmin(View view) {
        if (DEFAULT_VIBRATION) {
            v.vibrate(VIBRATION_TIME);
        }

        String userIdString = edtUserId.getText().toString();
        String passwordString = edtPassword.getText().toString();

        if (!VariableMethods.checkIfAllFieldsWereInserted(new String[]{userIdString, passwordString})) {
            showAlertDialog("Enter All Field");
            return;
        }

        if (userIdString.equals(USER_ID) && passwordString.equals(PASSWORD)) {
            Intent adminPage = new Intent(LogIn.this, AdminPage.class);
            startActivity(adminPage);
            finish();
        } else {
            showAlertDialog("user name or password is or both are wrong");
        }
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(LogIn.this)
                .setTitle("Oops").setMessage(message)
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
