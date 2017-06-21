package com.example.android.ambulancedriverapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

public class details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        TextView si = (TextView)findViewById(R.id.Si);
        TextView no = (TextView)findViewById(R.id.No);
        TextView ti = (TextView)findViewById(R.id.Ti);
        TextView start = (TextView)findViewById(R.id.Start);
        TextView end = (TextView)findViewById(R.id.End);
        Button B = (Button)findViewById(R.id.Location);
    }
}
