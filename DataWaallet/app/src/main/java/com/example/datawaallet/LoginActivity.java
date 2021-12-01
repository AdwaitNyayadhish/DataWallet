package com.example.datawaallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText loginEditEmail, loginEditPassword;
    Button loginButton;
    TextView loginRegister;
    ProgressBar loginProgressBar;
    FirebaseAuth lauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEditEmail = findViewById(R.id.loginEditEmail);
        loginEditPassword = findViewById(R.id.loginEditPassword);
        loginButton = findViewById(R.id.loginButton);
        loginRegister = findViewById(R.id.loginRegister);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        lauth = FirebaseAuth.getInstance();

        loginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEditEmail.getText().toString().trim();
                String password = loginEditPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    loginEditEmail.setError("Enter Email");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    loginEditPassword.setError("Enter Password");
                    return;
                }
                loginProgressBar.setVisibility(View.VISIBLE);

                //authenticating the user
                lauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            loginProgressBar.setVisibility(View.INVISIBLE);
                            //finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            loginProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }
}