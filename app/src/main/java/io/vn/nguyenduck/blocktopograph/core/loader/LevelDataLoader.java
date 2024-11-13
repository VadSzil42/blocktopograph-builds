package io.vn.nguyenduck.blocktopograph.core.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import io.vn.nguyenduck.nbt.io.LEDataInputStream;
import io.vn.nguyenduck.nbt.io.LEDataOutputStream;
import io.vn.nguyenduck.nbt.io.NBTInputStream;
import io.vn.nguyenduck.nbt.io.NBTOutputStream;
import io.vn.nguyenduck.nbt.tags.CompoundTag;

public class LevelDataLoader implements Loader {

    private CompoundTag<?> cachedTag;
    private final File file;
    private final String path;
    private int version;

    public LevelDataLoader(File file) {
        this.file = file;
        this.path = file.getPath();
    }

    public CompoundTag<?> getLevelData() {
        return cachedTag;
    }

    public void setLevelData(CompoundTag<?> tag) {
        cachedTag = tag;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public void load() throws Exception {
        if (cachedTag != null) return;
        try (var reader = new LEDataInputStream(new FileInputStream(file));
             var dataStream = new NBTInputStream(reader)) {
            version = reader.readInt();
            reader.readInt();// skip size

            cachedTag = (CompoundTag<?>) dataStream.readTag();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save() throws Exception {
        if (cachedTag == null) return;
        try (var os = new LEDataOutputStream(new FileOutputStream(file));
             var byteArrayStream = new ByteArrayOutputStream();
             var nbtStream = new NBTOutputStream(byteArrayStream)) {
            os.writeInt(version);
            nbtStream.writeTag(cachedTag);

            os.writeInt(byteArrayStream.size());
            os.write(byteArrayStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}