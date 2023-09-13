package net.minecraft.nbt.visitors;

import java.util.List;
import net.minecraft.nbt.NBTTagType;

public record FieldSelector(List<String> path, NBTTagType<?> type, String name) {

    public FieldSelector(NBTTagType<?> nbttagtype, String s) {
        this(List.of(), nbttagtype, s);
    }

    public FieldSelector(String s, NBTTagType<?> nbttagtype, String s1) {
        this(List.of(s), nbttagtype, s1);
    }

    public FieldSelector(String s, String s1, NBTTagType<?> nbttagtype, String s2) {
        this(List.of(s, s1), nbttagtype, s2);
    }
}
