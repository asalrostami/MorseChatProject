package com.example.asal.morsechatproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
        //hold display screen for 3 second
        Thread thread = new Thread()

        {
            @Override
            public void run() {
                try{
                    sleep(3000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    Intent main_page_intent = new Intent(WelcomActivity.this,MainActivity.class);
                    startActivity(main_page_intent);

                }

            }
        };
        thread.start();

    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
