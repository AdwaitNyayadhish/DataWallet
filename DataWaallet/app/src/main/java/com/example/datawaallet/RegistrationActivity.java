package com.example.datawaallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    EditText registrationPersonName,registrationRollNumber,registrationEmailAddress,registrationCollege,registrationPassword;
    Button registerButton;
    FirebaseAuth fAuth;
    ProgressBar registrationProgressBar;
    FirebaseFirestore fstore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registrationCollege=findViewById(R.id.registrationCollege);
        registrationPersonName=findViewById(R.id.registrationPersonName);
        registrationEmailAddress=findViewById(R.id.registrationEmailAddress);
        registrationPassword=findViewById(R.id.registrationPassword);
        registrationRollNumber=findViewById(R.id.registrationRollNumber);
        registrationProgressBar=findViewById(R.id.registrationProgressBar);
        registerButton=findViewById(R.id.registerButton);

        fAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=registrationEmailAddress.getText().toString().trim();
                String password=registrationPassword.getText().toString().trim();
                String personname=registrationPersonName.getText().toString().trim();
                String college=registrationCollege.getText().toString().trim();
                String rollno=registrationRollNumber.getText().toString().trim();
                Integer roll= Integer.parseInt(rollno);
                Boolean authority=false;
                if(TextUtils.isEmpty(email))
                {
                    registrationEmailAddress.setError("Email is Mandatory");
                    return;
                }
                if(TextUtils.isEmpty(college))
                {
                    registrationCollege.setError("College Name is Mandatory");
                    return;
                }
                if(TextUtils.isEmpty(personname))
                {
                    registrationPersonName.setError("Name is Mandatory");
                    return;
                }
                if(TextUtils.isEmpty(rollno))
                {
                    registrationRollNumber.setError("Roll Number is Mandatory");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    registrationPassword.setError("Password is Mandatory");
                    return;
                }
                if(password.length()<5)
                {
                    registrationPassword.setError("Password too short. Should be greater than 5 characters");
                    return;
                }
                registrationProgressBar.setVisibility(View.VISIBLE);

                //registering user

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegistrationActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                            userId= fAuth.getCurrentUser().getEmail();
                            DocumentReference documentReference=fstore.collection("users").document(fAuth.getUid());
                            Map<String,Object>user=new HashMap<>();
                            user.put("fname",personname);
                            user.put("email",email);
                            user.put("college",college);
                            user.put("rollno",roll);
                            user.put("authority",authority);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG","onSuccess: user profile created for :"+userId);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                            registrationProgressBar.setVisibility(View.INVISIBLE);

                        }else{
                            Toast.makeText(RegistrationActivity.this, "Error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            registrationProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }
}