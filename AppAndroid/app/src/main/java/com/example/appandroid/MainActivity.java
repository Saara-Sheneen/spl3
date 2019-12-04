package com.example.appandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


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
}
