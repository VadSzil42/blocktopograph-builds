package io.vn.nguyenduck.blocktopograph.activity.navigation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import io.vn.nguyenduck.blocktopograph.R;
import io.vn.nguyenduck.blocktopograph.activity.subview.WorldScannerFolderView;
import io.vn.nguyenduck.blocktopograph.setting.SettingManager;

public class SettingFragment extends Fragment {

    private SettingAdapter adapter;
    private final SettingManager MANAGER = SettingManager.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListView v = (ListView) inflater.inflate(R.layout.setting_fragment, container, false);
        if (adapter == null) adapter = new SettingAdapter(v);
        v.setAdapter(adapter);
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        SettingManager.forceLoad();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        SettingManager.forceSave();
    }

    @Override
    public void onStart() {
        super.onStart();
        SettingManager.forceLoad();
    }

    @Override
    public void onPause() {
        super.onPause();
        SettingManager.forceSave();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SettingManager.shutdown();
    }

    private static class SettingAdapter extends BaseAdapter {

        private final ArrayList<Object> views = new ArrayList<>();

        public SettingAdapter(ListView parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            views.add(new WorldScannerFolderView(inflater, parent));
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object getItem(int position) {
            return views.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return ((GetViewable) views.get(position)).getView();
        }
    }

    public interface GetViewable {
        View getView();
    }
}