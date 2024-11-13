package io.vn.nguyenduck.nbt.tags;

import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.TagType;

public class IntTag extends Tag<Integer> {
    public IntTag(String name, Integer value) {
        super(TagType.INT, name, value);
    }

    @Override
    public Tag<Integer> clone() {
        return new IntTag(name, value);
    }
}