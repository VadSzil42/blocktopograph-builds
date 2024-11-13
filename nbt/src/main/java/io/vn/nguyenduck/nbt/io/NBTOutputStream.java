package io.vn.nguyenduck.nbt.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterOutputStream;

import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.tags.ByteArrayTag;
import io.vn.nguyenduck.nbt.tags.ByteTag;
import io.vn.nguyenduck.nbt.tags.CompoundTag;
import io.vn.nguyenduck.nbt.tags.DoubleTag;
import io.vn.nguyenduck.nbt.tags.FloatTag;
import io.vn.nguyenduck.nbt.tags.IntArrayTag;
import io.vn.nguyenduck.nbt.tags.IntTag;
import io.vn.nguyenduck.nbt.tags.ListTag;
import io.vn.nguyenduck.nbt.tags.LongArrayTag;
import io.vn.nguyenduck.nbt.tags.LongTag;
import io.vn.nguyenduck.nbt.tags.ShortArrayTag;
import io.vn.nguyenduck.nbt.tags.ShortTag;
import io.vn.nguyenduck.nbt.tags.StringTag;

public class NBTOutputStream implements Closeable {

    private final LEDataOutputStream out;

    public NBTOutputStream(OutputStream os) throws IOException {
        this(os, Compression.NO);
    }

    public NBTOutputStream(OutputStream os, Compression compression) throws IOException {
        out = new LEDataOutputStream(switch (compression) {
            case NO -> os;
            case GZIP -> new GZIPOutputStream(os);
            case ZLIB -> new InflaterOutputStream(os);
        });
    }

    public void writeTag(Tag<?> tag) throws IOException {
        out.writeByte(tag.getType().id);
        out.writeChars(tag.getName());
        writeTagPayload(tag);
    }

    private void writeTagPayload(Tag<?> tag) throws IOException {
        switch (tag.getType()) {
            case END:
                out.writeByte(0);
                break;
            case BYTE:
                out.writeByte(((ByteTag) tag).getValue());
                break;
            case SHORT:
                out.writeShort(((ShortTag) tag).getValue());
                break;
            case INT:
                out.writeInt(((IntTag) tag).getValue());
                break;
            case LONG:
                out.writeLong(((LongTag) tag).getValue());
                break;
            case FLOAT:
                out.writeFloat(((FloatTag) tag).getValue());
                break;
            case DOUBLE:
                out.writeDouble(((DoubleTag) tag).getValue());
                break;
            case STRING:
                out.writeUTF(((StringTag) tag).getValue());
                break;
            case LIST: {
                ListTag<?> list = (ListTag<?>) tag;
                if (list.getValue().isEmpty()) out.writeByte(0);
                else out.writeByte(list.getChildType().id);
                out.writeInt(list.getValue().size());
                for (Tag<?> child : list.getValue()) writeTagPayload(child);
                break;
            }
            case COMPOUND: {
                for (Tag<?> child : (CompoundTag<?>) tag) writeTag(child);
                out.writeByte(0);
                break;
            }
            case BYTE_ARRAY: {
                ByteArrayTag byteArrayTag = (ByteArrayTag) tag;
                byte[] value = byteArrayTag.getValue();
                out.writeInt(value.length);
                out.write(value);
                break;
            }
            case SHORT_ARRAY: {
                ShortArrayTag shortArrayTag = (ShortArrayTag) tag;
                short[] value = shortArrayTag.getValue();
                out.writeInt(value.length);
                for (short v : value) out.writeShort(v);
                break;
            }
            case INT_ARRAY: {
                IntArrayTag intTag = (IntArrayTag) tag;
                int[] value = intTag.getValue();
                out.writeInt(value.length);
                for (int v : value) out.writeInt(v);
                break;
            }
            case LONG_ARRAY: {
                LongArrayTag longTag = (LongArrayTag) tag;
                long[] value = longTag.getValue();
                out.writeInt(value.length);
                for (long v : value) out.writeLong(v);
                break;
            }
        }
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}