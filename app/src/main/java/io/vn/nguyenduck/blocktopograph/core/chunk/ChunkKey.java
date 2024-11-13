package io.vn.nguyenduck.blocktopograph.core.chunk;

import androidx.annotation.NonNull;

import io.vn.nguyenduck.blocktopograph.core.world.Dimension;

public class ChunkKey {
    public final ChunkPosition position;
    public final ChunkTag tag;
    public final byte index;
    public final Dimension dimension;

    public ChunkKey(ChunkPosition position, ChunkTag tag) {
        this(position, tag, Dimension.Overworld);
    }

    public ChunkKey(ChunkPosition position, ChunkTag tag, Dimension dimension) {
        this(position, tag, dimension, 0);
    }

    public ChunkKey(ChunkPosition position, ChunkTag tag, Dimension dimension, int index) {
        this(position, tag, dimension, (byte) index);
    }

    public ChunkKey(ChunkPosition position, ChunkTag tag, Dimension dimension, byte index) {
        this.position = position;
        this.tag = tag;
        this.dimension = dimension;
        this.index = index;
    }

    @NonNull
    @Override
    public String toString() {
        var s = new StringBuilder().append("ChunkKey{").append(position);
        if (tag == ChunkTag.SubChunkPrefix) s.append(", ").append(index);
        s.append(", ").append(tag).append(", ").append(dimension).append("}");
        return s.toString();
    }
}