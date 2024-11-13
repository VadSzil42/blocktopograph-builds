package io.vn.nguyenduck.blocktopograph.core.chunk;

import java.nio.ByteBuffer;

public class PaletteData {

    public byte version;
    public byte[] data;
    public int position;
    public PaletteType type;

    public PaletteData(byte version, byte[] data) {
        this(version, data, PaletteType.BLOCK);
    }

    public PaletteData(byte version, byte[] data, PaletteType type) {
        this.version = version;
        this.data = data;
        this.type = type;
    }

    public static PaletteData read(ByteBuffer data) {
        var version = data.get();
        var position = 0;
        var offset = 0;

        switch (version) {
            case 9:
                position = 3;
                offset = 4;
                break;
            default:
                throw new IllegalArgumentException("Unsupported subchunk version: " + version);
        }

        return new PaletteData(version, data.array());
    }
}