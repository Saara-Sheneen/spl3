package com.example.appandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.Calendar;
public class Main2Activity extends AppCompatActivity {

    private Button btnStart, btnEnd;
    private String userId = "";
    private TextView textView;
    private String ip = "";
    private String port = ":4747";
    private String appName = "DroidCam";
    private String packageName = "com.dev47apps.droidcam";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private SoundPool soundPool;
    private int sound;
    private OkHttpClient client;
    private String currentDateTimeString = "";
    private String alertCount = "";
    private int alertCounter = 0;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String BASE_URL = "http://10.100.101.21:3000/"; //"http://172.16.22.49:3000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        btnStart = findViewById(R.id.start_btn);
        btnEnd = findViewById(R.id.end_btn);
        textView = findViewById(R.id.textview);

        userId = getLocalIpAddress() + port;
        ip = getLocalIpAddress() + port;



        for (int i = 0; i < userId.length(); i++){
            if ( userId.charAt(i) == '.' ) userId = replace(userId, i, '-');
            if ( userId.charAt(i) == ':' ) userId = replace(userId, i, '_');
        }

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

        this.database = FirebaseDatabase.getInstance();
        this.myRef = database.getReference("user").child(userId);



        myRef.setValue("active");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (value.equals("sleepy")){
                    soundPool.play(sound, 1, 1, 0, 0, 1);
                    //DATE AND TIME
                    alertCounter = alertCounter + 1;
                    alertCount = "Alert Time ";
                    currentDateTimeString += alertCount + alertCounter + ": "
                            + DateFormat.getDateTimeInstance().format(new Date()) + "\n";

                    textView.setText(currentDateTimeString);
                    System.out.println(currentDateTimeString);
                    myRef.setValue("active");
                }
                Toast.makeText(Main2Activity.this, value, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean t = openApp(Main2Activity.this, appName, packageName);
                try {
                    Thread.sleep(0 * 1000);
                }
                catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                if (t) {
                    doRequest(ip);
                }
            }
        });
    }

    private void doRequest(String ip){
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
                Toast.makeText(Main2Activity.this, "Request Failed Try Again!!!", Toast.LENGTH_LONG)
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

    public void stop(View view){
        myRef.setValue("inactive");
        textView.setText("");
        currentDateTimeString = "";
        alertCounter = 0;

        //finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRef.removeValue();
        soundPool.release();
        //soundPool = null;
    }

    public String replace(String str, int index, char replace){
        if(str==null){
            return str;
        }else if(index<0 || index>=str.length()){
            return str;
        }
        char[] chars = str.toCharArray();
        chars[index] = replace;
        return String.valueOf(chars);
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    System.out.println("ip1--:" + inetAddress);
                    System.out.println("ip2--:" + inetAddress.getHostAddress());

                    /* for getting IPV4 format */
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {

                        String ip = inetAddress.getHostAddress();
                        System.out.println("ip---::" + ip);
                        // return inetAddress.getHostAddress().toString();
                        return ip;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return null;
    }

    public static boolean openApp(Context context, String appName, String packageName) {
        if (isAppInstalled(context, packageName))
            if (isAppEnabled(context, packageName)){
                context.startActivity(context.getPackageManager().getLaunchIntentForPackage(packageName));
                return true;
            }
            else {
                Toast.makeText(context, appName + " app is not enabled.", Toast.LENGTH_SHORT).show();
                return false;
            }
        else {
            Toast.makeText(context, appName + " app is not installed.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return false;
    }

    private static boolean isAppEnabled(Context context, String packageName) {
        boolean appStatus = false;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (ai != null) {
                appStatus = ai.enabled;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appStatus;
    }
}
