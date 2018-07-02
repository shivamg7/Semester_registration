package com.example.shivamgupta.firebaseapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FormActivity4 extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    private int status_code;
    private ImageView mImageView;
    private StorageReference mStorageRef;
    private FirebaseDatabase database;
    private DatabaseReference mRef,mIndexRef;
    private String USN;
    private Button selectImage, Finish, uploadImage;
    private Uri imageToUploadUri;
    Bundle extras;
    Map<String, Object> student = new HashMap<>();
    ProgressDialog progressBar;
    private static int GALLERY_INTENT = 2;
    private static final int CAMERA_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form4);
        progressBar =  new ProgressDialog(FormActivity4.this);
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Students");
        mIndexRef = database.getReference("Index");
        firebaseAuth = FirebaseAuth.getInstance();
        selectImage = (Button) findViewById(R.id.selectImage);
        uploadImage = (Button) findViewById(R.id.uploadImage);
        mImageView = (ImageView) findViewById(R.id.imageView);
        Finish = (Button) findViewById(R.id.btFinish);
        extras = new Bundle();
        extras = getIntent().getExtras();
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        status_code = 0;
        if (!Initialize()) {
            finish();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Email = user.getUid();
            USN = user.getDisplayName();
           // USN = extras.getString("USN");
        }

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);

            }
        });

        Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setMessage("Registering...");
                progressBar.show();
                if (status_code == 1) {

                    mRef.child(USN).setValue(student).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.dismiss();
                            Intent intent = new Intent(FormActivity4.this, SignOut.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("USN",USN);
                            startActivity(intent);
                            Toast.makeText(FormActivity4.this, "Registered Realtime", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.dismiss();
                            Toast.makeText(FormActivity4.this, "Not Registered realtime", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(FormActivity4.this, "Image not uploaded", Toast.LENGTH_SHORT).show();
                }

            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(Environment.getExternalStorageDirectory(), "POST_IMAGE.jpg");
                chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                imageToUploadUri = Uri.fromFile(f);
                startActivityForResult(chooserIntent, CAMERA_PHOTO);
            }
        });


    }

    //Bundle Student Details for Upload. Details are Bundled in HashMap<String,Object>
    private boolean Initialize() {
        try {


            student.put("Name", extras.getString("Name"));
            student.put("Father's Name", extras.getString("Father"));
            student.put("USN",extras.getString("USN"));
            //student.put("Phone", extras.getString("Phone"));
            student.put("Gender",extras.getString("Gender"));
            student.put("DOB", extras.getString("DOB"));
            student.put("Semester",extras.getString("Semester"));
            student.put("Blood Group",extras.getString("Blood Group"));
            student.put("Father",extras.getString("Father"));
            student.put("Father's Email",extras.getString("Father's Email"));
            student.put("Father's Phone",extras.getString("Father's Phone"));
            student.put("Guardian",extras.getString("Guardian"));
            student.put("Guardian's Phone",extras.getString("Guardian's Phone"));
            student.put("Guardian's Email",extras.getString("Guardian's Email"));
            student.put("Mode of Admission",extras.getString("Mode of Admission"));
            student.put("Fee Amount 1",extras.getString("Fee Amount 1"));
            student.put("Challan no 1",extras.getString("Challan1 no"));
            student.put("Fee Amount 2",extras.getString("Fee Amount 2"));
            student.put("Challan no 2",extras.getString("Challan2 no"));
            String arr2[] = extras.getStringArray("SGPA list");
            student.put("SGPA(0)",arr2[0]);
            student.put("SGPA(1)",arr2[1]);
            student.put("SGPA(2)",arr2[2]);
            student.put("SGPA(3)",arr2[3]);
            student.put("SGPA(4)",arr2[4]);
            student.put("SGPA(5)",arr2[5]);
            student.put("CGPA",extras.getString("CGPA"));
            student.put("Technical Clubs",extras.getString("Technical Clubs"));

            student.put("Address Line 1", extras.getString("A1"));
            student.put("Address Line 2", extras.getString("A2"));
            student.put("Latitude", extras.getDouble("Latitude"));
            student.put("Longitude", extras.getDouble("Longitude"));

            return true;
        } catch (Exception e) {
            Log.e("FormActivity4","Error HashMap",e);
            Toast.makeText(FormActivity4.this, "Failed adding data to HashMap ", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //Activities for Gallery and Camera Intents
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            progressBar.setMessage("Uploading ...");
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.show();
            Uri uri = data.getData();
            final StorageReference filepath = mStorageRef.child("Photos").child(USN);
            UploadTask uploadTask = filepath.putFile(uri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.dismiss();
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).fit().centerCrop().into(mImageView);
                        }
                    });
                    Toast.makeText(FormActivity4.this, "Upload done", Toast.LENGTH_SHORT).show();
                    status_code = 1;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.dismiss();
                    Toast.makeText(FormActivity4.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    status_code = -1;
                }
            });


        }

        if (requestCode == CAMERA_PHOTO && resultCode == RESULT_OK) {

            if (imageToUploadUri != null) {
                progressBar.setMessage("Uploading ...");
                progressBar.setCanceledOnTouchOutside(false);
                progressBar.show();
                final StorageReference filepath = mStorageRef.child("Photos").child(USN);
                UploadTask uploadTask = filepath.putFile(imageToUploadUri);




                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                progressBar.dismiss();
                                Picasso.get().load(uri).fit().centerCrop().into(mImageView);
                            }
                        });
                        status_code = 1;
                        Toast.makeText(FormActivity4.this, "Upload done", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.dismiss();
                        status_code = -1;
                        Toast.makeText(FormActivity4.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){
            case R.id.logoutMenu:
                Logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void Logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(FormActivity4.this, MainActivity.class));
    }
}
