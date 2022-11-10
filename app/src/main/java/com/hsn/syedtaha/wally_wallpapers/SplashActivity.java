package com.hsn.syedtaha.wally_wallpapers;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private TextView tvSplash;
    private Animation animation_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        animation_bottom = AnimationUtils.loadAnimation(this, R.anim.text_animation);
        tvSplash = findViewById(R.id.idTVSplash);
        tvSplash.setAnimation(animation_bottom);



        Thread td = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch (Exception ex){
                    ex.printStackTrace();
                }finally {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            }

        }; td.start();
    }
}