package com.redi.contact;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {
    private TextView logText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logText = findViewById(R.id.log_text);
        try {
            File logFile = new File(getExternalFilesDir(null), "log.txt");
            FileInputStream fis = new FileInputStream(logFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
            br.close();

            logText.setText(fileContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        logText = findViewById(R.id.log_text);
        try {
            File logFile = new File(getExternalFilesDir(null), "log.txt");
            FileInputStream fis = new FileInputStream(logFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
            br.close();

            logText.setText(fileContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void clearLog(View view) {
        try {
            File logFile = new File(getExternalFilesDir(null), "log.txt");
            if (logFile.exists()) {
                logFile.delete();
                logText.setText("debug");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}

