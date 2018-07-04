package com.example.Genza999.myapplication;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;

import java.util.ArrayList;

public class listAdapter extends BaseAdapter{
    ArrayList<String> nameList;
    ArrayList<String> registers;
    Activity activity;
    public static CheckBox checkBox;
    private SpassFingerprint mSpassFingerprint;
    private Spass spass;

    private boolean onReadyIdentify = false;
    boolean isFeatureEnabled = false;



    ArrayList<Boolean> attendanceList;
    public listAdapter(Activity activity,ArrayList<String> nameList,ArrayList<String> reg) {
        this.nameList = nameList;
        this.activity = activity;
        this.registers = reg;
        attendanceList = new ArrayList<>();
        for(int i=0; i<nameList.size(); i++)
        {
            attendanceList.add(new Boolean(false));
        }

        mSpassFingerprint = new SpassFingerprint(activity);
        spass = new Spass();
        try{
            spass.initialize(activity);
        }catch (Exception e){

        }
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return nameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(activity);
            v = vi.inflate(R.layout.list_ele, null);
        }
        final int pos = position;
        TextView textView = v.findViewById(R.id.attendanceName);
        textView.setText(nameList.get(position));
        final View finalV = v;

        v.setOnClickListener(new View.OnClickListener() {

            private SpassFingerprint.IdentifyListener listener = new SpassFingerprint.IdentifyListener() {
                @Override
                public void onFinished(int eventStatus) {
                    onReadyIdentify = false;
                    if(eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS){
                        Toast.makeText(activity, "Thank you for Attending!", Toast.LENGTH_SHORT).show();
                        checkBox.setVisibility(View.VISIBLE);
                        checkBox.setChecked(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                            checkBox.callOnClick();
                        }
                        attendanceActivity.btnx.setVisibility(View.VISIBLE);
                    }else if (eventStatus == SpassFingerprint.STATUS_BUTTON_PRESSED) {
                        Toast.makeText(activity, "You pressed own button", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(activity, "Authentication failed", Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onReady() {
                    Toast.makeText(activity, "Please swipe your fingerprint below", Toast.LENGTH_SHORT).show();


                }

                @Override
                public void onStarted() {

                }

                @Override
                public void onCompleted() {

                }
            };
            @Override
            public void onClick(View view) {
                mSpassFingerprint.startIdentify(listener);
                checkBox = finalV.findViewById(R.id.attMarker);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attendanceList.set(pos,checkBox.isChecked());
                        Log.d("Attendance", nameList.get(position).toString() + " is present  " + attendanceList.get(position));
                    }
                });



            }
        });


        return v;
    }

    public void saveAll()
    {
        for(int i=0; i<nameList.size(); i++)
        {
            int sts = 1;
            if(attendanceList.get(i))
                sts = 1;
            else sts = 0;
            String qu = "INSERT INTO ATTENDANCE VALUES('" +attendanceActivity.time + "',"+
                    "" + attendanceActivity.period + ","+
                    "'" + registers.get(i) + "',"+
                    "" + sts + ");";
            AppBase.handler.execAction(qu);
            activity.finish();
        }
    }
}
