package io.vn.nguyenduck.blocktopograph.core.chunk;

import static io.vn.nguyenduck.blocktopograph.core.chunk.Chunk.readChunkKey;

public enum ChunkTag {

    Data3D(43),
    Version(44),
    Data2D(45),
    Data2DLegacy(46),
    SubChunkPrefix(47),
    LegacyTerrain(48),
    BlockEntity(49),
    Entity(50),
    PendingTicks(51),
    LegacyBlockExtraData(52),
    BiomeState(53),
    FinalizedState(54),
    ConversionData(55),
    BorderBlocks(56),
    HardcodedSpawners(57),
    RandomTicks(58),
    CheckSums(59),
    GenerationSeed(60),
    GeneratedPreCavesAndCliffsBlending(61), // unused
    BlendingBiomeHeight(62), // unused
    MetaDataHash(63),
    BlendingData(64),
    ActorDigestVersion(65),
    LegacyVersion(118);

    private final byte value;

    ChunkTag(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    public static boolean isChunkTag(byte[] key) {
        return readChunkKey(key) != null;
    }

    public static ChunkTag get(int value) {
        for (ChunkTag tag : ChunkTag.values()) {
            if (tag.getValue() == value) {
                return tag;
            }
        }
        return null;
    }
}