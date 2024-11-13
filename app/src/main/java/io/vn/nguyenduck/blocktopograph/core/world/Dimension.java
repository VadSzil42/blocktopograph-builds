package io.vn.nguyenduck.blocktopograph.core.world;

public enum Dimension {
    Overworld(0),
    Nether(1),
    TheEnd(2);

    private final int id;

    Dimension(int id) {
        this.id = id;
    }

    public static Dimension get(int id) {
        for (Dimension dimension : values()) {
            if (dimension.id == id) {
                return dimension;
            }
        }
        return null;
    }
}