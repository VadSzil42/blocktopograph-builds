package io.vn.nguyenduck.nbt.tags;

import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.TagType;

public class ShortArrayTag extends Tag<short[]> {
    public ShortArrayTag(String name, short[] value) {
        super(TagType.SHORT_ARRAY, name, value);
    }

    @Override
    public Tag<short[]> clone() {
        return new ShortArrayTag(name, value);
    }
}