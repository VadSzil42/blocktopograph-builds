package io.vn.nguyenduck.nbt;

public abstract class Tag<T> implements Cloneable {
    protected String name;
    protected TagType type;
    protected T value;

    protected Tag(TagType type, String name, T value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TagType getType() {
        return type;
    }

    public void setType(TagType type) {
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public abstract Tag<T> clone();
}