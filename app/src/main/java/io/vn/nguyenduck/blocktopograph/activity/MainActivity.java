package io.vn.nguyenduck.blocktopograph.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.vn.nguyenduck.blocktopograph.R;
import io.vn.nguyenduck.blocktopograph.setting.SettingManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_view);

        Fragment contentView = getSupportFragmentManager().findFragmentById(R.id.content_view);
        assert contentView != null;

        NavHostFragment navHostFragment = (NavHostFragment) contentView;
        navHostFragment.getViewLifecycleOwnerLiveData().observe(this, vLO -> {
            if (vLO == null) return;
            vLO.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
                switch (event) {
                    case ON_RESUME -> bottomNavigationView.setVisibility(View.VISIBLE);
                    case ON_PAUSE -> bottomNavigationView.setVisibility(View.GONE);
                }
            });
        });

        NavController navController = NavHostFragment.findNavController(contentView);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SettingManager.forceSave();
        SettingManager.shutdown();
    }
}