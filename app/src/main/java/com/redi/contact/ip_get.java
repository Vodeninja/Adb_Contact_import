package com.redi.contact;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;





public class ip_get extends AppCompatActivity {
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

    private Handler handler;
    private int delay = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_get);

        handler = new Handler();
        startRepeatingTask();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            makeRequest();
            handler.postDelayed(this, delay);
        }
    };

    void startRepeatingTask() {
        runnable.run();
    }

    void stopRepeatingTask() {
        handler.removeCallbacks(runnable);
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
                    //Toast.makeText(getApplicationContext(), ip, Toast.LENGTH_LONG).show();
                    addToLogIp(ip);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                addToLogIp("");
                //Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
