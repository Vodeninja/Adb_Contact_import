package com.redi.contact;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public class MainActivity extends AppCompatActivity {
    private TextView logText;
    private Handler handler;
    private int delay = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        startRepeatingTask();
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
    private void addToLogIp(String message) {
        try {
            File logFile = new File(getExternalFilesDir(null), "ip.txt");
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            PrintWriter writer = new PrintWriter(logFile);
            writer.print(message);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

            public interface IpifyService {
        @GET("get_ip.php")
        Call<String> getIp();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            makeRequest();
            handler.postDelayed(this, delay);
        }
    };

    public void startRepeatingTask() {
        runnable.run();
    }

    private void makeRequest() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit ret = new Retrofit.Builder()
                .baseUrl("https://kutovoy.vodeninja.ru/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        IpifyService service = ret.create(IpifyService.class);
        Call<String> call = service.getIp();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String ip = response.body().toString();
                    //Toast.makeText(getApplicationContext(), ip, Toast.LENGTH_LONG);
                    addToLogIp(ip);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                addToLogIp("");
            }
        });
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

