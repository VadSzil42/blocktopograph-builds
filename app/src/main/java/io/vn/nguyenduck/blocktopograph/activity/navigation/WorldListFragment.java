package io.vn.nguyenduck.blocktopograph.activity.navigation;

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
import java.util.function.Function;

import io.vn.nguyenduck.blocktopograph.R;
import io.vn.nguyenduck.blocktopograph.activity.subview.NBTEditorFragment;
import io.vn.nguyenduck.blocktopograph.setting.SettingManager;
import io.vn.nguyenduck.blocktopograph.utils.Utils;
import io.vn.nguyenduck.blocktopograph.world.WorldPreLoader;
import io.vn.nguyenduck.nbt.tags.CompoundTag;
import io.vn.nguyenduck.nbt.tags.LongTag;

public class WorldListFragment extends Fragment {

    private static List<String> WORLD_PATHS;

    private static Map<String, WorldPreLoader> WORLDS;
    private static List<String> WORLD_PATH_SCANNED;
    private static List<String> WORLD_PATH_ACCEPTED;
    private static WorldListAdapter ADAPTER;

    private static ExecutorService EXECUTOR_SERVICE;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WORLDS = Collections.synchronizedMap(new TreeMap<>());
        WORLD_PATH_SCANNED = Collections.synchronizedList(new ArrayList<>());
        WORLD_PATH_ACCEPTED = Collections.synchronizedList(new ArrayList<>());
        EXECUTOR_SERVICE = Executors.newWorkStealingPool(1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.world_list_fragment, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.world_list);
        ADAPTER = new WorldListAdapter((WorldPreLoader world) -> {
            var nbtEditorFragment = new NBTEditorFragment();
            nbtEditorFragment.setWorld(world);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_view, nbtEditorFragment)
                    .addToBackStack(null)
                    .commit();

            return null;
        });
        recyclerView.setAdapter(ADAPTER);
        return v;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onResume() {
        super.onResume();
        var worldPaths = SettingManager.getInstance().get("blocktopograph.world_scan_folders");
        if (worldPaths != null) WORLD_PATHS = ((List<String>) worldPaths.value);
        if (!EXECUTOR_SERVICE.isShutdown()) EXECUTOR_SERVICE.submit(this::loadWorlds);
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
                    if (world.getLevelData() == null) continue;
                    WORLD_PATH_ACCEPTED.add(p);
                    WORLDS.put(p, world);
                    requireActivity().runOnUiThread(() -> ADAPTER.notifyItemInserted(ADAPTER.getItemCount() - 1));
                }
            }
        }
    }

    private static class WorldListAdapter extends Adapter<ViewHolder> {

        private static final int GAMEMODE_CREATIVE = R.string.gamemode_creative;
        private static final int GAMEMODE_ADVENTURE = R.string.gamemode_adventure;
        private static final int GAMEMODE_SPECTATOR = R.string.gamemode_spectator;
        private static final int GAMEMODE_SURVIVAL = R.string.gamemode_survival;

        private final Function<WorldPreLoader, Void> callback;

        public WorldListAdapter(Function<WorldPreLoader, Void> onClickCallback) {
            this.callback = onClickCallback;
        }

        private WeakReference<ViewHolder> currentSelected = new WeakReference<>(null);

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.world_item, parent, false)) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            WorldPreLoader world = WORLDS.get(WORLD_PATH_ACCEPTED.get(position));
            View infoContainer = holder.infoContainer;
            View floatingAction = holder.floatingAction;

            TextView play_btn = floatingAction.findViewById(R.id.play_btn);
            TextView nbt_editor_btn = floatingAction.findViewById(R.id.nbt_editor_btn);
            TextView info_btn = floatingAction.findViewById(R.id.info_btn);

            nbt_editor_btn.setOnClickListener(v -> {
                callback.apply(world);
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
            var data = world.getLevelData();
            if (data == null) return;

            var icon = holder.icon;
            var iconDrawable = world.getIconDrawable();
            if (iconDrawable != null) icon.setImageDrawable(iconDrawable);
            else if (data.getValue("Generator").getValue().equals(2))
                icon.setImageResource(R.drawable.world_preview_flat);
            else icon.setImageResource(R.drawable.world_preview_default);

            holder.name.setText(world.getName());

            try {
                holder.gamemode.setText(switch ((byte) data.getValue("ForceGameType").getValue()) {
                    case 1 -> GAMEMODE_CREATIVE;
                    case 2 -> GAMEMODE_ADVENTURE;
                    case 3 -> GAMEMODE_SPECTATOR;
                    default -> GAMEMODE_SURVIVAL;
                });

                var exp = (CompoundTag<?>) data.getValue("experiments");
                if (exp.getValue().values().stream().allMatch(v -> v.getValue().equals(0)))
                    holder.experimental.setVisibility(View.GONE);

                var time = (LongTag) data.getValue("LastPlayed");
                DateFormat formater = SimpleDateFormat.getDateInstance(2);
                holder.lastPlay.setText(formater.format(new Date(time.getValue() * 1000)));
            } catch (Exception ignored) {}

            holder.size.setText(Utils.translateSizeToString(Utils.getSizeOf(world.file)));
        }

        @Override
        public int getItemCount() {
            return WORLDS.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        public final View infoContainer;
        public final View floatingAction;

        public final ImageView icon;
        public final TextView name;
        public final TextView gamemode;
        public final TextView experimental;
        public final TextView lastPlay;
        public final TextView size;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            infoContainer = itemView.findViewById(R.id.world_info_container);
            floatingAction = itemView.findViewById(R.id.floating_action);

            icon = itemView.findViewById(R.id.world_item_icon);
            name = itemView.findViewById(R.id.world_item_name);
            gamemode = itemView.findViewById(R.id.world_item_gamemode);
            experimental = itemView.findViewById(R.id.world_item_experimental);
            lastPlay = itemView.findViewById(R.id.world_item_last_play);
            size = itemView.findViewById(R.id.world_item_size);
        }
    }
}