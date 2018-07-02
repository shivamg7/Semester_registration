package com.example.shivamgupta.firebaseapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordReset extends AppCompatActivity {

    private Button Reset;
    private EditText Email;
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        Reset = (Button) findViewById(R.id.btnReset);
        Email = (EditText) findViewById(R.id.etEmail);
        mProgress = new ProgressDialog(PasswordReset.this);
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = Email.getText().toString().trim();
                mProgress.setMessage("Sending Reset Link");
                mProgress.show();

                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mProgress.dismiss();
                                    Toast.makeText(PasswordReset.this,"Email sent",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(PasswordReset.this, MainActivity.class));
                                    finish();
                                }
                                else{
                                    mProgress.dismiss();
                                    Toast.makeText(PasswordReset.this,"Failed",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
