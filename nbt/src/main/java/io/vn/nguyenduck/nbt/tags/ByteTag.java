package io.vn.nguyenduck.nbt.tags;

import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.TagType;

public class ByteTag extends Tag<Byte> {
    public ByteTag(String name, Byte value) {
        super(TagType.BYTE, name, value);
    }

    @Override
    public Tag<Byte> clone() {
        return new ByteTag(name, value);
    }
}