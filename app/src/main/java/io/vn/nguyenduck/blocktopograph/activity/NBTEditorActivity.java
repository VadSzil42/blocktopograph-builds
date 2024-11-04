package io.vn.nguyenduck.blocktopograph.activity;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.vn.nguyenduck.blocktopograph.R;
import io.vn.nguyenduck.blocktopograph.webserver.NBTEditorServer;

public class NBTEditorActivity extends AppCompatActivity {

    private final NBTEditorServer server = new NBTEditorServer(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nbt_editor_activity);

        WebView webView = findViewById(R.id.monaco_editor);
        var setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setDomStorageEnabled(true);
        setting.setDatabaseEnabled(true);
        webView.loadUrl("http://localhost:4723");
    }
}