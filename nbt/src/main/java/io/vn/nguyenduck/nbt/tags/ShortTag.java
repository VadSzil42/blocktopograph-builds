package io.vn.nguyenduck.nbt.tags;

import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.TagType;

public class ShortTag extends Tag<Short> {
    public ShortTag(String name, Short value) {
        super(TagType.SHORT, name, value);
    }

    @Override
    public Tag<Short> clone() {
        return new ShortTag(name, value);
    }
}