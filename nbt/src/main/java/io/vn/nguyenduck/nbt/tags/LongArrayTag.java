package io.vn.nguyenduck.nbt.tags;

import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.TagType;

public class LongArrayTag extends Tag<long[]> {
    public LongArrayTag(String name, long[] value) {
        super(TagType.LONG_ARRAY, name, value);
    }

    @Override
    public Tag<long[]> clone() {
        return new LongArrayTag(name, value);
    }
}