package net.minecraft.stats;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import net.minecraft.SystemUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.inventory.RecipeBookType;

public final class RecipeBookSettings {

    private static final Map<RecipeBookType, Pair<String, String>> TAG_FIELDS = ImmutableMap.of(RecipeBookType.CRAFTING, Pair.of("isGuiOpen", "isFilteringCraftable"), RecipeBookType.FURNACE, Pair.of("isFurnaceGuiOpen", "isFurnaceFilteringCraftable"), RecipeBookType.BLAST_FURNACE, Pair.of("isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable"), RecipeBookType.SMOKER, Pair.of("isSmokerGuiOpen", "isSmokerFilteringCraftable"));
    private final Map<RecipeBookType, RecipeBookSettings.a> states;

    private RecipeBookSettings(Map<RecipeBookType, RecipeBookSettings.a> map) {
        this.states = map;
    }

    public RecipeBookSettings() {
        this((Map) SystemUtils.make(Maps.newEnumMap(RecipeBookType.class), (enummap) -> {
            RecipeBookType[] arecipebooktype = RecipeBookType.values();
            int i = arecipebooktype.length;

            for (int j = 0; j < i; ++j) {
                RecipeBookType recipebooktype = arecipebooktype[j];

                enummap.put(recipebooktype, new RecipeBookSettings.a(false, false));
            }

        }));
    }

    public boolean isOpen(RecipeBookType recipebooktype) {
        return ((RecipeBookSettings.a) this.states.get(recipebooktype)).open;
    }

    public void setOpen(RecipeBookType recipebooktype, boolean flag) {
        ((RecipeBookSettings.a) this.states.get(recipebooktype)).open = flag;
    }

    public boolean isFiltering(RecipeBookType recipebooktype) {
        return ((RecipeBookSettings.a) this.states.get(recipebooktype)).filtering;
    }

    public void setFiltering(RecipeBookType recipebooktype, boolean flag) {
        ((RecipeBookSettings.a) this.states.get(recipebooktype)).filtering = flag;
    }

    public static RecipeBookSettings read(PacketDataSerializer packetdataserializer) {
        Map<RecipeBookType, RecipeBookSettings.a> map = Maps.newEnumMap(RecipeBookType.class);
        RecipeBookType[] arecipebooktype = RecipeBookType.values();
        int i = arecipebooktype.length;

        for (int j = 0; j < i; ++j) {
            RecipeBookType recipebooktype = arecipebooktype[j];
            boolean flag = packetdataserializer.readBoolean();
            boolean flag1 = packetdataserializer.readBoolean();

            map.put(recipebooktype, new RecipeBookSettings.a(flag, flag1));
        }

        return new RecipeBookSettings(map);
    }

    public void write(PacketDataSerializer packetdataserializer) {
        RecipeBookType[] arecipebooktype = RecipeBookType.values();
        int i = arecipebooktype.length;

        for (int j = 0; j < i; ++j) {
            RecipeBookType recipebooktype = arecipebooktype[j];
            RecipeBookSettings.a recipebooksettings_a = (RecipeBookSettings.a) this.states.get(recipebooktype);

            if (recipebooksettings_a == null) {
                packetdataserializer.writeBoolean(false);
                packetdataserializer.writeBoolean(false);
            } else {
                packetdataserializer.writeBoolean(recipebooksettings_a.open);
                packetdataserializer.writeBoolean(recipebooksettings_a.filtering);
            }
        }

    }

    public static RecipeBookSettings read(NBTTagCompound nbttagcompound) {
        Map<RecipeBookType, RecipeBookSettings.a> map = Maps.newEnumMap(RecipeBookType.class);

        RecipeBookSettings.TAG_FIELDS.forEach((recipebooktype, pair) -> {
            boolean flag = nbttagcompound.getBoolean((String) pair.getFirst());
            boolean flag1 = nbttagcompound.getBoolean((String) pair.getSecond());

            map.put(recipebooktype, new RecipeBookSettings.a(flag, flag1));
        });
        return new RecipeBookSettings(map);
    }

    public void write(NBTTagCompound nbttagcompound) {
        RecipeBookSettings.TAG_FIELDS.forEach((recipebooktype, pair) -> {
            RecipeBookSettings.a recipebooksettings_a = (RecipeBookSettings.a) this.states.get(recipebooktype);

            nbttagcompound.putBoolean((String) pair.getFirst(), recipebooksettings_a.open);
            nbttagcompound.putBoolean((String) pair.getSecond(), recipebooksettings_a.filtering);
        });
    }

    public RecipeBookSettings copy() {
        Map<RecipeBookType, RecipeBookSettings.a> map = Maps.newEnumMap(RecipeBookType.class);
        RecipeBookType[] arecipebooktype = RecipeBookType.values();
        int i = arecipebooktype.length;

        for (int j = 0; j < i; ++j) {
            RecipeBookType recipebooktype = arecipebooktype[j];
            RecipeBookSettings.a recipebooksettings_a = (RecipeBookSettings.a) this.states.get(recipebooktype);

            map.put(recipebooktype, recipebooksettings_a.copy());
        }

        return new RecipeBookSettings(map);
    }

    public void replaceFrom(RecipeBookSettings recipebooksettings) {
        this.states.clear();
        RecipeBookType[] arecipebooktype = RecipeBookType.values();
        int i = arecipebooktype.length;

        for (int j = 0; j < i; ++j) {
            RecipeBookType recipebooktype = arecipebooktype[j];
            RecipeBookSettings.a recipebooksettings_a = (RecipeBookSettings.a) recipebooksettings.states.get(recipebooktype);

            this.states.put(recipebooktype, recipebooksettings_a.copy());
        }

    }

    public boolean equals(Object object) {
        return this == object || object instanceof RecipeBookSettings && this.states.equals(((RecipeBookSettings) object).states);
    }

    public int hashCode() {
        return this.states.hashCode();
    }

    private static final class a {

        boolean open;
        boolean filtering;

        public a(boolean flag, boolean flag1) {
            this.open = flag;
            this.filtering = flag1;
        }

        public RecipeBookSettings.a copy() {
            return new RecipeBookSettings.a(this.open, this.filtering);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (!(object instanceof RecipeBookSettings.a)) {
                return false;
            } else {
                RecipeBookSettings.a recipebooksettings_a = (RecipeBookSettings.a) object;

                return this.open == recipebooksettings_a.open && this.filtering == recipebooksettings_a.filtering;
            }
        }

        public int hashCode() {
            int i = this.open ? 1 : 0;

            i = 31 * i + (this.filtering ? 1 : 0);
            return i;
        }

        public String toString() {
            return "[open=" + this.open + ", filtering=" + this.filtering + "]";
        }
    }
}
