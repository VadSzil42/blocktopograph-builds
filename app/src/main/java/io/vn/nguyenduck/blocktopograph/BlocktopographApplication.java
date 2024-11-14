package io.vn.nguyenduck.blocktopograph;

import android.app.Application;
import android.content.Intent;

import java.util.StringJoiner;

import io.vn.nguyenduck.blocktopograph.activity.ApplicationLogActivity;

public class BlocktopographApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            var intent = new Intent(this, ApplicationLogActivity.class);
            intent.putExtra("stack_trace", getStackTrace(throwable));
            startActivity(intent);
        });
    }

    private String getStackTrace(Throwable stackTrace) {
        var stack = stackTrace.getStackTrace();
        StringJoiner j = new StringJoiner("\n");
        for (StackTraceElement element : stack) {
            j.add(element.toString());
        }
        return j.toString();
    }
}