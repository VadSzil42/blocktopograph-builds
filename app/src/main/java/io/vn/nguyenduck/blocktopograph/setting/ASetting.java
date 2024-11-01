package io.vn.nguyenduck.blocktopograph.setting;

import java.util.Objects;
import java.util.function.Function;

public class ASetting {
    public final String path;
    public final Object defaultValue;
    public Object value;
    public final String name;
    public final String description;
    public final Function<Object, String> toString;
    public final Function<String, Object> fromString;

    public ASetting(String path, Object defaultValue, String name, String description) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.name = name;
        this.description = description;
        this.value = defaultValue;
        this.toString = Objects::toString;
        this.fromString = v -> v;
    }

    public ASetting(String path, Object defaultValue, String name, String description, Function<Object, String> toString, Function<String, Object> fromString) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.name = name;
        this.description = description;
        this.value = defaultValue;
        this.toString = toString;
        this.fromString = fromString;
    }

    public <T> T as(Class<T> type) {
        return type.cast(value);
    }
}