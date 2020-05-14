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

public class LoginActivity extends AppCompatActivity {

    //declaration
    EditText emailET, passET;
    Button loginBTN;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actbar = getSupportActionBar();
        actbar.setTitle("Login");

        /* enable back btn */
        actbar.setDisplayHomeAsUpEnabled(true);
        actbar.setDisplayShowHomeEnabled(true);

        /* initialise */
        emailET = findViewById(R.id.emailET);
        passET = findViewById(R.id.passET);
        loginBTN = findViewById(R.id.loginbtn);
        progressDialog = new ProgressDialog(this);

        /* Initialize Firebase Auth */
        mAuth = FirebaseAuth.getInstance();

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get email,pass
                String email = emailET.getText().toString().trim();
                String password = passET.getText().toString().trim();

                //validate
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailET.setError("Invalid email");
                } else if (password.length() < 6) {
                    passET.setError("Password must be more than 6 characters");
                } else {
                    loginUser(email, password);
                }
            }
        });

        progressDialog.setMessage("Logging you in");
    }

    private void loginUser(String email, String password) {
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //go back
        return super.onSupportNavigateUp();
    }
}
