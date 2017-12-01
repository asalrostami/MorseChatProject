package com.example.asal.morsechatproject;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.asal.morsechatproject.Model.TabsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FirebaseAuth mFirebaseAuth;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsPagerAdapter mTabsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mToolbar  = (Toolbar)findViewById(R.id.mainPage_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Morse Chat");

        mViewPager = (ViewPager)findViewById(R.id.main_tabs_pager);

        mTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsPagerAdapter);

        mTabLayout  = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser == null) {
           LogoutUser();
        }
    }

    private void LogoutUser() {
        Intent intent = new Intent(MainActivity.this, StartPageActivity.class);
        // doesn't allow the user to go to start page without login
        // and doesn't allow the user go back to main activity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    //for creating menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.main_menu,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_logout_button)
        {
            mFirebaseAuth.signOut();

            LogoutUser();
        }
        return true;
    }
}
