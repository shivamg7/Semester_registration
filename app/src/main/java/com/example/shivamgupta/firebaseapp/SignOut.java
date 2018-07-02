package com.example.shivamgupta.firebaseapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.Image;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class SignOut extends AppCompatActivity {

    private Button signOut;
    private TextView Name,Sem,Father,Dob,Gender,BG,Guardian,FPhone,GPhone,FEmail,GEmail;
    private TextView MOA,FA1,FA2,C1,C2,TC,A1,A2,Lat,Lon;
    private ImageView mImageView;
    HashMap<String, Object> student = new HashMap<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDref = database.getReference();
    private StorageReference mRef  = FirebaseStorage.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_out);
        signOut = (Button) findViewById(R.id.btnSignOut);
       final ProgressDialog mProgress = new ProgressDialog(SignOut.this);
        mProgress.setMessage("Loading Data");
        String USN = getIntent().getStringExtra("USN");
        mRef = mRef.child("Photos").child(USN);
        mDref = mDref.child("Students").child(USN);
        UI();


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if(dataSnapshot.getValue() == null){
                    mProgress.dismiss();
                    Log.e("SignOut", "onDataChange: "+false);

                }else {
                    student = (HashMap<String, Object>) dataSnapshot.getValue();
                    mProgress.dismiss();
                    Log.e("SignOut", "onDataChange: "+true);
                    displayData();
                }
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("SignOut",databaseError.getMessage());
                Toast.makeText(SignOut.this,"Server Failure", Toast.LENGTH_SHORT).show();
                mProgress.dismiss();
                // ...
            }
        };

        final StorageReference filepath = mRef;
        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(mImageView);
            }
        });



       /* Glide.with(SignOut.this)
                .using(new FirebaseImageLoader())
                .load(mRef)
                .into(mImageView);*/


        mDref.addListenerForSingleValueEvent(postListener);


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(SignOut.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                                finish();
                                startActivity(new Intent(SignOut.this,MainActivity.class));
                                Toast.makeText(SignOut.this,"Logged Out",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void UI(){
        mImageView = (ImageView) findViewById(R.id.imageView);
        Name = (TextView) findViewById(R.id.tvName);
        Gender = (TextView) findViewById(R.id.tvGender);
        Gender = (TextView) findViewById(R.id.tvGender);
        Sem = (TextView) findViewById(R.id.tvSem);
        BG = (TextView) findViewById(R.id.tvBG);
        Guardian = (TextView) findViewById(R.id.tvGuardian);
        FPhone= (TextView) findViewById(R.id.tvFatherPhone);
        GPhone = (TextView) findViewById(R.id.tvGuardianPhone);
        FEmail = (TextView) findViewById(R.id.tvFatherEmail);
        GEmail = (TextView) findViewById(R.id.tvGuardianEmail);
        MOA = (TextView) findViewById(R.id.tvMOA);
        FA1 = (TextView) findViewById(R.id.tvAmount1);
        FA2 = (TextView) findViewById(R.id.tvAmount2);
        C1 = (TextView) findViewById(R.id.tvChallan1);
        C2 = (TextView) findViewById(R.id.tvChallan2);
        TC = (TextView) findViewById(R.id.tvTC);
        Father = (TextView) findViewById(R.id.tvFather);
        Dob = (TextView) findViewById(R.id.tvDob);
        A1 = (TextView) findViewById(R.id.tvA1);
        A2 = (TextView) findViewById(R.id.tvA2);
        Lat = (TextView) findViewById(R.id.tvLatitude);
        Lon = (TextView) findViewById(R.id.tvLongitude);
    }

    private void displayData() {
        Name.setText(student.get("Name").toString());
        //Phone.setText(student.get("Phone").toString());
        Gender.setText(student.get("Gender").toString());
        Sem.setText(student.get("Semester").toString());
        Dob.setText(student.get("DOB").toString());
        BG.setText(student.get("Blood Group").toString());
        Father.setText(student.get("Father").toString());
        FPhone.setText(student.get("Father's Phone").toString());
        FEmail.setText(student.get("Father's Email").toString());
        Guardian.setText(student.get("Guardian").toString());
        GPhone.setText(student.get("Guardian's Phone").toString());
        GEmail.setText(student.get("Guardian's Email").toString());
        MOA.setText(student.get("Mode of Admission").toString());
        FA1.setText(student.get("Fee Amount 1").toString());
        FA2.setText(student.get("Fee Amount 2").toString());
        C1.setText(student.get("Challan no 1").toString());
        C2.setText(student.get("Challan no 2").toString());
        TC.setText(student.get("Technical Clubs").toString());
        A1.setText(student.get("Address Line 1").toString());
        A2.setText(student.get("Address Line 2").toString());
        Lat.setText(student.get("Latitude").toString());
        Lon.setText(student.get("Longitude").toString());
        //TODO Add Other details
        //Create Admin Layout
    }
}
