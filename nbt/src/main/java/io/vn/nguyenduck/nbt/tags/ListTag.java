package io.vn.nguyenduck.nbt.tags;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.TagType;

public class ListTag<T extends Tag<?>> extends Tag<List<T>> implements Iterable<T> {
    protected TagType childType;

    public ListTag(String name, List<T> value, TagType childType) {
        super(TagType.LIST, name, value);
        this.childType = childType;
    }

    public TagType getChildType() {
        return childType;
    }

    public T getValue(int index) {
        return value.get(index);
    }

    @Override
    public Tag<List<T>> clone() {
        return new ListTag<>(name, new ArrayList<>(value), childType);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return value.iterator();
    }
}