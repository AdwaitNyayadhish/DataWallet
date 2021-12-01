package com.example.datawaallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String user;
    Boolean authority;
    TextView email,rollNo,college,userName;
    ListView fileViewer;
    List<putFile> uploadedFiles;
    DatabaseReference databaseReference;
    static String data;

    Button fileDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        data=QrScanner.qrdata;
        fileDown=findViewById(R.id.fileDown);

        fAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        user=fAuth.getCurrentUser().getUid();
        email=findViewById(R.id.email);
        rollNo=findViewById(R.id.rollNo);
        college=findViewById(R.id.college);
        userName=findViewById(R.id.userName);

        fileViewer=findViewById(R.id.fileViewer);




        CollectionReference documentReference=fstore.collection("users");

        documentReference.document(data).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot docshot, @Nullable FirebaseFirestoreException error) {
                email.setText("Email id : " +docshot.getString("email"));
                userName.setText("Name : "+docshot.getString("fname"));
                rollNo.setText("Roll No. : "+docshot.getLong("rollno").intValue());
                college.setText("College : "+docshot.getString("college"));
            }
        });

        documentReference.document(user).addSnapshotListener(this,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                authority=(documentSnapshot.getBoolean("authority"));
               if(authority==true){
                   fileDown.setEnabled(true);
               }
               else{
                   fileDown.setText("Not Authorised to view Files" );
               }
            }
            private void retrievePdf() {

                databaseReference= FirebaseDatabase.getInstance().getReference("UploadedFiles").child(data);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            putFile putFile=ds.getValue(com.example.datawaallet.putFile.class);
                            uploadedFiles.add(putFile);
                        }

                        String[] uploadsName=new String[uploadedFiles.size()];

                        for(int i=0;i<uploadsName.length;i++){

                            uploadsName[i]=uploadedFiles.get(i).getName();

                        }

                        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_list_item_1,uploadsName){

                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                                View view=super.getView(position, convertView, parent);
                                TextView textViewer=(TextView)view.findViewById(android.R.id.text1);

                                textViewer.setTextColor(Color.BLACK);
                                textViewer.setTextSize(15);
                                return view;
                            }
                        };

                        fileViewer.setAdapter(arrayAdapter);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfileActivity.this, (error.toString()), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }
    public void retrieveFiles(View view) {
        startActivity(new Intent(getApplicationContext(),SeeFiles.class));
    }



}