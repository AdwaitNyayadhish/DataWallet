package com.example.datawaallet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.List;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import android.graphics.*;


public class MainActivity extends AppCompatActivity {
    TextView profileName;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String user;
    Button attendance,upload,up;
    ImageView vQR;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        profileName=findViewById(R.id.profileName);
        attendance=findViewById(R.id.attendance);
        upload=findViewById(R.id.uploadFile);
        up=findViewById(R.id.up);
        vQR=findViewById(R.id.vQR);



        fAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();

        user=fAuth.getCurrentUser().getUid();

        WindowManager manager =(WindowManager)getSystemService(WINDOW_SERVICE);
        Display display= manager.getDefaultDisplay();
        Point point=new Point();
        display.getSize(point);
        int width=point.x;
        int height=point.y;
        int dimen=width<height?width:height;
        dimen=dimen*3/4;
        qrgEncoder=new QRGEncoder(user,null,QRGContents.Type.TEXT,dimen);
        try{
            bitmap=qrgEncoder.encodeAsBitmap();
            vQR.setImageBitmap(bitmap);
        }catch (WriterException e){
            Log.e("Tag",e.toString());
        }


        DocumentReference documentReference=fstore.collection("users").document(user);
        documentReference.addSnapshotListener(this,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                       profileName.setText(documentSnapshot.getString("fname"));
            }
        });
        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),QrScanner.class));
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UploadActivity.class));
            }
        });
    }

    public void retrieveFiles(View view) {
        startActivity(new Intent(getApplicationContext(),RetrieveActivity.class));
    }
}