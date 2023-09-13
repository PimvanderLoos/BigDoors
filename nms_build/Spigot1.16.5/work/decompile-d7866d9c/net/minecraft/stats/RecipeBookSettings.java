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

    private static final Map<RecipeBookType, Pair<String, String>> a = ImmutableMap.of(RecipeBookType.CRAFTING, Pair.of("isGuiOpen", "isFilteringCraftable"), RecipeBookType.FURNACE, Pair.of("isFurnaceGuiOpen", "isFurnaceFilteringCraftable"), RecipeBookType.BLAST_FURNACE, Pair.of("isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable"), RecipeBookType.SMOKER, Pair.of("isSmokerGuiOpen", "isSmokerFilteringCraftable"));
    private final Map<RecipeBookType, RecipeBookSettings.a> b;

    private RecipeBookSettings(Map<RecipeBookType, RecipeBookSettings.a> map) {
        this.b = map;
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

    public void a(RecipeBookType recipebooktype, boolean flag) {
        ((RecipeBookSettings.a) this.b.get(recipebooktype)).a = flag;
    }

    public void b(RecipeBookType recipebooktype, boolean flag) {
        ((RecipeBookSettings.a) this.b.get(recipebooktype)).b = flag;
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
            RecipeBookSettings.a recipebooksettings_a = (RecipeBookSettings.a) this.b.get(recipebooktype);

            if (recipebooksettings_a == null) {
                packetdataserializer.writeBoolean(false);
                packetdataserializer.writeBoolean(false);
            } else {
                packetdataserializer.writeBoolean(recipebooksettings_a.a);
                packetdataserializer.writeBoolean(recipebooksettings_a.b);
            }
        }

    }

    public static RecipeBookSettings a(NBTTagCompound nbttagcompound) {
        Map<RecipeBookType, RecipeBookSettings.a> map = Maps.newEnumMap(RecipeBookType.class);

        RecipeBookSettings.a.forEach((recipebooktype, pair) -> {
            boolean flag = nbttagcompound.getBoolean((String) pair.getFirst());
            boolean flag1 = nbttagcompound.getBoolean((String) pair.getSecond());

            map.put(recipebooktype, new RecipeBookSettings.a(flag, flag1));
        });
        return new RecipeBookSettings(map);
    }

    public void b(NBTTagCompound nbttagcompound) {
        RecipeBookSettings.a.forEach((recipebooktype, pair) -> {
            RecipeBookSettings.a recipebooksettings_a = (RecipeBookSettings.a) this.b.get(recipebooktype);

            nbttagcompound.setBoolean((String) pair.getFirst(), recipebooksettings_a.a);
            nbttagcompound.setBoolean((String) pair.getSecond(), recipebooksettings_a.b);
        });
    }

    public RecipeBookSettings a() {
        Map<RecipeBookType, RecipeBookSettings.a> map = Maps.newEnumMap(RecipeBookType.class);
        RecipeBookType[] arecipebooktype = RecipeBookType.values();
        int i = arecipebooktype.length;

        for (int j = 0; j < i; ++j) {
            RecipeBookType recipebooktype = arecipebooktype[j];
            RecipeBookSettings.a recipebooksettings_a = (RecipeBookSettings.a) this.b.get(recipebooktype);

            map.put(recipebooktype, recipebooksettings_a.a());
        }

        return new RecipeBookSettings(map);
    }

    public void a(RecipeBookSettings recipebooksettings) {
        this.b.clear();
        RecipeBookType[] arecipebooktype = RecipeBookType.values();
        int i = arecipebooktype.length;

        for (int j = 0; j < i; ++j) {
            RecipeBookType recipebooktype = arecipebooktype[j];
            RecipeBookSettings.a recipebooksettings_a = (RecipeBookSettings.a) recipebooksettings.b.get(recipebooktype);

            this.b.put(recipebooktype, recipebooksettings_a.a());
        }

    }

    public boolean equals(Object object) {
        return this == object || object instanceof RecipeBookSettings && this.b.equals(((RecipeBookSettings) object).b);
    }

    public int hashCode() {
        return this.b.hashCode();
    }

    static final class a {

        private boolean a;
        private boolean b;

        public a(boolean flag, boolean flag1) {
            this.a = flag;
            this.b = flag1;
        }

        public RecipeBookSettings.a a() {
            return new RecipeBookSettings.a(this.a, this.b);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (!(object instanceof RecipeBookSettings.a)) {
                return false;
            } else {
                RecipeBookSettings.a recipebooksettings_a = (RecipeBookSettings.a) object;

                return this.a == recipebooksettings_a.a && this.b == recipebooksettings_a.b;
            }
        }

        public int hashCode() {
            int i = this.a ? 1 : 0;

            i = 31 * i + (this.b ? 1 : 0);
            return i;
        }

        public String toString() {
            return "[open=" + this.a + ", filtering=" + this.b + ']';
        }
    }
}
