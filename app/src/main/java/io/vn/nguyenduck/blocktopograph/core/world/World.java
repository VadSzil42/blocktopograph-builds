package io.vn.nguyenduck.blocktopograph.core.world;

import static io.vn.nguyenduck.blocktopograph.Constants.BOGGER;

import com.litl.leveldb.DB;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.vn.nguyenduck.blocktopograph.core.chunk.Chunk;
import io.vn.nguyenduck.blocktopograph.core.chunk.ChunkPosition;
import io.vn.nguyenduck.blocktopograph.core.chunk.ChunkTag;
import io.vn.nguyenduck.blocktopograph.world.WorldPreLoader;

public class World {
    private final WorldPreLoader preLoader;
    private final DB database;
    private final Map<byte[], byte[]> rawData = new HashMap<>();
    private final Map<ChunkPosition, Chunk> chunks = new HashMap<>();
    private final Map<byte[], byte[]> actorprefix = new HashMap<>();
    private final Map<byte[], byte[]> digp = new HashMap<>();

    public World(WorldPreLoader preLoader) {
        this.preLoader = preLoader;
        this.database = new DB(preLoader.path + "/db");
    }

    public void load() {
        var it = database.iterator();
        for (it.seekToFirst(); it.isValid(); it.next()) {
            var key = it.getKey();
            var value = it.getValue();
            if (ChunkTag.isChunkTag(key)) {
                var chunkKey = Chunk.readChunkKey(key);
                var pos = chunkKey.position;
                if (!chunks.containsKey(pos)) chunks.put(pos, new Chunk(pos));
                Objects.requireNonNull(chunks.get(pos)).load(key, value);
            } else if (new String(key).startsWith("actorprefix")) {
                actorprefix.put(key, value);
            } else if (new String(key).startsWith("digp")) {
                digp.put(key, value);
            } else {
                rawData.put(key, value);
                BOGGER.info(new String(key) + " " + Arrays.toString(value));
            }
        }
        it.close();
    }

    public void loadChunks() {
        for (var pos : chunks.keySet()) {
            var chunk = chunks.get(pos);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        database.close();
    }
}