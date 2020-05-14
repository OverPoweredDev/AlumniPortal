package com.omkarp.alumniportal;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    //declaration
    ActionBar actionBar;

    private FirebaseAuth mAuth;

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            //handle selection
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    actionBar.setTitle("Home");
                    //home fragment
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                    transaction1.replace(R.id.container, homeFragment, "");
                    transaction1.commit();
                    return true;

                case R.id.nav_profile:
                    actionBar.setTitle("Profile");
                    //profile fragment
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                    transaction2.replace(R.id.container, profileFragment, "");
                    transaction2.commit();
                    return true;

                case R.id.nav_users:
                    actionBar.setTitle("Chat");
                    //users fragment
                    UsersFragment usersFragment = new UsersFragment();
                    FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                    transaction3.replace(R.id.container, usersFragment, "");
                    transaction3.commit();
                    return true;

                case R.id.nav_post:
                    startActivity(new Intent(DashboardActivity.this, SendPostActivity.class));
                    finish();
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        actionBar = getSupportActionBar();

        //profileTV = findViewById(R.id.profileTV);
        BottomNavigationView navigationView = findViewById(R.id.nav_bottom);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        mAuth = FirebaseAuth.getInstance();

        //Fragment set to home by default
        actionBar.setTitle("Home");
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.replace(R.id.container, homeFragment, "");
        transaction1.commit();
    }

    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            //stay here
        } else {
            //goto mainactivity
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

}
