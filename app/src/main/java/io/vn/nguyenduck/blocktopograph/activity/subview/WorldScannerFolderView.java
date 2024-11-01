package io.vn.nguyenduck.blocktopograph.activity.subview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.vn.nguyenduck.blocktopograph.R;
import io.vn.nguyenduck.blocktopograph.activity.navigation.SettingFragment;
import io.vn.nguyenduck.blocktopograph.setting.ASetting;
import io.vn.nguyenduck.blocktopograph.setting.SettingManager;

@SuppressWarnings("unchecked")
public class WorldScannerFolderView implements SettingFragment.GetViewable {

    private final LinearLayout layout;
    private static final ASetting SETTING = SettingManager.getInstance().get("blocktopograph.world_scan_folders");
    private static final ArrayList<View> VIEWS = new ArrayList<>();

    public WorldScannerFolderView(LayoutInflater inflater, ViewGroup parent) {
        layout = (LinearLayout) inflater.inflate(R.layout.setting_default_world_scan_folder, parent, false);

        if (SETTING == null) return;

        TextView title = layout.findViewById(R.id.title);
        title.setText(SETTING.name);

        TextView description = layout.findViewById(R.id.description);
        description.setText(SETTING.description);
        description.setVisibility(SETTING.description.isEmpty() ? View.GONE : View.VISIBLE);

        RecyclerView listView = layout.findViewById(R.id.custom_folder);
        CustomFolderAdapter adapter = new CustomFolderAdapter();
        listView.setAdapter(adapter);

        ImageButton addButton = layout.findViewById(R.id.add_btn);
        addButton.setOnClickListener(v -> {
            if (!VIEWS.isEmpty()) {
                var last = VIEWS.get(VIEWS.size() - 1);
                EditText editText = last.findViewById(R.id.path);
                if (editText.getText().toString().equals("/sdcard")) return;
            }
            ((List<String>) SETTING.value).add("/sdcard");
            adapter.notifyItemInserted(VIEWS.size());
        });
    }

    private static class CustomFolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.path_item_in_world_scan_folder, parent, false)) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            View v = holder.itemView;
            EditText editText = v.findViewById(R.id.path);
            editText.setText(((List<String>) SETTING.value).get(position));
            editText.setOnEditorActionListener((l, id, e) -> {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) l.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(l.getWindowToken(), 0);
                    ((List<String>) SETTING.value).set(position, l.getText().toString());
                    l.clearFocus();
                    return true;
                }
                return false;
            });
            VIEWS.add(v);
            v.findViewById(R.id.delete_btn).setOnClickListener(l -> {
                ((List<String>) SETTING.value).remove(position);
                VIEWS.remove(position);
                notifyItemRemoved(position);
            });
        }

        @Override
        public int getItemCount() {
            return ((List<String>) SETTING.value).size();
        }
    }

    public View getView() {
        return layout;
    }
}