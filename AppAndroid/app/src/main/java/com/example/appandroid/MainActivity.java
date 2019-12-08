package com.example.appandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


public class MainActivity extends AppCompatActivity {

    TextView txtIp;
    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtIp = findViewById(R.id.editTextIp);
        btnStart = findViewById(R.id.btnStart);



    }

    public void onStartClick(View view){
        /*
        Button btn = (Button) view;
        System.out.println(btn.getText());
        String ip = txtIp.getText().toString();
        String _ip = txtIp.getText().toString();

        for (int i = 0; i < ip.length(); i++){
            if ( ip.charAt(i) == '.' ) ip = replace(ip, i, '-');
            if ( ip.charAt(i) == ':' ) ip = replace(ip, i, '_');
        }

        if (!ip.equals("")){
            Intent intent = new Intent(MainActivity.this, AlertActivity.class);
            intent.putExtra("ip", _ip);
            intent.putExtra("userId", ip);
            startActivity(intent);
        }

         */
        String appName = "DroidCam";
        String packageName = "com.dev47apps.droidcam";
        String port = ":4747";
        if (/*openApp(MainActivity.this, appName, packageName)*/ true) {
            Toast.makeText(MainActivity.this, getLocalIpAddress(), Toast.LENGTH_LONG)
                    .show();
            String ip = getLocalIpAddress() + port;
            String _ip = getLocalIpAddress() + port;

            for (int i = 0; i < ip.length(); i++){
                if ( ip.charAt(i) == '.' ) ip = replace(ip, i, '-');
                if ( ip.charAt(i) == ':' ) ip = replace(ip, i, '_');
            }

            if (!ip.equals("")){
                Intent intent = new Intent(MainActivity.this, AlertActivity.class);
                intent.putExtra("ip", _ip);
                intent.putExtra("userId", ip);
                startActivity(intent);
            }
        }
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
