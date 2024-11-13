package io.vn.nguyenduck.blocktopograph.core.chunk;

import static io.vn.nguyenduck.blocktopograph.Constants.BOGGER;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vn.nguyenduck.blocktopograph.core.world.Dimension;

public class Chunk {

    public final ChunkPosition position;
    public final Map<byte[], byte[]> rawData = new HashMap<>();
    public final List<SubChunk> subChunks = new ArrayList<>();
    private byte version;

    public Chunk(int x, int z) {
        this(new ChunkPosition(x, z));
    }

    public Chunk(ChunkPosition position) {
        this.position = position;
    }

    public byte getVersion() {
        return version;
    }

    public static ChunkKey readChunkKey(byte[] key) {
        var buffer = ByteBuffer.wrap(key);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        if (buffer.remaining() < 9) return null;

        var chunkX = buffer.getInt();
        var chunkZ = buffer.getInt();

        Dimension dimension = Dimension.Overworld;
        if (buffer.remaining() > 4) dimension = Dimension.get(buffer.getInt());
        ChunkTag chunkTag = null;
        if (buffer.hasRemaining()) chunkTag = ChunkTag.get(buffer.get());
        byte index = 0;
        if (buffer.hasRemaining()) index = buffer.get();

        if (chunkTag == null) return null;
        if (buffer.hasRemaining()) return null;

        return new ChunkKey(new ChunkPosition(chunkX, chunkZ), chunkTag, dimension, index);
    }

    public void load(byte[] key, byte[] data) {
        rawData.put(key, data);
        var buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        var chunkKey = readChunkKey(key);
        assert chunkKey != null;
        switch (chunkKey.tag) {
            case Version:
                version = buffer.get();
                break;
            case FinalizedState:
            case MetaDataHash:
            case BlendingData:
            case ActorDigestVersion:
                break;
            case SubChunkPrefix:
                loadSubChunkPrefix(chunkKey, buffer);
                break;
            case Data3D:
                loadData3D(chunkKey, buffer);
                break;
            default:
                BOGGER.info(chunkKey + " " + data.length + " " + Arrays.toString(data));
                break;
        }
    }

    private void loadSubChunkPrefix(ChunkKey k, ByteBuffer data) {
        var fName = String.format("/sdcard/test%s_%s_%s.nbt", position.x, position.z, k.index);
//        try {
//            var reader = new NBTInputStream(new ByteArrayInputStream(data.array()));
//            var dataParsed = reader.readTag();
//            BOGGER.info(dataParsed.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
        try (var out = new FileOutputStream(fName)) {
            out.write(data.array());
        } catch (Exception e2) {
            e2.printStackTrace();
        }
//        }
    }

    private void loadData3D(ChunkKey k, ByteBuffer data) {
//        var data3d = new Data3D(data);
//        data3d.readBiome();
        var fName = String.format("/sdcard/data3D%s_%s.nbt", position.x, position.z);

        try (var out = new FileOutputStream(fName)) {
            data.position(512);
            var dataNew = new byte[data.remaining()];
            data.get(dataNew);
            out.write(dataNew);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void addSubChunk(SubChunk subChunk) {
        subChunks.add(subChunk);
    }

    public void removeSubChunk(SubChunk subChunk) {
        subChunks.remove(subChunk);
    }

    public SubChunk getSubChunk(int y) {
        return subChunks.get(y >> 4);
    }

    public SubChunk getSubChunk(byte id) {
        return subChunks.get(id);
    }
}