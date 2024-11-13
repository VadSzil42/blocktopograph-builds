package io.vn.nguyenduck.nbt.tags;

import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.TagType;

public class FloatTag extends Tag<Float> {
    public FloatTag(String name, Float value) {
        super(TagType.FLOAT, name, value);
    }

    @Override
    public Tag<Float> clone() {
        return new FloatTag(name, value);
    }
}