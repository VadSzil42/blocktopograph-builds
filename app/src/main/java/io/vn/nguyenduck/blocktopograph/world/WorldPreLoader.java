package io.vn.nguyenduck.blocktopograph.world;

import static io.vn.nguyenduck.blocktopograph.Constants.BOGGER;
import static io.vn.nguyenduck.blocktopograph.Constants.WORLD_LEVELNAME_FILE;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.logging.Level;

import io.vn.nguyenduck.blocktopograph.core.loader.LevelDataLoader;
import io.vn.nguyenduck.nbt.tags.CompoundTag;

public class WorldPreLoader {

    public final File file;
    public final String path;
    public File icon;
    private File data;
    private LevelDataLoader levelData;

    public WorldPreLoader(String worldPath) {
        file = new File(worldPath);
        path = file.getPath();
    }

    public void update() {
        icon = fetchIcon();
        data = fetchWorldData();
    }

    public boolean hasData() {
        return new File(file, "level.dat").exists();
    }

    @Nullable
    private File fetchIcon() {
        if (icon != null) return icon;
        var i = Arrays.stream(file.listFiles((v, n) -> n.startsWith("world_icon"))).findFirst();
        return i.orElse(null);
    }

    @Nullable
    private File fetchWorldData() {
        if (data != null) return data;
        var i = Arrays.stream(file.listFiles((v, n) -> n.equals("level.dat"))).findFirst();
        return i.orElse(null);
    }

    @Nullable
    public Drawable getIconDrawable() {
        File iconFile = fetchIcon();
        if (iconFile == null) return null;
        Bitmap icon = BitmapFactory.decodeFile(iconFile.getPath());
        return new BitmapDrawable(Resources.getSystem(), icon);
    }

    public String getName() {
        File levelNameFile = new File(path, WORLD_LEVELNAME_FILE);
        if (!levelNameFile.exists()) return file.getName();
        try (var reader = new BufferedReader(new FileReader(levelNameFile))) {
            return reader.readLine();
        } catch (Exception e) {
            BOGGER.log(Level.SEVERE, "", e);
        }
        return "";
    }

    public CompoundTag<?> getLevelData() {
        if (data == null) return null;
        if (levelData == null) levelData = new LevelDataLoader(data);
        try {
            levelData.load();
        } catch (Exception e) {
            BOGGER.log(Level.SEVERE, "", e);
        }
        return levelData.getLevelData();
    }

    public void saveLevelData() throws Exception {
        levelData.save();
    }
}