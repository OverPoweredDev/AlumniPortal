package com.omkarp.alumniportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/* TODO: integrate registering into the main activity */

public class RegisterActivity extends AppCompatActivity {

    /* declaration */
    EditText emailET, passET;
    Button registerBTN;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actbar = getSupportActionBar();
        actbar.setTitle("Create Account");
        actbar.setDisplayHomeAsUpEnabled(true);
        actbar.setDisplayShowHomeEnabled(true);

        //initialise
        emailET = findViewById(R.id.emailET);
        passET = findViewById(R.id.passET);
        registerBTN = findViewById(R.id.registerbtn);
        progressDialog = new ProgressDialog(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        progressDialog.setMessage("Registering");

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get email,pass
                String email = emailET.getText().toString().trim();
                String password = passET.getText().toString().trim();
                //valid
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailET.setError("Invalid email");
                } else if (password.length() < 6) {
                    passET.setError("Password must be more than 6 characters");
                } else {
                    registeruser(email, password);
                }
            }
        });

    }

    private void registeruser(String email, String password) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

                            String email = user.getEmail();
                            String uid = user.getUid();
                            HashMap<Object, String> hashmap = new HashMap<>();

                            //when user is registered put data in hashmap
                            hashmap.put("email", email);
                            hashmap.put("uid", uid);
                            hashmap.put("name", "");
                            hashmap.put("phone", "");
                            hashmap.put("profilePicture", "");
                            hashmap.put("yearPassedOut", "");

                            //firebase instance
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                            //store user data in Users
                            DatabaseReference reference = firebaseDatabase.getReference("Users");

                            //put data from Hashmap to Database
                            reference.child(uid).setValue(hashmap);

                            Toast.makeText(RegisterActivity.this, "Registed \n" + user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //go back
        return super.onSupportNavigateUp();
    }
}
