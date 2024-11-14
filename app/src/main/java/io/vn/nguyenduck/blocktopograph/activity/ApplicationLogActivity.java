package io.vn.nguyenduck.blocktopograph.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.vn.nguyenduck.blocktopograph.R;

public class ApplicationLogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_log_activity);

        TextView log = findViewById(R.id.log);
        log.setText(getIntent().getStringExtra("stack_trace"));
    }
}