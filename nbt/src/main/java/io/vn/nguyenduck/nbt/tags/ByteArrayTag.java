package io.vn.nguyenduck.nbt.tags;

import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.TagType;

public class ByteArrayTag extends Tag<byte[]> {
    public ByteArrayTag(String name, byte[] value) {
        super(TagType.BYTE_ARRAY, name, value);
    }

    @Override
    public Tag<byte[]> clone() {
        return new ByteArrayTag(name, value);
    }
}