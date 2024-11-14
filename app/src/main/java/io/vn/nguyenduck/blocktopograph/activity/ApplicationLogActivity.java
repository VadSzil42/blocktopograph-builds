package io.vn.nguyenduck.blocktopograph.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

import io.vn.nguyenduck.blocktopograph.R;

public class ApplicationLogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_log_activity);

        LinearLayout logContainer = findViewById(R.id.log_container);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        String s = getIntent().getStringExtra("stack_trace");
        ArrayList<String> arr = new ArrayList<>(Arrays.asList(s.split("\n")));

        for (String line : arr) {
            TextView v = (TextView) inflater.inflate(R.layout.non_width_text_view, logContainer, false);
            v.setText(line);
            logContainer.addView(v);
        }

        SharedPreferences prefs = getSharedPreferences("crash_info", MODE_PRIVATE);
        prefs.edit().remove("stack_trace").apply();
    }
}