package io.vn.nguyenduck.nbt.tags;

import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.TagType;

public class DoubleTag extends Tag<Double> {
    public DoubleTag(String name, Double value) {
        super(TagType.DOUBLE, name, value);
    }

    @Override
    public Tag<Double> clone() {
        return new DoubleTag(name, value);
    }
}