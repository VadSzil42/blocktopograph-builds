package io.vn.nguyenduck.nbt.tags;

import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.TagType;

public class StringTag extends Tag<String> {
    public StringTag(String name, String value) {
        super(TagType.STRING, name, value);
    }

    @Override
    public Tag<String> clone() {
        return new StringTag(name, value);
    }
}