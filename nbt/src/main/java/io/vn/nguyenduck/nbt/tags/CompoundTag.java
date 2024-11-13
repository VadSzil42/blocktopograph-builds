package io.vn.nguyenduck.nbt.tags;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import io.vn.nguyenduck.nbt.Tag;
import io.vn.nguyenduck.nbt.TagType;

public class CompoundTag<T extends Tag<?>> extends Tag<Map<String, T>> implements Iterable<T> {
    public CompoundTag(String name, Map<String, T> value) {
        super(TagType.COMPOUND, name, value);
    }

    public T getValue(String name) {
        return value.get(name);
    }

    public void setValue(String name, T value) {
        this.value.put(name, value);
    }

    public void setValue(String name, Object value) {
        if (value instanceof Tag<?> v) {
            setValue(name, v);
        } else {
            System.out.println(value.getClass());
        }
    }

    @Override
    public Tag<Map<String, T>> clone() {
        return new CompoundTag<>(name, new TreeMap<>(value));
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return value.values().iterator();
    }
}