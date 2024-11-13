package io.vn.nguyenduck.nbt.io;

public enum Compression {
    NO(0),
    GZIP(1),
    ZLIB(2);

    private final int value;

    Compression(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}