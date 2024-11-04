package io.vn.nguyenduck.blocktopograph.activity.navigation;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.piegames.nbt.CompoundTag;
import io.vn.nguyenduck.blocktopograph.R;
import io.vn.nguyenduck.blocktopograph.activity.NBTEditorActivity;
import io.vn.nguyenduck.blocktopograph.setting.SettingManager;
import io.vn.nguyenduck.blocktopograph.utils.Utils;
import io.vn.nguyenduck.blocktopograph.world.WorldPreLoader;

public class WorldListFragment extends Fragment {

    private static List<String> WORLD_PATHS;

    private static final Map<String, WorldPreLoader> WORLDS = Collections.synchronizedMap(new TreeMap<>());
    private static final List<String> WORLD_PATH_SCANNED = Collections.synchronizedList(new ArrayList<>());
    private static final List<String> WORLD_PATH_ACCEPTED = Collections.synchronizedList(new ArrayList<>());
    private static WorldListAdapter ADAPTER;

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newWorkStealingPool(1);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.world_list_fragment, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.world_list);
        ADAPTER = new WorldListAdapter(this);
        recyclerView.setAdapter(ADAPTER);
        return v;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onResume() {
        super.onResume();
        var worldPaths = SettingManager.getInstance().get("blocktopograph.world_scan_folders");
        if (worldPaths != null) WORLD_PATHS = ((List<String>) worldPaths.value);
        EXECUTOR_SERVICE.submit(this::loadWorlds);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EXECUTOR_SERVICE.shutdown();
    }

    private void loadWorlds() {
        for (String path : WORLD_PATHS) {
            for (File file : Objects.requireNonNull(new File(path).listFiles())) {
                if (!file.isDirectory()) continue;
                String p = file.getPath();
                if (WORLD_PATH_SCANNED.contains(p)) continue;
                WORLD_PATH_SCANNED.add(p);
                if (WORLDS.containsKey(p)) {
                    WORLDS.get(p).update();
                } else {
                    var world = new WorldPreLoader(p);
                    if (world.getData() == null) continue;
                    WORLD_PATH_ACCEPTED.add(p);
                    WORLDS.put(p, world);
                    int index = WORLD_PATH_ACCEPTED.indexOf(p);
                    requireActivity().runOnUiThread(() -> {
                        ADAPTER.notifyItemChanged(index);
                    });
                }
            }
        }
    }

    private static class WorldListAdapter extends Adapter<ViewHolder> {

        private static final int GAMEMODE_CREATIVE = R.string.gamemode_creative;
        private static final int GAMEMODE_ADVENTURE = R.string.gamemode_adventure;
        private static final int GAMEMODE_SPECTATOR = R.string.gamemode_spectator;
        private static final int GAMEMODE_SURVIVAL = R.string.gamemode_survival;

        private final Fragment fragment;

        public WorldListAdapter(Fragment fragment) {
            this.fragment = fragment;
        }

        private WeakReference<ViewHolder> currentSelected = new WeakReference<>(null);

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.world_item, parent, false)) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            WorldPreLoader world = WORLDS.get(WORLD_PATH_ACCEPTED.get(position));
            View view = holder.itemView;
            View infoContainer = view.findViewById(R.id.world_info_container);
            View floatingAction = view.findViewById(R.id.floating_action);

            TextView play_btn = floatingAction.findViewById(R.id.play_btn);
            TextView nbt_editor_btn = floatingAction.findViewById(R.id.nbt_editor_btn);
            TextView info_btn = floatingAction.findViewById(R.id.info_btn);

            nbt_editor_btn.setOnClickListener(v -> {
                fragment.requireActivity().startActivity(new Intent(fragment.requireActivity(), NBTEditorActivity.class));
            });

            infoContainer.setOnClickListener(v -> {
                var selected = currentSelected.get();
                if (selected != null) selected.itemView
                        .findViewById(R.id.floating_action)
                        .setVisibility(View.GONE);
                if (holder.equals(selected)) {
                    currentSelected.clear();
                    floatingAction.setVisibility(View.GONE);
                } else {
                    currentSelected = new WeakReference<>(holder);
                    floatingAction.setVisibility(View.VISIBLE);
                }
            });

            assert world != null;
            CompoundTag data = (CompoundTag) world.getData();
            if (data == null) return;

            ImageView icon = view.findViewById(R.id.world_item_icon);
            Drawable iconDrawable = world.getIconDrawable();
            if (iconDrawable != null) icon.setImageDrawable(iconDrawable);
            else if (data.getIntValue("Generator").get() == 2)
                icon.setImageResource(R.drawable.world_preview_flat);
            else icon.setImageResource(R.drawable.world_preview_default);

            TextView name = view.findViewById(R.id.world_item_name);
            name.setText(world.getName());

            TextView gamemode = view.findViewById(R.id.world_item_gamemode);
            Integer gamemodeResId = switch (data.getByteValue("ForceGameType").get()) {
                case 1 -> GAMEMODE_CREATIVE;
                case 2 -> GAMEMODE_ADVENTURE;
                case 3 -> GAMEMODE_SPECTATOR;
                default -> GAMEMODE_SURVIVAL;
            };
            gamemode.setText(gamemodeResId);

            TextView experimental = view.findViewById(R.id.world_item_experimental);
            CompoundTag exp = data.getAsCompoundTag("experiments").get();
            if (exp.getValue().values().stream().allMatch(v -> (byte) v.getValue() == 0))
                experimental.setVisibility(View.GONE);

            TextView lastPlay = view.findViewById(R.id.world_item_last_play);
            Long time = data.getLongValue("LastPlayed").get();
            DateFormat formater = SimpleDateFormat.getDateInstance(2);
            lastPlay.setText(formater.format(new Date(time * 1000)));

            TextView size = view.findViewById(R.id.world_item_size);
            size.setText(Utils.translateSizeToString(Utils.getSizeOf(world.path)));
        }

        @Override
        public int getItemCount() {
            return WORLDS.size();
        }
    }
}