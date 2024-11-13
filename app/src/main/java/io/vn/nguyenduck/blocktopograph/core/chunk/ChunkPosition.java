package io.vn.nguyenduck.blocktopograph.core.chunk;

import androidx.annotation.NonNull;

import io.vn.nguyenduck.blocktopograph.core.world.Position;

public class ChunkPosition {

    public final int x;
    public final int z;

    public ChunkPosition(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public static ChunkPosition from(Position position) {
        return new ChunkPosition(position.x >> 4, position.z >> 4);
    }

    public static ChunkPosition from(int x, int y, int z) {
        return new ChunkPosition(x >> 4, z >> 4);
    }

    @NonNull
    @Override
    public String toString() {
        return x + " " + z;
    }
}