package com.help.android.miscellaneous;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.help.android.R;

public class EmptyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
    }
}
