package io.vn.nguyenduck.nbt.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.TagType;
import io.vn.nguyenduck.nbt.tags.ByteArrayTag;
import io.vn.nguyenduck.nbt.tags.ByteTag;
import io.vn.nguyenduck.nbt.tags.CompoundTag;
import io.vn.nguyenduck.nbt.tags.DoubleTag;
import io.vn.nguyenduck.nbt.tags.EndTag;
import io.vn.nguyenduck.nbt.tags.FloatTag;
import io.vn.nguyenduck.nbt.tags.IntArrayTag;
import io.vn.nguyenduck.nbt.tags.IntTag;
import io.vn.nguyenduck.nbt.tags.ListTag;
import io.vn.nguyenduck.nbt.tags.LongArrayTag;
import io.vn.nguyenduck.nbt.tags.LongTag;
import io.vn.nguyenduck.nbt.tags.ShortArrayTag;
import io.vn.nguyenduck.nbt.tags.ShortTag;
import io.vn.nguyenduck.nbt.tags.StringTag;

public class NBTInputStream implements Closeable {

    private final LEDataInputStream in;

    public NBTInputStream(InputStream is) throws IOException {
        this(is, Compression.NO);
    }

    public NBTInputStream(InputStream is, Compression compression) throws IOException {
        in = new LEDataInputStream(switch (compression) {
            case NO -> is;
            case GZIP -> new GZIPInputStream(is);
            case ZLIB -> new InflaterInputStream(is);
        });
    }

    public Tag<?> readTag() throws IOException {
        return readTag(0);
    }

    private Tag<?> readTag(int depth) throws IOException {
        int typeId = in.readByte();
        TagType type = TagType.get(typeId);
        String name = type == TagType.END ? "" : in.readUTF();
        return readTagPayload(type, name, depth);
    }

    private Tag<?> readTagPayload(TagType type, String name, int depth) throws IOException {
        switch (type) {
            case END:
                if (depth > 0) return new EndTag();
                throw new IOException("End tag found without a Compound/List tag preceding it.");
            case BYTE:
                return new ByteTag(name, in.readByte());
            case SHORT:
                return new ShortTag(name, in.readShort());
            case INT:
                return new IntTag(name, in.readInt());
            case LONG:
                return new LongTag(name, in.readLong());
            case FLOAT:
                return new FloatTag(name, in.readFloat());
            case DOUBLE:
                return new DoubleTag(name, in.readDouble());
            case STRING:
                return new StringTag(name, in.readUTF());
            case LIST: {
                TagType childType = TagType.get(in.readByte());
                int length = in.readInt();
                if (childType == TagType.END && length > 0)
                    throw new IOException("End tag not permitted in a list.");

                List<Tag<?>> tagList = new ArrayList<>(length);
                for (int i = 0; i < length; i++)
                    tagList.add(readTagPayload(childType, "", ++depth));

                return new ListTag<>(name, tagList, childType);
            }
            case COMPOUND: {
                Map<String, Tag<?>> compound = new TreeMap<>();
                while (true) {
                    Tag<?> tag = readTag(depth + 1);
                    if (tag instanceof EndTag) break;
                    compound.put(tag.getName(), tag);
                }
                return new CompoundTag<>(name, compound);
            }
            case INT_ARRAY: {
                int length = in.readInt();
                int[] ints = new int[length];
                for (int i = 0; i < length; i++) ints[i] = in.readInt();
                return new IntArrayTag(name, ints);
            }
            case LONG_ARRAY: {
                int length = in.readInt();
                long[] longs = new long[length];
                for (int i = 0; i < length; i++) longs[i] = in.readLong();
                return new LongArrayTag(name, longs);
            }
            case SHORT_ARRAY: {
                int length = in.readInt();
                short[] shorts = new short[length];
                for (int i = 0; i < length; i++) {
                    shorts[i] = in.readShort();
                }
                return new ShortArrayTag(name, shorts);
            }
            case BYTE_ARRAY: {
                int length = in.readInt();
                byte[] bytes = new byte[length];
                in.readFully(bytes);
                return new ByteArrayTag(name, bytes);
            }
            default:
                throw new IllegalArgumentException("Invalid tag type: " + type + ".");
        }
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}