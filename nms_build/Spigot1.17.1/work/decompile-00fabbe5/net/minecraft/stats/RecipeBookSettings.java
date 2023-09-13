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
        this((Map) SystemUtils.a((Object) Maps.newEnumMap(RecipeBookType.class), (enummap) -> {
            RecipeBookType[] arecipebooktype = RecipeBookType.values();
            int i = arecipebooktype.length;

            for (int j = 0; j < i; ++j) {
                RecipeBookType recipebooktype = arecipebooktype[j];

                enummap.put(recipebooktype, new RecipeBookSettings.a(false, false));
            }

        }));
    }

    public boolean a(RecipeBookType recipebooktype) {
        return ((RecipeBookSettings.a) this.states.get(recipebooktype)).open;
    }

    public void a(RecipeBookType recipebooktype, boolean flag) {
        ((RecipeBookSettings.a) this.states.get(recipebooktype)).open = flag;
    }

    public boolean b(RecipeBookType recipebooktype) {
        return ((RecipeBookSettings.a) this.states.get(recipebooktype)).filtering;
    }

    public void b(RecipeBookType recipebooktype, boolean flag) {
        ((RecipeBookSettings.a) this.states.get(recipebooktype)).filtering = flag;
    }

    public static RecipeBookSettings a(PacketDataSerializer packetdataserializer) {
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

    public void b(PacketDataSerializer packetdataserializer) {
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

    public static RecipeBookSettings a(NBTTagCompound nbttagcompound) {
        Map<RecipeBookType, RecipeBookSettings.a> map = Maps.newEnumMap(RecipeBookType.class);

        RecipeBookSettings.TAG_FIELDS.forEach((recipebooktype, pair) -> {
            boolean flag = nbttagcompound.getBoolean((String) pair.getFirst());
            boolean flag1 = nbttagcompound.getBoolean((String) pair.getSecond());

            map.put(recipebooktype, new RecipeBookSettings.a(flag, flag1));
        });
        return new RecipeBookSettings(map);
    }

    public void b(NBTTagCompound nbttagcompound) {
        RecipeBookSettings.TAG_FIELDS.forEach((recipebooktype, pair) -> {
            RecipeBookSettings.a recipebooksettings_a = (RecipeBookSettings.a) this.states.get(recipebooktype);

            nbttagcompound.setBoolean((String) pair.getFirst(), recipebooksettings_a.open);
            nbttagcompound.setBoolean((String) pair.getSecond(), recipebooksettings_a.filtering);
        });
    }

    public RecipeBookSettings a() {
        Map<RecipeBookType, RecipeBookSettings.a> map = Maps.newEnumMap(RecipeBookType.class);
        RecipeBookType[] arecipebooktype = RecipeBookType.values();
        int i = arecipebooktype.length;

        for (int j = 0; j < i; ++j) {
            RecipeBookType recipebooktype = arecipebooktype[j];
            RecipeBookSettings.a recipebooksettings_a = (RecipeBookSettings.a) this.states.get(recipebooktype);

            map.put(recipebooktype, recipebooksettings_a.a());
        }

        return new RecipeBookSettings(map);
    }

    public void a(RecipeBookSettings recipebooksettings) {
        this.states.clear();
        RecipeBookType[] arecipebooktype = RecipeBookType.values();
        int i = arecipebooktype.length;

        for (int j = 0; j < i; ++j) {
            RecipeBookType recipebooktype = arecipebooktype[j];
            RecipeBookSettings.a recipebooksettings_a = (RecipeBookSettings.a) recipebooksettings.states.get(recipebooktype);

            this.states.put(recipebooktype, recipebooksettings_a.a());
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

        public RecipeBookSettings.a a() {
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
