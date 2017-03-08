package com.example.hao.wxsign_student;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by HAO on 2016/7/26.
 */
public class WelcomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        /*
        * 欢迎页页面跳转
        * */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                String user_load = pref.getString("user", "");
                String password_load = pref.getString("password", "");
                Toast.makeText(WelcomeActivity.this, user_load, Toast.LENGTH_LONG).show();
                finish();
                if (user_load.equals("")) {

                    Intent intent = new Intent(WelcomeActivity.this, BangdingActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(WelcomeActivity.this, ChooseFuncActivity.class);
                    startActivity(intent);
                }
            }

        }, 1500);


    }
}