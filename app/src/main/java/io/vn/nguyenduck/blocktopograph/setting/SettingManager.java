package io.vn.nguyenduck.blocktopograph.setting;

import java.util.ArrayList;
import java.util.List;

public final class SettingManager {

    private static final SettingManager INSTANCE = new SettingManager();
    private final ArrayList<ASetting> settings = new ArrayList<>();

    private SettingManager() {
    }

    public static SettingManager getInstance() {
        return INSTANCE;
    }

    public int size() {
        return settings.size();
    }

    @SuppressWarnings("unchecked")
    public List<ASetting> get() {
        return (List<ASetting>) settings.clone();
    }

//    public static void register(ASetting setting) {
//        INSTANCE.addSetting(setting);
//    }

    public ASetting get(int index) {
        return settings.get(index);
    }

    public ASetting get(String key) {
        for (ASetting setting : settings) {
            if (setting.getKey().equals(key)) return setting;
        }
        return null;
    }

    public void set(String key, Object value) {
        for (ASetting setting : settings) {
            if (setting.getKey().equals(key)) {
                setting.value = value;
                return;
            }
        }
    }

    public void add(ASetting setting) {
        settings.add(setting);
    }
}