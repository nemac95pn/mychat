package com.example.nemanja.mychat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar; // ovde sam promenio import: android.widget.Toolbar; u android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;

    private Button LoginButton;
    private EditText LoginEmail;
    private EditText LoginPassword;
    private ProgressDialog loadingBar;

//  @SuppressLint("RestrictedApi") //impot nije hteo da radi bez ovoga


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth =FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign in");

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true); // bila greska, moralo da se doda ovo: @SuppressLint("RestrictedApi") da bi radilo.
        LoginButton = (Button) findViewById(R.id.login_button);
        LoginEmail = (EditText) findViewById(R.id.login_email);
        LoginPassword = (EditText) findViewById(R.id.login_password);
        loadingBar = new ProgressDialog(this);


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String email = LoginEmail.getText().toString();
                String password = LoginPassword.getText().toString();

                LoginUserAccount(email, password);
            }
        });
    }

    private void LoginUserAccount(String email, String password)
    {
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(LoginActivity.this, "Please, write your Email.", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(LoginActivity.this, "Please, write your Password.", Toast.LENGTH_SHORT).show();
        }
        else
            {
                loadingBar.setTitle("Login Account");
                loadingBar.setMessage("Please wait, we are signing up for you account");
                loadingBar.show();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Wrong Email or Password! Please write your valid Email and Password.", Toast.LENGTH_SHORT).show();
                        }

                        loadingBar.dismiss();
                    }
                });
            }
    }

/*
    private void setSupportActionBar(Toolbar mToolbar) {
    } //ovo je za: setSupportActionBar(mToolbar) nije hteo da radi
*/

}
