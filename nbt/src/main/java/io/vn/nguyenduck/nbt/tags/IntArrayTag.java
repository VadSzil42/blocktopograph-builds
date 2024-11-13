package io.vn.nguyenduck.nbt.tags;

import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.TagType;

public class IntArrayTag extends Tag<int[]> {
    public IntArrayTag(String name, int[] value) {
        super(TagType.INT_ARRAY, name, value);
    }

    @Override
    public Tag<int[]> clone() {
        return new IntArrayTag(name, value);
    }
}