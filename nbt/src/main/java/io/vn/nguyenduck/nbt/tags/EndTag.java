package io.vn.nguyenduck.nbt.tags;

import javax.lang.model.type.NullType;

import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.TagType;

public class EndTag extends Tag<NullType> {
    public EndTag() {
        super(TagType.END, null, null);
    }

    @Override
    public void setValue(NullType value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Tag<NullType> clone() {
        return new EndTag();
    }
}