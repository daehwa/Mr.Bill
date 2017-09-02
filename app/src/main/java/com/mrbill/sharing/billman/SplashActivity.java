package com.mrbill.sharing.billman;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.billman.sharing.billman.R;

/**
 * Created by snote on 2016-07-15.
 */
public class SplashActivity extends AppCompatActivity{
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent intent=new Intent(SplashActivity.this, AddInfoActivity.class);
                intent.putExtra("key",false);
                startActivity(intent);

                finish();
            }
        },1100);
    }

}
