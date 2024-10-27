package io.vn.nguyenduck.blocktopograph.activity.navigation;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.piegames.nbt.CompoundTag;
import io.vn.nguyenduck.blocktopograph.R;
import io.vn.nguyenduck.blocktopograph.setting.SettingManager;
import io.vn.nguyenduck.blocktopograph.utils.Utils;
import io.vn.nguyenduck.blocktopograph.world.WorldPreLoader;

public class WorldListFragment extends Fragment {

    private static List<String> WORLD_PATHS;

    private static final Map<String, WorldPreLoader> WORLDS = new TreeMap<>();
    private static final List<String> WORLD_PATH = new ArrayList<>();
    private static final WorldListAdapter ADAPTER = new WorldListAdapter();

    private static ExecutorService EXECUTOR_SERVICE;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EXECUTOR_SERVICE = Executors.newWorkStealingPool();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.world_list_fragment, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.world_list);
        recyclerView.setAdapter(ADAPTER);
        return v;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onResume() {
        super.onResume();
        var worldPaths = SettingManager.getInstance().get("blocktopograph.world_scan_folders");
        if (worldPaths != null) WORLD_PATHS = (List<String>) worldPaths.value;
        EXECUTOR_SERVICE.submit(this::loadWorlds);
    }

    private void loadWorlds() {
        WORLD_PATH.clear();
        for (String path : WORLD_PATHS) {
            for (File file : Objects.requireNonNull(new File(path).listFiles())) {
                if (!file.isDirectory()) continue;
                String p = file.getPath();
                WORLD_PATH.add(p);
                if (!WORLDS.containsKey(p)) {
                    WORLDS.put(p, new WorldPreLoader(p));
                    requireActivity().runOnUiThread(() ->
                            ADAPTER.notifyItemChanged(WORLD_PATH.indexOf(p))
                    );
                } else Objects.requireNonNull(WORLDS.get(p)).update();
            }
        }
    }

    private static class WorldListAdapter extends Adapter<ViewHolder> {

        private static final int GAMEMODE_CREATIVE = R.string.gamemode_creative;
        private static final int GAMEMODE_ADVENTURE = R.string.gamemode_adventure;
        private static final int GAMEMODE_SPECTATOR = R.string.gamemode_spectator;
        private static final int GAMEMODE_SURVIVAL = R.string.gamemode_survival;

        private ViewHolder currentSelected = null;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.world_item, parent, false)) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            WorldPreLoader world = WORLDS.get(WORLD_PATH.get(position));
            View view = holder.itemView;

            View floatingAction = view.findViewById(R.id.floating_action);

            view.setOnClickListener(v -> {
                if (currentSelected != null)
                    currentSelected.itemView.findViewById(R.id.floating_action).setVisibility(View.GONE);
                if (holder.equals(currentSelected)) {
                    currentSelected = null;
                    floatingAction.setVisibility(View.GONE);
                } else {
                    currentSelected = holder;
                    floatingAction.setVisibility(View.VISIBLE);
                }
            });

            assert world != null;
            CompoundTag data = (CompoundTag) world.getData();

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