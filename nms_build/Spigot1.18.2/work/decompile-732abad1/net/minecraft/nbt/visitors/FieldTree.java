package net.minecraft.nbt.visitors;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagType;

public record FieldTree(int a, Map<String, NBTTagType<?>> b, Map<String, FieldTree> c) {

    private final int depth;
    private final Map<String, NBTTagType<?>> selectedFields;
    private final Map<String, FieldTree> fieldsToRecurse;

    private FieldTree(int i) {
        this(i, new HashMap(), new HashMap());
    }

    public FieldTree(int i, Map<String, NBTTagType<?>> map, Map<String, FieldTree> map1) {
        this.depth = i;
        this.selectedFields = map;
        this.fieldsToRecurse = map1;
    }

    public static FieldTree createRoot() {
        return new FieldTree(1);
    }

    public void addEntry(FieldSelector fieldselector) {
        if (this.depth <= fieldselector.path().size()) {
            ((FieldTree) this.fieldsToRecurse.computeIfAbsent((String) fieldselector.path().get(this.depth - 1), (s) -> {
                return new FieldTree(this.depth + 1);
            })).addEntry(fieldselector);
        } else {
            this.selectedFields.put(fieldselector.name(), fieldselector.type());
        }

    }

    public boolean isSelected(NBTTagType<?> nbttagtype, String s) {
        return nbttagtype.equals(this.selectedFields().get(s));
    }

    public int depth() {
        return this.depth;
    }

    public Map<String, NBTTagType<?>> selectedFields() {
        return this.selectedFields;
    }

    public Map<String, FieldTree> fieldsToRecurse() {
        return this.fieldsToRecurse;
    }
}
