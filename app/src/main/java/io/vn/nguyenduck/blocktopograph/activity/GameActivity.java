package io.vn.nguyenduck.blocktopograph.activity;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;

import io.vn.nguyenduck.blocktopograph.core.Blocktopograph;

public class GameActivity extends AndroidApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.initialize(new Blocktopograph());
    }
}