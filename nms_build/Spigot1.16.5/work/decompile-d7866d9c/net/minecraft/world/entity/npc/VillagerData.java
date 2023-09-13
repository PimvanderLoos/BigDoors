package net.minecraft.world.entity.npc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.IRegistry;

public class VillagerData {

    private static final int[] b = new int[]{0, 10, 70, 150, 250};
    public static final Codec<VillagerData> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(IRegistry.VILLAGER_TYPE.fieldOf("type").orElseGet(() -> {
            return VillagerType.PLAINS;
        }).forGetter((villagerdata) -> {
            return villagerdata.c;
        }), IRegistry.VILLAGER_PROFESSION.fieldOf("profession").orElseGet(() -> {
            return VillagerProfession.NONE;
        }).forGetter((villagerdata) -> {
            return villagerdata.d;
        }), Codec.INT.fieldOf("level").orElse(1).forGetter((villagerdata) -> {
            return villagerdata.e;
        })).apply(instance, VillagerData::new);
    });
    private final VillagerType c;
    private final VillagerProfession d;
    private final int e;

    public VillagerData(VillagerType villagertype, VillagerProfession villagerprofession, int i) {
        this.c = villagertype;
        this.d = villagerprofession;
        this.e = Math.max(1, i);
    }

    public VillagerType getType() {
        return this.c;
    }

    public VillagerProfession getProfession() {
        return this.d;
    }

    public int getLevel() {
        return this.e;
    }

    public VillagerData withType(VillagerType villagertype) {
        return new VillagerData(villagertype, this.d, this.e);
    }

    public VillagerData withProfession(VillagerProfession villagerprofession) {
        return new VillagerData(this.c, villagerprofession, this.e);
    }

    public VillagerData withLevel(int i) {
        return new VillagerData(this.c, this.d, i);
    }

    public static int c(int i) {
        return d(i) ? VillagerData.b[i] : 0;
    }

    public static boolean d(int i) {
        return i >= 1 && i < 5;
    }
}
