package com.example.appandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AlertActivity extends AppCompatActivity {

    private String userId;
    private String ip;
    private Button buttonStop;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private SoundPool soundPool;
    private int sound;
    private OkHttpClient client;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String BASE_URL = "http://172.16.20.107:3000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        Bundle bundle = getIntent().getExtras();
        buttonStop = findViewById(R.id.btnStop);

        client = new OkHttpClient();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(6)
                .setAudioAttributes(audioAttributes)
                .build();

        sound = soundPool.load(this, R.raw.alarm, 1);


        if (bundle != null) {

            this.userId = bundle.getString("userId");
            this.ip = bundle.getString("ip");

            Toast.makeText(AlertActivity.this, this.userId, Toast.LENGTH_LONG).show();

            this.database = FirebaseDatabase.getInstance();
            this.myRef = database.getReference("user").child(this.userId);

            myRef.setValue("active");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    if (value.equals("sleepy")){
                        soundPool.play(sound, 1, 1, 0, 0, 1);
                        myRef.setValue("active");
                    }
                    Toast.makeText(AlertActivity.this, value, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                }
            });
            doRequest(ip);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(6)
                .setAudioAttributes(audioAttributes)
                .build();
        sound = soundPool.load(this, R.raw.alarm, 1);
    }

    private void doRequest(String ip){
        try {
            Thread.sleep(10 * 1000);
        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        System.out.println(ip);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ip", ip);
            jsonObject.put("type", "2");
        } catch (JSONException e) {
            System.out.println(e.toString());
        }
        String jsonString = jsonObject.toString();
        RequestBody body = RequestBody.create(JSON, jsonString);
        String url = BASE_URL + "";
        Request request = new Request.Builder()
                .header("X-Client-Type", "Android")
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(AlertActivity.this, "Request Failed Try Again!!!", Toast.LENGTH_LONG)
                        .show();
                System.out.println(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res;
                try {
                    res = response.body().toString();
                    System.out.println(res);
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void onStopClick(View view){
        myRef.setValue("inactive");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRef.setValue("inactive");
        soundPool.release();
        //soundPool = null;
    }

}
