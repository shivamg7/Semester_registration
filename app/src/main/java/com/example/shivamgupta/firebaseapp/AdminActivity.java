package com.example.shivamgupta.firebaseapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";
    private EditText mQuery;
    private TextView Name,Sem,Father,Dob,Gender,BG,Guardian,FPhone,GPhone,FEmail,GEmail;
    private TextView MOA,FA1,FA2,C1,C2,TC,A1,A2,Lat,Lon;
    private ImageView mImageView;
    private Button mSubmit,mPDF;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDRef = database.getReference(),DRef,sgpaRef;
    HashMap<String, Object> student = new HashMap<>();
    private ProgressDialog mProgress;
    private StorageReference mRef,Ref;
    private Image myImg;
    final long ONE_MEGABYTE = 1024*1024*5;
    private boolean DOCUMENT_OK = false, HAVE_IMG = false;
    private String email;
    private int Margin_left=40,Margin_top=40;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        UI();
        mProgress = new ProgressDialog(AdminActivity.this);
        mRef = FirebaseStorage.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.e("Imformation","UID : "+user.getUid());

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DOCUMENT_OK = false; HAVE_IMG = false;
                if(invalid()){
                    Toast.makeText(AdminActivity.this,"Invalid ", Toast.LENGTH_SHORT).show();
                }
                else {
                    mProgress.setMessage("Retrieving Document");
                    mProgress.show();
                    email = mQuery.getText().toString().trim();
                    DRef = mDRef.child("Students").child(email);
                    ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get Post object and use the values to update the UI
                            if(dataSnapshot.getValue() == null){
                                Toast.makeText(AdminActivity.this,"Document doesnot exist", Toast.LENGTH_SHORT).show();
                                DOCUMENT_OK = false;
                                mProgress.dismiss();
                            }else {
                                student = (HashMap<String, Object>) dataSnapshot.getValue();
                                mProgress.dismiss();
                                DOCUMENT_OK = true;
                                Log.e("AdminActivity", "onDataChange: "+DOCUMENT_OK);
                                displayData();
                            }
                            // ...
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.e("Admin Activity",databaseError.getMessage());
                            Toast.makeText(AdminActivity.this,"Server Failure", Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();
                            // ...
                        }
                    };


                    Ref = mRef.child("Photos").child(email);
                    mProgress.setMessage("Downloading Image");
                    mProgress.setCancelable(false);
                    mProgress.show();
                    Ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Data for "images/island.jpg" is returns, use this as needed
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , stream);
                            try {
                                myImg = Image.getInstance(stream.toByteArray());
                                myImg.setAlignment(Image.LEFT);
                                HAVE_IMG =  true;
                                Log.e("AdminActivity", "onSuccess: "+HAVE_IMG);
                            }catch(Exception e){
                                Toast.makeText(AdminActivity.this,"Null Image", Toast.LENGTH_SHORT).show();
                            }
                            mProgress.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("AdminActivity","Excep :  "+exception.getMessage());
                            mProgress.dismiss();
                            // Handle any errors
                        }
                    });


                    final StorageReference filepath = Ref;
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).fit().centerCrop().into(mImageView);
                        }
                    });

                    /*Glide.with(AdminActivity.this)
                            .using(new FirebaseImageLoader())
                            .load(Ref)
                            .into(mImageView);*/


                    DRef.addListenerForSingleValueEvent(postListener);

                }
            }
        });

        mPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setMessage("Creating PDF...");
                mProgress.setCancelable(false);
                mProgress.show();
                if(DOCUMENT_OK && HAVE_IMG){

                    if(generatePDF(email,myImg,student)) {
                        Toast.makeText(AdminActivity.this,"PDF created", Toast.LENGTH_SHORT).show();
                        openPDF(email);
                    }else {
                        Toast.makeText(AdminActivity.this,"PDF creation failed", Toast.LENGTH_SHORT).show();
                    }
                }else if(DOCUMENT_OK){
                    Toast.makeText(AdminActivity.this,"Image being downloaded", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AdminActivity.this,"Invalid Document", Toast.LENGTH_SHORT).show();
                }
                mProgress.dismiss();
            }
        });

    }

    private boolean generatePDF(String fname, Image myImg, Map<String, Object> student) {
        try {
            String fpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/vindroid";
            Log.e(TAG,"fpath:"+fpath);
            File dir = new File(fpath);
            if(!dir.exists()){
                //  dir.mkdirs();
                Log.e(TAG,"Result of Mkdir : "+dir.mkdirs());
            }else {
                Log.e(TAG,"Already Exists");
            }


            File file = new File(dir,fname+".pdf");
            if (!file.exists()) {
                file.createNewFile();
            }
            //Toast.makeText(AdminActivity.this,"File created ", Toast.LENGTH_SHORT).show();
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);
            Font f=new Font(Font.FontFamily.TIMES_ROMAN,15.0f,Font.NORMAL,BaseColor.BLACK);
            Rectangle rect = new Rectangle(PageSize.A4.getWidth(),PageSize.A4.getHeight());
            rect.setBorder(Rectangle.BOX);
            rect.setBorderWidth(2);
            Document document = new Document(PageSize.A4,Margin_left,Margin_left,Margin_top,Margin_top);
            PdfWriter writer =  PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
            PdfHeader event = new PdfHeader();
            writer.setPageEvent(event);
            List list = new List(List.UNORDERED);
            document.open();
            addmetadata(document);
            document.add(rect);
            myImg.scaleToFit(640/2,480/2);
            myImg.setAbsolutePosition(PageSize.A4.getWidth()-myImg.getScaledWidth()-Margin_left,PageSize.A4.getHeight()-myImg.getScaledHeight()-Margin_top);
            document.add(myImg);
            addParagraph(document,"Name",f);
            addParagraph(document,"USN",f);
            addParagraph(document,"DOB",f);
            addParagraph(document,"Gender",f);
            addParagraph(document,"Semester",f);
            addParagraph(document,"Blood Group",f);
            addParagraph(document,"Father",f);
            addParagraph(document,"Father's Phone",f);
            addParagraph(document,"Father's Email",f);
            addParagraph(document,"Guardian",f);
            addParagraph(document,"Guardian's Phone",f);
            addParagraph(document,"Guardian's Email",f);
            addParagraph(document,"Mode of Admission",f);
            addParagraph(document,"CGPA",f);
            for(int i=0;i<6;i++) {
                addParagraph(document,"SGPA("+i+")",f);
            }
            addParagraph(document,"Fee Amount 1",f);
            addParagraph(document,"Challan no 1",f);
            addParagraph(document,"Fee Amount 2",f);
            addParagraph(document,"Challan no 2",f);
            addParagraph(document,"Technical Clubs",f);


            //TODO Add other Details

            list.setListSymbol(new Chunk("Address : "));
            list.add(new ListItem((String)student.get("Address Line 1"),f));
            list.add(new ListItem((String)student.get("Address Line 2"),f));
            document.add(list);
            document.add(Chunk.NEWLINE);

            document.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (DocumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void addParagraph(Document document,String content,Font f){
        try {
            document.add(new Paragraph(content+"     :   "+student.get(content),f));
        }catch(DocumentException e){
            Log.e("Document Error", "Failed adding"+content);
        }
    }

    private void addmetadata(Document document) {
        document.addTitle(student.get("USN")+"pdf");
        document.addSubject("Registration details");
        document.addAuthor("Students");
        document.addCreator("Students");
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

    private boolean invalid() {
        return mQuery.getText().toString().trim().isEmpty();
    }

    private void UI() {
        mQuery = (EditText) findViewById(R.id.etQuery);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mSubmit = (Button) findViewById(R.id.btnQuery);
        mPDF = (Button) findViewById(R.id.btPDF);
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
        //Phone = (TextView) findViewById(R.id.tvPhone);
        Father = (TextView) findViewById(R.id.tvFather);
        Dob = (TextView) findViewById(R.id.tvDob);
        A1 = (TextView) findViewById(R.id.tvA1);
        A2 = (TextView) findViewById(R.id.tvA2);
        Lat = (TextView) findViewById(R.id.tvLatitude);
        Lon = (TextView) findViewById(R.id.tvLongitude);
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
        finish();
        AuthUI.getInstance()
                .signOut(AdminActivity.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        finish();
                        startActivity(new Intent(AdminActivity.this,MainActivity.class));
                        Toast.makeText(AdminActivity.this,"Logged Out",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class PdfHeader extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                Rectangle pageSize = document.getPageSize();
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Semester Registration Form", FontFactory.getFont(FontFactory.COURIER, 18, Font.NORMAL)), pageSize.getLeft(275), pageSize.getTop(30), 0);
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, new Phrase(String.format("RVCE", String.valueOf(writer.getCurrentPageNumber()))),
                        pageSize.getRight(30), pageSize.getTop(30), 0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void openPDF(String fname){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/vindroid/"+fname+".pdf");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}

