package com.example.Genza999.myapplication;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by Edward on 5/31/2018.
 */

public class ReportActivity extends AppCompatActivity {
    Button bn;
    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate);
        bn = findViewById(R.id.button2);
        if (shouldAskPermissions()) {
            askPermissions();
        }


        bn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                File dbFile=getDatabasePath("ASSIST.sqlite");
                File exportDir = new File(Environment.getExternalStorageDirectory(), "");
                if (!exportDir.exists())
                {
                    exportDir.mkdirs();
                }

                File file = new File(exportDir, "attendanceReport.csv");
                try
                {
                    file.createNewFile();
                    CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                    SQLiteDatabase db = AppBase.handler.database/*getReadableDatabase()*/;
                    Cursor curCSV = db.rawQuery("SELECT * FROM ATTENDANCE",null);
                    csvWrite.writeNext(curCSV.getColumnNames());
                    while(curCSV.moveToNext())
                    {
                        //Which column you want to exprort
                        String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2)
                                ,curCSV.getString(3)};
                        csvWrite.writeNext(arrStr);
                    }
                    csvWrite.close();
                    curCSV.close();
                }
                catch(Exception sqlEx)
                {
                    Log.e("ReportActivity", sqlEx.getMessage(), sqlEx);
                }
            }
        });

    }

}

