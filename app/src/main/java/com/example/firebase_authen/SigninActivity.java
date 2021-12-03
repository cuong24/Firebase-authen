package com.example.firebase_authen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase_authen.model.User;
import com.example.firebase_authen.model.UserProfile;
import com.example.firebase_authen.model.UserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SigninActivity extends AppCompatActivity {


    TextInputEditText signinRegEmail;
    TextInputEditText signinRegPassword;
    TextView tvRegisterHere;
    Button btnSignIn;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        signinRegEmail = findViewById(R.id.signinRegEmail);
        signinRegPassword = findViewById(R.id.signinRegPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvRegisterHere = findViewById(R.id.tvRegisterHere);

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
        db = FirebaseFirestore.getInstance();

        db.collection("users")
            .whereEqualTo("name", signinRegEmail.getText().toString())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.get("password").equals(signinRegPassword.getText().toString())) {
                                Toast.makeText(SigninActivity.this, "Sign in successfully", Toast.LENGTH_SHORT).show();
                                User user = new User();
                                user.setName((String) document.get("name"));
                                user.setPassword((String) document.get("password"));
                                user.setType(document.get("type") == "LEADER");
                                user.setUid(document.getId());
                                UserProfile.getInstance(user);
                                finish();
                            } else {
                                Toast.makeText(SigninActivity.this, "No account found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(SigninActivity.this, "Error signing in. Please check you internet", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}