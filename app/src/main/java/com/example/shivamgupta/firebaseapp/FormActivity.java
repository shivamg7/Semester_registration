package com.example.shivamgupta.firebaseapp;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;


public class FormActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView Name,USN,FatherName,FatherPhone,FatherEmail,GuardianName,GuardianEmail,GuardianPhone;
    private EditText DOB;
    private RadioGroup rGroup;
    private RadioButton checkedRadioButton;
    private Spinner Semester,BloodGroup;
    private static String selectedSem;
    private String selectedBG,selectedGender,error_validate;
    private Button Submit;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Calendar myCalendar;
    Bundle bd;
    // DynamoDBMapper dynamoDBMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        myCalendar = Calendar.getInstance();
        UI();
        if(firebaseUser != null) {
           String fixed_USN =  firebaseUser.getDisplayName();
            USN.setText(fixed_USN);
            USN.setEnabled(false);
        }
       final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(FormActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkedRadioButton =(RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if(isChecked){
                    selectedGender = checkedRadioButton.getText().toString();
                    //Toast.makeText(FormActivity.this,"SEX : " + checkedRadioButton.getText(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        ArrayAdapter<CharSequence> SemAdapter = ArrayAdapter.createFromResource(this,
                R.array.semester_array, android.R.layout.simple_spinner_item);
        SemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Semester.setAdapter(SemAdapter);

        Semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSem = (String) parent.getItemAtPosition(position);
                //Toast.makeText(FormActivity.this,"Semester :"+selectedSem,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(FormActivity.this,"Select Semester",Toast.LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<CharSequence> BGAdapter = ArrayAdapter.createFromResource(this,
                R.array.blood_group_array, android.R.layout.simple_spinner_item);
        BGAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BloodGroup.setAdapter(BGAdapter);

        BloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBG = (String) parent.getItemAtPosition(position);
                //Toast.makeText(FormActivity.this,"BloodGroup :"+selectedBG,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(FormActivity.this,"Select Blood Group",Toast.LENGTH_SHORT).show();
            }
        });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if(validate()) {
                 bd.putString("Name", Name.getText().toString());
                 bd.putString("USN", USN.getText().toString());
                 bd.putString("DOB", DOB.getText().toString());
                 bd.putString("Gender",selectedGender);
                 bd.putString("Semester", selectedSem);
                 bd.putString("Blood Group", selectedBG);
                 bd.putString("Father", FatherName.getText().toString());
                 bd.putString("Father's Phone", FatherPhone.getText().toString());
                 bd.putString("Father's Email", FatherEmail.getText().toString());
                 bd.putString("Guardian", GuardianName.getText().toString());
                 bd.putString("Guardian's Phone", GuardianPhone.getText().toString());
                 bd.putString("Guardian's Email", GuardianEmail.getText().toString());
                 Intent it = new Intent(FormActivity.this, FormActivity2.class);
                 it.putExtras(bd);
                 startActivity(it);
             }else {
                 Toast.makeText(FormActivity.this,error_validate,Toast.LENGTH_SHORT).show();
             }

            }
        });
    }

    private boolean validate() {
        //Validate USN using regular expressions
        //Semester and Blood Selected or not
        //Email validation using regex
        //Valid Phone numbers, 10 digits
        //set String error_validate Accordingly
        final String USN_REGX = "^1RV1[1-9](IS|CV|BT|CS|ME)[0-2][0-9][0-9]";
        final String EMAIL_REGEX =  "^[\\w-\\+]+@[\\w-\\+](\\.)[\\w-\\+]$";
        boolean flag_USN =  Pattern.compile(USN_REGX).matcher(USN.getText().toString().trim()).matches();
        boolean flag_EMAIL = Pattern.compile(EMAIL_REGEX).matcher(GuardianEmail.getText().toString().trim()).matches()||Pattern.compile(EMAIL_REGEX).matcher(FatherEmail.getText().toString().trim()).matches();
        if(FatherName.getText().toString().trim().isEmpty()||GuardianName.getText().toString().trim().isEmpty()){
            error_validate = "Incomplete Form";
        }else if(!valid_phone(FatherPhone)||!valid_phone(GuardianPhone)){
            error_validate = "Invalid Phone Numbers";
        }else if (!flag_USN){
            error_validate = "Invalid USN";
        }else if(false){
            error_validate = "Invalud Emails";
        }else {
            return true;
        }
        //TODO
        return false;
    }

    private boolean valid_phone(TextView Phone) {
        //Toast.makeText(FormActivity.this, "Length is 1"+Phone.getText().toString().length(), Toast.LENGTH_SHORT).show();

        if(Phone.getText().toString().length()!=10){
          //  Toast.makeText(FormActivity.this, "Length is"+Phone.getText().toString().length(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void UI(){
        Name = (TextView)findViewById(R.id.etName);
        USN =  findViewById(R.id.etUSN);
        FatherPhone = (TextView) findViewById(R.id.etPhoneFather);
        FatherPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        FatherEmail = (TextView) findViewById(R.id.etEmailFather);
        GuardianName = (TextView) findViewById(R.id.etNameGuardian);
        GuardianPhone = (TextView) findViewById(R.id.etPhoneGuardian);
        GuardianPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        GuardianEmail = (TextView) findViewById(R.id.etEmailGuardian);
        FatherName = (TextView)findViewById(R.id.etNameFather);
        DOB = findViewById(R.id.etDOB);
        DOB.setKeyListener(null);
        rGroup = (RadioGroup) findViewById(R.id.radioGroup);
        Semester = (Spinner) findViewById(R.id.semester);
        BloodGroup = (Spinner) findViewById(R.id.bloodg);
        checkedRadioButton = (RadioButton) rGroup.findViewById(rGroup.getCheckedRadioButtonId());
        Submit = (Button)findViewById(R.id.btnSubmit);
        bd = new Bundle();
        firebaseAuth = FirebaseAuth.getInstance();
        rGroup.check(R.id.radiogroupF);
    }

    public static String getSelectedSem(){
        return selectedSem;
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
        startActivity(new Intent(FormActivity.this, MainActivity.class));
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        DOB.setText(sdf.format(myCalendar.getTime()));
    }
}

