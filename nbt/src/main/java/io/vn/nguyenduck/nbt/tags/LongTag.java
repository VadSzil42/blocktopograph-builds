package io.vn.nguyenduck.nbt.tags;

import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.TagType;

public class LongTag extends Tag<Long> {
    public LongTag(String name, Long value) {
        super(TagType.LONG, name, value);
    }

    @Override
    public Tag<Long> clone() {
        return new LongTag(name, value);
    }
}