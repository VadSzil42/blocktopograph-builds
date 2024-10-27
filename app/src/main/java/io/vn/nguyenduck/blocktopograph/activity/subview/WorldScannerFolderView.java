package io.vn.nguyenduck.blocktopograph.activity.subview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.vn.nguyenduck.blocktopograph.R;
import io.vn.nguyenduck.blocktopograph.activity.navigation.SettingFragment;

public class WorldScannerFolderView implements SettingFragment.getViewable {

    private final LinearLayout layout;

    public WorldScannerFolderView(LayoutInflater inflater, ViewGroup parent) {
        layout = (LinearLayout) inflater.inflate(R.layout.setting_default_world_scan_folder, parent, false);
        ListView listView = layout.findViewById(R.id.custom_folder);
        listView.setAdapter(new CustomFolderAdapter(listView));
    }

    private static class CustomFolderAdapter extends BaseAdapter {

        private List<String> defaultPaths = List.of("/sdcard/games/com.mojang/minecraftWorlds");
        private List<View> views = new ArrayList<>();

        public CustomFolderAdapter(ListView parent) {
            for (String path : defaultPaths) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.path_item_in_world_scan_folder, parent, false);
                ((TextView) layout.findViewById(R.id.path)).setText(path);
                views.add(layout);
            }
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return views.get(position);
        }
    }

    public View getView() {
        return layout;
    }
}