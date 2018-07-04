package com.example.Genza999.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;

public class Student_Registartion extends AppCompatActivity {


    Activity activity = this;
    Spinner spinner;
    private SpassFingerprint mSpassFingerprint;
    private Spass spass;
    private Context mContext;
    private boolean onReadyIdentify = false;
    boolean isFeatureEnabled = false;
    Button btn;


    private SpassFingerprint.RegisterListener mRegisterListener = new SpassFingerprint.RegisterListener() {
        @Override
        public void onFinished() {
            Toast.makeText(Student_Registartion.this, "Fingerprint Registered", Toast.LENGTH_SHORT).show();
            btn.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__registartion);

        mContext = this;
        mSpassFingerprint = new SpassFingerprint(Student_Registartion.this);
        spass = new Spass();
        try{
            spass.initialize(mContext);
        }catch (Exception e){

        }

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, AppBase.divisions);
        spinner.setAdapter(adapter);

        btn = (Button) findViewById(R.id.buttonSAVE);
        assert btn != null;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDatabase(v);
            }
        });

        isFeatureEnabled = spass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT);

        final Button registerFingerprint = findViewById(R.id.registerFinger);
        registerFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!isFeatureEnabled) {
                        Toast.makeText(Student_Registartion.this, "Finger print service is not supported", Toast.LENGTH_SHORT).show();
                    } else {

                        if(onReadyIdentify == false){
                            mSpassFingerprint.registerFinger(Student_Registartion.this,mRegisterListener);
                            Toast.makeText(Student_Registartion.this, "Teacher's Fingerprint needed to register", Toast.LENGTH_SHORT).show();


                        }

                    }
                }catch (UnsupportedOperationException e){
                    Toast.makeText(Student_Registartion.this, "Finger print service is not supported", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    public void saveToDatabase(View view) {
        EditText name = (EditText)findViewById(R.id.edit_name);
        EditText roll = (EditText)findViewById(R.id.roll);
        EditText register = (EditText)findViewById(R.id.register);
        EditText contact = (EditText)findViewById(R.id.contact);
        String classSelected = spinner.getSelectedItem().toString();

        if(name.getText().length()<2||roll.getText().length()==0||register.getText().length()<2||
                contact.getText().length()<2||classSelected.length()<2)
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setTitle("Invalid");
            alert.setMessage("Insufficient Data");
            alert.setPositiveButton("OK", null);
            alert.show();
            return;
        }

        String qu = "INSERT INTO STUDENT VALUES('" +name.getText().toString()+ "'," +
                "'" + classSelected +"',"+
                "'" + register.getText().toString().toUpperCase() +"',"+
                "'" + contact.getText().toString() +"',"+
                "" + Integer.parseInt(roll.getText().toString()) +");";
        Log.d("Student Reg" , qu);
        AppBase.handler.execAction(qu);
        qu = "SELECT * FROM STUDENT WHERE regno = '" + register.getText().toString() +  "';";
        Log.d("Student Reg" , qu);
        if(AppBase.handler.execQuery(qu)!=null)
        {
            Toast.makeText(getBaseContext(),"Student Added", Toast.LENGTH_LONG).show();
            this.finish();
        }
    }
}
