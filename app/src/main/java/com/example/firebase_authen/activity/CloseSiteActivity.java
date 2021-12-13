package com.example.firebase_authen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firebase_authen.R;

public class CloseSiteActivity extends AppCompatActivity {
    private String siteID;
    private Button confirmBtn, cancelBtn;
    private EditText noOfPositiveCase, noOfTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_site);
        bindUI();
        addOnClick();
        Intent intent = getIntent();
        siteID = (String) intent.getExtras().get("siteID");
    }

    public void bindUI() {
        confirmBtn = findViewById(R.id.confirm_closeSite);
        cancelBtn = findViewById(R.id.cancel_closeSite);
        noOfPositiveCase = findViewById(R.id.noOfPositiveCase_closeSite);
        noOfTest = findViewById(R.id.noOfTest_closeSite);
    }

    public void addOnClick() {
        confirmBtn.setOnClickListener(view -> confirm());
        cancelBtn.setOnClickListener(view -> cancel());
    }

    public void confirm() {
        if (!noOfPositiveCase.getText().toString().equals("") || !noOfTest.getText().toString().equals("")) {
            Intent intent = new Intent(CloseSiteActivity.this, SiteDetailActivity.class);
            intent.putExtra("noOfPositiveCase", Integer.parseInt(noOfPositiveCase.getText().toString()));
            intent.putExtra("noOfTest", Integer.parseInt(noOfTest.getText().toString()));
            setResult(RESULT_OK, intent);
            finish();
        } else {
            noOfPositiveCase.setError("Number of Positive case is required!");
            noOfTest.setError("Number of test is required!");
        }
    }

    public void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
}