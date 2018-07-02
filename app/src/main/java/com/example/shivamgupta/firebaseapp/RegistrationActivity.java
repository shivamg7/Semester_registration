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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private EditText userName,userEmail,userPassword;
    private Button regButton;
    private TextView userLogin;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    progressDialog = new ProgressDialog(RegistrationActivity.this);
                    progressDialog.setMessage("Registering ...");
                    progressDialog.show();

                    //Upload Data to Database
                    String user_email = userEmail.getText().toString().trim();
                    String user_password = userPassword.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(userName.getText().toString().trim())
                                        .build();

                                Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                sendEmailVerification();
                            }
                            else
                                Toast.makeText(RegistrationActivity.this, "Registration unSuccessful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }
        });
    }

    private void setupUIViews() {
        userName = (EditText) findViewById(R.id.etUserName);
        userPassword =(EditText) findViewById(R.id.etUserPassword);
        userEmail =(EditText) findViewById(R.id.etUserEmail);
        regButton = (Button) findViewById(R.id.etRegister);
        userLogin = (TextView) findViewById(R.id.tvUserLogin);

    }

    private Boolean validate() {
        final String EMAIL_REGEX =  "^[\\w-\\+]+(\\.[\\w]+)*@rvce(\\.)edu(\\.)in$";
        final String USN_REGX = "^1RV1[1-9](IS|CV|BT|CS|ME)[0-2][0-9][0-9]";
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        String name = userName.getText().toString().trim();
        String password = userPassword.getText().toString().trim();
        String email = userEmail.getText().toString().trim();
        matcher = pattern.matcher(email);
        //TODO write Regex for matching USN
        if(name.isEmpty() || password.isEmpty() || email.isEmpty()  ) {
            Toast.makeText(this, "Please Enter all the details", Toast.LENGTH_SHORT).show();
        }else {
            if(matcher.matches()){
                if(!Pattern.compile(USN_REGX).matcher(name).matches()){
                    Toast.makeText(this, "Invalid USN", Toast.LENGTH_SHORT).show();
                }
                else{
                    return true;
                }
            }else {
                Toast.makeText(this, "Only RVCE email id allowed", Toast.LENGTH_SHORT).show();
            }

        }
        return false;
    }

    private void sendEmailVerification() {
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null ){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(userName.getText().toString().trim())
                    .build();

            firebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("RegistrationActivity", "User profile updated.");
                                Toast.makeText(RegistrationActivity.this,"UserName :"+firebaseUser.getDisplayName(),Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(RegistrationActivity.this,"UserName Failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this, "Email verification sent", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    }else {
                        Toast.makeText(RegistrationActivity.this,"Email hasn't been sent",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
