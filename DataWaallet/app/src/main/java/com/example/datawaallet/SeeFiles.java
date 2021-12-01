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

import java.util.ArrayList;
import java.util.List;

public class SeeFiles extends AppCompatActivity {

    DatabaseReference databaseReference;
    ListView seeFiles;
    List<putFile> uploadedFiles;
    Button uploadFiles;
    FirebaseAuth fAuth;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_files);

        seeFiles=findViewById(R.id.seeFiles);
        uploadedFiles=new ArrayList<>();
        fAuth= FirebaseAuth.getInstance();
        user=ProfileActivity.data;
        retrieveFiles();

        seeFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                putFile putFile=uploadedFiles.get(i);

                Intent intent= new Intent(Intent.ACTION_VIEW);
                intent.setType("application/pdf");
                intent.setData(Uri.parse(putFile.getUrl()));
                startActivity(intent);
            }
        });
    }

    private void retrieveFiles() {

        databaseReference= FirebaseDatabase.getInstance().getReference("UploadedFiles").child(user);
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

                seeFiles.setAdapter(arrayAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SeeFiles.this, (error.toString()), Toast.LENGTH_SHORT).show();

            }
        });
    }
}