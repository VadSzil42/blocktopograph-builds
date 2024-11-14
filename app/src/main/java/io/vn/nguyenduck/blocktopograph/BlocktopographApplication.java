package io.vn.nguyenduck.blocktopograph;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.PrintWriter;
import java.io.StringWriter;

import io.vn.nguyenduck.blocktopograph.activity.ApplicationLogActivity;

public class BlocktopographApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            SharedPreferences preferences = getSharedPreferences("crash_info", MODE_PRIVATE);
            preferences.edit().putString("stack_trace", "true").apply();

            new Thread(() -> {
                Intent intent = new Intent(getApplicationContext(), ApplicationLogActivity.class);
                intent.putExtra("stack_trace", getStackTrace(throwable));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                System.exit(2);
            }).start();
        });
    }

    private static final String[] allowedPackages = {
            "io.vn.nguyenduck.blocktopograph",
            "io.vn.nguyenduck.nbt"
    };

    private String getStackTrace(Throwable stackTrace) {
        var writer = new StringWriter();
        stackTrace.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}