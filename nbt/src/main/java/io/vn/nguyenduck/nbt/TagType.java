package io.vn.nguyenduck.nbt;

import java.util.Objects;

public enum TagType {
    END("End", "end", 0),
    BYTE("Byte", "byte", 1),
    SHORT("Short", "short", 2),
    INT("Int", "int", 3),
    LONG("Long", "long", 4),
    FLOAT("Float", "float", 5),
    DOUBLE("Double", "double", 6),
    BYTE_ARRAY("ByteArray", "byte[]", 7),
    STRING("String", "string", 8),
    LIST("List", "list", 9),
    COMPOUND("Compound", "compound", 10),
    INT_ARRAY("IntArray", "int[]", 11),
    LONG_ARRAY("LongArray", "long[]", 12),
    SHORT_ARRAY("ShortArray", "short[]", 100);

    public final String typeName;
    public final String lowerCaseTypeName;
    public final int id;

    TagType(String typeName, String lowerCaseTypeName, int id) {
        this.typeName = typeName;
        this.lowerCaseTypeName = lowerCaseTypeName;
        this.id = id;
    }

    public static TagType get(int id) {
        for (TagType type : TagType.values()) {
            if (type.id == id) {
                return type;
            }
        }
        return null;
    }

    public static TagType get(String name) {
        for (TagType type : TagType.values()) {
            if (Objects.equals(type.typeName, name) || Objects.equals(type.lowerCaseTypeName, name)) {
                return type;
            }
        }
        return null;
    }
}