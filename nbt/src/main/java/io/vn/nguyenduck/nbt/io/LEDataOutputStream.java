package io.vn.nguyenduck.nbt.io;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LEDataOutputStream extends FilterOutputStream implements DataOutput {

    protected DataOutputStream out;

    public LEDataOutputStream(OutputStream os) {
        super(os);
        out = new DataOutputStream(os);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeByte(int v) throws IOException {
        out.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        out.writeShort(Short.reverseBytes((short) v));
    }

    @Override
    public void writeChar(int v) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeInt(int v) throws IOException {
        out.writeInt(Integer.reverseBytes(v));
    }

    @Override
    public void writeLong(long v) throws IOException {
        out.writeLong(Long.reverseBytes(v));
    }

    @Override
    public void writeFloat(float v) throws IOException {
        out.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        out.writeLong(Long.reverseBytes(Double.doubleToLongBits(v)));
    }

    @Override
    public void writeBytes(@NotNull String s) throws IOException {
        out.writeBytes(s);
    }

    @Override
    public void writeChars(@NotNull String s) throws IOException {
        writeShort(s.length());
        out.write(s.getBytes());
    }

    @Override
    public void writeUTF(@NotNull String s) throws IOException {
        throw new UnsupportedOperationException();
    }
}