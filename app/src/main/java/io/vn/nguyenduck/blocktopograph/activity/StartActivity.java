package io.vn.nguyenduck.blocktopograph.activity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION;
import static io.vn.nguyenduck.blocktopograph.Constants.BOGGER;
import static io.vn.nguyenduck.blocktopograph.utils.Utils.isAndroid11Up;
import static io.vn.nguyenduck.blocktopograph.utils.Utils.transferStream;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import io.vn.nguyenduck.blocktopograph.R;
import io.vn.nguyenduck.blocktopograph.setting.ASetting;
import io.vn.nguyenduck.blocktopograph.setting.SettingManager;

public class StartActivity extends AppCompatActivity {

    private final int STORAGE_PERMISSION_CODE = 0x7832;
//    private final int SHIZUKU_PERMISSION_CODE = 0xbfde;

    private boolean StoragePermission = false;
//    private boolean ShizukuPermission = false;
//    private boolean ShizukuInstalled = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

//        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        StoragePermission = StoragePermission || hasFileAccessPermission();
//        ShizukuInstalled = ShizukuInstalled || hasInstalledShizuku();
//        ShizukuPermission = ShizukuPermission || hasShizukuPermission();

        if (!StoragePermission) requestStoragePermission();
//        if (isAndroid11Up()) {
//            if (isRooted()) {
//                startMainActivity();
//            } else if (!ShizukuInstalled) {
//                showShizukuAppNeeded();
//            } else if (!Shizuku.pingBinder()) {
//                showNotRunningShizuku();
//            } else if (!ShizukuPermission) {
//                Shizuku.requestPermission(SHIZUKU_PERMISSION_CODE);
//            }
//            if (StoragePermission && ShizukuPermission) {
//                startMainActivity();
//            }
        else {
            loadSetting();
            startMainActivity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
    }

//    private boolean hasInstalledShizuku() {
//        try {
//            getPackageManager().getPackageInfo(SHIZUKU_PACKAGE_NAME, 0);
//            return true;
//        } catch (Exception ignored) {
//            return false;
//        }
//    }

//    private boolean hasShizukuPermission() {
//        return Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PERMISSION_GRANTED;
//    }

    private boolean hasFileAccessPermission() {
        if (isAndroid11Up()) {
            return Environment.isExternalStorageManager();
        } else {
            return (checkSelfPermission(WRITE_EXTERNAL_STORAGE) |
                    checkSelfPermission(READ_EXTERNAL_STORAGE)
            ) == PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (isAndroid11Up()) {
            startActivityIfNeeded(
                    new Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION),
                    STORAGE_PERMISSION_CODE
            );
        } else {
            requestPermissions(
                    new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE
            );
        }
    }

//    private void showShizukuAppNeeded() {
//        new AlertDialog.Builder(this)
//                .setTitle("Shizuku Not Installed!")
//                .setMessage("You need to install shizuku first")
//                .setPositiveButton("Open Google Play Store", (d, w) -> showShizukuInMarket())
//                .setNegativeButton("Exit", (d, w) -> System.exit(0))
//                .setCancelable(false)
//                .show();
//    }

//    private void showNotRunningShizuku() {
//        new AlertDialog.Builder(this)
//                .setTitle("Shizuku Not Running!")
//                .setMessage("You need to start shizuku and run it first")
//                .setPositiveButton("Open Shizuku", (d, w) -> openShizuku())
//                .setNegativeButton("Exit", (d, w) -> System.exit(0))
//                .setCancelable(false)
//                .show();
//    }

//    private void showShizukuInMarket() {
//        try {
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + SHIZUKU_PACKAGE_NAME)));
//        } catch (Exception ignored) {
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + SHIZUKU_PACKAGE_NAME)));
//        }
//    }

//    private void openShizuku() {
//        startActivity(getPackageManager().getLaunchIntentForPackage(SHIZUKU_PACKAGE_NAME));
//    }

//    private final Shizuku.OnRequestPermissionResultListener REQUEST_PERMISSION_RESULT_LISTENER = this::onRequestPermissionResult;

//    private void onRequestPermissionResult(int requestCode, int result) {
//        if (requestCode == SHIZUKU_PERMISSION_CODE)
//            ShizukuPermission = result == PERMISSION_GRANTED;
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            StoragePermission = grantResults.length > 0 &&
                    Arrays.stream(grantResults).allMatch(v -> v == PERMISSION_GRANTED);
        }
    }

    private void loadSetting() {
        File files = getExternalFilesDir(null);
        File setting = new File(files, "setting.json");
        if (!setting.exists()) {
            try (InputStream is = getResources().getAssets().open("setting.json");
                 OutputStream os = new FileOutputStream(setting)) {
                setting.createNewFile();
                transferStream(is, os);
            } catch (Exception e) {
                BOGGER.log(Level.SEVERE, "Failed to create setting file", e);
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(setting))) {
            String s = reader.lines().collect(Collectors.joining());
            JSONObject json = new JSONObject(s);
            var keys = getAllKeys(json);
            for (int i = 0; i < keys.size(); i++) {
                var key = keys.get(i).first;
                Object val = keys.get(i).second;
                if (val instanceof JSONArray v) {
                    val = new ArrayList<>();
                    for (int j = 0; j < v.length(); j++) {
                        ((List<Object>) val).add(v.opt(j));
                    }
                }
                Object finalVal = val;
                SettingManager.getInstance().add(new ASetting() {
                    @Override
                    public String getKey() {
                        return key;
                    }

                    @Override
                    public String getCategory() {
                        return "Blocktopograph";
                    }

                    @Override
                    public String getName() {
                        return "World Scan Folders";
                    }

                    @Override
                    public String getDescription() {
                        return "";
                    }

                    @Override
                    public Object getDefaultValue() {
                        return finalVal;
                    }
                });
            }
        } catch (Exception e) {
            BOGGER.log(Level.SEVERE, "Failed to load setting", e);
        }
    }

    private List<Pair<String, Object>> getAllKeys(JSONObject json) {
        List<Pair<String, Object>> result = new ArrayList<>();
        traverse(json, "", result);
        return result;
    }

    private void traverse(JSONObject jsonObject, String currentPath, List<Pair<String, Object>> result) {
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.opt(key);
            String path = currentPath.isEmpty() ? key : currentPath + "." + key;

            if (value instanceof JSONObject) {
                traverse((JSONObject) value, path, result);
            } else {
                result.add(new Pair<>(path, value));
            }
        }
    }
}