package com.hienthai.chatappnodejs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText edtYourName = findViewById(R.id.edtYourName);

        findViewById(R.id.btnJoinRoom).setOnClickListener(v -> {

        });

    }
}