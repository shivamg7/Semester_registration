package com.example.shivamgupta.firebaseapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class FormActivity2 extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RadioGroup AdGroup;
    private RadioButton checkedButton;
    private Button Next;
    private TextView tvs1,tvs2,tvs3,tvs4,tvs5,tvs6,tvcgpa,tvsgpa;
    private EditText Amount1,Challan1;
    private EditText Amount2,Challan2;
    private EditText sgpa1,sgpa2,sgpa3,sgpa4,sgpa5,sgpa6;
    private EditText cgpa;
    private EditText Clubs;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form2);
        extras = getIntent().getExtras();  //getting User details from previous activity
        UI();

        AdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkedButton =(RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedButton.isChecked();
                if(isChecked){
                    Toast.makeText(FormActivity2.this,"Mode : " + checkedButton.getText(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        String semester = FormActivity.getSelectedSem();
        switch (semester) {
            case "1":
                sgpa1.setVisibility(View.GONE); tvs1.setVisibility(View.GONE);
                sgpa2.setVisibility(View.GONE); tvs2.setVisibility(View.GONE);
                sgpa3.setVisibility(View.GONE); tvs3.setVisibility(View.GONE);
                sgpa4.setVisibility(View.GONE); tvs4.setVisibility(View.GONE);
                sgpa5.setVisibility(View.GONE); tvs5.setVisibility(View.GONE);
                sgpa6.setVisibility(View.GONE); tvs6.setVisibility(View.GONE);
                cgpa.setVisibility(View.GONE); tvcgpa.setVisibility(View.GONE); tvsgpa.setVisibility(View.GONE);
                break;

            case "2":
                sgpa3.setVisibility(View.GONE);tvs3.setVisibility(View.GONE);
                sgpa2.setVisibility(View.GONE);tvs2.setVisibility(View.GONE);
                sgpa4.setVisibility(View.GONE);tvs4.setVisibility(View.GONE);
                sgpa5.setVisibility(View.GONE);tvs5.setVisibility(View.GONE);
                sgpa6.setVisibility(View.GONE);tvs6.setVisibility(View.GONE);
                break;

            case "3":
                sgpa3.setVisibility(View.GONE);tvs3.setVisibility(View.GONE);
                sgpa4.setVisibility(View.GONE);tvs4.setVisibility(View.GONE);
                sgpa5.setVisibility(View.GONE);tvs5.setVisibility(View.GONE);
                sgpa6.setVisibility(View.GONE);tvs6.setVisibility(View.GONE);
                break;

            case "4":
                sgpa4.setVisibility(View.GONE);tvs4.setVisibility(View.GONE);
                sgpa5.setVisibility(View.GONE);tvs5.setVisibility(View.GONE);
                sgpa6.setVisibility(View.GONE);tvs6.setVisibility(View.GONE);
                break;

            case "5":
                sgpa5.setVisibility(View.GONE);tvs5.setVisibility(View.GONE);
                sgpa6.setVisibility(View.GONE);tvs6.setVisibility(View.GONE);
                break;

            case "6":
                sgpa6.setVisibility(View.GONE);tvs6.setVisibility(View.GONE);
                break;

            default:
                break;
        }



        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Validate()){
                final String [] sgpa = {sgpa1.getText().toString().isEmpty()?"NA":sgpa1.getText().toString(),sgpa2.getText().toString().isEmpty()?"NA":sgpa2.getText().toString(),sgpa3.getText().toString().isEmpty()?"NA":sgpa3.getText().toString(),
                        sgpa4.getText().toString().isEmpty()?"NA":sgpa4.getText().toString(),sgpa5.getText().toString().isEmpty()?"NA":sgpa5.getText().toString(),sgpa6.getText().toString().isEmpty()?"NA":sgpa6.getText().toString()};
                extras.putString("Mode of Admission",checkedButton.getText().toString());
                extras.putString("Fee Amount 1",Amount1.getText().toString());
                extras.putString("Challan1 no",Challan1.getText().toString());
                extras.putString("Fee Amount 2",Amount2.getText().toString());
                extras.putString("Challan2 no",Challan2.getText().toString());
                extras.putStringArray("SGPA list",sgpa);
                extras.putString("CGPA",cgpa.getText().toString());
                extras.putString("Technical Clubs",Clubs.getText().toString());
                Intent it = new Intent(FormActivity2.this, FormActivity3.class);
                it.putExtras(extras);
                startActivity(it);
                }
                else {
                    Toast.makeText(FormActivity2.this, "Invalid Data",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private boolean Validate(){
        if(Amount1.getText().toString().trim().isEmpty()||Amount2.getText().toString().trim().isEmpty()||Challan1.getText().toString().trim().isEmpty()||Challan2.getText().toString().trim().isEmpty())
                return false;
        return true;

           //TODO
    }


    private void UI(){

        Clubs=(EditText)findViewById(R.id.clubs);
        Amount1 = (EditText) findViewById(R.id.amount1);
        Amount2 = (EditText) findViewById(R.id.amount2);
        Challan1 = (EditText) findViewById(R.id.challan1);
        Challan2 = (EditText) findViewById(R.id.challan2);
        sgpa1 = (EditText) findViewById(R.id.sgpa1); tvs1 = (TextView) findViewById(R.id.tvs1);
        sgpa2 = (EditText) findViewById(R.id.sgpa2); tvs2 = (TextView) findViewById(R.id.tvs2);
        sgpa3 = (EditText) findViewById(R.id.sgpa3); tvs3 = (TextView) findViewById(R.id.tvs3);
        sgpa4 = (EditText) findViewById(R.id.sgpa4); tvs4 = (TextView) findViewById(R.id.tvs4);
        sgpa5 = (EditText) findViewById(R.id.sgpa5); tvs5 = (TextView) findViewById(R.id.tvs5);
        sgpa6 = (EditText) findViewById(R.id.sgpa6); tvs6 = (TextView) findViewById(R.id.tvs6);
        cgpa = (EditText) findViewById(R.id.cgpa); tvcgpa = (TextView) findViewById(R.id.tvcgpa); tvsgpa = findViewById(R.id.tvsgpa);
        AdGroup = (RadioGroup) findViewById(R.id.admission) ;
        AdGroup.check(R.id.admissionCOMEDK);
        checkedButton = (RadioButton) AdGroup.findViewById(AdGroup.getCheckedRadioButtonId());
        Next=(Button)findViewById(R.id.btnNext);
        firebaseAuth = FirebaseAuth.getInstance();
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
        startActivity(new Intent(FormActivity2.this, MainActivity.class));
    }
}
