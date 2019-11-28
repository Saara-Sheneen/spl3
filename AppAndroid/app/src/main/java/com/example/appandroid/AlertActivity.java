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

public class AlertActivity extends AppCompatActivity {

    private String userId;
    private Button buttonStop;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private SoundPool soundPool;
    private int sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        Bundle bundle = getIntent().getExtras();
        buttonStop = findViewById(R.id.btnStop);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(6)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }

        sound = soundPool.load(this, R.raw.alarm, 1);


        if (bundle != null) {

            this.userId = bundle.getString("ip");

            Toast.makeText(AlertActivity.this, this.userId, Toast.LENGTH_LONG).show();

            this.database = FirebaseDatabase.getInstance();
            this.myRef = database.getReference("user").child(this.userId);

            myRef.setValue("active");

            // Read from the database
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    if (value.equals("sleepy")){
                        soundPool.play(sound, 1, 1, 0, 0, 1);
                        //soundPool.autoPause();
                        myRef.setValue("active");
                    }
                    Toast.makeText(AlertActivity.this, value, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                }
            });
        }

    }



    public void onStopClick(View view){
        soundPool.play(sound, 1, 1, 0, 0, 1);

        myRef.setValue("inactive");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }

}
