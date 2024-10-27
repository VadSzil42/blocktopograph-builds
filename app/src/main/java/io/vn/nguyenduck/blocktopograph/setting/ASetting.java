package io.vn.nguyenduck.blocktopograph.setting;

public abstract class ASetting {

    public Object value = getDefaultValue();

    public abstract String getKey();

    public abstract String getCategory();

    public abstract String getName();

    public abstract String getDescription();

    public abstract Object getDefaultValue();

    public Class<?> getType() {
        return getDefaultValue().getClass();
    }
}