package com.example.shivamgupta.firebaseapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;


import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText Name;
    private EditText Password;
    //private TextView Info;
    private Button Login;
    private int counter = 5;
    private TextView userRegistration,passwordReset;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Name = (EditText) findViewById(R.id.etName);
        Password = (EditText)findViewById(R.id.etUserEmail);
        //Info = (TextView)findViewById(R.id.tvinfo);
        Login = (Button)findViewById(R.id.btnLogin);
        userRegistration = (TextView)findViewById(R.id.etRegister);
        passwordReset = (TextView) findViewById(R.id.etForgot);
        //Info.setText(getString(R.string.attempts,5));
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.CAMERA};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }



        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
            if(checkEmailVerification()) {
                finish();
                startActivity(new Intent(MainActivity.this, FormActivity.class));
               /* Intent intent = new Intent(MainActivity.this, SignOut.class);
                intent.putExtra("USN", user.getDisplayName());
                startActivity(intent);*/
            }
        }

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                validate(Name.getText().toString(), Password.getText().toString());
            }
        });

        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });

        passwordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PasswordReset.class));
            }
        });

    }

    private boolean hasPermissions(Context context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void validate(String userName, String userPassword){

        if(userName.isEmpty() || userPassword.isEmpty()){
            Toast.makeText(MainActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(userName,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    if(checkEmailVerification())
                        startActivity(new Intent(MainActivity.this, FormActivity.class));
                }else{
                    Toast.makeText(MainActivity.this,"Login Unsuccessful",Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private boolean checkEmailVerification() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Boolean emailFlag = firebaseUser.isEmailVerified();

        if(firebaseUser.getUid().equals("fcOA5hwyfDQj4PMcRv3WjLJBs2R2")){
            finish();
            startActivity(new Intent(MainActivity.this,AdminActivity.class));
            return false;
        }

        if(emailFlag) {
            return true;
        }else {
            Toast.makeText(this, "Verify Your Email", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
            return  false;
        }

    }

}