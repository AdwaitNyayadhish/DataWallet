package com.example.datawaallet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class AttendanceActivity extends AppCompatActivity {

    static String sub1,fname1,recTime1,rollno1;
    static String sub,user,recTime,fname,roll;
    static Integer rollno;
    FirebaseAuth fauth;
    FirebaseFirestore fStore;
    DatabaseReference databaseReference;
    TextView dataRoll,dataSub,dataTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        sub=QrScanner.qrdata;
        fauth=FirebaseAuth.getInstance();
        user=fauth.getCurrentUser().getUid();
        fStore=FirebaseFirestore.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("Attendance");
        dataRoll=findViewById(R.id.dataRoll);
        dataTime=findViewById(R.id.dataTime);
        dataSub=findViewById(R.id.dataSub);

        DocumentReference documentReference=fStore.collection("users").document(user);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                rollno=documentSnapshot.getLong("rollno").intValue();
                fname=documentSnapshot.getString("fname");
                Date currentTime=Calendar.getInstance().getTime();
                SimpleDateFormat time=new SimpleDateFormat("dd/MM/YY HH:mm:ss");
                recTime=time.format(currentTime);
                roll=rollno.toString();
                dataRoll.setText("Roll No : "+roll);
                dataSub.setText("Subject : "+sub);
                dataTime.setText("Date and Time:"+recTime);
                model model=new model(rollno,recTime);
                databaseReference.child(sub).child(String.valueOf(rollno)).setValue(model);
                gettingdata();
            }
        });

    }
    public void gettingdata(){
        fname1=fname;
        rollno1=roll;
        recTime1=recTime;
        sub1=sub;
        new SendRequest().execute();
        Toast.makeText(this, "Your Attendance has been recorded"+rollno1, Toast.LENGTH_LONG).show();
    }
}

