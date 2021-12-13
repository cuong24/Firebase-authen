package com.example.firebase_authen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firebase_authen.R;
import com.example.firebase_authen.repository.UserRepository;
import com.google.android.material.textfield.TextInputEditText;

public class SigninActivity extends AppCompatActivity {


    TextInputEditText signinRegEmail;
    TextInputEditText signinRegPassword;
    TextView tvRegisterHere;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        bindUI();
        addOnClick();
    }

    public void bindUI(){
        signinRegEmail = findViewById(R.id.signinRegEmail);
        signinRegPassword = findViewById(R.id.signinRegPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvRegisterHere = findViewById(R.id.tvRegisterHere);
    }

    public void addOnClick(){
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signin();
            }
        });

        tvRegisterHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SigninActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void signin() {
        UserRepository.validateUser(signinRegEmail.getText().toString(), signinRegPassword.getText().toString(), new UserRepository.validateUserCallBack() {
            @Override
            public void onCallBack(boolean isValidUser, boolean getAccounts) {
                if (isValidUser && getAccounts) {
                    Toast.makeText(SigninActivity.this, "Sign in successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (getAccounts) {
                    Toast.makeText(SigninActivity.this, "No account found", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SigninActivity.this, "Cannot get accounts. Check you internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}