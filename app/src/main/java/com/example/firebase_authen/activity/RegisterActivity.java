package com.example.firebase_authen.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase_authen.R;
import com.example.firebase_authen.model.User;
import com.example.firebase_authen.repository.UserRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegisterActivity extends AppCompatActivity {

    TextInputEditText etRegEmail;
    TextInputEditText etRegPassword;
    TextView tvLoginHere;
    Button btnRegister;
    CheckBox checkboxLeader;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bindUI();
        addOnClick();
    }

    public void bindUI(){
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPass);
        tvLoginHere = findViewById(R.id.tvLoginHere);
        checkboxLeader = findViewById(R.id.checkBoxIsLeader);
        btnRegister = findViewById(R.id.btnRegister);
    }

    public void addOnClick(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        tvLoginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void register (){
        User user = new User();
        user.setName(etRegEmail.getText().toString());
        user.setPassword(etRegPassword.getText().toString());
        user.setType(checkboxLeader.isChecked());
        UserRepository.addUser(user, new UserRepository.addUserCallBack() {
            @Override
            public void onCallBack(boolean isSuccess) {
                if(isSuccess){
                    Toast.makeText(RegisterActivity.this, "New user created", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Cannot create new user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

