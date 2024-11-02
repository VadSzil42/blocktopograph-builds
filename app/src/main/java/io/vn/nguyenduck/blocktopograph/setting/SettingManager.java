package io.vn.nguyenduck.blocktopograph.setting;

import static io.vn.nguyenduck.blocktopograph.Constants.BOGGER;

import android.content.Context;
import android.util.Pair;

import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.logging.Level;

public final class SettingManager {

    private static Context CONTEXT = null;
    private static SettingManager INSTANCE = null;

    private static final ArrayList<ASetting> settings = new ArrayList<>();
    private static final HashMap<String, Integer> path_map = new HashMap<>();

    private static final ExecutorService loaderPool = Executors.newWorkStealingPool();
    private static final ExecutorService saverPool = Executors.newWorkStealingPool();

    private SettingManager() {
    }

    public static void initialize(Context context) {
        if (INSTANCE != null) return;
        CONTEXT = context.getApplicationContext();
        INSTANCE = new SettingManager();
    }

    public static SettingManager getInstance() {
        if (INSTANCE == null) throw new IllegalStateException("SettingManager is not initialized");
        return INSTANCE;
    }

    public int size() {
        return settings.size();
    }

    public static void register(String path, Object defaultValue, String name, String description) {
        register(path, defaultValue, name, description, null, null);
    }

    public static void register(String path, Object defaultValue, String name, String description, Function<Object, String> toString, Function<String, Object> fromString) {
        path_map.put(path, settings.size());
        settings.add(new ASetting(path, defaultValue, name, description, toString, fromString));
    }

    @SuppressWarnings("unchecked")
    public List<ASetting> get() {
        return (List<ASetting>) settings.clone();
    }

    public ASetting get(int index) {
        return settings.get(index);
    }

    public ASetting get(String key) {
        for (ASetting setting : settings) {
            if (setting.path.equals(key)) return setting;
        }
        return null;
    }

    public void set(String key, Object value) {
        for (ASetting setting : settings) {
            if (setting.path.equals(key)) {
                setting.value = value;
                return;
            }
        }
    }

    public static void forceLoad() {
        if (!loaderPool.isShutdown()) loaderPool.submit(SettingManager::load);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void load() {
        File setting = new File(CONTEXT.getExternalFilesDir(null), "setting.json");
        if (!setting.exists()) return;

        try (Reader reader = new FileReader(setting)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder s = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                s.append(line);
            }
            if (s.toString().isEmpty()) return;
            JSONObject json = new JSONObject(s.toString());
            var keys = getAllKeys(json);
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i).first;
                Object val = keys.get(i).second;
                if (path_map.containsKey(key)) {
                    var index = path_map.get(key);
                    assert index != null;
                    var aSetting = settings.get(index);
                    aSetting.value = aSetting.fromString.apply(val.toString());
                }
            }
        } catch (Exception e) {
            BOGGER.log(Level.SEVERE, "Failed to load setting", e);
        }
    }

    public static void forceSave() {
        if (!saverPool.isShutdown()) saverPool.submit(SettingManager::save);
    }

    private static void save() {
        File setting = new File(CONTEXT.getExternalFilesDir(null), "setting.json");

        try {
            if (!setting.exists()) setting.createNewFile();
        } catch (Exception ignored) {
        }

        try (Writer write = new FileWriter(setting)) {
            JSONObject json = new JSONObject();
            for (ASetting v : settings) {
                JSONObject json2 = json;
                var path = v.path;
                var spl = path.split("\\.");
                for (int i = 0; i < spl.length - 1; i++) {
                    if (!json2.has(spl[i])) json2.put(spl[i], new JSONObject());
                    json2 = json2.getJSONObject(spl[i]);
                }
                json2.put(spl[spl.length - 1], v.toString.apply(v.value));
            }
            write.write(json.toString());
        } catch (Exception e) {
            BOGGER.log(Level.SEVERE, "Failed to save setting", e);
        }
    }

    public static void shutdown() {
        if (!loaderPool.isShutdown()) loaderPool.shutdown();
        if (!saverPool.isShutdown()) saverPool.shutdown();
    }

    private static List<Pair<String, Object>> getAllKeys(JSONObject json) {
        List<Pair<String, Object>> result = new ArrayList<>();
        traverse(json, "", result);
        return result;
    }

    private static void traverse(JSONObject jsonObject, String currentPath, List<Pair<String, Object>> result) {
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