package io.vn.nguyenduck.nbt.io;

import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LEDataInputStream extends FilterInputStream implements DataInput {

    protected DataInputStream in;

    public LEDataInputStream(InputStream is) {
        super(is);
        in = new DataInputStream(is);
    }

    @Override
    public void readFully(@NotNull byte[] b) throws IOException {
        in.readFully(b);
    }

    @Override
    public void readFully(@NotNull byte[] b, int off, int len) throws IOException {
        in.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return in.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return in.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return in.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return in.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return Short.reverseBytes(in.readShort());
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return (char) (Integer.reverseBytes(in.readUnsignedShort()) >> 16);
    }

    @Override
    public char readChar() throws IOException {
        return Character.reverseBytes(in.readChar());
    }

    @Override
    public int readInt() throws IOException {
        return Integer.reverseBytes(in.readInt());
    }

    @Override
    public long readLong() throws IOException {
        return Long.reverseBytes(in.readLong());
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(Integer.reverseBytes(readInt()));
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(Long.reverseBytes(readLong()));
    }

    @Override
    public String readLine() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull String readUTF() throws IOException {
        int length = readUnsignedShort();
        byte[] rawName = new byte[length];
        readFully(rawName);
        return new String(rawName);
    }
}