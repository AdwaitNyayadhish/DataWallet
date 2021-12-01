package com.example.datawaallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends AppCompatActivity {

    EditText fileLocation;
    Button uploadButton;
    FirebaseAuth fAuth;
    String user,filename;
    ListView viewFiles;
    List<putFile> uploadedFiles;

    StorageReference storageReference;
    DatabaseReference databaseReference,databaseRefer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        fileLocation=findViewById(R.id.fileLocation);
        uploadButton=findViewById(R.id.uploadButton);
        viewFiles=findViewById(R.id.viewFiles);
        uploadedFiles=new ArrayList<>();


        fAuth=FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser().getUid();
        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference("UploadedFiles");

        uploadButton.setEnabled(false);



        fileLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
            }
        });


    }



    private void selectFile() {
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"FILE SELECT"),12);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==12&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            uploadButton.setEnabled(true);
            fileLocation.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/")+1));
            filename=data.getDataString().substring(data.getDataString().lastIndexOf("/")+1).trim();
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadFile(data.getData());
                }
            });
        }
    }

    private void uploadFile(Uri data) {

    final ProgressDialog progressDialog=new ProgressDialog(this);
    progressDialog.setTitle("Please Wait");
    progressDialog.show();

    StorageReference reference= storageReference.child(user).child(filename);
    reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isComplete());
            Uri uri=uriTask.getResult();

            putFile putFile=new putFile(filename,uri.toString());
            databaseReference.child(user).child(databaseReference.push().getKey()).setValue(putFile);
            Toast.makeText(UploadActivity.this,"File Uploaded",Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
            double progress=(100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
            progressDialog.setMessage("Uploading file...."+progress+"%");

        }
    });
    }
}