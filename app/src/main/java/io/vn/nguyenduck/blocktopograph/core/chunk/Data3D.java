package io.vn.nguyenduck.blocktopograph.core.chunk;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import io.vn.nguyenduck.blocktopograph.core.biome.Biome;

/**
 * @author <a href="https://github.com/Hao-1337">@Hao-1337 - Vũ Quang Hào</a>
 */
public class Data3D {
    public ArrayList<Biome> biomes = new ArrayList<>(256);
    protected ArrayList<Short> heightMap = new ArrayList<>(256);

    ByteBuffer buffer;

    public Data3D(ByteBuffer buffer) {
        this.buffer = buffer;
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < 256; i++) {
            biomes.add(Biome.Plains);
        }
    }

    private boolean getBitFromByte(int bitIndex) {
        int byteIndex = bitIndex / 8;
        int bitOffset = bitIndex % 8;
        byte value = buffer.get(byteIndex);
        return (value & (1 << bitOffset)) != 0;
    }

    private int getBitsFromBytes(int bitStart, int bitLen) {
        if (bitLen <= 0x08) {
            int byteStart = bitStart / 8;
            int byteOffset = bitStart % 8;

            int value = Byte.toUnsignedInt(buffer.get(byteStart))
                    | (Byte.toUnsignedInt(buffer.get(byteStart + 1)) << 8);
            value >>>= byteOffset;

            int mask = (1 << bitLen) - 1;
            return value & mask;
        }
        int result = 0;
        for (int b = 0; b < bitLen; b++) {
            if (getBitFromByte(bitStart + b)) {
                result |= 1 << b;
            }
        }
        return result;
    }

    private int getBlockId(int offset, int blocksPerWord, int bitsPerBlock, int x, int z, int y) {
        int blockPos = (((x * 16) + z) * 16) + y;
        int wordStart = blockPos / blocksPerWord;
        int bitOffset = (blockPos % blocksPerWord) * bitsPerBlock;
        int bitStart = wordStart * 4 * 8 + bitOffset;
        return getBitsFromBytes(offset * 8 + bitStart, bitsPerBlock);
    }

    public void readBiome() {
        int offset = 512;

        while (offset < buffer.limit()) {
            byte header = buffer.get(offset++);
            if ((header & 0x01) != 0x01) return;
            if (header == (byte) 0xFF) {
                continue;
            }

            int bitsPerBlock = header >> 1;
            int blocksPerWord = (int) Math.floor(32.0 / bitsPerBlock);
            int wordCount = (int) Math.ceil(4096.0 / (double) blocksPerWord);
            int paletteOffset = wordCount * 4 + offset;

            if (bitsPerBlock == 0) {
                Biome biome = Biome.fromIndex(buffer.getInt(offset));

                for (int i = 0; i < 256; i++) {
                    biomes.set(i, biome);
                }

                offset += 4;
                continue;
            }

            int paletteLength = buffer.getInt(paletteOffset);
            paletteOffset += 4;

            Biome[] palette = new Biome[paletteLength];
            for (int i = 0; i < paletteLength; i++) {
                palette[i] = Biome.fromIndex(buffer.getInt(paletteOffset + i * 4));
            }

            for (int cy = 0; cy < 16; cy++)
                for (int cx = 0; cx < 16; cx++)
                    for (int cz = 0; cz < 16; cz++)
                        biomes.set(cx * 16 + cz,
                                palette[getBlockId(offset, blocksPerWord, bitsPerBlock, cx, cz, cy)]);

            offset = (paletteOffset + 4 * paletteLength);
        }
    }

    public void readHeight() {
        buffer.position(0);

        for (int i = 0; i < 256; i++) {
            heightMap.add(buffer.getShort());
        }
    }
}