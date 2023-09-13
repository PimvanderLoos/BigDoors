package net.minecraft.nbt.visitors;

import java.util.List;
import net.minecraft.nbt.NBTTagType;

public record FieldSelector(List<String> a, NBTTagType<?> b, String c) {

    private final List<String> path;
    private final NBTTagType<?> type;
    private final String name;

    public FieldSelector(NBTTagType<?> nbttagtype, String s) {
        this(List.of(), nbttagtype, s);
    }

    public FieldSelector(String s, NBTTagType<?> nbttagtype, String s1) {
        this(List.of(s), nbttagtype, s1);
    }

    public FieldSelector(String s, String s1, NBTTagType<?> nbttagtype, String s2) {
        this(List.of(s, s1), nbttagtype, s2);
    }

    public FieldSelector(List<String> list, NBTTagType<?> nbttagtype, String s) {
        this.path = list;
        this.type = nbttagtype;
        this.name = s;
    }

    public List<String> path() {
        return this.path;
    }

    public NBTTagType<?> type() {
        return this.type;
    }

    public String name() {
        return this.name;
    }
}
